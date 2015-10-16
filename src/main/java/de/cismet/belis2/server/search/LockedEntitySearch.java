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
import Sirius.server.middleware.types.MetaObjectNode;

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

import static de.cismet.belis2.server.utils.BelisServerUtils.implodeArray;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = RestApiCidsServerSearch.class)
public class LockedEntitySearch extends AbstractCidsServerSearch implements RestApiCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(LockedEntitySearch.class);

    //~ Instance fields --------------------------------------------------------

    @Getter private final SearchInfo searchInfo;

    @Getter @Setter private Collection<String> objectToCheck;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LockedEntitySearch object.
     */
    public LockedEntitySearch() {
        searchInfo = new SearchInfo();
        searchInfo.setKey(this.getClass().getName());
        searchInfo.setName(this.getClass().getSimpleName());
        searchInfo.setDescription("Search for Belis locks");

        final List<SearchParameterInfo> parameterDescription = new LinkedList<SearchParameterInfo>();
        final SearchParameterInfo searchParameterInfo;

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("objectToCheck");
        searchParameterInfo.setType(Type.UNDEFINED);
        parameterDescription.add(searchParameterInfo);

        searchInfo.setParameterDescription(parameterDescription);

        final SearchParameterInfo resultParameterInfo = new SearchParameterInfo();
        resultParameterInfo.setKey("return");
        resultParameterInfo.setArray(true);
        resultParameterInfo.setType(Type.UNDEFINED);
        searchInfo.setResultDescription(resultParameterInfo);
    }

    /**
     * Creates a new LockedEntitySearch object.
     *
     * @param  objectToCheck  DOCUMENT ME!
     */
    public LockedEntitySearch(final Collection<String> objectToCheck) {
        this();
        setObjectToCheck(objectToCheck);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection performServerSearch() {
        try {
            final Collection<MetaObjectNode> locks = new ArrayList<MetaObjectNode>();

            final Collection<String> whereList = new ArrayList<String>();
            for (final String lockedObject : objectToCheck) {
                if (lockedObject != null) {
                    final String[] splitted = lockedObject.split("@");
                    final String objectId = splitted[0];
                    final String classId = splitted[1];
                    whereList.add("(class_id = " + classId + " AND object_id = " + objectId + ")");
                } else {
                    LOG.warn("Entity is null. could not check if its locked");
                }
            }

            if (whereList.isEmpty()) {
                return locks;
            }

            final String whereSnippet = implodeArray(whereList.toArray(new String[0]), " OR ");
            final MetaClass mcSperre = CidsBean.getMetaClassFromTableName("BELIS2", "sperre");
            final MetaClass mcSperreEntity = CidsBean.getMetaClassFromTableName("BELIS2", "sperre_entity");
            final String query = "SELECT DISTINCT " + mcSperre.getID() + ", " + mcSperre.getTableName() + "."
                        + mcSperre.getPrimaryKey() + ", lock_timestamp" + " "
                        + "FROM " + mcSperre.getTableName() + ", " + mcSperreEntity.getTableName() + " "
                        + "WHERE sperre.id = fk_sperre AND " + whereSnippet + " "
                        + "ORDER BY lock_timestamp;";
            final MetaObject[] mos = DomainServerImpl.getServerInstance().getMetaObject(getUser(), query);

            if (mos != null) {
                for (final MetaObject mo : mos) {
                    locks.add(new MetaObjectNode("BELIS2", mo.getId(), mo.getClassID(), ""));
                }
            }
            return locks;
        } catch (Exception ex) {
            LOG.error(ex, ex);
            return null;
        }
    }
}
