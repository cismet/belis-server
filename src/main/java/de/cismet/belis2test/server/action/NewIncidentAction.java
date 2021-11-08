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
package de.cismet.belis2test.server.action;

import Sirius.server.middleware.impls.domainserver.DomainServerImpl;
import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import lombok.Getter;

import java.rmi.Remote;

import java.sql.Date;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cismet.belis.commons.constants.ArbeitsauftragPropertyConstants;
import de.cismet.belis.commons.constants.ArbeitsprotokollPropertyConstants;
import de.cismet.belis.commons.constants.BelisMetaClassConstants;
import de.cismet.belis.commons.constants.VeranlassungPropertyConstants;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerAction;
import de.cismet.cids.server.search.CidsServerSearch;
import de.cismet.cids.server.search.SearchException;

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

    public static final String TASKNAME = "addIncident";

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        OBJEKT_ID, OBJEKT_TYP, DOKUMENT_URLS, BEZEICHNUNG, BESCHREIBUNG, BEMERKUNG, AKTION, ARBEITSAUFTRAG,
        ARBEITSAUFTRAG_ZUGEWIESEN_AN, IMAGES
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

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ExceptionType {

        //~ Enum constants -----------------------------------------------------

        ERROR, WARN
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
        final Integer arbeitsauftragZugewiesenAn = (Integer)getParam(ParameterType.ARBEITSAUFTRAG_ZUGEWIESEN_AN
                        .toString(),
                Integer.class);

        final ArrayList images = (ArrayList)getListParam(ParameterType.IMAGES.toString(),
                ArrayList.class);
        final List<String> urls = new ArrayList<String>();

        for (final Object image : images) {
            final ImageData data = new ImageData((Map)image);
            final String url = UploadDocumentServerAction.writeImage(
                    data.getPrefix(),
                    data.getTs(),
                    data.getDescription(),
                    data.getEnding(),
                    data.getImageData(),
                    goId,
                    goClassName);

            urls.add(url);
        }

        final MetaClass goMetaClass;
        try {
            goMetaClass = CidsBean.getMetaClassFromTableName(DOMAIN, goClassName);
        } catch (final Exception ex) {
            throw logAndNewException("could not get Metaclass with tableName=" + goClassName, ex, ExceptionType.ERROR);
        }
        if (goMetaClass == null) {
            throw logAndNewException("metaclass " + goClassName + " not found", ExceptionType.WARN);
        }

        final Date now = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        final CidsBean arbeitsauftragBean;
        if (Aktion.EINZELAUFTRAG.toString().equals(aktion)) {
            try {
                arbeitsauftragBean = createArbeitsauftragBean(arbeitsauftragZugewiesenAn, now);
            } catch (final Exception ex) {
                throw logAndNewException("could not create Arbeitsauftrag", ex, ExceptionType.WARN);
            }
        } else if (Aktion.ADD2ARBEITSAUFTRAG.toString().equals(aktion)) {
            final int arbeitsauftragId = (Integer)getParam(
                    ParameterType.ARBEITSAUFTRAG.toString(),
                    Integer.class);
            try {
                arbeitsauftragBean = searchArbeitsauftragBean(arbeitsauftragId);
            } catch (final Exception ex) {
                throw logAndNewException("search Arbeitsauftrag failed", ex, ExceptionType.WARN);
            }
            if (arbeitsauftragBean == null) {
                throw logAndNewException("could not find Arbeitsauftrag with id=" + arbeitsauftragId,
                    ExceptionType.WARN);
            }
        } else if (Aktion.VERANLASSUNG.toString().equals(aktion)) {
            arbeitsauftragBean = null;
        } else {
            throw logAndNewException("unknow Aktion-Type: " + aktion, ExceptionType.WARN);
        }

        final CidsBean goBean;
        try {
            goBean = DomainServerImpl.getServerInstance().getMetaObject(getUser(), goId, goMetaClass.getID()).getBean();
        } catch (final Exception ex) {
            throw logAndNewException("could not find " + goClassName + " with id=" + goId, ex, ExceptionType.WARN);
        }

        final CidsBean veranlassungBean = createVeranlassungBean(
                goBean,
                goClassName,
                bezeichnung,
                beschreibung,
                bemerkung,
                now,
                urls);

        try {
            DomainServerImpl.getServerInstance().insertMetaObject(getUser(), veranlassungBean.getMetaObject());
        } catch (final Exception ex) {
            throw logAndNewException("could not insert new Veranlassung", ex, ExceptionType.ERROR);
        }

        if (arbeitsauftragBean != null) {
            final Collection<CidsBean> arbeitsprotokolle = arbeitsauftragBean.getBeanCollectionProperty(
                    ArbeitsauftragPropertyConstants.PROP__AR_PROTOKOLLE);
            final CidsBean arbeitsauftragProtokollBean;
            try {
                arbeitsauftragProtokollBean = createArbeitsprotokollBean(
                        goBean,
                        goClassName,
                        arbeitsprotokolle.size()
                                + 1,
                        (String)veranlassungBean.getProperty(VeranlassungPropertyConstants.PROP__NUMMER));
            } catch (final Exception ex) {
                throw logAndNewException("could not create arbeitsprotokoll", ex, ExceptionType.ERROR);
            }
            arbeitsprotokolle.add(arbeitsauftragProtokollBean);

            if (MetaObject.NEW == arbeitsauftragBean.getMetaObject().getStatus()) {
                try {
                    DomainServerImpl.getServerInstance()
                            .insertMetaObject(getUser(), arbeitsauftragBean.getMetaObject());
                } catch (final Exception ex) {
                    throw logAndNewException("could not insert new Arbeitsauftrag", ex, ExceptionType.ERROR);
                }
            } else {
                try {
                    DomainServerImpl.getServerInstance()
                            .updateMetaObject(getUser(), arbeitsauftragBean.getMetaObject());
                } catch (final Exception ex) {
                    throw logAndNewException("could not update Veranlassung", ex, ExceptionType.ERROR);
                }
            }
        }

        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   message  DOCUMENT ME!
     * @param   type     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Exception logAndNewException(final String message, final ExceptionType type) {
        return logAndNewException(message, null, type);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   message  DOCUMENT ME!
     * @param   ex       DOCUMENT ME!
     * @param   type     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Exception logAndNewException(final String message, final Exception ex, final ExceptionType type) {
        if (type != null) {
            switch (type) {
                case WARN: {
                    if (ex == null) {
                        LOG.warn(message);
                    } else {
                        LOG.warn(message, ex);
                    }
                }
                break;
                case ERROR: {
                    if (ex == null) {
                        LOG.error(message);
                    } else {
                        LOG.error(message, ex);
                    }
                }
            }
        }
        if (ex == null) {
            return new Exception(message);
        } else {
            return new Exception(message, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   arbeitsauftragId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private CidsBean searchArbeitsauftragBean(final int arbeitsauftragId) throws Exception {
        final MetaClass arbeitsauftragMetaClass = CidsBean.getMetaClassFromTableName(
                DOMAIN,
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
                        + arbeitsauftragId + " not found");
        }
        final CidsBean arbeitsauftragBean = arbeitsauftragMo.getBean();
        return arbeitsauftragBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   teamId  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private CidsBean getTeamBean(final int teamId) throws Exception {
        final MetaClass teamMetaClass = CidsBean.getMetaClassFromTableName(
                DOMAIN,
                BelisMetaClassConstants.MC_TEAM);
        if (teamMetaClass == null) {
            throw new Exception("metaclass for team not found");
        }
        final MetaObject teamMo = DomainServerImpl.getServerInstance()
                    .getMetaObject(
                        getUser(),
                        teamId,
                        teamMetaClass.getId());
        if (teamMo == null) {
            throw new Exception(BelisMetaClassConstants.MC_TEAM + " with id "
                        + teamId + " not found");
        }
        final CidsBean teamBean = teamMo.getBean();
        return teamBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   arbeitsauftragZugewiesenAn  DOCUMENT ME!
     * @param   now                         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private CidsBean createArbeitsauftragBean(final Integer arbeitsauftragZugewiesenAn, final java.sql.Date now)
            throws Exception {
        final CidsBean arbeitsauftragBean = CidsBean.createNewCidsBeanFromTableName(
                DOMAIN,
                BelisMetaClassConstants.MC_ARBEITSAUFTRAG);

        final String arbeitsauftragNummer = NextArbeitsauftragNummerSearch.getStringRepresentation((List<Long>)
                executeSearch(new NextArbeitsauftragNummerSearch()));

        arbeitsauftragBean.setProperty(ArbeitsauftragPropertyConstants.PROP__NUMMER, arbeitsauftragNummer);
        arbeitsauftragBean.setProperty(ArbeitsauftragPropertyConstants.PROP__ANGELEGT_AM, now);
        arbeitsauftragBean.setProperty(ArbeitsauftragPropertyConstants.PROP__ANGELEGT_VON, getUser().getName());

        if (arbeitsauftragZugewiesenAn != null) {
            arbeitsauftragBean.setProperty(
                ArbeitsauftragPropertyConstants.PROP__ZUGEWIESEN_AN,
                getTeamBean(arbeitsauftragZugewiesenAn));
        }
        return arbeitsauftragBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   goBean        DOCUMENT ME!
     * @param   goClassName   DOCUMENT ME!
     * @param   bezeichnung   DOCUMENT ME!
     * @param   beschreibung  DOCUMENT ME!
     * @param   bemerkung     DOCUMENT ME!
     * @param   now           DOCUMENT ME!
     * @param   urls          DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private CidsBean createVeranlassungBean(final CidsBean goBean,
            final String goClassName,
            final String bezeichnung,
            final String beschreibung,
            final String bemerkung,
            final java.sql.Date now,
            final Collection<String> urls) throws Exception {
        final CidsBean veranlassungBean = CidsBean.createNewCidsBeanFromTableName(
                DOMAIN,
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
        veranlassungBean.setProperty(VeranlassungPropertyConstants.PROP__DATUM, now);
        veranlassungBean.setProperty(VeranlassungPropertyConstants.PROP__USERNAME, getUser().getName());
        veranlassungBean.setProperty(VeranlassungPropertyConstants.PROP__NUMMER, veranlassungNummer);
        veranlassungBean.setProperty(VeranlassungPropertyConstants.PROP__FK_ART, veranlassungsArtBean);

        if (urls != null) {
            for (final String urlMitBeschreibung : urls) {
                final String[] urlMitBeschreibungArray = urlMitBeschreibung.split("\\n");
                final String url = urlMitBeschreibungArray[0];
                final String urlBeschreibung = urlMitBeschreibungArray[1];
                final CidsBean dmsUrl = createDmsURLFromLink(url, urlBeschreibung);
                veranlassungBean.getBeanCollectionProperty(VeranlassungPropertyConstants.PROP__AR_DOKUMENTE)
                        .add(dmsUrl);
            }
        }

        return veranlassungBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   goBean               DOCUMENT ME!
     * @param   goClassName          DOCUMENT ME!
     * @param   protokollnummer      DOCUMENT ME!
     * @param   veranlassungsnummer  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private CidsBean createArbeitsprotokollBean(final CidsBean goBean,
            final String goClassName,
            final int protokollnummer,
            final String veranlassungsnummer) throws Exception {
        final CidsBean arbeitsauftragProtokoll = CidsBean.createNewCidsBeanFromTableName(
                DOMAIN,
                BelisMetaClassConstants.MC_ARBEITSPROTOKOLL);

        final String arbeitsauftragProtokollCollectionProperty;
        if (BelisMetaClassConstants.MC_ABZWEIGDOSE.equalsIgnoreCase(goClassName)) {
            arbeitsauftragProtokollCollectionProperty = ArbeitsprotokollPropertyConstants.PROP__FK_ABZWEIGDOSE;
        } else if (BelisMetaClassConstants.MC_LEITUNG.equalsIgnoreCase(goClassName)) {
            arbeitsauftragProtokollCollectionProperty = ArbeitsprotokollPropertyConstants.PROP__FK_LEITUNG;
        } else if (BelisMetaClassConstants.MC_TDTA_LEUCHTEN.equalsIgnoreCase(goClassName)) {
            arbeitsauftragProtokollCollectionProperty = ArbeitsprotokollPropertyConstants.PROP__FK_LEUCHTE;
        } else if (BelisMetaClassConstants.MC_MAUERLASCHE.equalsIgnoreCase(goClassName)) {
            arbeitsauftragProtokollCollectionProperty = ArbeitsprotokollPropertyConstants.PROP__FK_MAUERLASCHE;
        } else if (BelisMetaClassConstants.MC_SCHALTSTELLE.equalsIgnoreCase(goClassName)) {
            arbeitsauftragProtokollCollectionProperty = ArbeitsprotokollPropertyConstants.PROP__FK_SCHALTSTELLE;
        } else if (BelisMetaClassConstants.MC_TDTA_STANDORT_MAST.equalsIgnoreCase(goClassName)) {
            arbeitsauftragProtokollCollectionProperty = ArbeitsprotokollPropertyConstants.PROP__FK_STANDORT;
        } else {
            throw new Exception("could not determine collectionProperty for " + goClassName);
        }
        arbeitsauftragProtokoll.setProperty(arbeitsauftragProtokollCollectionProperty, goBean);
        arbeitsauftragProtokoll.setProperty(ArbeitsprotokollPropertyConstants.PROP__PROTOKOLLNUMMER, protokollnummer);
        arbeitsauftragProtokoll.setProperty(
            ArbeitsprotokollPropertyConstants.PROP__VERANLASSUNGSNUMMER,
            veranlassungsnummer);

        return arbeitsauftragProtokoll;
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
        getDummyLocalServers().put(DOMAIN, getMetaService());
    }

    @Override
    public String getTaskName() {
        return TASKNAME;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Map getDummyLocalServers() {
        return dummyLocalServers;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    private static class ImageData {

        //~ Instance fields ----------------------------------------------------

        String imageData;
        String ending;
        String description;
        long ts;
        String prefix;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ImageData object.
         *
         * @param  data  DOCUMENT ME!
         */
        public ImageData(final Map data) {
            imageData = getParam(data, "imagedata");
            ending = getParam(data, "ending");
            description = getParam(data, "description");
            prefix = getParam(data, "prefix");
            ts = Long.parseLong(getParam(data, "ts"));
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @param   data  DOCUMENT ME!
         * @param   name  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        private String getParam(final Map data, final String name) {
            for (final Object key : data.keySet()) {
                if (key instanceof String) {
                    final String keyString = (String)key;

                    if (keyString.equalsIgnoreCase(name.toLowerCase())) {
                        return (String)data.get(key);
                    }
                }
            }

            return null;
        }
    }
}
