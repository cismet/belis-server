/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.belis2.server.search;

import Sirius.server.middleware.interfaces.domainserver.MetaService;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.cismet.belis.commons.constants.BelisMetaClassConstants;

import de.cismet.cids.server.search.AbstractCidsServerSearch;

import de.cismet.cidsx.base.types.Type;

import de.cismet.cidsx.server.api.types.SearchInfo;
import de.cismet.cidsx.server.api.types.SearchParameterInfo;
import de.cismet.cidsx.server.search.RestApiCidsServerSearch;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = RestApiCidsServerSearch.class)
public class HighestLfdNummerSearch extends AbstractCidsServerSearch implements RestApiCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(HighestLfdNummerSearch.class);

    //~ Instance fields --------------------------------------------------------

    @Getter private final SearchInfo searchInfo;

    @Getter @Setter private String strassenschluessel;
    @Getter @Setter private Integer kennziffer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new HighestLfdNummerSearch object.
     */
    public HighestLfdNummerSearch() {
        searchInfo = new SearchInfo();
        searchInfo.setKey(this.getClass().getName());
        searchInfo.setName(this.getClass().getSimpleName());
        searchInfo.setDescription("Search for laufende Nummer");

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

        searchInfo.setParameterDescription(parameterDescription);

        final SearchParameterInfo resultParameterInfo = new SearchParameterInfo();
        resultParameterInfo.setKey("return");
        resultParameterInfo.setArray(true);
        resultParameterInfo.setType(Type.INTEGER);
        searchInfo.setResultDescription(resultParameterInfo);
    }

    /**
     * Creates a new HighestLfdNummerSearch object.
     *
     * @param  strassenschluessel  DOCUMENT ME!
     * @param  kennziffer          DOCUMENT ME!
     */
    public HighestLfdNummerSearch(final String strassenschluessel, final Integer kennziffer) {
        this();
        setStrassenschluessel(strassenschluessel);
        setKennziffer(kennziffer);
    }

    //~ Methods ----------------------------------------------------------------

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
