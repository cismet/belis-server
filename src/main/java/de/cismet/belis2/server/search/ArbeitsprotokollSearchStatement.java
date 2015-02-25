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

import de.cismet.cids.server.search.CidsServerSearch;

import static de.cismet.belis2.server.search.BelisSearchStatement.generateLikeQuery;
import static de.cismet.belis2.server.search.BelisSearchStatement.generateVonBisQuery;
import static de.cismet.belis2.server.search.BelisSearchStatement.implodeArray;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = CidsServerSearch.class)
public class ArbeitsprotokollSearchStatement extends BelisSearchStatement {

    //~ Instance fields --------------------------------------------------------

    private String auftragAngelegtAm_von;
    private String auttragAngelegtAm_bis;
    private String auftragAngelegtVon;
    private String auftragZugewiesenAn;
    private String auftragsNummer;
    private String auftragVeranlassungsNummer;
    private String datum_von;
    private String datum_bis;
    private String monteur;
    private String material;
    private String defekt;
    private String bemerkung;
    private String veranlassungsnummer;
    private Integer protokollnummer;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ArbeitsprotokollSearchStatement object.
     */
    public ArbeitsprotokollSearchStatement() {
        setArbeitsprotokollEnabled(true);
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

        return implodeArray(parts.toArray(new String[0]), " AND ");
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
     * @param  zugewiesenAn  DOCUMENT ME!
     */
    public void setAuftragZugewiesenAn(final String zugewiesenAn) {
        this.auftragZugewiesenAn = zugewiesenAn;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  angelegtVon  DOCUMENT ME!
     */
    public void setAuftragAngelegtVon(final String angelegtVon) {
        this.auftragAngelegtVon = angelegtVon;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  auftragsNummer  DOCUMENT ME!
     */
    public void setAuftragsNummer(final String auftragsNummer) {
        this.auftragsNummer = auftragsNummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAuftragVeranlassungsNummer() {
        return auftragVeranlassungsNummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  veranlassungsNummer  DOCUMENT ME!
     */
    public void setAuftragVeranlassungsNummer(final String veranlassungsNummer) {
        this.auftragVeranlassungsNummer = veranlassungsNummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAuftragAngelegtAm_von() {
        return auftragAngelegtAm_von;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  auftragAngelegtAm_von  DOCUMENT ME!
     */
    public void setAuftragAngelegtAm_von(final String auftragAngelegtAm_von) {
        this.auftragAngelegtAm_von = auftragAngelegtAm_von;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getAuttragAngelegtAm_bis() {
        return auttragAngelegtAm_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  auttragAngelegtAm_bis  DOCUMENT ME!
     */
    public void setAuttragAngelegtAm_bis(final String auttragAngelegtAm_bis) {
        this.auttragAngelegtAm_bis = auttragAngelegtAm_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDatum_von() {
        return datum_von;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  datum_von  DOCUMENT ME!
     */
    public void setDatum_von(final String datum_von) {
        this.datum_von = datum_von;
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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDatum_bis() {
        return datum_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  datum_bis  DOCUMENT ME!
     */
    public void setDatum_bis(final String datum_bis) {
        this.datum_bis = datum_bis;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getMonteur() {
        return monteur;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  monteur  DOCUMENT ME!
     */
    public void setMonteur(final String monteur) {
        this.monteur = monteur;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getMaterial() {
        return material;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  material  DOCUMENT ME!
     */
    public void setMaterial(final String material) {
        this.material = material;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getBemerkung() {
        return bemerkung;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  bemerkung  DOCUMENT ME!
     */
    public void setBemerkung(final String bemerkung) {
        this.bemerkung = bemerkung;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getVeranlassungsnummer() {
        return veranlassungsnummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  veranlassungsnummer  DOCUMENT ME!
     */
    public void setVeranlassungsnummer(final String veranlassungsnummer) {
        this.veranlassungsnummer = veranlassungsnummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Integer getProtokollnummer() {
        return protokollnummer;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  protokollnummer  DOCUMENT ME!
     */
    public void setProtokollnummer(final Integer protokollnummer) {
        this.protokollnummer = protokollnummer;
    }
}
