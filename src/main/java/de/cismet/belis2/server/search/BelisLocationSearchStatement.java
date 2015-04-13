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

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.cismet.belis.commons.constants.BelisMetaClassConstants;

import de.cismet.cids.server.search.AbstractCidsServerSearch;
import de.cismet.cids.server.search.CidsServerSearch;
import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

/**
 * DOCUMENT ME!
 *
 * @author   mroncoroni
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsServerSearch.class)
public class BelisLocationSearchStatement extends AbstractCidsServerSearch implements MetaObjectNodeServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(BelisLocationSearchStatement.class);

    //~ Instance fields --------------------------------------------------------

    private String strassenschluessel;
    private Integer kennziffer;
    private Integer laufendeNummer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BelisLocationSearchStatement object.
     */
    public BelisLocationSearchStatement() {
    }

    /**
     * Creates a new BelisLocationSearchStatement object.
     *
     * @param  strassenschluessel  DOCUMENT ME!
     * @param  kennziffer          DOCUMENT ME!
     * @param  laufendeNummer      DOCUMENT ME!
     */
    public BelisLocationSearchStatement(final String strassenschluessel,
            final Integer kennziffer,
            final Integer laufendeNummer) {
        setStrassenschluessel(strassenschluessel);
        setKennziffer(kennziffer);
        setLaufendeNummer(laufendeNummer);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getStrassenschluessel() {
        return strassenschluessel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  strassenschluessel  DOCUMENT ME!
     */
    public final void setStrassenschluessel(final String strassenschluessel) {
        this.strassenschluessel = strassenschluessel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getKennziffer() {
        return kennziffer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kennziffer  DOCUMENT ME!
     */
    public final void setKennziffer(final Integer kennziffer) {
        this.kennziffer = kennziffer;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getLaufendeNummer() {
        return laufendeNummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  laufendeNummer  DOCUMENT ME!
     */
    public final void setLaufendeNummer(final Integer laufendeNummer) {
        this.laufendeNummer = laufendeNummer;
    }

    @Override
    public Collection<MetaObjectNode> performServerSearch() {
        try {
            final MetaService ms = (MetaService)getActiveLocalServers().get(BelisMetaClassConstants.DOMAIN);

            if ((strassenschluessel == null) && (kennziffer == null) && (laufendeNummer == null)) {
                return new ArrayList<MetaObjectNode>();
            }

            final Collection<ArrayList> results = new HashSet<ArrayList>();
            results.addAll(retrieveStandort(ms, strassenschluessel, kennziffer, laufendeNummer));
            results.addAll(retrieveSchaltstelle(ms, strassenschluessel, laufendeNummer));
            results.addAll(retrieveMauerlasche(ms, strassenschluessel, laufendeNummer));

            final List<MetaObjectNode> result = new ArrayList<MetaObjectNode>();
            for (final ArrayList al : results) {
                if (al != null) {
                    final int cid = (Integer)al.get(0);
                    final int oid = (Integer)al.get(1);
                    final MetaObjectNode mon = new MetaObjectNode(BelisMetaClassConstants.DOMAIN, oid, cid, "");
                    result.add(mon);
                }
            }

            return result;
        } catch (final Exception ex) {
            LOG.error("while executing location search", ex);

            return new ArrayList<MetaObjectNode>();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ms                  DOCUMENT ME!
     * @param   strassenschluessel  DOCUMENT ME!
     * @param   kennziffer          DOCUMENT ME!
     * @param   laufendeNummer      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<ArrayList> retrieveStandort(final MetaService ms,
            final String strassenschluessel,
            final Integer kennziffer,
            final Integer laufendeNummer) {
        try {
            if (strassenschluessel == null) {
                System.out.println("At least the strassenschluessel must be != null");
                return new ArrayList<ArrayList>();
            }

            final MetaClass MC_STANDORT = ms.getClassByTableName(getUser(), "tdta_standort_mast");

            String query;
            if (kennziffer != null) {
                query = "SELECT " + MC_STANDORT.getId()
                            + ", tdta_standort_mast.id FROM tdta_standort_mast, tkey_strassenschluessel, tkey_kennziffer WHERE "
                            + "tdta_standort_mast.fk_strassenschluessel = tkey_strassenschluessel.id AND tkey_strassenschluessel.pk like '"
                            + strassenschluessel + "' "
                            + "AND tdta_standort_mast.fk_kennziffer = tkey_kennziffer.id "
                            + "AND tkey_kennziffer.kennziffer = " + kennziffer + " ";
            } else {
                query = "SELECT " + MC_STANDORT.getId()
                            + ", tdta_standort_mast.id FROM tdta_standort_mast, tkey_strassenschluessel WHERE "
                            + "tdta_standort_mast.fk_strassenschluessel = tkey_strassenschluessel.id AND tkey_strassenschluessel.pk like '"
                            + strassenschluessel + "' ";
            }
            if (laufendeNummer != null) {
                query += "AND tdta_standort_mast.lfd_nummer = " + laufendeNummer + " ";
            }
            query += "AND (tdta_standort_mast.is_deleted IS NULL or tdta_standort_mast.is_deleted IS FALSE) ";

            LOG.info(query);
            return ms.performCustomSearch(query);
        } catch (final Exception ex) {
            LOG.error("error in retrieveStandort", ex);
            return new ArrayList<ArrayList>();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ms                  DOCUMENT ME!
     * @param   strassenschluessel  DOCUMENT ME!
     * @param   laufendeNummer      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<ArrayList> retrieveSchaltstelle(final MetaService ms,
            final String strassenschluessel,
            final Integer laufendeNummer) {
        try {
            if (strassenschluessel == null) {
                return new ArrayList<ArrayList>();
            }

            final MetaClass MC_SCHALTSTELLE = ms.getClassByTableName(getUser(), "schaltstelle");

            String query = "SELECT " + MC_SCHALTSTELLE.getId()
                        + ", schaltstelle.id FROM schaltstelle, tkey_strassenschluessel WHERE "
                        + "schaltstelle.fk_strassenschluessel = tkey_strassenschluessel.id AND tkey_strassenschluessel.pk like '"
                        + strassenschluessel + "' ";

            if (laufendeNummer != null) {
                query += "AND schaltstelle.laufende_nummer = " + laufendeNummer + " ";
            }

            query += "AND (tdta_standort_mast.is_deleted IS NULL or tdta_standort_mast.is_deleted IS FALSE) ";

            LOG.info(query);
            return ms.performCustomSearch(query);
        } catch (final Exception ex) {
            LOG.error("error in retrieveSchaltstelle", ex);
            return new ArrayList<ArrayList>();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ms                  DOCUMENT ME!
     * @param   strassenschluessel  DOCUMENT ME!
     * @param   laufendeNummer      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Collection<ArrayList> retrieveMauerlasche(final MetaService ms,
            final String strassenschluessel,
            final Integer laufendeNummer) {
        try {
            if (strassenschluessel == null) {
                return new ArrayList<ArrayList>();
            }

            final MetaClass MC_MAUERLASCHE = ms.getClassByTableName(getUser(), "mauerlasche");

            String query = "SELECT " + MC_MAUERLASCHE.getId()
                        + ", mauerlasche.id FROM mauerlasche, tkey_strassenschluessel WHERE "
                        + "mauerlasche.fk_strassenschluessel = tkey_strassenschluessel.id AND tkey_strassenschluessel.pk like '"
                        + strassenschluessel + "' ";

            if (laufendeNummer != null) {
                query += "AND mauerlasche.laufende_nummer = " + laufendeNummer + " ";
            }

            query += "AND (tdta_standort_mast.is_deleted IS NULL or tdta_standort_mast.is_deleted IS FALSE) ";
            
            LOG.info(query);
            return ms.performCustomSearch(query);
        } catch (final Exception ex) {
            LOG.error("error in retrieveMauerlasche", ex);
            return new ArrayList<ArrayList>();
        }
    }
}
