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

import de.cismet.belis2.server.action.AbstractBelisServerAction;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class AbstractProtokollServerAction extends AbstractBelisServerAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AbstractProtokollServerAction.class);

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
                final MetaObject mo = DomainServerImpl.getServerInstance()
                            .getMetaObject(
                                getUser(),
                                protokollId,
                                CidsBean.getMetaClassFromTableName("BELIS2", "arbeitsprotokoll").getId());
                executeAktion(mo.getBean());
                return DomainServerImpl.getServerInstance().updateMetaObject(getUser(), mo);
            } catch (Exception ex) {
                LOG.fatal(ex, ex);
                throw new RuntimeException(ex);
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
                "BELIS2",
                "arbeitsprotokollaktion");
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
    protected abstract void executeAktion(final CidsBean protokoll) throws Exception;
}
