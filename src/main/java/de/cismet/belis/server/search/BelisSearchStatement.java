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

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.cismet.cids.server.search.AbstractCidsServerSearch;
import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cismap.commons.jtsgeometryfactories.PostGisGeometryFactory;

/**
 * DOCUMENT ME!
 *
 * @author   mroncoroni
 * @version  $Revision$, $Date$
 */
public class BelisSearchStatement extends AbstractCidsServerSearch implements MetaObjectNodeServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(BelisSearchStatement.class);

    //~ Instance fields --------------------------------------------------------

    private final boolean standort;
    private final boolean schaltstelle;
    private final boolean mauerlasche;
    private final boolean leitung;
    private final boolean abzweigdose;
    private final Geometry geometry;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BelisSearchStatement object.
     *
     * @param  geometry  DOCUMENT ME!
     */
    public BelisSearchStatement(final Geometry geometry) {
        this(true, true, true, true, true, geometry);
    }

    /**
     * Creates a new BelisSearchStatement object.
     *
     * @param  standort      DOCUMENT ME!
     * @param  schaltstelle  DOCUMENT ME!
     * @param  mauerlasche   DOCUMENT ME!
     * @param  leitung       DOCUMENT ME!
     * @param  abzweigdose   DOCUMENT ME!
     * @param  geometry      DOCUMENT ME!
     */
    public BelisSearchStatement(
            final boolean standort,
            final boolean schaltstelle,
            final boolean mauerlasche,
            final boolean leitung,
            final boolean abzweigdose,
            final Geometry geometry) {
        this.standort = standort;
        this.schaltstelle = schaltstelle;
        this.mauerlasche = mauerlasche;
        this.leitung = leitung;
        this.abzweigdose = abzweigdose;
        this.geometry = geometry;
    }

    //~ Methods ----------------------------------------------------------------

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

    @Override
    public Collection<MetaObjectNode> performServerSearch() {
        try {
            if (!standort && !schaltstelle && !mauerlasche && !leitung && !abzweigdose) {
                return new ArrayList<MetaObjectNode>();
            }

            final ArrayList<String> union = new ArrayList<String>();
            final ArrayList<String> join = new ArrayList<String>();
            if (standort) {
                union.add("SELECT 29 AS classid, id AS objectid, fk_geom FROM tdta_standort_mast");
                // join.add("");
            }
            if (schaltstelle) {
                union.add("SELECT 15 AS classid, id AS objectid, fk_geom FROM schaltstelle");
            }
            if (mauerlasche) {
                union.add("SELECT 14 AS classid, id AS objectid, fk_geom FROM mauerlasche");
            }
            if (leitung) {
                union.add("SELECT 11 AS classid, id AS objectid, fk_geom FROM leitung");
            }
            if (abzweigdose) {
                union.add("SELECT 5 AS classid, id AS objectid, fk_geom FROM abzweigdose");
            }
            final String implodedUnion = implodeArray(union.toArray(new String[0]), " UNION ");
            final String implodedJoin = implodeArray(join.toArray(new String[0]), " LEFT JOIN ");

            String query = "SELECT DISTINCT classid, objectid"
                        + " FROM (" + implodedUnion + ") AS geom_objects"
                        + " LEFT JOIN tdta_standort_mast ON classid = 29 AND tdta_standort_mast.id = objectid"
                        + " LEFT JOIN schaltstelle ON classid = 15 AND schaltstelle.id = objectid"
                        + " LEFT JOIN mauerlasche ON classid = 14 AND mauerlasche.id = objectid"
                        + " LEFT JOIN leitung ON classid = 11 AND leitung.id = objectid"
                        + " LEFT JOIN abzweigdose ON classid = 5 AND abzweigdose.id = objectid"
                        + ", geom"
                        + " WHERE geom.id = geom_objects.fk_geom"
                        + " AND"
                        + " (tdta_standort_mast.id IS NOT null"
                        + " OR schaltstelle.id IS NOT null"
                        + " OR mauerlasche.id IS NOT null"
                        + " OR leitung.id IS NOT null"
                        + " OR abzweigdose.id IS NOT null"
                        + " )";

            if (geometry != null) {
                final String geostring = PostGisGeometryFactory.getPostGisCompliantDbString(geometry);
                if ((geometry instanceof Polygon) || (geometry instanceof MultiPolygon)) {
                    query += " AND geo_field &&\n"
                                + "st_buffer(\n"
                                + "GeometryFromText('" + geostring + "')\n"
                                + ", 0.000001)\n"
                                + "and intersects(geo_field,st_buffer(GeometryFromText('" + geostring
                                + "'), 0.000001))";
                } else {
                    query += " AND geo_field &&\n"
                                + "st_buffer(\n"
                                + "GeometryFromText('" + geostring + "')\n"
                                + ", 0.000001)\n"
                                + "and intersects(geo_field, GeometryFromText('" + geostring + "'))";
                }
            }

            final String andQueryPart = getAndQueryPart();
            if (andQueryPart != null) {
                query += " AND " + andQueryPart;
            }

            final List<MetaObjectNode> result = new ArrayList<MetaObjectNode>();
            final MetaService ms = (MetaService)getActiveLocalServers().get("BELIS");
            final ArrayList<ArrayList> searchResult = ms.performCustomSearch(query);
            for (final ArrayList al : searchResult) {
                final int cid = (Integer)al.get(0);
                final int oid = (Integer)al.get(1);
                final MetaObjectNode mon = new MetaObjectNode("BELIS", oid, cid, "");
                result.add(mon);
            }

            return result;
        } catch (RemoteException ex) {
            LOG.error("Problem", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String getAndQueryPart() {
        return null;
    }
}
