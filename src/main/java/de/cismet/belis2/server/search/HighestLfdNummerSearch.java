/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.belis2.server.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.cismet.belis.commons.constants.BelisMetaClassConstants;

import de.cismet.cids.server.search.AbstractCidsServerSearch;
import de.cismet.cids.server.search.CidsServerSearch;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsServerSearch.class)
public class HighestLfdNummerSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(HighestLfdNummerSearch.class);

    //~ Instance fields --------------------------------------------------------

    private String strassenschluessel;
    private Integer kennziffer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HighestLfdNummerSearch object.
     */
    public HighestLfdNummerSearch() {
    }

    /**
     * Creates a new HighestLfdNummerSearch object.
     *
     * @param  strassenschluessel  DOCUMENT ME!
     * @param  kennziffer          DOCUMENT ME!
     */
    public HighestLfdNummerSearch(final String strassenschluessel, final Integer kennziffer) {
        setStrassenschluessel(strassenschluessel);
        setKennziffer(kennziffer);
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

    @Override
    public Collection performServerSearch() {
        final List<Integer> numbers = new ArrayList<Integer>();

        final String query = "SELECT MAX(tdta_standort_mast.lfd_nummer) "
                    + "FROM tdta_standort_mast "
                    + "LEFT JOIN tkey_strassenschluessel ON tdta_standort_mast.fk_strassenschluessel = tkey_strassenschluessel.id "
                    + "LEFT JOIN tkey_kennziffer ON tdta_standort_mast.fk_kennziffer = tkey_kennziffer.id "
                    + "WHERE tkey_strassenschluessel.pk like '" + strassenschluessel + "' "
                    + "AND tkey_kennziffer.kennziffer = " + kennziffer + ";";

        final MetaService metaService = (MetaService)getActiveLocalServers().get(BelisMetaClassConstants.DOMAIN);

        try {
            for (final ArrayList fields : metaService.performCustomSearch(query)) {
                numbers.add((Integer)fields.get(0));
            }
        } catch (Exception ex) {
            LOG.error("problem fortfuehrung item search", ex);
        }

        return numbers;
    }
}
