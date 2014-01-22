/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.belis2.server.search;

import java.util.ArrayList;
import java.util.Collection;

import static de.cismet.belis2.server.search.BelisSearchStatement.generateLikeQuery;
import static de.cismet.belis2.server.search.BelisSearchStatement.generateVonBisQuery;
import static de.cismet.belis2.server.search.BelisSearchStatement.implodeArray;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class ArbeitsauftragSearchStatement extends BelisSearchStatement {

    //~ Instance fields --------------------------------------------------------

    private String angelegtAm_von;
    private String angelegtAm_bis;
    private String angelegtVon;
    private String zugewiesenAn;
    private String auftragsNummer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ArbeitsauftragSearchStatement object.
     */
    public ArbeitsauftragSearchStatement() {
        setArbeitsauftragEnabled(true);
        // this.erstellungsjahr = erstellungsjahr;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected String getAndQueryPart() {
        final Collection<String> parts = new ArrayList<String>();

        parts.add(generateVonBisQuery("arbeitsauftrag.angelegt_am", angelegtAm_von, angelegtAm_bis));
        parts.add(generateLikeQuery("arbeitsauftrag.angelegt_von", angelegtVon));
        parts.add(generateLikeQuery("arbeitsauftrag.zugewiesen_an", zugewiesenAn));
        parts.add(generateLikeQuery("arbeitsauftrag.nummer", auftragsNummer));
        return implodeArray(parts.toArray(new String[0]), " AND ");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  angelegtAm_von  DOCUMENT ME!
     * @param  angelegtAm_bis  DOCUMENT ME!
     */
    public void setAngelegtAm(final String angelegtAm_von, final String angelegtAm_bis) {
        this.angelegtAm_von = angelegtAm_von;
        this.angelegtAm_bis = angelegtAm_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  zugewiesenAn  DOCUMENT ME!
     */
    public void setZugewiesenAn(final String zugewiesenAn) {
        this.zugewiesenAn = zugewiesenAn;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  angelegtVon  DOCUMENT ME!
     */
    public void setAngelegtVon(final String angelegtVon) {
        this.angelegtVon = angelegtVon;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  auftragsNummer  DOCUMENT ME!
     */
    public void setAuftragsNummer(final String auftragsNummer) {
        this.auftragsNummer = auftragsNummer;
    }
}
