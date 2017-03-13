/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.belis2.server.action;

import Sirius.server.middleware.impls.domainserver.DomainServerImpl;
import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import java.rmi.Remote;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cismet.belis.commons.constants.ArbeitsauftragPropertyConstants;
import de.cismet.belis.commons.constants.BelisMetaClassConstants;
import de.cismet.belis.commons.constants.VeranlassungPropertyConstants;

import de.cismet.belis2.server.search.NextArbeitsauftragNummerSearch;
import de.cismet.belis2.server.search.NextVeranlassungNummerSearch;
import de.cismet.belis2.server.search.VeranlassungsArtSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerAction;
import de.cismet.cids.server.search.CidsServerSearch;
import de.cismet.cids.server.search.SearchException;

import static de.cismet.belis2.server.action.AbstractBelisServerAction.createDmsURLFromLink;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ServerAction.class)
public class NewIncidentAction extends AbstractBelisServerAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(NewIncidentAction.class);
    private static final String SCHLUESSEL_STOERUNG = "S";

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        OBJEKT_ID, OBJEKT_TYP, DOKUMENT_URLS, BEZEICHNUNG, BESCHREIBUNG, BEMERKUNG, AKTION, ARBEITSAUFTRAG,
        ARBEITSAUFTRAG_ZUGEWIESEN_AN
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum Aktion {

        //~ Enum constants -----------------------------------------------------

        VERANLASSUNG, EINZELAUFTRAG, ADD2ARBEITSAUFTRAG
    }

    //~ Instance fields --------------------------------------------------------

    private final Map dummyLocalServers = new HashMap<String, Remote>();

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Object processExecution() throws Exception {
        final int goId = (Integer)getParam(ParameterType.OBJEKT_ID.toString(), Integer.class);
        final String goClassName = ((String)getParam(ParameterType.OBJEKT_TYP.toString(), String.class)).toLowerCase();
        final String bezeichnung = (String)getParam(ParameterType.BEZEICHNUNG.toString(), String.class);
        final String beschreibung = (String)getParam(ParameterType.BESCHREIBUNG.toString(), String.class);
        final String bemerkung = (String)getParam(ParameterType.BEMERKUNG.toString(), String.class);
        final String aktion = (String)getParam(ParameterType.AKTION.toString(), String.class);
        final int arbeitsauftragId = (Integer)getParam(ParameterType.OBJEKT_ID.toString(), Integer.class);
        final String arbeitsauftragZugewiesenAn = (String)getParam(ParameterType.ARBEITSAUFTRAG_ZUGEWIESEN_AN
                        .toString(),
                String.class);

        final Collection<String> urls = (Collection<String>)getListParam(ParameterType.DOKUMENT_URLS.toString(),
                String.class);

        final MetaClass goMetaClass = CidsBean.getMetaClassFromTableName(BelisMetaClassConstants.DOMAIN, goClassName);
        if (goMetaClass == null) {
            throw new Exception("metaclass " + goClassName + " not found");
        }

        final CidsBean arbeitsauftragBean;
        if (Aktion.ADD2ARBEITSAUFTRAG.toString().equals(aktion)) {
            final String arbeitsauftragNummer = NextArbeitsauftragNummerSearch.getStringRepresentation((List<Long>)
                    executeSearch(new NextArbeitsauftragNummerSearch()));
            arbeitsauftragBean = CidsBean.createNewCidsBeanFromTableName(
                    BelisMetaClassConstants.DOMAIN,
                    BelisMetaClassConstants.MC_ARBEITSAUFTRAG);
            arbeitsauftragBean.setProperty(ArbeitsauftragPropertyConstants.PROP__NUMMER, arbeitsauftragNummer);
            arbeitsauftragBean.setProperty(ArbeitsauftragPropertyConstants.PROP__ANGELEGT_AM, new Date());
            arbeitsauftragBean.setProperty(ArbeitsauftragPropertyConstants.PROP__ANGELEGT_VON, getUser().getName());
            arbeitsauftragBean.setProperty(
                ArbeitsauftragPropertyConstants.PROP__ZUGEWIESEN_AN,
                arbeitsauftragZugewiesenAn);
        } else if (Aktion.ADD2ARBEITSAUFTRAG.toString().equals(aktion)) {
            final MetaClass arbeitsauftragMetaClass = CidsBean.getMetaClassFromTableName(
                    BelisMetaClassConstants.DOMAIN,
                    BelisMetaClassConstants.MC_ARBEITSAUFTRAG);
            if (arbeitsauftragMetaClass == null) {
                throw new Exception("metaclass " + arbeitsauftragMetaClass + " not found");
            }
            final MetaObject arbeitsauftragMo = DomainServerImpl.getServerInstance()
                        .getMetaObject(
                            getUser(),
                            arbeitsauftragId,
                            arbeitsauftragMetaClass.getId());
            if (arbeitsauftragMo == null) {
                throw new Exception(BelisMetaClassConstants.MC_ARBEITSAUFTRAG + " with id "
                            + arbeitsauftragMetaClass.getId() + " not found");
            }
            arbeitsauftragBean = arbeitsauftragMo.getBean();
        } else if (Aktion.VERANLASSUNG.toString().equals(aktion)) {
            arbeitsauftragBean = null;
        } else {
            throw new Exception("unknow Aktion-Type: " + aktion);
        }

        final CidsBean veranlassungBean = CidsBean.createNewCidsBeanFromTableName(
                BelisMetaClassConstants.DOMAIN,
                BelisMetaClassConstants.MC_VERANLASSUNG);

        final String veranlassungGoCollectionProperty;
        if (BelisMetaClassConstants.MC_ABZWEIGDOSE.equalsIgnoreCase(goClassName)) {
            veranlassungGoCollectionProperty = VeranlassungPropertyConstants.PROP__AR_ABZWEIGDOSEN;
        } else if (BelisMetaClassConstants.MC_LEITUNG.equalsIgnoreCase(goClassName)) {
            veranlassungGoCollectionProperty = VeranlassungPropertyConstants.PROP__AR_LEITUNGEN;
        } else if (BelisMetaClassConstants.MC_TDTA_LEUCHTEN.equalsIgnoreCase(goClassName)) {
            veranlassungGoCollectionProperty = VeranlassungPropertyConstants.PROP__AR_LEUCHTEN;
        } else if (BelisMetaClassConstants.MC_MAUERLASCHE.equalsIgnoreCase(goClassName)) {
            veranlassungGoCollectionProperty = VeranlassungPropertyConstants.PROP__AR_MAUERLASCHEN;
        } else if (BelisMetaClassConstants.MC_SCHALTSTELLE.equalsIgnoreCase(goClassName)) {
            veranlassungGoCollectionProperty = VeranlassungPropertyConstants.PROP__AR_SCHALTSTELLEN;
        } else if (BelisMetaClassConstants.MC_TDTA_STANDORT_MAST.equalsIgnoreCase(goClassName)) {
            veranlassungGoCollectionProperty = VeranlassungPropertyConstants.PROP__AR_STANDORTE;
        } else {
            throw new Exception("could not determine collectionProperty for " + goClassName);
        }

        final CidsBean goBean = DomainServerImpl.getServerInstance()
                    .getMetaObject(getUser(), goId, goMetaClass.getID())
                    .getBean();

        final String veranlassungNummer = NextVeranlassungNummerSearch.getStringRepresentation((List<Long>)
                executeSearch(new NextVeranlassungNummerSearch()));

        final Collection<MetaObject> veranlassungsArtMos = executeSearch(new VeranlassungsArtSearch(
                    VeranlassungsArtSearch.SearchBy.SCHLUESSEL,
                    SCHLUESSEL_STOERUNG,
                    true));
        if ((veranlassungsArtMos == null) || veranlassungsArtMos.isEmpty()) {
            throw new Exception("could not find veranlassung_art with schluessel = S");
        }
        final CidsBean veranlassungsArtBean = veranlassungsArtMos.iterator().next().getBean();

        veranlassungBean.getBeanCollectionProperty(veranlassungGoCollectionProperty).add(goBean);
        veranlassungBean.setProperty(VeranlassungPropertyConstants.PROP__BEZEICHNUNG, bezeichnung);
        veranlassungBean.setProperty(VeranlassungPropertyConstants.PROP__BESCHREIBUNG, beschreibung);
        veranlassungBean.setProperty(VeranlassungPropertyConstants.PROP__BEMERKUNGEN, bemerkung);
        veranlassungBean.setProperty(VeranlassungPropertyConstants.PROP__DATUM, new Date());
        veranlassungBean.setProperty(VeranlassungPropertyConstants.PROP__USERNAME, getUser().getName());
        veranlassungBean.setProperty(VeranlassungPropertyConstants.PROP__NUMMER, veranlassungNummer);
        veranlassungBean.setProperty(VeranlassungPropertyConstants.PROP__FK_ART, veranlassungsArtBean);

        for (final String urlMitBeschreibung
                    : (Collection<String>)getListParam(
                        AddDokumentServerAction.ParameterType.DOKUMENT_URL.toString(),
                        String.class)) {
            final String[] urlMitBeschreibungArray = urlMitBeschreibung.split("\\n");
            final String url = urlMitBeschreibungArray[0];
            final String urlBeschreibung = urlMitBeschreibungArray[1];
            final CidsBean dmsUrl = createDmsURLFromLink(url, urlBeschreibung);
            veranlassungBean.getBeanCollectionProperty(VeranlassungPropertyConstants.PROP__AR_DOKUMENTE).add(dmsUrl);
        }

        DomainServerImpl.getServerInstance().insertMetaObject(getUser(), veranlassungBean.getMetaObject());

        if (arbeitsauftragBean != null) {
            arbeitsauftragBean.getBeanCollectionProperty(ArbeitsauftragPropertyConstants.PROP__N_VERANLASSUNGEN)
                    .add(veranlassungBean);
        }

        if (MetaObject.NEW == arbeitsauftragBean.getMetaObject().getStatus()) {
            DomainServerImpl.getServerInstance().insertMetaObject(getUser(), arbeitsauftragBean.getMetaObject());
        } else {
            DomainServerImpl.getServerInstance().updateMetaObject(getUser(), arbeitsauftragBean.getMetaObject());
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   search  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  SearchException  DOCUMENT ME!
     */
    private Collection executeSearch(final CidsServerSearch search) throws SearchException {
        search.setUser(getUser());
        search.setActiveLocalServers(getDummyLocalServers());
        return search.performServerSearch();
    }

    @Override
    public void setMetaService(final MetaService metaService) {
        super.setMetaService(metaService);
        getDummyLocalServers().put(BelisMetaClassConstants.DOMAIN, getMetaService());
    }

    @Override
    public String getTaskName() {
        return "AddIncident";
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map getDummyLocalServers() {
        return dummyLocalServers;
    }
}
