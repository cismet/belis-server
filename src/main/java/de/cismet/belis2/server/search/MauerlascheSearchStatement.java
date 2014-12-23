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
package de.cismet.belis2.server.search;

import de.cismet.cids.server.search.CidsServerSearch;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsServerSearch.class)
public class MauerlascheSearchStatement extends BelisSearchStatement {

    //~ Instance fields --------------------------------------------------------

    private String erstellungsjahr;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauerlascheSearchStatement object.
     */
    public MauerlascheSearchStatement() {
        setMauerlascheEnabled(true);
    }

    /**
     * Creates a new MauerlascheSearchStatement object.
     *
     * @param  erstellungsjahr  DOCUMENT ME!
     */
    public MauerlascheSearchStatement(final String erstellungsjahr) {
        this();
        setErstellungsjahr(erstellungsjahr);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  erstellungsjahr  DOCUMENT ME!
     */
    public final void setErstellungsjahr(final String erstellungsjahr) {
        this.erstellungsjahr = erstellungsjahr;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getErstellungsjahr() {
        return erstellungsjahr;
    }

    @Override
    protected String getAndQueryPart() {
        return "mauerlasche.erstellungsjahr = '" + erstellungsjahr + "'";
    }
}
