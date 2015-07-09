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
public class NextArbeitsauftragNummerSearch extends AbstractCidsServerSearch implements RestApiCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(NextArbeitsauftragNummerSearch.class);

    //~ Instance fields --------------------------------------------------------

    @Getter
    private final SearchInfo searchInfo;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NextVeranlassungNummerSearch object.
     */
    public NextArbeitsauftragNummerSearch() {
        searchInfo = new SearchInfo();
        searchInfo.setKey(this.getClass().getName());
        searchInfo.setName(this.getClass().getSimpleName());
        searchInfo.setDescription("Search for next Arbeitsauftragsnummer");

        final List<SearchParameterInfo> parameterDescription = new LinkedList<SearchParameterInfo>();
        searchInfo.setParameterDescription(parameterDescription);

        final SearchParameterInfo resultParameterInfo = new SearchParameterInfo();
        resultParameterInfo.setKey("return");
        resultParameterInfo.setArray(true);
        resultParameterInfo.setType(Type.LONG);
        searchInfo.setResultDescription(resultParameterInfo);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final List<Long> numbers = new ArrayList<Long>();

        final String query = "SELECT nextval('arbeitsauftragnummer_seq');";

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
