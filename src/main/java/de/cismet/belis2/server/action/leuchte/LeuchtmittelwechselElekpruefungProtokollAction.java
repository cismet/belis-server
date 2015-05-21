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
import de.cismet.belis2.server.action.standort.ElektrischePruefungProtokollAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerAction;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ServerAction.class)
public class LeuchtmittelwechselElekpruefungProtokollAction extends ProtokollAction {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        WECHSELDATUM, LEBENSDAUER, LEUCHTMITTEL, PRUEFDATUM, ERDUNG_IN_ORDNUNG
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void executeAktion(final CidsBean protokoll) throws Exception {
        final CidsBean leuchte = (CidsBean)protokoll.getProperty("fk_leuchte");
        final CidsBean standort = (CidsBean)leuchte.getProperty("fk_standort");
        final Collection<CidsBean> aktionen = protokoll.getBeanCollectionProperty("n_aktionen");

        aktionen.add(createAktion(
                "Wechseldatum",
                leuchte,
                "wechseldatum",
                getParam(ParameterType.WECHSELDATUM.toString(), Timestamp.class)));
        aktionen.add(createAktion(
                "Lebensdauer",
                leuchte,
                "lebensdauer",
                getParam(ParameterType.LEBENSDAUER.toString(), Double.class)));
        aktionen.add(createAktion(
                "Leuchtmittel",
                leuchte,
                "leuchtmittel",
                getCidsBeanFromParam(ParameterType.LEUCHTMITTEL.toString(), "leuchtmittel")));
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
