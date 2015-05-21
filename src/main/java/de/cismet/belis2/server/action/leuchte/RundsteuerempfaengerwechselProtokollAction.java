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
package de.cismet.belis2.server.action.leuchte;

import java.sql.Timestamp;

import java.util.Collection;

import de.cismet.belis2.server.action.ProtokollAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerAction;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ServerAction.class)
public class RundsteuerempfaengerwechselProtokollAction extends ProtokollAction {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        EINBAUDATUM, RUNDSTEUEREMPFAENGER
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void executeAktion(final CidsBean protokoll) throws Exception {
        final CidsBean leuchte = (CidsBean)protokoll.getProperty("fk_leuchte");
        final Collection<CidsBean> aktionen = protokoll.getBeanCollectionProperty("n_aktionen");

        aktionen.add(createAktion(
                "Einbaudatum",
                leuchte,
                "einbaudatum",
                getParam(ParameterType.EINBAUDATUM.toString(), Timestamp.class)));
        aktionen.add(createAktion(
                "Rundsteuerempfänger",
                leuchte,
                "rundsteuerempfaenger",
                getCidsBeanFromParam(ParameterType.RUNDSTEUEREMPFAENGER.toString(), "rundsteuerempfaenger")));
    }

    @Override
    public String getTaskName() {
        return getClass().getSimpleName();
    }
}
