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

import static de.cismet.belis2.server.search.BelisSearchStatement.generateIdQuery;
import static de.cismet.belis2.server.search.BelisSearchStatement.generateLikeQuery;
import static de.cismet.belis2.server.search.BelisSearchStatement.generateVonBisQuery;
import static de.cismet.belis2.server.search.BelisSearchStatement.implodeArray;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VeranlassungSearchStatement extends BelisSearchStatement {

    //~ Instance fields --------------------------------------------------------

    private String datum_von;
    private String datum_bis;
    private Integer grund_id;
    private String angelegtVon;
    private String nummer;
    private String bemerkungen;
    private String bezeichnung;
    private String beschreibung;
    private String infobaustein;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VeranlassungSearchStatement object.
     */
    public VeranlassungSearchStatement() {
        setVeranlassungEnabled(true);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected String getAndQueryPart() {
        final Collection<String> parts = new ArrayList<String>();

        parts.add(generateVonBisQuery("veranlassung.datum", datum_von, datum_bis));
        parts.add(generateIdQuery("veranlassung.fk_art", grund_id));
        parts.add(generateLikeQuery("veranlassung.username", angelegtVon));
        parts.add(generateLikeQuery("veranlassung.nummer", nummer));
        parts.add(generateLikeQuery("veranlassung.beschreibung", beschreibung));
        parts.add(generateLikeQuery("veranlassung.bezeichnung", bezeichnung));
        parts.add(generateLikeQuery("veranlassung.bemerkungen", bemerkungen));
        if (infobaustein != null) {
            parts.add("(SELECT true FROM jt_veranlassung_infobaustein, infobaustein "
                        + "WHERE jt_veranlassung_infobaustein.fk_infobaustein = infobaustein.id "
                        + "AND veranlassung_reference = veranlassung.id "
                        + "AND (infobaustein.schluessel ilike '" + infobaustein + "' OR infobaustein.wert ilike '"
                        + infobaustein + "') "
                        + "LIMIT 1)");
        }

        return implodeArray(parts.toArray(new String[0]), " AND ");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  datum_von  DOCUMENT ME!
     * @param  datum_bis  DOCUMENT ME!
     */
    public void setDatum(final String datum_von, final String datum_bis) {
        this.datum_von = datum_von;
        this.datum_bis = datum_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  grund_id  DOCUMENT ME!
     */
    public void setGrund_id(final Integer grund_id) {
        this.grund_id = grund_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  nummer  DOCUMENT ME!
     */
    public void setNummer(final String nummer) {
        this.nummer = nummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bemerkungen  DOCUMENT ME!
     */
    public void setBemerkungen(final String bemerkungen) {
        this.bemerkungen = bemerkungen;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bezeichnung  DOCUMENT ME!
     */
    public void setBezeichnung(final String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  beschreibung  DOCUMENT ME!
     */
    public void setBeschreibung(final String beschreibung) {
        this.beschreibung = beschreibung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  infobaustein  DOCUMENT ME!
     */
    public void setInfobaustein(final String infobaustein) {
        this.infobaustein = infobaustein;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  angelegtVon  DOCUMENT ME!
     */
    public void setAngelegtVon(final String angelegtVon) {
        this.angelegtVon = angelegtVon;
    }
}
