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
public class ProtokollStandortMasterneuerungServerActionV3 extends AbstractProtokollServerActionV3 {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        INBETRIEBNAHMEDATUM, MONTAGEFIRMA
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void executeAktion(final CidsBean protokoll) throws Exception {
        final CidsBean standort = (CidsBean)protokoll.getProperty("fk_standort");
        final Collection<CidsBean> aktionen = protokoll.getBeanCollectionProperty("n_aktionen");

        aktionen.add(createAktion(
                "Inbetriebnahme",
                standort,
                "inbetriebnahme_mast",
                getParam(ParameterType.INBETRIEBNAHMEDATUM.toString(), Timestamp.class)));
        aktionen.add(createAktion(
                "Montagefirma",
                standort,
                "montagefirma",
                getParam(ParameterType.MONTAGEFIRMA.toString(), String.class)));
        aktionen.add(createAktion(
                "Standsicherheitsprüfung",
                standort,
                "standsicherheitspruefung",
                null));
        aktionen.add(createAktion("Verfahren", standort, "verfahren", null));
        aktionen.add(createAktion(
                "Nächstes Prüfdatum",
                standort,
                "naechstes_pruefdatum",
                null));
        // Statusupdates
        setStatus(protokoll);
    }

    @Override
    public String getTaskName() {
        return "protokollStandortMasterneuerung";
    }
}
