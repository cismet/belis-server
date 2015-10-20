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

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.cismet.belis2.server.utils.BelisServerUtils;

import de.cismet.cidsx.base.types.Type;

import de.cismet.cidsx.server.api.types.SearchParameterInfo;
import de.cismet.cidsx.server.search.RestApiCidsServerSearch;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = RestApiCidsServerSearch.class)
public class StandortSearchStatement extends BelisSearchStatement {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(StandortSearchStatement.class);

    //~ Instance fields --------------------------------------------------------

    @Getter @Setter private String inbetriebnahme_mast_von;
    @Getter @Setter private String inbetriebnahme_mast_bis;
    @Getter @Setter private String mastschutz_von;
    @Getter @Setter private String mastschutz_bis;
    @Getter @Setter private String mastanstrich_von;
    @Getter @Setter private String mastanstrich_bis;
    @Getter @Setter private String elek_pruefung_von;
    @Getter @Setter private String elek_pruefung_bis;
    @Getter @Setter private String standsicherheitspruefung_von;
    @Getter @Setter private String standsicherheitspruefung_bis;
    @Getter @Setter private Integer mastart_id;
    @Getter @Setter private Integer masttyp_id;
    @Getter @Setter private Integer klassifizierung_id;
    @Getter @Setter private Integer anlagengruppe_id;
    @Getter @Setter private Integer unterhaltspflicht_mast_id;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MastSearchStatement object.
     */
    public StandortSearchStatement() {
        setStandortEnabled(true);

        final List<SearchParameterInfo> parameterDescription = getSearchInfo().getParameterDescription();
        SearchParameterInfo searchParameterInfo;

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("inbetriebnahme_mast_von");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("inbetriebnahme_mast_bis");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("mastschutz_von");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("mastschutz_bis");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("mastanstrich_von");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("mastanstrich_bis");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("elek_pruefung_von");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("elek_pruefung_bis");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("standsicherheitspruefung_von");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("standsicherheitspruefung_bis");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("mastart_id");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("masttyp_id");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("klassifizierung_id");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("klassifizierung_id");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("anlagengruppe_id");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("unterhaltspflicht_mast_id");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);
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
     * @param  inbetriebnahme_mast_von  DOCUMENT ME!
     * @param  inbetriebnahme_mast_bis  DOCUMENT ME!
     */
    public void setInbetriebnahme_mast(final String inbetriebnahme_mast_von,
            final String inbetriebnahme_mast_bis) {
        this.inbetriebnahme_mast_von = inbetriebnahme_mast_von;
        this.inbetriebnahme_mast_bis = inbetriebnahme_mast_bis;
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
