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

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class NextArbeitsauftragNummerSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(NextArbeitsauftragNummerSearch.class);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NextVeranlassungNummerSearch object.
     */
    public NextArbeitsauftragNummerSearch() {
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
