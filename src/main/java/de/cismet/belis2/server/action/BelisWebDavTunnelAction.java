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

import de.cismet.belis2.server.utils.BelisWebdavProperties;

import de.cismet.cids.server.actions.ServerAction;
import de.cismet.cids.server.actions.WebDavTunnelAction;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ServerAction.class)
public class BelisWebDavTunnelAction extends WebDavTunnelAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BelisWebDavTunnelAction.class);
    public static final String TASK_NAME = "BelisWebDavTunnelAction";

    //~ Instance fields --------------------------------------------------------

    private final BelisWebdavProperties properties;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BelisWebDavTunnelAction object.
     */
    public BelisWebDavTunnelAction() {
        BelisWebdavProperties properties = null;
        try {
            properties = BelisWebdavProperties.load();
        } catch (final Exception ex) {
            LOG.info("BelisWebDavTunnelAction could not load the properties", ex);
        }
        this.properties = properties;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected String getUsername() {
        return (properties != null) ? properties.getUsername() : null;
    }

    @Override
    protected String getPassword() {
        return (properties != null) ? properties.getPassword() : null;
    }

    @Override
    protected String getWebdavPath() {
        return (properties != null) ? properties.getUrl() : null;
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }
}
