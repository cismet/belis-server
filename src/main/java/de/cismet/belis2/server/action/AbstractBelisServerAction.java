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
import Sirius.server.middleware.interfaces.domainserver.MetaServiceStore;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.newuser.User;

import org.apache.commons.collections.MultiHashMap;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
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
public abstract class AbstractBelisServerAction implements UserAwareServerAction, MetaServiceStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            AbstractBelisServerAction.class);

    //~ Instance fields --------------------------------------------------------

    protected final MultiHashMap paramsHashMap = new MultiHashMap();
    private Object body;
    private User user;
    private MetaService metaService;

    //~ Methods ----------------------------------------------------------------

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(final User user) {
        this.user = user;
    }

    @Override
    public void setMetaService(final MetaService metaService) {
        this.metaService = metaService;
    }

    @Override
    public MetaService getMetaService() {
        return metaService;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Object getBody() {
        return this.body;
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
        final MetaClass metaClass = CidsBean.getMetaClassFromTableName("BELIS2", tableName.toLowerCase());

        final Integer objectId = (Integer)getParam(key, Integer.class);
        if (objectId == null) {
            return null;
        } else {
            return DomainServerImpl.getServerInstance().getMetaObject(getUser(), objectId, metaClass.getId()).getBean();
        }
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
        if (paramsHashMap.containsKey(key.toLowerCase())) {
            for (final String value : (List<String>)paramsHashMap.get(key.toLowerCase())) {
                final Object object;
                if (value == null) {
                    object = null;
                } else if (Date.class.equals(clazz)) {
                    final long timestamp = Long.parseLong(value);
                    object = new Date(timestamp);
                } else if (java.sql.Date.class.equals(clazz)) {
                    final long timestamp = Long.parseLong(value);
                    object = new java.sql.Date(timestamp);
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
        } else {
            return null;
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
        final Collection values = getListParam(key, clazz);
        if ((values == null) || values.isEmpty()) {
            return null;
        } else {
            return values.iterator().next();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key    DOCUMENT ME!
     * @param  value  DOCUMENT ME!
     */
    protected void addParam(final String key, final Object value) {
        paramsHashMap.put(key, value);
    }

    @Override
    public Object execute(final Object body, final ServerActionParameter... params) {
        this.body = body;
        paramsHashMap.clear();
        for (final ServerActionParameter param : params) {
            final String key = param.getKey().toLowerCase();
            final Object value = param.getValue();
            if ((value instanceof String) || (value == null)) {
                final String singleValue = (String)value;
                paramsHashMap.put(key, singleValue);
            } else if ((value instanceof Object[]) || (value instanceof Collection)) {
                final Collection collection;
                if (value instanceof Object[]) {
                    collection = Arrays.asList((Object[])value);
                } else if (value instanceof Collection) {
                    collection = (Collection)value;
                } else {
                    collection = null;
                }
                if (collection != null) {
                    for (final Object singleValue : collection) {
                        if (singleValue instanceof String) {
                            paramsHashMap.put(key, singleValue);
                        }
                    }
                }
            } else {
                final String message = "parameter value was neither a string or collection/array of strings";
                LOG.error(message);
                return new Exception(message);
            }
        }

        try {
            return processExecution();
        } catch (final Exception ex) {
            final String message = "error while processExecution()";
            LOG.error(message, ex);
            return ex;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  java.lang.Exception
     */
    protected abstract Object processExecution() throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @param   value  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected static String valueToString(final Object value) {
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
     * @param   inputArray  DOCUMENT ME!
     * @param   glueString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String implodeArray(final String[] inputArray, final String glueString) {
        String output = "";
        if (inputArray.length > 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append(inputArray[0]);
            for (int i = 1; i < inputArray.length; i++) {
                sb.append(glueString);
                sb.append(inputArray[i]);
            }
            output = sb.toString();
        }
        return output;
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
    public static CidsBean createDmsURLFromLink(final String link, final String description) throws Exception {
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

//    public Collection<CidsBean> checkIfLocked(final Collection<CidsBean> objectToCheck, final User user) {
//        final Collection<CidsBean> sperreBean = new ArrayList<CidsBean>();
//
//        final Collection<String> whereList = new ArrayList<String>();
//        for (final CidsBean lockedObject : objectToCheck) {
//            if (lockedObject != null) {
//                if (lockedObject.getMetaObject().getStatus() == MetaObject.NEW) {
//                    if (LOG.isDebugEnabled()) {
//                        LOG.debug("Entity is not yet persisted. Therefore it is surely not locked");
//                    }
//                } else {
//                    final int classId = lockedObject.getMetaObject().getMetaClass().getID();
//                    final int objectId = lockedObject.getMetaObject().getID();
//                    whereList.add("(class_id = " + classId + " AND object_id = " + objectId + ")");
//                }
//            } else {
//                LOG.warn("Entity is null. could not check if its locked");
//            }
//        }
//
//        if (whereList.isEmpty()) {
//            return sperreBean;
//        }
//
//        final String whereSnippet = implodeArray(whereList.toArray(new String[0]), " OR ");
//        final MetaClass mcSperre = CidsBean.getMetaClassFromTableName("BELIS2", "sperre");
//        final MetaClass mcSperreEntity = CidsBean.getMetaClassFromTableName("BELIS2", "sperre_entity");
//        final String query = "SELECT DISTINCT " + mcSperre.getID() + ", " + mcSperre.getTableName() + "."
//                    + mcSperre.getPrimaryKey() + ", lock_timestamp" + " "
//                    + "FROM " + mcSperre.getTableName() + ", " + mcSperreEntity.getTableName() + " "
//                    + "WHERE sperre.id = fk_sperre AND " + whereSnippet + " "
//                    + "ORDER BY lock_timestamp;";
//        final MetaObject[] mos = DomainServerImpl.getServerInstance().getMetaObject(user, query);
//
//        if (mos != null) {
//            for (final MetaObject mo : mos) {
//                final CidsBean lock = mo.getBean();
//                if (LOG.isDebugEnabled()) {
//                    LOG.debug("A lock for the desired object is already existing and is hold by: "
//                                + lock.getUserString());
//                }
//                sperreBean.add(lock);
//            }
//        }
//        return sperreBean;
//    }
}
