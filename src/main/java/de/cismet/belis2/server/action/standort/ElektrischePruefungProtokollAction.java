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
package de.cismet.belis2.server.action.standort;

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
public class ElektrischePruefungProtokollAction extends ProtokollAction {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        PRUEFDATUM, ERDUNG_IN_ORDNUNG
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void executeAktion(final CidsBean protokoll) throws Exception {
        final CidsBean standort = (CidsBean)protokoll.getProperty("fk_standort");
        final Collection<CidsBean> aktionen = protokoll.getBeanCollectionProperty("n_aktionen");

        aktionen.add(createAktion(
                "Elektrische Prüfung",
                standort,
                "elek_pruefung",
                getParam(ParameterType.PRUEFDATUM.toString(), Timestamp.class)));
        aktionen.add(createAktion(
                "Erdung in Ordnung",
                standort,
                "erdung",
                getParam(
                    ParameterType.ERDUNG_IN_ORDNUNG.toString(),
                    Boolean.class)));
    }

    @Override
    public String getTaskName() {
        return getClass().getSimpleName();
    }
}
