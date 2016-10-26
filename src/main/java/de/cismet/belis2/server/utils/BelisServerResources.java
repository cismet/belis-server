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

import lombok.Getter;

import de.cismet.cids.utils.serverresources.ServerResource;
import de.cismet.cids.utils.serverresources.TextServerResource;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public enum BelisServerResources {

    //~ Enum constants ---------------------------------------------------------

    MOTD_BELIS2_PROPERTIES(new TextServerResource("/motd/belis2.properties")),

    WEBDAV(new TextServerResource("/webdav/WebDav.properties"));

    //~ Instance fields --------------------------------------------------------

    @Getter private final ServerResource value;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Props object.
     *
     * @param  value  DOCUMENT ME!
     */
    BelisServerResources(final ServerResource value) {
        this.value = value;
    }
}
