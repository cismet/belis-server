/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.belis2test.server.action;

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.cismet.belis.commons.constants.BelisMetaClassConstants;
import de.cismet.belis.commons.constants.VeranlassungsartPropertyConstants;

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
public class VeranlassungsArtSearch extends AbstractCidsServerSearch implements RestApiCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(VeranlassungsArtSearch.class);

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public static enum SearchBy {

        //~ Enum constants -----------------------------------------------------

        BEZEICHNUNG, SCHLUESSEL
    }

    //~ Instance fields --------------------------------------------------------

    @Getter private final SearchInfo searchInfo;

    @Getter @Setter private String search;

    @Getter @Setter private SearchBy searchBy;

    @Getter @Setter private boolean exactSearch;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VeranlassungsArtSearch object.
     */
    public VeranlassungsArtSearch() {
        searchInfo = new SearchInfo();
        searchInfo.setKey(this.getClass().getName());
        searchInfo.setName(this.getClass().getSimpleName());
        searchInfo.setDescription("get VeranlassungArt by search string");

        final List<SearchParameterInfo> parameterDescription = new LinkedList<SearchParameterInfo>();

        final SearchParameterInfo searchParameterInfo;
        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("search");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        final SearchParameterInfo searchByParameterInfo;
        searchByParameterInfo = new SearchParameterInfo();
        searchByParameterInfo.setKey("searchBy");
        searchByParameterInfo.setType(Type.UNDEFINED);
        parameterDescription.add(searchByParameterInfo);

        final SearchParameterInfo exactSearchParameterInfo;
        exactSearchParameterInfo = new SearchParameterInfo();
        exactSearchParameterInfo.setKey("exactSearch");
        exactSearchParameterInfo.setType(Type.BOOLEAN);
        parameterDescription.add(exactSearchParameterInfo);

        searchInfo.setParameterDescription(parameterDescription);

        final SearchParameterInfo resultParameterInfo = new SearchParameterInfo();
        resultParameterInfo.setKey("return");
        resultParameterInfo.setArray(true);
        resultParameterInfo.setType(Type.ENTITY);
        searchInfo.setResultDescription(resultParameterInfo);
    }

    /**
     * Creates a new VeranlassungsArtSearch object.
     *
     * @param  searchBy     DOCUMENT ME!
     * @param  search       DOCUMENT ME!
     * @param  exactSearch  DOCUMENT ME!
     */
    public VeranlassungsArtSearch(final SearchBy searchBy, final String search, final boolean exactSearch) {
        this();
        setSearchBy(searchBy);
        setSearch(search);
        setExactSearch(exactSearch);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        String query = "";
        try {
            final Collection<MetaObject> result = new ArrayList<MetaObject>();
            final MetaClass mcVeranlassungArt =
                ((MetaService)getActiveLocalServers().get(AbstractBelisServerAction.DOMAIN)).getClassByTableName(
                    getUser(),
                    BelisMetaClassConstants.MC_VERANLASSUNGSART);

            final String whereProp;
            switch (getSearchBy()) {
                case SCHLUESSEL: {
                    whereProp = VeranlassungsartPropertyConstants.PROP__SCHLUESSEL;
                }
                break;
                default: {
                    whereProp = VeranlassungsartPropertyConstants.PROP__BEZEICHNUNG;
                }
            }

            if ((getSearch() != null) && getSearch().contains("'")) {
                return null; // quick and dirty until we have a better solution
            }
            final String whereSearch = isExactSearch() ? getSearch() : ("%" + getSearch() + "%");
            query = "SELECT DISTINCT " + mcVeranlassungArt.getID() + ", "
                        + mcVeranlassungArt.getPrimaryKey()
                        + " FROM " + mcVeranlassungArt.getTableName()
                        + " WHERE " + whereProp + " LIKE '" + whereSearch + "';";

            final MetaObject[] mos = ((MetaService)getActiveLocalServers().get(AbstractBelisServerAction.DOMAIN))
                        .getMetaObject(getUser(), query);

            if (mos != null) {
                for (final MetaObject mo : mos) {
                    result.add(mo);
                }
            }
            return result;
        } catch (Exception ex) {
            LOG.error("Error during performServerSearch(" + query + ");", ex);
            return null;
        }
    }
}
