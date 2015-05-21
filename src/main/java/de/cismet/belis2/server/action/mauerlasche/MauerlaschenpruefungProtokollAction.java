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
package de.cismet.belis2.server.action.mauerlasche;

import java.sql.Timestamp;

import java.util.Collection;

import de.cismet.belis2.server.action.FortfuehrungsantragProtokollAction;
import de.cismet.belis2.server.action.ProtokollAction;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerAction;

import static de.cismet.belis2.server.action.ProtokollAction.createProtokollBean;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ServerAction.class)
public class MauerlaschenpruefungProtokollAction extends ProtokollAction {

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        PRUEFDATUM, DOKUMENT
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void executeAktion(final CidsBean protokoll) throws Exception {
        final CidsBean mauerlasche = (CidsBean)protokoll.getProperty("fk_mauerlasche");
        final Collection<CidsBean> aktionen = protokoll.getBeanCollectionProperty("n_aktionen");

        final Collection<String> urls = getListParam(ParameterType.DOKUMENT.toString(), String.class);
        for (final String urlMitBeschreibung : urls) {
            final String[] urlBeschreibungArray = urlMitBeschreibung.split("\\n");
            final CidsBean dmsurl = createDmsURLFromLink(urlBeschreibungArray[0], urlBeschreibungArray[1]);
            mauerlasche.getBeanCollectionProperty("dokumente").add(dmsurl);
            aktionen.add(createProtokollBean("neues Dokument", urlMitBeschreibung, null));
        }

        aktionen.add(createAktion(
                "Prüfdatum",
                mauerlasche,
                "pruefdatum",
                getParam(ParameterType.PRUEFDATUM.toString(), Timestamp.class)));
    }

    @Override
    public String getTaskName() {
        return getClass().getSimpleName();
    }
}
