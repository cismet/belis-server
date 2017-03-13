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
import Sirius.server.middleware.types.MetaClass;

import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerAction;

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

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AddDokumentServerAction.class);

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        OBJEKT_ID, OBJEKT_TYP, DOKUMENT_URLS, BEZEICHNUNG, BESCHREIBUNG, BEMERKUNG, AKTION, ARBEITSAUFTRAG
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum AKTION {

        //~ Enum constants -----------------------------------------------------

        VERANLASSUNG, EINZELAUFTRAG, ADD2ARBEITSAUFTRAG
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Object processExecution() throws Exception {
        final int goObjectId = (Integer)getParam(ParameterType.OBJEKT_ID.toString(), Integer.class);
        final String goClassName = (String)getParam(ParameterType.OBJEKT_TYP.toString(), String.class);
        final String bezeichnung = (String)getParam(ParameterType.BEZEICHNUNG.toString(), String.class);
        final String beschreibung = (String)getParam(ParameterType.BESCHREIBUNG.toString(), String.class);
        final String bemerkung = (String)getParam(ParameterType.BEMERKUNG.toString(), String.class);
        final String aktion = (String)getParam(ParameterType.AKTION.toString(), String.class);
        final int arbeitsauftrag = (Integer)getParam(ParameterType.OBJEKT_ID.toString(), Integer.class);

        final Collection<String> urls = (Collection<String>)getListParam(ParameterType.DOKUMENT_URLS.toString(),
                String.class);

        final MetaClass goMetaClass = CidsBean.getMetaClassFromTableName("BELIS2", goClassName.toLowerCase());
        if (goMetaClass == null) {
            throw new Exception("metaclass " + goClassName + " not found");
        }
        final int goClassId = goMetaClass.getID();

        final CidsBean goBean = DomainServerImpl.getServerInstance()
                    .getMetaObject(getUser(), goObjectId, goClassId)
                    .getBean();

//         for (final String urlMitBeschreibung
//                    : (Collection<String>)getListParam(AddDokumentServerAction.ParameterType.DOKUMENT_URL.toString(), String.class)) {
//            final String[] urlMitBeschreibungArray = urlMitBeschreibung.split("\\n");
//            final String url = urlMitBeschreibungArray[0];
//            final String beschreibung = urlMitBeschreibungArray[1];
//            final CidsBean dmsUrl = createDmsURLFromLink(url, beschreibung);
//            dokumente.add(dmsUrl);
//        }
//        DomainServerImpl.getServerInstance().updateMetaObject(getUser(), bean.getMetaObject());
        return null;
    }

    @Override
    public String getTaskName() {
        return "AddIncident";
    }
}
