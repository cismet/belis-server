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

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.cismet.belis2.server.utils.BelisServerUtils;

import de.cismet.cidsx.base.types.Type;

import de.cismet.cidsx.server.api.types.SearchParameterInfo;
import de.cismet.cidsx.server.search.RestApiCidsServerSearch;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = RestApiCidsServerSearch.class)
public class VeranlassungSearchStatement extends BelisSearchStatement {

    //~ Instance fields --------------------------------------------------------

    @Getter
    @Setter
    private String datum_von;
    @Getter
    @Setter
    private String datum_bis;
    @Getter
    @Setter
    private Integer grund_id;
    @Getter
    @Setter
    private String angelegtVon;
    @Getter
    @Setter
    private String nummer;
    @Getter
    @Setter
    private String bemerkungen;
    @Getter
    @Setter
    private String bezeichnung;
    @Getter
    @Setter
    private String beschreibung;
    @Getter
    @Setter
    private String infobaustein;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new VeranlassungSearchStatement object.
     */
    public VeranlassungSearchStatement() {
        setVeranlassungEnabled(true);

        final List<SearchParameterInfo> parameterDescription = getSearchInfo().getParameterDescription();
        SearchParameterInfo searchParameterInfo;

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("datum_von");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("datum_bis");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("grund_id");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("angelegtVon");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("nummer");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("bemerkungen");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("bezeichnung");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("beschreibung");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("infobaustein");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected String getAndQueryPart() {
        final Collection<String> parts = new ArrayList<String>();

        parts.add(generateVonBisQuery("veranlassung.datum", datum_von, datum_bis));
        parts.add(generateIdQuery("veranlassung.fk_art", grund_id));
        parts.add(generateLikeQuery("veranlassung.username", angelegtVon));
        parts.add(generateLikeQuery("veranlassung.nummer", nummer));
        parts.add(generateLikeQuery("veranlassung.beschreibung", beschreibung));
        parts.add(generateLikeQuery("veranlassung.bezeichnung", bezeichnung));
        parts.add(generateLikeQuery("veranlassung.bemerkungen", bemerkungen));
        if (infobaustein != null) {
            parts.add("(SELECT true FROM jt_veranlassung_infobaustein, infobaustein "
                        + "WHERE jt_veranlassung_infobaustein.fk_infobaustein = infobaustein.id "
                        + "AND veranlassung_reference = veranlassung.id "
                        + "AND (infobaustein.schluessel ilike '" + infobaustein + "' OR infobaustein.wert ilike '"
                        + infobaustein + "') "
                        + "LIMIT 1)");
        }

        return BelisServerUtils.implodeArray(parts.toArray(new String[0]), " AND ");
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
}
