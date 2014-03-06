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
public class VeranlassungsschluesselSearch extends AbstractCidsServerSearch {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(VeranlassungsschluesselSearch.class);

    //~ Instance fields --------------------------------------------------------

    private final String veranlassungsnummer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VeranlassungsschluesselSearch object.
     *
     * @param  veranlassungsnummer  DOCUMENT ME!
     */
    public VeranlassungsschluesselSearch(final String veranlassungsnummer) {
        this.veranlassungsnummer = veranlassungsnummer;
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
