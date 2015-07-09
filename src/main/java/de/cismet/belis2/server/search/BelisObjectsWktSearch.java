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

import lombok.Getter;

import java.util.List;

import de.cismet.cidsx.base.types.Type;

import de.cismet.cidsx.server.api.types.SearchParameterInfo;
import de.cismet.cidsx.server.search.RestApiCidsServerSearch;

import de.cismet.cismap.commons.CrsTransformer;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = RestApiCidsServerSearch.class)
public class BelisObjectsWktSearch extends BelisSearchStatement {

    //~ Instance fields --------------------------------------------------------

    @Getter
    private String geometryFromWkt;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new BelisObjectsWktSearch object.
     */
    public BelisObjectsWktSearch() {
        final List<SearchParameterInfo> parameterDescription = getSearchInfo().getParameterDescription();
        final SearchParameterInfo searchParameterInfo;

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("geometryFromWkt");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   wktGeometry  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public void setGeometryFromWkt(final String wktGeometry) throws Exception {
        this.geometryFromWkt = wktGeometry;
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

        if (srid < 0) {
            setGeometry(new WKTReader().read(wkt));
        } else {
            final GeometryFactory geomFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), srid);
            final Geometry geom = CrsTransformer.transformToDefaultCrs(new WKTReader(geomFactory).read(wkt));
            geom.setSRID(-1);
            setGeometry(geom);
        }
    }
}
