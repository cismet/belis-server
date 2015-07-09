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
import de.cismet.cids.server.search.CidsServerSearch;

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
public class VeranlassungsschluesselSearch extends AbstractCidsServerSearch implements RestApiCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(VeranlassungsschluesselSearch.class);

    //~ Instance fields --------------------------------------------------------

    @Getter
    private final SearchInfo searchInfo;

    @Getter
    @Setter
    private String veranlassungsnummer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VeranlassungsschluesselSearch object.
     */
    public VeranlassungsschluesselSearch() {
        searchInfo = new SearchInfo();
        searchInfo.setKey(this.getClass().getName());
        searchInfo.setName(this.getClass().getSimpleName());
        searchInfo.setDescription("Search for Veranlassungsschluessel");

        final List<SearchParameterInfo> parameterDescription = new LinkedList<SearchParameterInfo>();
        final SearchParameterInfo searchParameterInfo;

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("veranlassungsnummer");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchInfo.setParameterDescription(parameterDescription);

        final SearchParameterInfo resultParameterInfo = new SearchParameterInfo();
        resultParameterInfo.setKey("return");
        resultParameterInfo.setArray(true);
        resultParameterInfo.setType(Type.STRING);
        searchInfo.setResultDescription(resultParameterInfo);
    }

    /**
     * Creates a new VeranlassungsschluesselSearch object.
     *
     * @param  veranlassungsnummer  DOCUMENT ME!
     */
    public VeranlassungsschluesselSearch(final String veranlassungsnummer) {
        this();
        setVeranlassungsnummer(veranlassungsnummer);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        final List<String> numbers = new ArrayList<String>();

        final String query =
            "SELECT 'V' || veranlassung.nummer || CASE WHEN veranlassungsart.schluessel IS NULL THEN '' ELSE veranlassungsart.schluessel END "
                    + "FROM veranlassung LEFT JOIN veranlassungsart ON veranlassungsart.id = veranlassung.fk_art "
                    + "WHERE nummer ilike '"
                    + veranlassungsnummer
                    + "'";
        final MetaService metaService = (MetaService)getActiveLocalServers().get(BelisMetaClassConstants.DOMAIN);

        try {
            for (final ArrayList fields : metaService.performCustomSearch(query)) {
                numbers.add((String)fields.get(0));
            }
        } catch (Exception ex) {
            LOG.error("problem VeranlassungsschluesselSearch", ex);
        }

        return numbers;
    }
}
