/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.belis2.server.search;

import Sirius.server.middleware.impls.domainserver.DomainServerImpl;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;

import lombok.Getter;
import lombok.Setter;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.cismet.cids.dynamics.CidsBean;

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
public class VeranlassungByNummerSearch extends AbstractCidsServerSearch implements RestApiCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(VeranlassungByNummerSearch.class);

    //~ Instance fields --------------------------------------------------------

    @Getter private final SearchInfo searchInfo;

    @Getter @Setter private String nummer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LockedEntitySearch object.
     */
    public VeranlassungByNummerSearch() {
        searchInfo = new SearchInfo();
        searchInfo.setKey(this.getClass().getName());
        searchInfo.setName(this.getClass().getSimpleName());
        searchInfo.setDescription("get Veranlassung by Nummer");

        final List<SearchParameterInfo> parameterDescription = new LinkedList<SearchParameterInfo>();
        final SearchParameterInfo searchParameterInfo;

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("nummer");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchInfo.setParameterDescription(parameterDescription);

        final SearchParameterInfo resultParameterInfo = new SearchParameterInfo();
        resultParameterInfo.setKey("return");
        resultParameterInfo.setArray(true);
        resultParameterInfo.setType(Type.ENTITY);
        searchInfo.setResultDescription(resultParameterInfo);
    }

    /**
     * Creates a new LockedEntitySearch object.
     *
     * @param  nummer  objectToCheck DOCUMENT ME!
     */
    public VeranlassungByNummerSearch(final String nummer) {
        this();
        setNummer(nummer);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        String query = "";
        try {
            final Collection<MetaObject> singleVeranlassung = new ArrayList<MetaObject>();
            final MetaClass mcVeranlassung = CidsBean.getMetaClassFromTableName("BELIS2", "veranlassung");
            query = "SELECT DISTINCT " + mcVeranlassung.getID() + ", "
                        + mcVeranlassung.getPrimaryKey()
                        + " FROM " + mcVeranlassung.getTableName()
                        + " WHERE nummer='" + nummer + "';";

            final MetaObject[] mos = DomainServerImpl.getServerInstance().getMetaObject(getUser(), query);

            if (mos != null) {
                for (final MetaObject mo : mos) {
                    singleVeranlassung.add(mo);
                }
            }
            return singleVeranlassung;
        } catch (Exception ex) {
            LOG.error("Error during performServerSearch(" + query + ");", ex);
            return null;
        }
    }
}
