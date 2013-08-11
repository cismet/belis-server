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
package de.cismet.belis.server.search;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

/**
 * DOCUMENT ME!
 *
 * @author   mroncoroni
 * @version  $Revision$, $Date$
 */
public class StandortSearchStatement extends BelisSearchStatement {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(StandortSearchStatement.class);

    //~ Instance fields --------------------------------------------------------

    private String mastschutz_von;
    private String mastschutz_bis;
    private String mastanstrich_von;
    private String mastanstrich_bis;
    private Integer laufendeNummer_von;
    private Integer laufendeNummer_bis;
    private String montagefirma;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MastSearchStatement object.
     *
     * @param  mastschutz_von      actualParcel DOCUMENT ME!
     * @param  mastschutz_bis      DOCUMENT ME!
     * @param  mastanstrich_von    DOCUMENT ME!
     * @param  mastanstrich_bis    DOCUMENT ME!
     * @param  laufendeNummer_von  DOCUMENT ME!
     * @param  laufendeNummer_bis  DOCUMENT ME!
     * @param  montagefirma        DOCUMENT ME!
     * @param  geometry            DOCUMENT ME!
     */
    public StandortSearchStatement(
            final String mastschutz_von,
            final String mastschutz_bis,
            final String mastanstrich_von,
            final String mastanstrich_bis,
            final Integer laufendeNummer_von,
            final Integer laufendeNummer_bis,
            final String montagefirma,
            final Geometry geometry) {
        super(
            true,
            false,
            false,
            false,
            false,
            geometry);
        this.mastschutz_von = mastschutz_von;
        this.mastschutz_bis = mastschutz_bis;
        this.mastanstrich_von = mastanstrich_von;
        this.mastanstrich_bis = mastanstrich_bis;
        this.laufendeNummer_von = laufendeNummer_von;
        this.laufendeNummer_bis = laufendeNummer_bis;
        this.montagefirma = montagefirma;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected String getAndQueryPart() {
        final Collection<String> parts = new ArrayList<String>();
        if (mastschutz_von != null) {
            if (mastschutz_bis != null) {
                parts.add("tdta_standort_mast.mastschutz BETWEEN '" + mastschutz_von + "' AND '" + mastschutz_bis
                            + "'");
            } else {
                parts.add("tdta_standort_mast.mastschutz >= '" + mastschutz_von + "'");
            }
        } else if (mastschutz_bis != null) {
            parts.add("tdta_standort_mast.mastschutz <= '" + mastschutz_bis + "'");
        }
        if (mastanstrich_von != null) {
            if (mastanstrich_bis != null) {
                parts.add("tdta_standort_mast.mastanstrich BETWEEN '" + mastanstrich_von + "' AND '" + mastanstrich_bis
                            + "'");
            } else {
                parts.add("tdta_standort_mast.mastanstrich >= '" + mastanstrich_von + "'");
            }
        } else if (mastanstrich_bis != null) {
            parts.add("tdta_standort_mast.mastanstrich <= '" + mastanstrich_bis + "'");
        }
        if (laufendeNummer_von != null) {
            if (laufendeNummer_bis != null) {
                parts.add("tdta_standort_mast.lfd_nummer BETWEEN '" + laufendeNummer_von + "' AND '"
                            + laufendeNummer_bis + "'");
            } else {
                parts.add("tdta_standort_mast.lfd_nummer >= '" + laufendeNummer_von + "'");
            }
        } else if (laufendeNummer_bis != null) {
            parts.add("tdta_standort_mast.lfd_nummer <= '" + laufendeNummer_bis + "'");
        }
        if (montagefirma != null) {
            parts.add("tdta_standort_mast.montagefirma ilike '" + montagefirma + "'");
        }
        return implodeArray(parts.toArray(new String[0]), " AND ");
    }

    /**
     * DOCUMENT ME!
     *
     * @param   inputArray  DOCUMENT ME!
     * @param   glueString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String implodeArray(final String[] inputArray, final String glueString) {
        String output = "";
        if (inputArray.length > 0) {
            final StringBuilder sb = new StringBuilder();
            sb.append(inputArray[0]);
            for (int i = 1; i < inputArray.length; i++) {
                sb.append(glueString);
                sb.append(inputArray[i]);
            }
            output = sb.toString();
        }
        return output;
    }
}
