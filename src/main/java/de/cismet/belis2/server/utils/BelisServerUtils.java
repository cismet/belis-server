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

import Sirius.server.middleware.impls.domainserver.DomainServerImpl;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;
import Sirius.server.newuser.User;

import java.sql.Date;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.belis.commons.constants.BelisMetaClassConstants;

import de.cismet.belis2.server.search.LockedEntitySearch;

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
     * @param   objectsToLock  DOCUMENT ME!
     * @param   user           DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     * @throws  LockAlreadyExistsException    DOCUMENT ME!
     */
    public static MetaObjectNode lockEntities(final Collection<String> objectsToLock, final User user)
            throws ActionNotSuccessfulException, LockAlreadyExistsException {
        try {
            if (objectsToLock != null) {
                final Collection<MetaObjectNode> locks = checkIfLocked(objectsToLock, user);
                if ((locks != null) && !locks.isEmpty()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("A lock for the desired object is already existing");
                    }
                    // ToDo internationalise
                    throw new LockAlreadyExistsException(
                        "A lock for the desired object is already existing",
                        (Collection)locks);
                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("There is no Lock for the object");
                    }

                    final CidsBean newLock = CidsBean.createNewCidsBeanFromTableName("BELIS2", "sperre");
                    newLock.setProperty("lock_timestamp", new Date(new java.util.Date().getTime()));
                    newLock.setProperty(
                        "user_string",
                        user.getName()
                                + ((user.getUserGroup() != null) ? ("@" + user.getUserGroup()) : ""));
                    for (final String objectToLock : objectsToLock) {
                        final String[] splitted = objectToLock.split("@");
                        final Integer objectId = Integer.parseInt(splitted[0]);
                        final Integer classdId = Integer.parseInt(splitted[1]);
                        final CidsBean newLockEntity = CidsBean.createNewCidsBeanFromTableName(
                                "BELIS2",
                                "sperre_entity");
                        newLockEntity.setProperty("class_id", classdId);
                        newLockEntity.setProperty("object_id", objectId);
                        newLock.getBeanCollectionProperty("n_sperre_entities").add(newLockEntity);
                    }

                    final MetaObject persisted = DomainServerImpl.getServerInstance()
                                .insertMetaObject(user, newLock.getMetaObject());

                    final MetaObjectNode mon = new MetaObjectNode(
                            "BELIS2",
                            persisted.getID(),
                            persisted.getClassID(),
                            "",null,null);// TODO: Check4CashedGeomAndLightweightJson
                    mon.setObject(persisted);
                    return mon;
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("The objectcollection to lock is null");
                }
                throw new ActionNotSuccessfulException("The objectcollection to lock is null");
            }
        } catch (ActionNotSuccessfulException e) {
            throw e;
        } catch (LockAlreadyExistsException e) {
            throw e;
        } catch (final Exception ex) {
            LOG.error("Exception while creating lock", ex);
            throw new ActionNotSuccessfulException("Exception while creating lock", ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   objectToCheck  lockedObjects DOCUMENT ME!
     * @param   user           DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Collection<MetaObjectNode> checkIfLocked(final Collection<String> objectToCheck, final User user)
            throws Exception {
        final LockedEntitySearch search = new LockedEntitySearch(objectToCheck);
        search.setUser(user);

        return (Collection<MetaObjectNode>)search.performServerSearch();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   holdedLock  DOCUMENT ME!
     * @param   user        DOCUMENT ME!
     *
     * @throws  ActionNotSuccessfulException  DOCUMENT ME!
     */
    public void unlock(final CidsBean holdedLock, final User user) throws ActionNotSuccessfulException {
        try {
            if (holdedLock != null) {
                DomainServerImpl.getServerInstance().deleteMetaObject(user, holdedLock.getMetaObject());
            }
        } catch (final Exception ex) {
            LOG.error("Failure while releasing lock", ex);
            throw new ActionNotSuccessfulException("Failure while releasing lock", ex);
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
}
