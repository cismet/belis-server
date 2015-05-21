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
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import org.apache.commons.collections.MultiHashMap;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerActionParameter;
import de.cismet.cids.server.actions.UserAwareServerAction;

import de.cismet.tools.URLSplitter;

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

        PROTOKOLL_ID;
    }

    //~ Instance fields --------------------------------------------------------

    private final MultiHashMap paramsHashMap = new MultiHashMap();
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
     * @param   key        DOCUMENT ME!
     * @param   tableName  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    protected CidsBean getCidsBeanFromParam(final String key, final String tableName) throws Exception {
        final MetaClass metaClass = CidsBean.getMetaClassFromTableName("BELIS2", tableName);

        final int objectId = (Integer)getParam(key, Integer.class);
        return DomainServerImpl.getServerInstance().getMetaObject(getUser(), objectId, metaClass.getId()).getBean();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key    DOCUMENT ME!
     * @param   clazz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  UnsupportedOperationException  DOCUMENT ME!
     */
    protected Collection getListParam(final String key, final Class clazz) {
        final Collection objects = new ArrayList();
        for (final String value : (List<String>)paramsHashMap.get(key.toLowerCase())) {
            final Object object;
            if (Date.class.equals(clazz)) {
                final long timestamp = Long.parseLong(value);
                object = new Date(timestamp);
            } else if (Timestamp.class.equals(clazz)) {
                final long timestamp = Long.parseLong(value);
                object = new Timestamp(timestamp);
            } else if (Integer.class.equals(clazz)) {
                object = Integer.parseInt(value);
            } else if (Float.class.equals(clazz)) {
                object = Float.parseFloat(value);
            } else if (Long.class.equals(clazz)) {
                object = Long.parseLong(value);
            } else if (Double.class.equals(clazz)) {
                object = Double.parseDouble(value);
            } else if (Boolean.class.equals(clazz)) {
                if ("ja".equals(value.toLowerCase())) {
                    object = true;
                } else if ("nein".equals(value.toLowerCase())) {
                    object = false;
                } else {
                    throw new UnsupportedOperationException("wrong boolean value");
                }
            } else if (String.class.equals(clazz)) {
                object = value;
            } else {
                throw new UnsupportedOperationException("this class is not supported");
            }

            objects.add(object);
        }
        return objects;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key    DOCUMENT ME!
     * @param   clazz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Object getParam(final String key, final Class clazz) {
        return getListParam(key, clazz).iterator().next();
    }

    @Override
    public Object execute(final Object body, final ServerActionParameter... params) {
        paramsHashMap.clear();
        for (final ServerActionParameter param : params) {
            paramsHashMap.put(param.getKey().toLowerCase(), (String)param.getValue());
        }

        final Integer protokollId = (Integer)getParam(ParameterType.PROTOKOLL_ID.toString(), Integer.class);

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
     * @param   link         DOCUMENT ME!
     * @param   description  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception             DOCUMENT ME!
     * @throws  NullPointerException  DOCUMENT ME!
     */
    public CidsBean createDmsURLFromLink(final String link, final String description) throws Exception {
        if ((link == null) || (description == null)) {
            throw new NullPointerException();
        }

        final CidsBean dmsUrlBean = CidsBean.createNewCidsBeanFromTableName("BELIS2", "dms_url");
        final CidsBean urlBean = CidsBean.createNewCidsBeanFromTableName("BELIS2", "url");
        final CidsBean urlBaseBean = CidsBean.createNewCidsBeanFromTableName("BELIS2", "url_base");
        final URLSplitter splitter = new URLSplitter(link);
        dmsUrlBean.setProperty("description", description);
        urlBean.setProperty("url_base_id", urlBaseBean);
        dmsUrlBean.setProperty("url_id", urlBean);
        urlBaseBean.setProperty("path", splitter.getPath());
        urlBaseBean.setProperty("prot_prefix", splitter.getProt_prefix());
        urlBaseBean.setProperty("server", splitter.getServer());
        urlBean.setProperty("object_name", splitter.getObject_name());
        return dmsUrlBean;
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
}
