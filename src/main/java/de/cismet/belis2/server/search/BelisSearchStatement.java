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

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObjectNode;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.cismet.belis.commons.constants.BelisMetaClassConstants;

import de.cismet.cids.server.search.AbstractCidsServerSearch;
import de.cismet.cids.server.search.CidsServerSearch;
import de.cismet.cids.server.search.builtin.GeoSearch;

import de.cismet.cismap.commons.jtsgeometryfactories.PostGisGeometryFactory;

/**
 * DOCUMENT ME!
 *
 * @author   mroncoroni
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsServerSearch.class)
public class BelisSearchStatement extends AbstractCidsServerSearch implements GeoSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(BelisSearchStatement.class);

    //~ Instance fields --------------------------------------------------------

    private boolean standortEnabled = false;
    private boolean schaltstelleEnabled = false;
    private boolean mauerlascheEnabled = false;
    private boolean leitungEnabled = false;
    private boolean abzweigdoseEnabled = false;
    private boolean leuchteEnabled = false;
    private boolean veranlassungEnabled = false;
    private boolean arbeitsauftragEnabled = false;
    private boolean arbeitsprotokollEnabled = false;

    private boolean activeObjectsOnly = true;
    private boolean workedoffObjectsOnly = false;
    private boolean specialOnly = false;
    private boolean deletedOnly = false;
    private boolean showDeleted = false;

    private Geometry geometry;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BelisSearchStatement object.
     */
    public BelisSearchStatement() {
    }

    /**
     * Creates a new BelisSearchStatement object.
     *
     * @param  standortEnabled        DOCUMENT ME!
     * @param  leuchteEnabled         DOCUMENT ME!
     * @param  schaltstelleEnabled    DOCUMENT ME!
     * @param  mauerlascheEnabled     DOCUMENT ME!
     * @param  leitungEnabled         DOCUMENT ME!
     * @param  abzweigdoseEnabled     DOCUMENT ME!
     * @param  veranlassungEnabled    DOCUMENT ME!
     * @param  arbeitsauftragEnabled  DOCUMENT ME!
     */
    public BelisSearchStatement(
            final boolean standortEnabled,
            final boolean leuchteEnabled,
            final boolean schaltstelleEnabled,
            final boolean mauerlascheEnabled,
            final boolean leitungEnabled,
            final boolean abzweigdoseEnabled,
            final boolean veranlassungEnabled,
            final boolean arbeitsauftragEnabled) {
        setStandortEnabled(standortEnabled);
        setLeuchteEnabled(leuchteEnabled);
        setSchaltstelleEnabled(schaltstelleEnabled);
        setMauerlascheEnabled(mauerlascheEnabled);
        setLeitungEnabled(leitungEnabled);
        setAbzweigdoseEnabled(abzweigdoseEnabled);
        setVeranlassungEnabled(veranlassungEnabled);
        setArbeitsauftragEnabled(arbeitsauftragEnabled);
        setArbeitsprotokollEnabled(false);
        setSpecialOnly(false);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  activeObjectsOnly  DOCUMENT ME!
     */
    public void setActiveObjectsOnly(final boolean activeObjectsOnly) {
        this.activeObjectsOnly = activeObjectsOnly;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  standortEnabled  DOCUMENT ME!
     */
    public final void setStandortEnabled(final boolean standortEnabled) {
        this.standortEnabled = standortEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  schaltstelleEnabled  DOCUMENT ME!
     */
    public final void setSchaltstelleEnabled(final boolean schaltstelleEnabled) {
        this.schaltstelleEnabled = schaltstelleEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mauerlascheEnabled  DOCUMENT ME!
     */
    public final void setMauerlascheEnabled(final boolean mauerlascheEnabled) {
        this.mauerlascheEnabled = mauerlascheEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  leitungEnabled  DOCUMENT ME!
     */
    public final void setLeitungEnabled(final boolean leitungEnabled) {
        this.leitungEnabled = leitungEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  abzweigdoseEnabled  DOCUMENT ME!
     */
    public final void setAbzweigdoseEnabled(final boolean abzweigdoseEnabled) {
        this.abzweigdoseEnabled = abzweigdoseEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  leuchteEnabled  DOCUMENT ME!
     */
    public final void setLeuchteEnabled(final boolean leuchteEnabled) {
        this.leuchteEnabled = leuchteEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  veranlassungEnabled  DOCUMENT ME!
     */
    public final void setVeranlassungEnabled(final boolean veranlassungEnabled) {
        this.veranlassungEnabled = veranlassungEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  arbeitsauftragEnabled  DOCUMENT ME!
     */
    public final void setArbeitsauftragEnabled(final boolean arbeitsauftragEnabled) {
        this.arbeitsauftragEnabled = arbeitsauftragEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isStandortEnabled() {
        return standortEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSchaltstelleEnabled() {
        return schaltstelleEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isMauerlascheEnabled() {
        return mauerlascheEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isLeitungEnabled() {
        return leitungEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isAbzweigdoseEnabled() {
        return abzweigdoseEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isLeuchteEnabled() {
        return leuchteEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isVeranlassungEnabled() {
        return veranlassungEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isArbeitsauftragEnabled() {
        return arbeitsauftragEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isArbeitsprotokollEnabled() {
        return arbeitsprotokollEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  arbeitsprotokollEnabled  DOCUMENT ME!
     */
    public final void setArbeitsprotokollEnabled(final boolean arbeitsprotokollEnabled) {
        this.arbeitsprotokollEnabled = arbeitsprotokollEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isActiveObjectsOnly() {
        return activeObjectsOnly;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  geometry  DOCUMENT ME!
     */
    @Override
    public void setGeometry(final Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isDeletedOnly() {
        return deletedOnly;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  deletedOnly  DOCUMENT ME!
     */
    public void setDeletedOnly(final boolean deletedOnly) {
        this.deletedOnly = deletedOnly;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isShowDeleted() {
        return showDeleted;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  showDeleted  DOCUMENT ME!
     */
    public void setShowDeleted(final boolean showDeleted) {
        this.showDeleted = showDeleted;
    }

    @Override
    public Collection<MetaObjectNode> performServerSearch() {
        try {
            final MetaService ms = (MetaService)getActiveLocalServers().get(BelisMetaClassConstants.DOMAIN);
            final MetaClass MC_STANDORT = ms.getClassByTableName(getUser(), "tdta_standort_mast");
            final MetaClass MC_LEUCHTE = ms.getClassByTableName(getUser(), "tdta_leuchten");
            final MetaClass MC_SCHALTSTELLE = ms.getClassByTableName(getUser(), "schaltstelle");
            final MetaClass MC_LEITUNG = ms.getClassByTableName(getUser(), "leitung");
            final MetaClass MC_ABZWEIGDOSE = ms.getClassByTableName(getUser(), "abzweigdose");
            final MetaClass MC_MAUERLASCHE = ms.getClassByTableName(getUser(), "mauerlasche");
            final MetaClass MC_VERANLASSUNG = ms.getClassByTableName(getUser(), "veranlassung");
            final MetaClass MC_ARBEITSAUFTRAG = ms.getClassByTableName(getUser(), "arbeitsauftrag");
            final MetaClass MC_ARBEITSPROTOKOLL = ms.getClassByTableName(getUser(), "arbeitsprotokoll");

            if (!standortEnabled && !leuchteEnabled && !schaltstelleEnabled && !mauerlascheEnabled && !leitungEnabled
                        && !abzweigdoseEnabled && !veranlassungEnabled
                        && !arbeitsauftragEnabled && !arbeitsprotokollEnabled) {
                return new ArrayList<MetaObjectNode>();
            }

            final ArrayList<String> union = new ArrayList<String>();
            final ArrayList<String> join = new ArrayList<String>();
            final ArrayList<String> joinFilter = new ArrayList<String>();
            if (!specialOnly && standortEnabled) {
                union.add(
                    "SELECT "
                            + MC_STANDORT.getId()
                            + " AS classid, id AS objectid, id AS searchIntoId, is_deleted, fk_geom, 'Standort'::text AS searchIntoClass FROM tdta_standort_mast");
                join.add(
                    "tdta_standort_mast ON geom_objects.searchIntoClass = 'Standort' AND tdta_standort_mast.id = geom_objects.searchIntoId");
                joinFilter.add("tdta_standort_mast.id IS NOT null");
            }
            if (!specialOnly && leuchteEnabled) {
                union.add(
                    "SELECT "
                            + MC_LEUCHTE.getId()
                            + " AS classid, tdta_leuchten.id AS objectid, tdta_leuchten.id AS searchIntoId, tdta_standort_mast.is_deleted, tdta_standort_mast.fk_geom AS fk_geom, 'Leuchte'::text AS searchIntoClass FROM tdta_leuchten LEFT JOIN tdta_standort_mast ON tdta_leuchten.fk_standort = tdta_standort_mast.id");
                join.add(
                    "tdta_leuchten ON geom_objects.searchIntoClass = 'Leuchte' AND tdta_leuchten.id = geom_objects.searchIntoId AND (tdta_leuchten.is_deleted IS NULL OR tdta_leuchten.is_deleted IS FALSE)");
                joinFilter.add("tdta_leuchten.id IS NOT null");
            }
            if (!specialOnly && schaltstelleEnabled) {
                union.add(
                    "SELECT "
                            + MC_SCHALTSTELLE.getId()
                            + " AS classid, id AS objectid, id AS searchIntoId, is_deleted, fk_geom, 'Schaltstelle'::text AS searchIntoClass FROM schaltstelle");
                join.add(
                    "schaltstelle ON geom_objects.searchIntoClass = 'Schaltstelle' AND schaltstelle.id = geom_objects.searchIntoId");
                joinFilter.add("schaltstelle.id IS NOT null");
            }
            if (!specialOnly && mauerlascheEnabled) {
                union.add(
                    "SELECT "
                            + MC_MAUERLASCHE.getId()
                            + " AS classid, id AS objectid, id AS searchIntoId, is_deleted, fk_geom, 'Mauerlasche'::text AS searchIntoClass FROM mauerlasche");
                join.add(
                    "mauerlasche ON geom_objects.searchIntoClass = 'Mauerlasche' AND mauerlasche.id = geom_objects.searchIntoId");
                joinFilter.add("mauerlasche.id IS NOT null");
            }
            if (!specialOnly && leitungEnabled) {
                union.add(
                    "SELECT "
                            + MC_LEITUNG.getId()
                            + " AS classid, id AS objectid, id AS searchIntoId, is_deleted, fk_geom, 'Leitung'::text AS searchIntoClass FROM leitung");
                join.add(
                    "leitung ON geom_objects.searchIntoClass = 'Leitung' AND leitung.id = geom_objects.searchIntoId");
                joinFilter.add("leitung.id IS NOT null");
            }
            if (!specialOnly && abzweigdoseEnabled) {
                union.add(
                    "SELECT "
                            + MC_ABZWEIGDOSE.getId()
                            + " AS classid, id AS objectid, id AS searchIntoId, is_deleted, fk_geom, 'Abzweigdose'::text AS searchIntoClass FROM abzweigdose");
                join.add(
                    "abzweigdose ON geom_objects.searchIntoClass = 'Abzweigdose' AND abzweigdose.id = geom_objects.searchIntoId");
                joinFilter.add("abzweigdose.id IS NOT null");
            }
            if (veranlassungEnabled) {
//                final String closedSelect = "SELECT veranlassung.id AS veranlassung_id, open_arbeitsauftrag.percent "
//                            + "FROM ( "
//                            + "SELECT veranlassung.nummer AS veranlassung_nummer, (count(CASE WHEN arbeitsprotokollstatus.schluessel::int > 0 THEN 1 ELSE null END) / count(*)::float) AS percent FROM arbeitsauftrag  "
//                            + "LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference  "
//                            + "LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll  "
//                            + "LEFT JOIN arbeitsprotokollstatus ON arbeitsprotokoll.fk_status = arbeitsprotokollstatus.id  "
//                            + "LEFT JOIN veranlassung ON arbeitsprotokoll.veranlassungsnummer like veranlassung.nummer "
//                            + "GROUP BY arbeitsauftrag.id, veranlassung.nummer "
//                            + ") as open_arbeitsauftrag, veranlassung  "
//                            + "WHERE open_arbeitsauftrag.veranlassung_nummer like veranlassung.nummer AND open_arbeitsauftrag.percent < 1  "
//                            + "GROUP BY veranlassung.id, open_arbeitsauftrag.percent";
//
//                final String percentCondition = ((activeObjectsOnly) ? "closedselect.percent < 1" : "TRUE")
//                            + " AND "
//                            + ((workedoffObjectsOnly) ? "closedselect.percent >= 1" : "TRUE");
                final String closedSelect =
                    "SELECT veranlassung.id AS veranlassung_id, count(*) AS arbeitsprotokoll_count "
                            + "FROM arbeitsprotokoll, veranlassung "
                            + "WHERE veranlassung.nummer like arbeitsprotokoll.veranlassungsnummer "
                            + "GROUP BY veranlassung.id";
                final String percentCondition = ((activeObjectsOnly) ? "closedselect.arbeitsprotokoll_count IS NULL"
                                                                     : "TRUE")
                            + " AND "
                            + ((workedoffObjectsOnly) ? "closedselect.arbeitsprotokoll_count IS NOT NULL" : "TRUE");
//                            + ((workedoffObjectsOnly) ? "closedselect.percent >= 1" : "TRUE");
                if (!specialOnly || (specialOnly && leuchteEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_VERANLASSUNG.getId() + " AS classid, "
                                + "   veranlassung.id AS objectid, "
                                + "   veranlassung.id AS searchIntoId, "
                                + "   veranlassung.is_deleted AS is_deleted, "
                                + "   tdta_standort_mast.fk_geom AS fk_geom, "
                                + "   'Veranlassung'::text AS searchIntoClass "
                                + "FROM "
                                + "   veranlassung "
                                + "LEFT JOIN (" + closedSelect
                                + ") AS closedSelect ON closedselect.veranlassung_id = veranlassung.id, "
                                + "   jt_veranlassung_leuchte, "
                                + "   tdta_leuchten, "
                                + "   tdta_standort_mast "
                                + "WHERE "
                                + "   veranlassung.ar_leuchten = jt_veranlassung_leuchte.veranlassung_reference "
                                + "   AND tdta_leuchten.id = jt_veranlassung_leuchte.fk_leuchte "
                                + "   AND tdta_standort_mast.id = tdta_leuchten.fk_standort "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && leitungEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_VERANLASSUNG.getId() + " AS classid, "
                                + "   veranlassung.id AS objectid, "
                                + "   veranlassung.id AS searchIntoId, "
                                + "   veranlassung.is_deleted AS is_deleted, "
                                + "   leitung.fk_geom AS fk_geom, "
                                + "   'Veranlassung'::text AS searchIntoClass "
                                + "FROM "
                                + "   veranlassung "
                                + "LEFT JOIN (" + closedSelect
                                + ") AS closedSelect ON closedselect.veranlassung_id = veranlassung.id, "
                                + "   jt_veranlassung_leitung, "
                                + "   leitung "
                                + "WHERE "
                                + "   veranlassung.ar_leitungen = jt_veranlassung_leitung.veranlassung_reference "
                                + "   AND leitung.id = jt_veranlassung_leitung.fk_leitung "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && abzweigdoseEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_VERANLASSUNG.getId() + " AS classid, "
                                + "   veranlassung.id AS objectid, "
                                + "   veranlassung.id AS searchIntoId, "
                                + "   veranlassung.is_deleted AS is_deleted, "
                                + "   abzweigdose.fk_geom AS fk_geom, "
                                + "   'Veranlassung'::text AS searchIntoClass "
                                + "FROM "
                                + "   veranlassung "
                                + "LEFT JOIN (" + closedSelect
                                + ") AS closedSelect ON closedselect.veranlassung_id = veranlassung.id, "
                                + "   jt_veranlassung_abzweigdose, "
                                + "   abzweigdose "
                                + "WHERE "
                                + "   veranlassung.ar_leitungen = jt_veranlassung_abzweigdose.veranlassung_reference "
                                + "   AND abzweigdose.id = jt_veranlassung_abzweigdose.fk_abzweigdose "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && standortEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_VERANLASSUNG.getId() + " AS classid, "
                                + "   veranlassung.id AS objectid, "
                                + "   veranlassung.id AS searchIntoId, "
                                + "   veranlassung.is_deleted AS is_deleted, "
                                + "   tdta_standort_mast.fk_geom AS fk_geom, "
                                + "   'Veranlassung'::text AS searchIntoClass "
                                + "FROM "
                                + "   veranlassung "
                                + "LEFT JOIN (" + closedSelect
                                + ") AS closedSelect ON closedselect.veranlassung_id = veranlassung.id, "
                                + "   jt_veranlassung_standort, "
                                + "   tdta_standort_mast "
                                + "WHERE "
                                + "   veranlassung.ar_standorte = jt_veranlassung_standort.veranlassung_reference "
                                + "   AND tdta_standort_mast.id = jt_veranlassung_standort.fk_standort "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && schaltstelleEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_VERANLASSUNG.getId() + " AS classid, "
                                + "   veranlassung.id AS objectid, "
                                + "   veranlassung.id AS searchIntoId, "
                                + "   veranlassung.is_deleted AS is_deleted, "
                                + "   schaltstelle.fk_geom AS fk_geom, "
                                + "   'Veranlassung'::text AS searchIntoClass "
                                + "FROM "
                                + "   veranlassung "
                                + "LEFT JOIN (" + closedSelect
                                + ") AS closedSelect ON closedselect.veranlassung_id = veranlassung.id, "
                                + "   jt_veranlassung_schaltstelle, "
                                + "   schaltstelle "
                                + "WHERE "
                                + "   veranlassung.ar_schaltstellen = jt_veranlassung_schaltstelle.veranlassung_reference "
                                + "   AND schaltstelle.id = jt_veranlassung_schaltstelle.fk_schaltstelle "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && mauerlascheEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_VERANLASSUNG.getId() + " AS classid, "
                                + "   veranlassung.id AS objectid, "
                                + "   veranlassung.id AS searchIntoId, "
                                + "   veranlassung.is_deleted AS is_deleted, "
                                + "   mauerlasche.fk_geom AS fk_geom, "
                                + "   'Veranlassung'::text AS searchIntoClass "
                                + "FROM "
                                + "   veranlassung "
                                + "LEFT JOIN (" + closedSelect
                                + ") AS closedSelect ON closedselect.veranlassung_id = veranlassung.id, "
                                + "   jt_veranlassung_mauerlasche, "
                                + "   mauerlasche "
                                + "WHERE "
                                + "   veranlassung.ar_mauerlaschen = jt_veranlassung_mauerlasche.veranlassung_reference "
                                + "   AND mauerlasche.id = jt_veranlassung_mauerlasche.fk_mauerlasche "
                                + "   AND " + percentCondition);
                }
                union.add("SELECT "
                            + "   " + MC_VERANLASSUNG.getId() + " AS classid, "
                            + "   veranlassung.id AS objectid, "
                            + "   veranlassung.id AS searchIntoId, "
                            + "   veranlassung.is_deleted AS is_deleted, "
                            + "   geometrie.fk_geom AS fk_geom, "
                            + "   'Veranlassung'::text AS searchIntoClass "
                            + "FROM "
                            + "   veranlassung "
                            + "LEFT JOIN (" + closedSelect
                            + ") AS closedSelect ON closedselect.veranlassung_id = veranlassung.id, "
                            + "   jt_veranlassung_geometrie, "
                            + "   geometrie "
                            + "WHERE "
                            + "   veranlassung.ar_geometrien = jt_veranlassung_geometrie.veranlassung_reference AND "
                            + "   geometrie.id = jt_veranlassung_geometrie.fk_geometrie "
                            + "   AND " + percentCondition);
                join.add(
                    "veranlassung ON geom_objects.searchIntoClass = 'Veranlassung' AND veranlassung.id = geom_objects.searchIntoId");
                joinFilter.add("veranlassung.id IS NOT null");
            }
            if (arbeitsauftragEnabled) {
                final String closedSelect =
                    "SELECT arbeitsauftrag.id AS arbeitsauftrag_id, (count(CASE WHEN arbeitsprotokollstatus.schluessel::int > 0 THEN 1 ELSE null END) / count(*)::float) AS percent FROM arbeitsauftrag "
                            + "LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                            + "LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll "
                            + "LEFT JOIN arbeitsprotokollstatus ON arbeitsprotokoll.fk_status = arbeitsprotokollstatus.id "
                            + "GROUP BY arbeitsauftrag.id";
                final String percentCondition = ((activeObjectsOnly) ? "closedselect.percent < 1" : "TRUE")
                            + " AND "
                            + ((workedoffObjectsOnly) ? "closedselect.percent >= 1" : "TRUE");
                if (!specialOnly || (specialOnly && leuchteEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_ARBEITSAUFTRAG.getId() + " AS classid, "
                                + "   arbeitsauftrag.id AS objectid, "
                                + "   arbeitsauftrag.id AS searchIntoId, "
                                + "   arbeitsauftrag.is_deleted AS is_deleted, "
                                + "   tdta_standort_mast.fk_geom AS fk_geom, "
                                + "   'Arbeitsauftrag'::text AS searchIntoClass "
                                + "FROM arbeitsauftrag "
                                + "   LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                                + "   LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll, "
                                + "   tdta_leuchten, "
                                + "   tdta_standort_mast, "
                                + "   (" + closedSelect + ") AS closedSelect "
                                + "WHERE tdta_leuchten.id = arbeitsprotokoll.fk_leuchte "
                                + "   AND tdta_standort_mast.id = tdta_leuchten.fk_standort "
                                + "   AND closedselect.arbeitsauftrag_id = arbeitsauftrag.id "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && leitungEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_ARBEITSAUFTRAG.getId() + " AS classid, "
                                + "   arbeitsauftrag.id AS objectid, "
                                + "   arbeitsauftrag.id AS searchIntoId, "
                                + "   arbeitsauftrag.is_deleted AS is_deleted, "
                                + "   leitung.fk_geom AS fk_geom, "
                                + "   'Arbeitsauftrag'::text AS searchIntoClass "
                                + "FROM arbeitsauftrag "
                                + "   LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                                + "   LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll, "
                                + "   leitung, "
                                + "   (" + closedSelect + ") AS closedSelect "
                                + "WHERE leitung.id = arbeitsprotokoll.fk_leitung "
                                + "   AND closedselect.arbeitsauftrag_id = arbeitsauftrag.id "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && abzweigdoseEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_ARBEITSAUFTRAG.getId() + " AS classid, "
                                + "   arbeitsauftrag.id AS objectid, "
                                + "   arbeitsauftrag.id AS searchIntoId, "
                                + "   arbeitsauftrag.is_deleted AS is_deleted, "
                                + "   abzweigdose.fk_geom AS fk_geom, "
                                + "   'Arbeitsauftrag'::text AS searchIntoClass "
                                + "FROM arbeitsauftrag "
                                + "   LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                                + "   LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll, "
                                + "   abzweigdose, "
                                + "   (" + closedSelect + ") AS closedSelect "
                                + "WHERE abzweigdose.id = arbeitsprotokoll.fk_abzweigdose "
                                + "   AND closedselect.arbeitsauftrag_id = arbeitsauftrag.id "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && standortEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_ARBEITSAUFTRAG.getId() + " AS classid, "
                                + "   arbeitsauftrag.id AS objectid, "
                                + "   arbeitsauftrag.id AS searchIntoId, "
                                + "   arbeitsauftrag.is_deleted AS is_deleted, "
                                + "   tdta_standort_mast.fk_geom AS fk_geom, "
                                + "   'Arbeitsauftrag'::text AS searchIntoClass "
                                + "FROM arbeitsauftrag "
                                + "   LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                                + "   LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll, "
                                + "   tdta_standort_mast, "
                                + "   (" + closedSelect + ") AS closedSelect "
                                + "WHERE tdta_standort_mast.id = arbeitsprotokoll.fk_standort "
                                + "   AND closedselect.arbeitsauftrag_id = arbeitsauftrag.id "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && schaltstelleEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_ARBEITSAUFTRAG.getId() + " AS classid, "
                                + "   arbeitsauftrag.id AS objectid, "
                                + "   arbeitsauftrag.id AS searchIntoId, "
                                + "   arbeitsauftrag.is_deleted AS is_deleted, "
                                + "   schaltstelle.fk_geom AS fk_geom, "
                                + "   'Arbeitsauftrag'::text AS searchIntoClass "
                                + "FROM arbeitsauftrag "
                                + "   LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                                + "   LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll, "
                                + "   schaltstelle, "
                                + "   (" + closedSelect + ") AS closedSelect "
                                + "WHERE schaltstelle.id = arbeitsprotokoll.fk_schaltstelle "
                                + "   AND closedselect.arbeitsauftrag_id = arbeitsauftrag.id "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && mauerlascheEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_ARBEITSAUFTRAG.getId() + " AS classid, "
                                + "   arbeitsauftrag.id AS objectid, "
                                + "   arbeitsauftrag.id AS searchIntoId, "
                                + "   arbeitsauftrag.is_deleted AS is_deleted, "
                                + "   mauerlasche.fk_geom AS fk_geom, "
                                + "   'Arbeitsauftrag'::text AS searchIntoClass "
                                + "FROM arbeitsauftrag "
                                + "   LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                                + "   LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll, "
                                + "   mauerlasche, "
                                + "   (" + closedSelect + ") AS closedSelect "
                                + "WHERE mauerlasche.id = arbeitsprotokoll.fk_mauerlasche "
                                + "   AND closedselect.arbeitsauftrag_id = arbeitsauftrag.id "
                                + "   AND " + percentCondition);
                }
                union.add("SELECT "
                            + "   " + MC_ARBEITSAUFTRAG.getId() + " AS classid, "
                            + "   arbeitsauftrag.id AS objectid, "
                            + "   arbeitsauftrag.id AS searchIntoId, "
                            + "   arbeitsauftrag.is_deleted AS is_deleted, "
                            + "   geometrie.fk_geom AS fk_geom, "
                            + "   'Arbeitsauftrag'::text AS searchIntoClass "
                            + "FROM arbeitsauftrag "
                            + "   LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                            + "   LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll, "
                            + "   geometrie, "
                            + "   (" + closedSelect + ") AS closedSelect "
                            + "WHERE geometrie.id = arbeitsprotokoll.fk_geometrie "
                            + "   AND closedselect.arbeitsauftrag_id = arbeitsauftrag.id "
                            + "   AND " + percentCondition);
                join.add(
                    "arbeitsauftrag ON geom_objects.searchIntoClass = 'Arbeitsauftrag' AND arbeitsauftrag.id = geom_objects.searchIntoId");
                joinFilter.add("arbeitsauftrag.id IS NOT null");
            }
            if (arbeitsprotokollEnabled) {
                final String closedSelect =
                    "SELECT arbeitsauftrag.id AS arbeitsauftrag_id, (count(CASE WHEN fk_status > 0 THEN 1 ELSE null END) / count(*)::float) AS percent FROM arbeitsauftrag "
                            + "LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                            + "LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll "
                            + "LEFT JOIN arbeitsprotokollstatus ON arbeitsprotokoll.fk_status = arbeitsprotokollstatus.id "
                            + "GROUP BY arbeitsauftrag.id";
                final String percentCondition = ((activeObjectsOnly) ? "closedselect.percent < 1" : "TRUE")
                            + " AND "
                            + ((workedoffObjectsOnly) ? "closedselect.percent >= 1" : "TRUE");
                if (!specialOnly || (specialOnly && leuchteEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_ARBEITSPROTOKOLL.getId() + " AS classid, "
                                + "   arbeitsprotokoll.id AS objectid, "
                                + "   arbeitsprotokoll.id AS searchIntoId, "
                                + "   arbeitsprotokoll.is_deleted AS is_deleted, "
                                + "   tdta_standort_mast.fk_geom AS fk_geom, "
                                + "   'Arbeitsprotokoll'::text AS searchIntoClass "
                                + "FROM arbeitsauftrag "
                                + "   LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                                + "   LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll, "
                                + "   tdta_leuchten, "
                                + "   tdta_standort_mast, "
                                + "   (" + closedSelect + ") AS closedSelect "
                                + "WHERE tdta_leuchten.id = arbeitsprotokoll.fk_leuchte "
                                + "   AND tdta_standort_mast.id = tdta_leuchten.fk_standort "
                                + "   AND closedselect.arbeitsauftrag_id = arbeitsauftrag.id "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && leitungEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_ARBEITSPROTOKOLL.getId() + " AS classid, "
                                + "   arbeitsprotokoll.id AS objectid, "
                                + "   arbeitsprotokoll.id AS searchIntoId, "
                                + "   arbeitsprotokoll.is_deleted AS is_deleted, "
                                + "   leitung.fk_geom AS fk_geom, "
                                + "   'Arbeitsauftrag'::text AS searchIntoClass "
                                + "FROM arbeitsauftrag "
                                + "   LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                                + "   LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll, "
                                + "   leitung, "
                                + "   (" + closedSelect + ") AS closedSelect "
                                + "WHERE leitung.id = arbeitsprotokoll.fk_leitung "
                                + "   AND closedselect.arbeitsauftrag_id = arbeitsauftrag.id "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && abzweigdoseEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_ARBEITSPROTOKOLL.getId() + " AS classid, "
                                + "   arbeitsprotokoll.id AS objectid, "
                                + "   arbeitsprotokoll.id AS searchIntoId, "
                                + "   arbeitsprotokoll.is_deleted AS is_deleted, "
                                + "   abzweigdose.fk_geom AS fk_geom, "
                                + "   'Arbeitsauftrag'::text AS searchIntoClass "
                                + "FROM arbeitsauftrag "
                                + "   LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                                + "   LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll, "
                                + "   abzweigdose, "
                                + "   (" + closedSelect + ") AS closedSelect "
                                + "WHERE abzweigdose.id = arbeitsprotokoll.fk_abzweigdose "
                                + "   AND closedselect.arbeitsauftrag_id = arbeitsauftrag.id "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && standortEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_ARBEITSPROTOKOLL.getId() + " AS classid, "
                                + "   arbeitsprotokoll.id AS objectid, "
                                + "   arbeitsprotokoll.id AS searchIntoId, "
                                + "   arbeitsprotokoll.is_deleted AS is_deleted, "
                                + "   tdta_standort_mast.fk_geom AS fk_geom, "
                                + "   'Arbeitsauftrag'::text AS searchIntoClass "
                                + "FROM arbeitsauftrag "
                                + "   LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                                + "   LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll, "
                                + "   tdta_standort_mast, "
                                + "   (" + closedSelect + ") AS closedSelect "
                                + "WHERE tdta_standort_mast.id = arbeitsprotokoll.fk_standort "
                                + "   AND closedselect.arbeitsauftrag_id = arbeitsauftrag.id "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && schaltstelleEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_ARBEITSPROTOKOLL.getId() + " AS classid, "
                                + "   arbeitsprotokoll.id AS objectid, "
                                + "   arbeitsprotokoll.id AS searchIntoId, "
                                + "   arbeitsprotokoll.is_deleted AS is_deleted, "
                                + "   schaltstelle.fk_geom AS fk_geom, "
                                + "   'Arbeitsauftrag'::text AS searchIntoClass "
                                + "FROM arbeitsauftrag "
                                + "   LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                                + "   LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll, "
                                + "   schaltstelle, "
                                + "   (" + closedSelect + ") AS closedSelect "
                                + "WHERE schaltstelle.id = arbeitsprotokoll.fk_schaltstelle "
                                + "   AND closedselect.arbeitsauftrag_id = arbeitsauftrag.id "
                                + "   AND " + percentCondition);
                }
                if (!specialOnly || (specialOnly && mauerlascheEnabled)) {
                    union.add("SELECT "
                                + "   " + MC_ARBEITSPROTOKOLL.getId() + " AS classid, "
                                + "   arbeitsprotokoll.id AS objectid, "
                                + "   arbeitsprotokoll.id AS searchIntoId, "
                                + "   arbeitsprotokoll.is_deleted AS is_deleted, "
                                + "   mauerlasche.fk_geom AS fk_geom, "
                                + "   'Arbeitsauftrag'::text AS searchIntoClass "
                                + "FROM arbeitsauftrag "
                                + "   LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                                + "   LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll, "
                                + "   mauerlasche, "
                                + "   (" + closedSelect + ") AS closedSelect "
                                + "WHERE mauerlasche.id = arbeitsprotokoll.fk_mauerlasche "
                                + "   AND closedselect.arbeitsauftrag_id = arbeitsauftrag.id "
                                + "   AND " + percentCondition);
                }
                union.add("SELECT "
                            + "   " + MC_ARBEITSPROTOKOLL.getId() + " AS classid, "
                            + "   arbeitsprotokoll.id AS objectid, "
                            + "   arbeitsprotokoll.id AS searchIntoId, "
                            + "   arbeitsprotokoll.is_deleted AS is_deleted, "
                            + "   geometrie.fk_geom AS fk_geom, "
                            + "   'Arbeitsauftrag'::text AS searchIntoClass "
                            + "FROM arbeitsauftrag "
                            + "   LEFT JOIN jt_arbeitsauftrag_arbeitsprotokoll ON arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference "
                            + "   LEFT JOIN  arbeitsprotokoll ON arbeitsprotokoll.id = jt_arbeitsauftrag_arbeitsprotokoll.fk_arbeitsprotokoll, "
                            + "   geometrie, "
                            + "   (" + closedSelect + ") AS closedSelect "
                            + "WHERE geometrie.id = arbeitsprotokoll.fk_geometrie "
                            + "   AND closedselect.arbeitsauftrag_id = arbeitsauftrag.id "
                            + "   AND " + percentCondition);
                join.add(
                    "arbeitsprotokoll ON geom_objects.searchIntoClass = 'Arbeitsprotokoll' AND arbeitsprotokoll.id = geom_objects.searchIntoId");
                joinFilter.add("arbeitsprotokoll.id IS NOT null");
            }
            final String implodedUnion = implodeArray(union.toArray(new String[0]), " UNION ");
            final String implodedJoin = (joinFilter.isEmpty())
                ? "" : (" LEFT JOIN " + implodeArray(join.toArray(new String[0]), " LEFT JOIN "));
            final String implodedJoinFilter = implodeArray(joinFilter.toArray(new String[0]), " OR ");

            final String deletedCondition;
            if (isShowDeleted()) {
                if (isDeletedOnly()) {
                    deletedCondition = "(geom_objects.is_deleted IS TRUE)";
                } else {
                    deletedCondition = "TRUE";
                }
            } else {
                deletedCondition = "(geom_objects.is_deleted IS NULL OR geom_objects.is_deleted IS FALSE)";
            }

            String query = "SELECT DISTINCT classid, objectid"
                        + " FROM ("
                        + implodedUnion
                        + ") AS geom_objects"
                        + " "
                        + implodedJoin
                        + ", geom"
                        + " WHERE geom.id = geom_objects.fk_geom"
                        + " AND ("
                        + implodedJoinFilter
                        + ") AND "
                        + deletedCondition;

            if (geometry != null) {
                final String geostring = PostGisGeometryFactory.getPostGisCompliantDbString(geometry);
                if ((geometry instanceof Polygon) || (geometry instanceof MultiPolygon)) {
                    query += " AND geo_field && "
                                + "st_buffer("
                                + "GeometryFromText('"
                                + geostring
                                + "')"
                                + ", 0.000001) "
                                + "and intersects(geo_field,st_buffer(GeometryFromText('"
                                + geostring
                                + "'), 0.000001))";
                } else {
                    query += " AND geo_field && "
                                + "st_buffer("
                                + "GeometryFromText('"
                                + geostring
                                + "') "
                                + ", 0.000001) "
                                + "and intersects(geo_field, GeometryFromText('"
                                + geostring
                                + "'))";
                }
            }

            final String andQueryPart = getAndQueryPart();
            if ((andQueryPart != null) && !andQueryPart.trim().isEmpty()) {
                query += " AND "
                            + andQueryPart;
            }

            final List<MetaObjectNode> result = new ArrayList<MetaObjectNode>();
            final ArrayList<ArrayList> searchResult = ms.performCustomSearch(query);
            LOG.info(query);
            for (final ArrayList al : searchResult) {
                final int cid = (Integer)al.get(0);
                final int oid = (Integer)al.get(1);
                final MetaObjectNode mon = new MetaObjectNode(BelisMetaClassConstants.DOMAIN, oid, cid, "");
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
    public boolean isWorkedoffObjectsOnly() {
        return workedoffObjectsOnly;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workedoffObjectsOnly  DOCUMENT ME!
     */
    public void setWorkedoffObjectsOnly(final boolean workedoffObjectsOnly) {
        this.workedoffObjectsOnly = workedoffObjectsOnly;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isSpecialOnly() {
        return specialOnly;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  specialOnly  DOCUMENT ME!
     */
    public final void setSpecialOnly(final boolean specialOnly) {
        this.specialOnly = specialOnly;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String getAndQueryPart() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   field  DOCUMENT ME!
     * @param   id     DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String generateIdQuery(final String field, final Integer id) {
        final String query;
        if (id != null) {
            query = field
                        + " = "
                        + id
                        + "";
        } else {
            query = "TRUE";
        }
        return query;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   field  DOCUMENT ME!
     * @param   like   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String generateLikeQuery(final String field, final String like) {
        final String query;
        if (like != null) {
            query = field
                        + " like '"
                        + like
                        + "'";
        } else {
            query = "TRUE";
        }
        return query;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   field  DOCUMENT ME!
     * @param   von    DOCUMENT ME!
     * @param   bis    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String generateVonBisQuery(final String field, final String von, final String bis) {
        final String query;
        if (von != null) {
            if (bis != null) {
                query = field
                            + " BETWEEN '"
                            + von
                            + "' AND '"
                            + bis
                            + "'";
            } else {
                query = field
                            + " >= '"
                            + von
                            + "'";
            }
        } else if (bis != null) {
            query = field
                        + " <= '"
                        + bis
                        + "'";
        } else {
            query = "TRUE";
        }
        return query;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   inputArray  DOCUMENT ME!
     * @param   glueString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String implodeArray(final String[] inputArray, final String glueString) {
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
    public String getSearchSql(final String domainKey) {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }
}
