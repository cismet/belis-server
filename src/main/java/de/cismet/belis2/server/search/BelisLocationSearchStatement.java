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

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import de.cismet.belis.commons.constants.BelisMetaClassConstants;

import de.cismet.cids.server.search.AbstractCidsServerSearch;
import de.cismet.cids.server.search.CidsServerSearch;
import de.cismet.cids.server.search.MetaObjectNodeServerSearch;

import de.cismet.cidsx.base.types.Type;

import de.cismet.cidsx.server.api.types.SearchInfo;
import de.cismet.cidsx.server.api.types.SearchParameterInfo;
import de.cismet.cidsx.server.search.RestApiCidsServerSearch;

/**
 * DOCUMENT ME!
 *
 * @author   mroncoroni
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = RestApiCidsServerSearch.class)
public class BelisLocationSearchStatement extends AbstractCidsServerSearch implements RestApiCidsServerSearch,
    MetaObjectNodeServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    /** LOGGER. */
    private static final transient Logger LOG = Logger.getLogger(BelisLocationSearchStatement.class);

    //~ Instance fields --------------------------------------------------------

    @Getter private final SearchInfo searchInfo;

    @Getter @Setter private String strassenschluessel;
    @Getter @Setter private Integer kennziffer;
    @Getter @Setter private Integer laufendeNummer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BelisLocationSearchStatement object.
     */
    public BelisLocationSearchStatement() {
        searchInfo = new SearchInfo();
        searchInfo.setKey(this.getClass().getName());
        searchInfo.setName(this.getClass().getSimpleName());
        searchInfo.setDescription("Search for Belis entites by location key");

        final List<SearchParameterInfo> parameterDescription = new LinkedList<SearchParameterInfo>();
        SearchParameterInfo searchParameterInfo;

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("strassenschluessel");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("kennziffer");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("laufendeNummer");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);

        searchInfo.setParameterDescription(parameterDescription);

        final SearchParameterInfo resultParameterInfo = new SearchParameterInfo();
        resultParameterInfo.setKey("return");
        resultParameterInfo.setArray(true);
        resultParameterInfo.setType(Type.UNDEFINED);
        searchInfo.setResultDescription(resultParameterInfo);
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
        this();
        setStrassenschluessel(strassenschluessel);
        setKennziffer(kennziffer);
        setLaufendeNummer(laufendeNummer);
    }

    //~ Methods ----------------------------------------------------------------

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
                    final MetaObjectNode mon = new MetaObjectNode(BelisMetaClassConstants.DOMAIN, oid, cid, "",null,null);// TODO: Check4CashedGeomAndLightweightJson
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

            query += "AND (schaltstelle.is_deleted IS NULL or schaltstelle.is_deleted IS FALSE) ";

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

            query += "AND (mauerlasche.is_deleted IS NULL or mauerlasche.is_deleted IS FALSE) ";

            LOG.info(query);
            return ms.performCustomSearch(query);
        } catch (final Exception ex) {
            LOG.error("error in retrieveMauerlasche", ex);
            return new ArrayList<ArrayList>();
        }
    }
}
