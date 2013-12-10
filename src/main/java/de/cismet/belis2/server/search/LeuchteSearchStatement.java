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

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

import static de.cismet.belis2.server.search.BelisSearchStatement.generateIdQuery;
import static de.cismet.belis2.server.search.BelisSearchStatement.generateVonBisQuery;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class LeuchteSearchStatement extends BelisSearchStatement {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(LeuchteSearchStatement.class);

    //~ Instance fields --------------------------------------------------------

    private String inbetriebnahme_leuchte_von;
    private String inbetriebnahme_leuchte_bis;
    private String wechseldatum_von;
    private String wechseldatum_bis;
    private String naechster_wechsel_von;
    private String naechster_wechsel_bis;
    private Integer fk_leuchttyp_id;
    // private Integer zaehler;
    private String rundsteuerempfaenger;
    private String schaltstelle;
    private Integer anschlussleistung_1dk_id;
    private Integer anschlussleistung_2dk_id;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MastSearchStatement object.
     */
    public LeuchteSearchStatement() {
        super(
            false,
            true,
            false,
            false,
            false,
            false,
            false,
            false);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  inbetriebnahme_leuchte_von  DOCUMENT ME!
     * @param  inbetriebnahme_leuchte_bis  DOCUMENT ME!
     */
    public void setInbetriebnahme_leuchte(final String inbetriebnahme_leuchte_von,
            final String inbetriebnahme_leuchte_bis) {
        this.inbetriebnahme_leuchte_von = inbetriebnahme_leuchte_von;
        this.inbetriebnahme_leuchte_bis = inbetriebnahme_leuchte_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  wechseldatum_von  DOCUMENT ME!
     * @param  wechseldatum_bis  DOCUMENT ME!
     */
    public void setWechseldatum(final String wechseldatum_von, final String wechseldatum_bis) {
        this.wechseldatum_von = wechseldatum_von;
        this.wechseldatum_bis = wechseldatum_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  naechster_wechsel_von  DOCUMENT ME!
     * @param  naechster_wechsel_bis  DOCUMENT ME!
     */
    public void setNaechster_wechsel(final String naechster_wechsel_von, final String naechster_wechsel_bis) {
        this.naechster_wechsel_von = naechster_wechsel_von;
        this.naechster_wechsel_bis = naechster_wechsel_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fk_leuchttyp_id  DOCUMENT ME!
     */
    public void setFk_leuchttyp(final Integer fk_leuchttyp_id) {
        this.fk_leuchttyp_id = fk_leuchttyp_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  rundsteuerempfaenger  DOCUMENT ME!
     */
    public void setRundsteuerempfaenger(final String rundsteuerempfaenger) {
        this.rundsteuerempfaenger = rundsteuerempfaenger;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  schaltstelle  DOCUMENT ME!
     */
    public void setSchaltstelle(final String schaltstelle) {
        this.schaltstelle = schaltstelle;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  anschlussleistung_1dk_id  DOCUMENT ME!
     */
    public void setAnschlussleistung_1dk(final Integer anschlussleistung_1dk_id) {
        this.anschlussleistung_1dk_id = anschlussleistung_1dk_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  anschlussleistung_2dk_id  DOCUMENT ME!
     */
    public void setAnschlussleistung_2dk(final Integer anschlussleistung_2dk_id) {
        this.anschlussleistung_2dk_id = anschlussleistung_2dk_id;
    }

    @Override
    protected String getAndQueryPart() {
        final Collection<String> parts = new ArrayList<String>();

        parts.add(generateVonBisQuery(
                "tdta_leuchten.inbetriebnahme_leuchte",
                inbetriebnahme_leuchte_von,
                inbetriebnahme_leuchte_bis));
        parts.add(generateVonBisQuery("tdta_leuchten.wechseldatum", wechseldatum_von, wechseldatum_bis));
        parts.add(generateVonBisQuery("tdta_leuchten.naechster_wechsel", naechster_wechsel_von, naechster_wechsel_bis));

        parts.add(generateLikeQuery("tdta_leuchten.rundsteuerempfaenger", rundsteuerempfaenger));

        parts.add(generateIdQuery("tdta_leuchten.fk_leuchttyp", fk_leuchttyp_id));
        parts.add(generateLikeQuery("tdta_leuchten.schaltstelle", schaltstelle));
        parts.add(generateIdQuery("tdta_leuchten.anschlussleistung_1dk", anschlussleistung_1dk_id));
        parts.add(generateIdQuery("tdta_leuchten.anschlussleistung_2dk", anschlussleistung_2dk_id));

        return implodeArray(parts.toArray(new String[0]), " AND ");
    }
}
