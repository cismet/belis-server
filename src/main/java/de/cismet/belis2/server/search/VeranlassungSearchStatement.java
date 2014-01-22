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

import java.util.ArrayList;
import java.util.Collection;

import static de.cismet.belis2.server.search.BelisSearchStatement.generateIdQuery;
import static de.cismet.belis2.server.search.BelisSearchStatement.generateVonBisQuery;
import static de.cismet.belis2.server.search.BelisSearchStatement.implodeArray;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class VeranlassungSearchStatement extends BelisSearchStatement {

    //~ Instance fields --------------------------------------------------------

    private String datum_von;
    private String datum_bis;
    private Integer grund_id;
    private String angelegtVon;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VeranlassungSearchStatement object.
     */
    public VeranlassungSearchStatement() {
        setVeranlassungEnabled(true);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected String getAndQueryPart() {
        final Collection<String> parts = new ArrayList<String>();

        parts.add(generateVonBisQuery("veranlassung.datum", datum_von, datum_bis));
        parts.add(generateIdQuery("veranlassung.fk_art", grund_id));
        parts.add(generateLikeQuery("veranlassung.username", angelegtVon));
        return implodeArray(parts.toArray(new String[0]), " AND ");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  datum_von  DOCUMENT ME!
     * @param  datum_bis  DOCUMENT ME!
     */
    public void setDatum(final String datum_von, final String datum_bis) {
        this.datum_von = datum_von;
        this.datum_bis = datum_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  grund_id  DOCUMENT ME!
     */
    public void setGrund_id(final Integer grund_id) {
        this.grund_id = grund_id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  angelegtVon  DOCUMENT ME!
     */
    public void setAngelegtVon(final String angelegtVon) {
        this.angelegtVon = angelegtVon;
    }
}
