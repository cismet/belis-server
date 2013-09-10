/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.belis.server.search;

import com.vividsolutions.jts.geom.Geometry;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class MauerlascheSearchStatement extends BelisSearchStatement {

    //~ Instance fields --------------------------------------------------------

    private String erstellungsjahr;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauerlascheSearchStatement object.
     *
     * @param  erstellungsjahr  DOCUMENT ME!
     * @param  geometry         DOCUMENT ME!
     */
    public MauerlascheSearchStatement(final String erstellungsjahr, final Geometry geometry) {
        super(
            false,
            false,
            false,
            true,
            false,
            false);
        this.erstellungsjahr = erstellungsjahr;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected String getAndQueryPart() {
        return "mauerlasche.erstellungsjahr = '" + erstellungsjahr + "'";
    }
}
