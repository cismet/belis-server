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
package de.cismet.belis2.server.action.ProtokollAktion;

import java.sql.Date;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerAction;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ServerAction.class)
public class ProtokollStatusServerAction extends AbstractProtokollServerAction {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        MONTEUR, DATUM, STATUS, BEMERKUNG, MATERIAL
    }

    //~ Methods ----------------------------------------------------------------

    // curl -u WendlingM@BELIS2:buggalo -F "taskparams={\"parameters\":{\"protokollId\":\"537\", \"bemerkung\":\"Dies
    // ist ein Test-Text\"}};type=application/json"
    // http://localhost:8890/actions/BELIS2.ProtokollFortfuehrungsantrag/tasks?role=all

    @Override
    public String getTaskName() {
        return "ProtokollStatusAenderung";
    }

    @Override
    protected void executeAktion(final CidsBean protokoll) throws Exception {
        protokoll.setProperty("monteur", (String)getParam(ParameterType.MONTEUR.toString(), String.class));
        protokoll.setProperty("datum", getParam(ParameterType.DATUM.toString(), Date.class));
        protokoll.setProperty("fk_status", getCidsBeanFromParam(ParameterType.STATUS.toString(), "leuchtmittel"));
        protokoll.setProperty("bemerkung", (String)getParam(ParameterType.BEMERKUNG.toString(), String.class));
        protokoll.setProperty("material", (String)getParam(ParameterType.MATERIAL.toString(), String.class));
    }
}
