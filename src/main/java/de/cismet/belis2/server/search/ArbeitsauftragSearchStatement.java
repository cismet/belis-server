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
public class ArbeitsauftragSearchStatement extends BelisSearchStatement {

    //~ Instance fields --------------------------------------------------------

    @Getter
    @Setter
    private String angelegtAm_von;
    @Getter
    @Setter
    private String angelegtAm_bis;
    @Getter
    @Setter
    private String angelegtVon;
    @Getter
    @Setter
    private Integer zugewiesenAn;
    @Getter
    @Setter
    private String auftragsNummer;
    @Getter
    @Setter
    private String veranlassungsNummer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ArbeitsauftragSearchStatement object.
     */
    public ArbeitsauftragSearchStatement() {
        setArbeitsauftragEnabled(true);

        final List<SearchParameterInfo> parameterDescription = getSearchInfo().getParameterDescription();
        SearchParameterInfo searchParameterInfo;

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("angelegtAm_von");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("angelegtAm_bis");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("angelegtVon");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("zugewiesenAn");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("auftragsNummer");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("veranlassungsNummer");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected String getAndQueryPart() {
        final Collection<String> parts = new ArrayList<String>();

        parts.add(generateVonBisQuery("arbeitsauftrag.angelegt_am", angelegtAm_von, angelegtAm_bis));
        parts.add(generateLikeQuery("arbeitsauftrag.angelegt_von", angelegtVon));
        parts.add(generateIdQuery("arbeitsauftrag.zugewiesen_an", zugewiesenAn));
        parts.add(generateLikeQuery("arbeitsauftrag.nummer", auftragsNummer));

        if (veranlassungsNummer != null) {
            parts.add("(SELECT true FROM jt_arbeitsauftrag_arbeitsprotokoll, arbeitsprotokoll \n"
                        + "WHERE arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference AND arbeitsprotokoll.id = fk_arbeitsprotokoll\n"
                        + "AND veranlassungsnummer ilike '" + veranlassungsNummer + "' LIMIT 1)");
        }

        return BelisServerUtils.implodeArray(parts.toArray(new String[0]), " AND ");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  angelegtAm_von  DOCUMENT ME!
     * @param  angelegtAm_bis  DOCUMENT ME!
     */
    public void setAngelegtAm(final String angelegtAm_von, final String angelegtAm_bis) {
        this.angelegtAm_von = angelegtAm_von;
        this.angelegtAm_bis = angelegtAm_bis;
    }
}
