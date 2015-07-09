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

import java.util.List;

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
public class MauerlascheSearchStatement extends BelisSearchStatement {

    //~ Instance fields --------------------------------------------------------

    @Getter
    @Setter
    private String erstellungsjahr;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new MauerlascheSearchStatement object.
     */
    public MauerlascheSearchStatement() {
        final List<SearchParameterInfo> parameterDescription = getSearchInfo().getParameterDescription();
        final SearchParameterInfo searchParameterInfo;

        searchParameterInfo = new SearchParameterInfo();
        searchParameterInfo.setKey("erstellungsjahr");
        searchParameterInfo.setType(Type.STRING);
        parameterDescription.add(searchParameterInfo);

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

    @Override
    protected String getAndQueryPart() {
        return "mauerlasche.erstellungsjahr = '" + erstellungsjahr + "'";
    }
}
