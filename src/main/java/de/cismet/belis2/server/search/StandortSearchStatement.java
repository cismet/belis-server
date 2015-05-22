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

import de.cismet.belis2.server.utils.BelisServerUtils;

import de.cismet.cids.server.search.CidsServerSearch;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsServerSearch.class)
public class StandortSearchStatement extends BelisSearchStatement {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(StandortSearchStatement.class);

    //~ Instance fields --------------------------------------------------------

    private String inbetriebnahme_mast_von;
    private String inbetriebnahme_mast_bis;
    private String mastschutz_von;
    private String mastschutz_bis;
    private String mastanstrich_von;
    private String mastanstrich_bis;
    private String elek_pruefung_von;
    private String elek_pruefung_bis;
    private String standsicherheitspruefung_von;
    private String standsicherheitspruefung_bis;
    private Integer mastart_id;
    private Integer masttyp_id;
    private Integer klassifizierung_id;
    private Integer anlagengruppe_id;
    private Integer unterhaltspflicht_mast_id;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MastSearchStatement object.
     */
    public StandortSearchStatement() {
        setStandortEnabled(true);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  mastschutz_von  DOCUMENT ME!
     * @param  mastschutz_bis  DOCUMENT ME!
     */
    public void setMastschutz(final String mastschutz_von, final String mastschutz_bis) {
        this.mastschutz_von = mastschutz_von;
        this.mastschutz_bis = mastschutz_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mastanstrich_von  DOCUMENT ME!
     * @param  mastanstrich_bis  DOCUMENT ME!
     */
    public void setMastanstrich(final String mastanstrich_von, final String mastanstrich_bis) {
        this.mastanstrich_von = mastanstrich_von;
        this.mastanstrich_bis = mastanstrich_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  elek_pruefung_von  DOCUMENT ME!
     * @param  elek_pruefung_bis  DOCUMENT ME!
     */
    public void setElek_pruefung(final String elek_pruefung_von, final String elek_pruefung_bis) {
        this.elek_pruefung_von = elek_pruefung_von;
        this.elek_pruefung_bis = elek_pruefung_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  standsicherheitspruefung_von  DOCUMENT ME!
     * @param  standsicherheitspruefung_bis  DOCUMENT ME!
     */
    public void setStandsicherheitspruefung(final String standsicherheitspruefung_von,
            final String standsicherheitspruefung_bis) {
        this.standsicherheitspruefung_von = standsicherheitspruefung_von;
        this.standsicherheitspruefung_bis = standsicherheitspruefung_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mastart_id  DOCUMENT ME!
     */
    public void setMastart_id(final Integer mastart_id) {
        this.mastart_id = mastart_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  masttyp_id  DOCUMENT ME!
     */
    public void setMasttyp_id(final Integer masttyp_id) {
        this.masttyp_id = masttyp_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  klassifizierung_id  DOCUMENT ME!
     */
    public void setKlassifizierung_id(final Integer klassifizierung_id) {
        this.klassifizierung_id = klassifizierung_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  anlagengruppe_id  DOCUMENT ME!
     */
    public void setAnlagengruppe_id(final Integer anlagengruppe_id) {
        this.anlagengruppe_id = anlagengruppe_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  inbetriebnahme_mast_von  DOCUMENT ME!
     * @param  inbetriebnahme_mast_bis  DOCUMENT ME!
     */
    public void setInbetriebnahme_mast(final String inbetriebnahme_mast_von,
            final String inbetriebnahme_mast_bis) {
        this.inbetriebnahme_mast_von = inbetriebnahme_mast_von;
        this.inbetriebnahme_mast_bis = inbetriebnahme_mast_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  unterhaltspflicht_mast_id  DOCUMENT ME!
     */
    public void setUnterhaltspflicht_mast_id(final Integer unterhaltspflicht_mast_id) {
        this.unterhaltspflicht_mast_id = unterhaltspflicht_mast_id;
    }

    @Override
    protected String getAndQueryPart() {
        final Collection<String> parts = new ArrayList<String>();

        parts.add(generateVonBisQuery("tdta_standort_mast.mastschutz", mastschutz_von, mastschutz_bis));
        parts.add(generateVonBisQuery("tdta_standort_mast.mastanstrich", mastanstrich_von, mastanstrich_bis));
        parts.add(generateVonBisQuery("tdta_standort_mast.elek_pruefung", elek_pruefung_von, elek_pruefung_bis));
        parts.add(generateVonBisQuery(
                "tdta_standort_mast.standsicherheitspruefung",
                standsicherheitspruefung_von,
                standsicherheitspruefung_bis));

        parts.add(generateIdQuery("tdta_standort_mast.fk_mastart", mastart_id));
        parts.add(generateIdQuery("tdta_standort_mast.fk_masttyp", masttyp_id));
        parts.add(generateIdQuery("tdta_standort_mast.fk_klassifizierung", klassifizierung_id));
        parts.add(generateIdQuery("tdta_standort_mast.anlagengruppe", anlagengruppe_id));
        parts.add(generateIdQuery("tdta_standort_mast.fk_unterhaltspflicht_mast", unterhaltspflicht_mast_id));
        parts.add(generateVonBisQuery(
                "tdta_standort_mast.inbetriebnahme_mast",
                inbetriebnahme_mast_von,
                inbetriebnahme_mast_bis));

        return BelisServerUtils.implodeArray(parts.toArray(new String[0]), " AND ");
    }
}
