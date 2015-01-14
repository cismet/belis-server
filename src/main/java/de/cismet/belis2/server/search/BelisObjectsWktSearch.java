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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTReader;

import de.cismet.cids.server.search.CidsServerSearch;

import de.cismet.commons.converter.ConversionException;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsServerSearch.class)
public class BelisObjectsWktSearch extends BelisSearchStatement {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   wktGeometry  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void setGeometryFromWkt(final String wktGeometry) throws Exception {              
        final int skIndex = wktGeometry.indexOf(';');
        final String wkt;
        final int srid;
        if (skIndex > 0) {
            final String sridKV = wktGeometry.substring(0, skIndex);
            final int eqIndex = sridKV.indexOf('=');

            if (eqIndex > 0) {
                srid = Integer.parseInt(sridKV.substring(eqIndex + 1));
                wkt = wktGeometry.substring(skIndex + 1);
            } else {
                wkt = wktGeometry;
                srid = -1;
            }
        } else {
            wkt = wktGeometry;
            srid = -1;
        }

        final Geometry geometry;

        if (srid < 0) {
            geometry = new WKTReader().read(wkt);
        } else {
            final GeometryFactory geomFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), srid);
            geometry = new WKTReader(geomFactory).read(wkt);
        }

        
        if (geometry.getSRID() < 0) {
            setGeometry(geometry);
        } else {
            setGeometry(CrsTransformer.transformToDefaultCrs(geometry));
        }
    }
}
