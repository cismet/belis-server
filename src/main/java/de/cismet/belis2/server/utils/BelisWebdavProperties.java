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

import java.util.Properties;

import de.cismet.cids.utils.serverresources.ServerResourcesLoader;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BelisWebdavProperties extends Properties {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BelisWebdavProperties object.
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private BelisWebdavProperties() throws Exception {
        super(ServerResourcesLoader.getInstance().loadProperties(BelisServerResources.WEBDAV.getValue()));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getUrl() {
        return getProperty("url");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getUsername() {
        return getProperty("username");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getPassword() {
        return getProperty("password");
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static BelisWebdavProperties load() throws Exception {
        return new BelisWebdavProperties();
    }
}
