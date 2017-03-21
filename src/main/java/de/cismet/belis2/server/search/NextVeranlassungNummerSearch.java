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

import org.apache.log4j.Logger;

import java.text.DecimalFormat;

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
public class NextVeranlassungNummerSearch extends AbstractCidsServerSearch implements RestApiCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(NextVeranlassungNummerSearch.class);

    private static final DecimalFormat DF = new DecimalFormat("00000000");

    //~ Instance fields --------------------------------------------------------

    @Getter private final SearchInfo searchInfo;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NextVeranlassungNummerSearch object.
     */
    public NextVeranlassungNummerSearch() {
        searchInfo = new SearchInfo();
        searchInfo.setKey(this.getClass().getName());
        searchInfo.setName(this.getClass().getSimpleName());
        searchInfo.setDescription("Search for next Veranlassungsnummer");

        final List<SearchParameterInfo> parameterDescription = new LinkedList<SearchParameterInfo>();
        searchInfo.setParameterDescription(parameterDescription);

        final SearchParameterInfo resultParameterInfo = new SearchParameterInfo();
        resultParameterInfo.setKey("return");
        resultParameterInfo.setArray(true);
        resultParameterInfo.setType(Type.LONG);
        searchInfo.setResultDescription(resultParameterInfo);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   searchResult  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static String getStringRepresentation(final List<Long> searchResult) {
        final Long number = (searchResult.isEmpty()) ? null : searchResult.get(0);
        return DF.format(number);
    }

    @Override
    public Collection performServerSearch() {
        final List<Long> numbers = new ArrayList<Long>();

        final String query = "SELECT nextval('veranlassungnummer_seq');";

        final MetaService metaService = (MetaService)getActiveLocalServers().get(BelisMetaClassConstants.DOMAIN);

        try {
            for (final ArrayList fields : metaService.performCustomSearch(query)) {
                numbers.add((Long)fields.get(0));
            }
        } catch (Exception ex) {
            LOG.error("", ex);
        }

        return numbers;
    }
}
