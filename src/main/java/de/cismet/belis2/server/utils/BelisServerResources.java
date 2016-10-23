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

import de.cismet.cids.utils.serverresources.ServerResourcesLoader;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public enum BelisServerResources {

    //~ Enum constants ---------------------------------------------------------

    MOTD_BELIS2_PROPERTIES("/motd/belis2.properties", ServerResourcesLoader.Type.TEXT),

    WEBDAV("/webdav/WebDav.properties", ServerResourcesLoader.Type.TEXT);

    //~ Instance fields --------------------------------------------------------

    private final String value;
    private final ServerResourcesLoader.Type type;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Props object.
     *
     * @param  value  DOCUMENT ME!
     * @param  type   DOCUMENT ME!
     */
    BelisServerResources(final String value, final ServerResourcesLoader.Type type) {
        this.value = value;
        this.type = type;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getValue() {
        return value;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ServerResourcesLoader.Type getType() {
        return type;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public Object loadServerResources() throws Exception {
        final ServerResourcesLoader loader = ServerResourcesLoader.getInstance();
        switch (type) {
            case JASPER_REPORT: {
                return loader.loadJasperReportResource(value);
            }
            case TEXT: {
                return loader.loadTextResource(value);
            }
            case BINARY: {
                return loader.loadBinaryResource(value);
            }
            default: {
                throw new Exception("unknown serverResource type");
            }
        }
    }
}
