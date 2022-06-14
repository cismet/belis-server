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

import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerAction;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ServerAction.class)
public class ProtokollFortfuehrungsantragServerActionV3 extends AbstractProtokollServerActionV3 {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        BEMERKUNG
    }

    //~ Methods ----------------------------------------------------------------

    // curl -u WendlingM@BELIS2:buggalo -F "taskparams={\"parameters\":{\"protokollId\":\"537\", \"bemerkung\":\"Dies
    // ist ein Test-Text\"}};type=application/json"
    // http://localhost:8890/actions/BELIS2.ProtokollFortfuehrungsantrag/tasks?role=all

    @Override
    public String getTaskName() {
        return "protokollFortfuehrungsantrag";
    }

    @Override
    protected void executeAktion(final CidsBean protokoll) throws Exception {
        final Collection<CidsBean> aktionen = protokoll.getBeanCollectionProperty("n_aktionen");
        aktionen.add(createProtokollBean(
                "Sonstiges",
                (String)getParam(ParameterType.BEMERKUNG.toString(), String.class),
                null));
        // Statusupdates
        setStatus(protokoll);
    }
}
