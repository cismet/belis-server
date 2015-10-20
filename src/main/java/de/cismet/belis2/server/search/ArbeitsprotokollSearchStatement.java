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
public class ArbeitsprotokollSearchStatement extends BelisSearchStatement {

    //~ Instance fields --------------------------------------------------------

    @Getter @Setter private String auftragAngelegtAm_von;
    @Getter @Setter private String auttragAngelegtAm_bis;
    @Getter @Setter private String auftragAngelegtVon;
    @Getter @Setter private String auftragZugewiesenAn;
    @Getter @Setter private String auftragsNummer;
    @Getter @Setter private String auftragVeranlassungsNummer;
    @Getter @Setter private String datum_von;
    @Getter @Setter private String datum_bis;
    @Getter @Setter private String monteur;
    @Getter @Setter private String material;
    @Getter @Setter private String defekt;
    @Getter @Setter private String bemerkung;
    @Getter @Setter private String veranlassungsnummer;
    @Getter @Setter private Integer protokollnummer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ArbeitsprotokollSearchStatement object.
     */
    public ArbeitsprotokollSearchStatement() {
        setArbeitsprotokollEnabled(true);

        final List<SearchParameterInfo> parameterDescription = getSearchInfo().getParameterDescription();
        SearchParameterInfo searchParameterInfo;

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("auftragAngelegtAm_von");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("auttragAngelegtAm_bis");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("auftragAngelegtVon");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("auftragZugewiesenAn");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("auftragAngelegtVon");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("auftragsNummer");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("auftragVeranlassungsNummer");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("datum_von");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("datum_bis");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("monteur");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("material");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("defekt");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("bemerkung");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("veranlassungsnummer");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("protokollnummer");
        searchParameterInfo.setType(Type.INTEGER);
        parameterDescription.add(searchParameterInfo);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected String getAndQueryPart() {
        final Collection<String> parts = new ArrayList<String>();

        parts.add(generateVonBisQuery("arbeitsauftrag.angelegt_am", auftragAngelegtAm_von, auttragAngelegtAm_bis));
        parts.add(generateLikeQuery("arbeitsauftrag.angelegt_von", auftragAngelegtVon));
        parts.add(generateLikeQuery("arbeitsauftrag.zugewiesen_an", auftragZugewiesenAn));
        parts.add(generateLikeQuery("arbeitsauftrag.nummer", auftragsNummer));
        parts.add(generateVonBisQuery("arbeitsprotokoll.datum", datum_von, datum_bis));
        parts.add(generateLikeQuery("arbeitsprotokoll.monteur", monteur));
        parts.add(generateLikeQuery("arbeitsprotokoll.material", material));
        parts.add(generateLikeQuery("arbeitsprotokoll.defekt", defekt));
        parts.add(generateLikeQuery("arbeitsprotokoll.bemerkung", bemerkung));
        parts.add(generateLikeQuery("arbeitsprotokoll.veranlassungsnummer", veranlassungsnummer));
        parts.add(generateLikeQuery("arbeitsprotokoll.protokollnummer", auftragsNummer));

        if (auftragVeranlassungsNummer != null) {
            parts.add("(SELECT true FROM jt_arbeitsauftrag_arbeitsprotokoll, arbeitsprotokoll \n"
                        + "WHERE arbeitsauftrag.id = jt_arbeitsauftrag_arbeitsprotokoll.arbeitsauftrag_reference AND arbeitsprotokoll.id = fk_arbeitsprotokoll\n"
                        + "AND veranlassungsnummer ilike '" + auftragVeranlassungsNummer + "' LIMIT 1)");
        }

        return BelisServerUtils.implodeArray(parts.toArray(new String[0]), " AND ");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  angelegtAm_von  DOCUMENT ME!
     * @param  angelegtAm_bis  DOCUMENT ME!
     */
    public void setAuftragAngelegtAm(final String angelegtAm_von, final String angelegtAm_bis) {
        this.auftragAngelegtAm_von = angelegtAm_von;
        this.auttragAngelegtAm_bis = angelegtAm_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  datum_von  DOCUMENT ME!
     * @param  datum_bis  DOCUMENT ME!
     */
    public void setDatum_von(final String datum_von, final String datum_bis) {
        this.datum_von = datum_von;
        this.datum_bis = datum_bis;
    }
}
