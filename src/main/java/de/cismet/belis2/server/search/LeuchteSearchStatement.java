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
public class LeuchteSearchStatement extends BelisSearchStatement {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(LeuchteSearchStatement.class);

    //~ Instance fields --------------------------------------------------------

    @Getter
    @Setter
    private String inbetriebnahme_leuchte_von;
    @Getter
    @Setter
    private String inbetriebnahme_leuchte_bis;
    @Getter
    @Setter
    private String wechseldatum_von;
    @Getter
    @Setter
    private String wechseldatum_bis;
    @Getter
    @Setter
    private String naechster_wechsel_von;
    @Getter
    @Setter
    private String naechster_wechsel_bis;
    @Getter
    @Setter
    private Integer fk_leuchttyp_id;
    @Getter
    @Setter
    private Integer fk_standort_id;
    // @Getter @Setter
    // private Integer zaehler;
    @Getter
    @Setter
    private Integer fk_rundsteuerempfaenger_id;
    @Getter
    @Setter
    private String schaltstelle;
    @Getter
    @Setter
    private Integer fk_dk1_id;
    @Getter
    @Setter
    private Integer fk_dk2_id;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MastSearchStatement object.
     */
    public LeuchteSearchStatement() {
        setLeuchteEnabled(true);

        final List<SearchParameterInfo> parameterDescription = getSearchInfo().getParameterDescription();
        SearchParameterInfo searchParameterInfo;

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("inbetriebnahme_leuchte_von");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("inbetriebnahme_leuchte_bis");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("wechseldatum_von");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("wechseldatum_bis");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("naechster_wechsel_von");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("naechster_wechsel_bis");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("fk_leuchttyp_id");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("fk_standort_id");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("fk_rundsteuerempfaenger_id");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("schaltstelle");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("fk_dk1_id");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("fk_dk2_id");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);
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

    @Override
    protected String getAndQueryPart() {
        final Collection<String> parts = new ArrayList<String>();

        parts.add(generateVonBisQuery(
                "tdta_leuchten.inbetriebnahme_leuchte",
                inbetriebnahme_leuchte_von,
                inbetriebnahme_leuchte_bis));
        parts.add(generateVonBisQuery("tdta_leuchten.wechseldatum", wechseldatum_von, wechseldatum_bis));
        parts.add(generateVonBisQuery("tdta_leuchten.naechster_wechsel", naechster_wechsel_von, naechster_wechsel_bis));

        parts.add(generateIdQuery("tdta_leuchten.rundsteuerempfaenger", fk_rundsteuerempfaenger_id));

        parts.add(generateIdQuery("tdta_leuchten.fk_leuchttyp", fk_leuchttyp_id));
        parts.add(generateLikeQuery("tdta_leuchten.schaltstelle", schaltstelle));
        parts.add(generateIdQuery("tdta_leuchten.fk_dk1", fk_dk1_id));
        parts.add(generateIdQuery("tdta_leuchten.fk_dk2", fk_dk2_id));
        parts.add(generateIdQuery("tdta_leuchten.fk_standort", fk_standort_id));

        return BelisServerUtils.implodeArray(parts.toArray(new String[0]), " AND ");
    }
}
