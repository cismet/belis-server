/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.belis2.server.search;

import com.vividsolutions.jts.io.WKTReader;

import de.cismet.cids.server.search.CidsServerSearch;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsServerSearch.class)
public class BelisObjectsWktSearch extends BelisSearchStatement {

    //~ Instance fields --------------------------------------------------------

    private final WKTReader wktReater = new WKTReader();

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   wktGeometry  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void setGeometryFromWkt(final String wktGeometry) throws Exception {
        setGeometry(wktReater.read(wktGeometry));
    }
}
