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

import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObjectNode;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.belis.commons.constants.BelisMetaClassConstants;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
public class BelisTopicSearchStatement extends BelisSearchStatement {

    //~ Static fields/initializers ---------------------------------------------

    private static final transient Logger LOG = Logger.getLogger(BelisTopicSearchStatement.class);

    //~ Methods ----------------------------------------------------------------

    @Override
    public Collection<MetaObjectNode> performServerSearch() {
        try {
            final MetaService ms = (MetaService)getActiveLocalServers().get(BelisMetaClassConstants.DOMAIN);
            final MetaClass MC_STANDORT = ms.getClassByTableName(getUser(), "tdta_standort_mast");
            final MetaClass MC_LEUCHTE = ms.getClassByTableName(getUser(), "tdta_leuchten");
            final MetaClass MC_SCHALTSTELLE = ms.getClassByTableName(getUser(), "schaltstelle");
            final MetaClass MC_LEITUNG = ms.getClassByTableName(getUser(), "leitung");
            final MetaClass MC_ABZWEIGDOSE = ms.getClassByTableName(getUser(), "abzweigdose");
            final MetaClass MC_MAUERLASCHE = ms.getClassByTableName(getUser(), "mauerlasche");
            final MetaClass MC_VERANLASSUNG = ms.getClassByTableName(getUser(), "veranlassung");
            final MetaClass MC_ARBEITSAUFTRAG = ms.getClassByTableName(getUser(), "arbeitsauftrag");

            final ArrayList<Integer> classeIdList = new ArrayList<Integer>();
            final String snippet = getClassesInSnippetsPerDomain().get(BelisMetaClassConstants.DOMAIN)
                        .replace("(", "")
                        .replace(")", "");
            for (final String classId : snippet.split(",")) {
                try {
                    classeIdList.add(Integer.parseInt(classId));
                } catch (final Exception ex) {
                    LOG.error("error while parsing id", ex);
                }
            }
            setStandortEnabled(classeIdList.contains(MC_STANDORT.getID()));
            setLeuchteEnabled(classeIdList.contains(MC_LEUCHTE.getID()));
            setSchaltstelleEnabled(classeIdList.contains(MC_SCHALTSTELLE.getID()));
            setLeitungEnabled(classeIdList.contains(MC_LEITUNG.getID()));
            setAbzweigdoseEnabled(classeIdList.contains(MC_ABZWEIGDOSE.getID()));
            setMauerlascheEnabled(classeIdList.contains(MC_MAUERLASCHE.getID()));
            setVeranlassungEnabled(classeIdList.contains(MC_VERANLASSUNG.getID()));
            setArbeitsauftragEnabled(classeIdList.contains(MC_ARBEITSAUFTRAG.getID()));

            return super.performServerSearch();
        } catch (RemoteException ex) {
            LOG.error("Problem", ex);
            throw new RuntimeException(ex);
        }
    }
}
