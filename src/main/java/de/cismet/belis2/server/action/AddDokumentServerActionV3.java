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
import de.cismet.cids.server.connectioncontext.ConnectionContextBackend;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ServerAction.class)
public class AddDokumentServerActionV3 extends AbstractBelisServerActionV3 {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AddDokumentServerActionV3.class);
    private static final ConnectionContext CC = ConnectionContext.create(
            AbstractConnectionContext.Category.ACTION,
            "AddDokumentServerAction");

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        OBJEKT_ID, OBJEKT_TYP, DOKUMENT_URL
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Object processExecution() throws Exception {
        final int objectId = (Integer)getParam(ParameterType.OBJEKT_ID.toString(), Integer.class);
        final String className = (String)getParam(ParameterType.OBJEKT_TYP.toString(), String.class);
        final MetaClass metaClass = CidsBean.getMetaClassFromTableName(DOMAIN, className.toLowerCase(), CC);
        if (metaClass == null) {
            throw new Exception("metaclass " + className + " not found");
        }
        final int classId = metaClass.getID();

        final CidsBean bean = DomainServerImpl.getServerInstance()
                    .getMetaObject(getUser(), objectId, classId, CC)
                    .getBean();

        if (bean == null) {
            throw new Exception(className + " with id " + objectId + " not found");
        }

        final String tableName = metaClass.getTableName().toLowerCase();
        final String dokumenteProperty;

        if ("leitung".equals(tableName)) {
            dokumenteProperty = "dokumente";
        } else if ("tdta_leuchten".equals(tableName)) {
            dokumenteProperty = "dokumente";
        } else if ("tdta_standort_mast".equals(tableName)) {
            dokumenteProperty = "dokumente";
        } else if ("abzweigdose".equals(tableName)) {
            dokumenteProperty = "dokumente";
        } else if ("mauerlasche".equals(tableName)) {
            dokumenteProperty = "dokumente";
        } else if ("schaltstelle".equals(tableName)) {
            dokumenteProperty = "dokumente";
        } else if ("geometrie".equals(tableName)) {
            dokumenteProperty = "dokumente";
        } else {
            throw new Exception("dokument upload not supported for " + className);
        }

        final Collection<CidsBean> dokumente = bean.getBeanCollectionProperty(dokumenteProperty);
        for (final String urlMitBeschreibung
                    : (Collection<String>)getListParam(ParameterType.DOKUMENT_URL.toString(), String.class)) {
            final String[] urlMitBeschreibungArray = urlMitBeschreibung.split("\\n");
            final String url = urlMitBeschreibungArray[0];
            final String beschreibung = urlMitBeschreibungArray[1];
            final CidsBean dmsUrl = createDmsURLFromLink(url, beschreibung);
            dokumente.add(dmsUrl);
        }
        DomainServerImpl.getServerInstance().updateMetaObject(getUser(), bean.getMetaObject(), CC);

        return true;
    }

    @Override
    public String getTaskName() {
        return "addDocument";
    }
}
