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
package de.cismet.belis2.server.utils;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.tools.URLSplitter;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BelisServerUtils {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BelisServerUtils.class);

    //~ Methods ----------------------------------------------------------------

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
