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
package de.cismet.belis2.server.action.ProtokollAktion;

import Sirius.server.middleware.impls.domainserver.DomainServerImpl;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import java.sql.Date;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.cismet.belis2.server.action.AbstractBelisServerActionV3;
import de.cismet.belis2.server.utils.BelisServerUtils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractProtokollServerActionV3 extends AbstractBelisServerActionV3 {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AbstractProtokollServerActionV3.class);
    private static final ConnectionContext CC = ConnectionContext.create(
            AbstractConnectionContext.Category.ACTION,
            "ProtokollServerActionV3");

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        PROTOKOLL_ID;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Object processExecution() {
        final Integer protokollId = (Integer)getParam(ParameterType.PROTOKOLL_ID.toString(), Integer.class);

        if (protokollId != null) {
            try {
                final int classId = CidsBean.getMetaClassFromTableName(DOMAIN, "arbeitsprotokoll", CC).getId();
                final String entityKey = protokollId + "@" + classId;
                final Collection<String> toLock = new ArrayList<String>(Arrays.asList(entityKey));
                final MetaObject mo = DomainServerImpl.getServerInstance()
                            .getMetaObject(
                                getUser(),
                                protokollId,
                                classId,
                                CC);

                final CidsBean protokoll = mo.getBean();

                final Collection<CidsBean> children = new ArrayList<CidsBean>();
                children.add((CidsBean)protokoll.getProperty("fk_mauerlasche"));
                children.add((CidsBean)protokoll.getProperty("fk_leuchte"));
                children.add((CidsBean)protokoll.getProperty("fk_leitung"));
                children.add((CidsBean)protokoll.getProperty("fk_standort"));
                children.add((CidsBean)protokoll.getProperty("fk_abzweigdose"));
                children.add((CidsBean)protokoll.getProperty("fk_schaltstelle"));
                children.add((CidsBean)protokoll.getProperty("fk_geometrie"));

                for (final CidsBean child : children) {
                    if (child != null) {
                        toLock.add(child.getMetaObject().getID() + "@" + child.getMetaObject().getClassID());
                        break;
                    }
                }

                final Collection<MetaObjectNode> locks = BelisServerUtils.checkIfLocked(toLock, getUser());
                if (!locks.isEmpty()) {
                    return locks;
                } else {
                    // aquire lock
                    final MetaObjectNode lockNode = BelisServerUtils.lockEntities(toLock, getUser());
                    try {
                        // do action
                        executeAktion(protokoll);
                        DomainServerImpl.getServerInstance().updateMetaObject(getUser(), mo, CC);
                    } finally {
                        // release lock
                        DomainServerImpl.getServerInstance().deleteMetaObject(getUser(), lockNode.getObject(), CC);
                    }
                    return true;
                }
            } catch (Exception ex) {
                LOG.fatal(ex, ex);
                return ex;
            }
        } else {
            throw new RuntimeException("missing id as param");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   aenderung  DOCUMENT ME!
     * @param   newValue   DOCUMENT ME!
     * @param   oldValue   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static CidsBean createProtokollBean(final String aenderung, final String newValue, final String oldValue)
            throws Exception {
        final CidsBean arbeitsprotokollaktionBean = CidsBean.createNewCidsBeanFromTableName(
                DOMAIN,
                "arbeitsprotokollaktion",
                CC);
        arbeitsprotokollaktionBean.setProperty("aenderung", aenderung);
        arbeitsprotokollaktionBean.setProperty("alt", oldValue);
        arbeitsprotokollaktionBean.setProperty("neu", newValue);
        return arbeitsprotokollaktionBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   desc             DOCUMENT ME!
     * @param   workbenchEntity  DOCUMENT ME!
     * @param   property         DOCUMENT ME!
     * @param   newValue         DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected static CidsBean createAktion(final String desc,
            final CidsBean workbenchEntity,
            final String property,
            final Object newValue) throws Exception {
        final Object oldValue = workbenchEntity.getProperty(property);
        workbenchEntity.setProperty(property, newValue);

        return createProtokollBean(desc, valueToString(newValue), valueToString(oldValue));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   protokoll  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected void setStatus(final CidsBean protokoll) throws Exception {
        // the if statements are because of compatibility reasons (not needed for ios app versions > 0.9)

        if (paramsHashMap.get(ProtokollStatusServerAction.ParameterType.MONTEUR.toString().toLowerCase()) != null) {
            protokoll.setProperty(
                "monteur",
                (String)getParam(ProtokollStatusServerAction.ParameterType.MONTEUR.toString(), String.class));
        }
        if (paramsHashMap.get(ProtokollStatusServerAction.ParameterType.DATUM.toString().toLowerCase()) != null) {
            protokoll.setProperty(
                "datum",
                getParam(ProtokollStatusServerAction.ParameterType.DATUM.toString(), Date.class));
        }
        if (paramsHashMap.get(ProtokollStatusServerAction.ParameterType.STATUS.toString().toLowerCase()) != null) {
            protokoll.setProperty(
                "fk_status",
                getCidsBeanFromParam(
                    ProtokollStatusServerAction.ParameterType.STATUS.toString(),
                    "arbeitsprotokollstatus"));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   protokoll  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected abstract void executeAktion(final CidsBean protokoll) throws Exception;
}
