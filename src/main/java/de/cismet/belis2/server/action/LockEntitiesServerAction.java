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

import Sirius.server.newuser.User;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.belis2.server.utils.BelisServerUtils;

import de.cismet.cids.server.actions.ServerAction;
import de.cismet.cids.server.actions.ServerActionParameter;
import de.cismet.cids.server.actions.UserAwareServerAction;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ServerAction.class)
public class LockEntitiesServerAction implements UserAwareServerAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            LockEntitiesServerAction.class);

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        ENTITY_KEY
    }

    //~ Instance fields --------------------------------------------------------

    private final Collection<String> entityKeys = new ArrayList<String>();
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

    @Override
    public Object execute(final Object body, final ServerActionParameter... params) {
        entityKeys.clear();
        for (final ServerActionParameter param : params) {
            if (ParameterType.ENTITY_KEY.name().toLowerCase().equals(param.getKey().toLowerCase())) {
                entityKeys.add((String)param.getValue());
            }
        }

        try {
            return BelisServerUtils.lockEntities(entityKeys, user);
        } catch (final Exception ex) {
            return ex;
        }
    }

    @Override
    public String getTaskName() {
        return "LockEntities";
    }
}
