/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.belis2.server.search;

import Sirius.server.localserver._class.ClassCache;
import Sirius.server.middleware.impls.domainserver.DomainServerImpl;
import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.middleware.types.MetaObjectNode;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.belis.commons.constants.BelisMetaClassConstants;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.search.AbstractCidsServerSearch;
import de.cismet.cids.server.search.CidsServerSearch;

import static de.cismet.belis2.server.utils.BelisServerUtils.implodeArray;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsServerSearch.class)
public class LockedEntitySearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(LockedEntitySearch.class);

    //~ Instance fields --------------------------------------------------------

    private Collection<String> objectToCheck;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LockedEntitySearch object.
     */
    public LockedEntitySearch() {
    }

    /**
     * Creates a new LockedEntitySearch object.
     *
     * @param  objectToCheck  DOCUMENT ME!
     */
    public LockedEntitySearch(final Collection<String> objectToCheck) {
        setObjectToCheck(objectToCheck);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<String> getObjectToCheck() {
        return objectToCheck;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  objectToCheck  DOCUMENT ME!
     */
    public final void setObjectToCheck(final Collection<String> objectToCheck) {
        this.objectToCheck = objectToCheck;
    }

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
