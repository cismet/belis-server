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
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;
import de.cismet.cids.server.actions.UserAwareServerAction;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public abstract class ProtokollAction implements UserAwareServerAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProtokollAction.class);

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        PROTOKOLL_ID {

            @Override
            public String toString() {
                return "protokollId";
            }
        }
    }

    //~ Instance fields --------------------------------------------------------

    private final HashMap<String, Object> paramsHashMap = new HashMap<String, Object>();
    private User user;

    //~ Methods ----------------------------------------------------------------

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(final User user) {
        this.user = user;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Object getParam(final String key) {
        return paramsHashMap.get(key);
    }

    @Override
    public Object execute(final Object body, final ServerActionParameter... params) {
        Integer protokollId = null;

        paramsHashMap.clear();
        for (final ServerActionParameter param : params) {
            paramsHashMap.put(param.getKey(), param.getValue());
        }

        protokollId = Integer.parseInt((String)paramsHashMap.get(ParameterType.PROTOKOLL_ID.toString()));

        if (protokollId != null) {
            try {
                final MetaObject mo = DomainServerImpl.getServerInstance()
                            .getMetaObject(
                                getUser(),
                                protokollId,
                                CidsBean.getMetaClassFromTableName("BELIS2", "arbeitsprotokoll").getId());
                executeAktion(mo.getBean());
                return DomainServerImpl.getServerInstance().updateMetaObject(user, mo);
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

        final CidsBean arbeitsprotokollaktionBean = CidsBean.createNewCidsBeanFromTableName(
                "BELIS2",
                "arbeitsprotokollaktion");
        arbeitsprotokollaktionBean.setProperty("aenderung", desc);
        arbeitsprotokollaktionBean.setProperty("alt", valueToString(oldValue));
        arbeitsprotokollaktionBean.setProperty("neu", valueToString(newValue));
        return arbeitsprotokollaktionBean;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   value  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String valueToString(final Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Date) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            return dateFormat.format(value);
        } else if (value instanceof Boolean) {
            return (Boolean)value ? "Ja" : "Nein";
        } else {
            return value.toString();
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
