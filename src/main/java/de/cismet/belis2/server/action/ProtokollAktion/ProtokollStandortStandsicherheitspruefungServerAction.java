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

import java.sql.Timestamp;

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
public class ProtokollStandortStandsicherheitspruefungServerAction extends AbstractProtokollServerAction {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        PRUEFDATUM, VERFAHREN, NAECHSTES_PRUEFDATUM
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void executeAktion(final CidsBean protokoll) throws Exception {
        final CidsBean standort = (CidsBean)protokoll.getProperty("fk_standort");
        final Collection<CidsBean> aktionen = protokoll.getBeanCollectionProperty("n_aktionen");

        aktionen.add(createAktion(
                "Standsicherheitsprüfung",
                standort,
                "standsicherheitspruefung",
                getParam(ParameterType.PRUEFDATUM.toString(), Timestamp.class)));
        aktionen.add(createAktion(
                "Verfahren",
                standort,
                "verfahren",
                getParam(ParameterType.VERFAHREN.toString(), String.class)));
        aktionen.add(createAktion(
                "Nächstes Prüfdatum",
                standort,
                "naechstes_pruefdatum",
                getParam(ParameterType.NAECHSTES_PRUEFDATUM.toString(), Timestamp.class)));
        // Statusupdates
        setStatus(protokoll);
    }

    @Override
    public String getTaskName() {
        return "ProtokollStandortStandsicherheitspruefung";
    }
}
