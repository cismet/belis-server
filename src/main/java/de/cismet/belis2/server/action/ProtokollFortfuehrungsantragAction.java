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
public class ProtokollFortfuehrungsantragAction extends ProtokollAction {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        BEMERKUNG {

            @Override
            public String toString() {
                return "bemerkung";
            }
        }
    }

    //~ Methods ----------------------------------------------------------------

    // curl -u WendlingM@BELIS2:buggalo -F "taskparams={\"parameters\":{\"protokollId\":\"537\", \"bemerkung\":\"Dies ist ein Test-Text\"}};type=application/json" http://localhost:8890/actions/BELIS2.ProtokollFortfuehrungsantrag/tasks?role=all

    @Override
    public String getTaskName() {
        return "ProtokollFortfuehrungsantrag";
    }

    @Override
    protected void executeAktion(final CidsBean protokoll) throws Exception {
        final CidsBean arbeitsprotokollaktionBean = CidsBean.createNewCidsBeanFromTableName(
                "BELIS2",
                "arbeitsprotokollaktion");
        arbeitsprotokollaktionBean.setProperty("aenderung", "Sonstiges");
        arbeitsprotokollaktionBean.setProperty("alt", null);
        arbeitsprotokollaktionBean.setProperty("neu", (String)getParam(ParameterType.BEMERKUNG.toString()));
        final Collection<CidsBean> aktionen = protokoll.getBeanCollectionProperty("n_aktionen");
        aktionen.add(arbeitsprotokollaktionBean);
    }
}
