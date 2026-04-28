/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.belis2.server.action;

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

import Sirius.server.middleware.impls.domainserver.DomainServerImpl;
import Sirius.server.middleware.interfaces.domainserver.MetaService;
import Sirius.server.middleware.interfaces.domainserver.MetaServiceStore;
import Sirius.server.middleware.types.MetaClass;
import Sirius.server.middleware.types.MetaObject;
import Sirius.server.newuser.User;

import java.sql.Date;

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.cismet.belis.commons.constants.BelisMetaClassConstants;

import de.cismet.belis2.server.search.NextVeranlassungNummerSearch;
import de.cismet.belis2.server.search.VeranlassungByNummerSearch;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerAction;
import de.cismet.cids.server.actions.ServerActionParameter;
import de.cismet.cids.server.actions.UserAwareServerAction;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ServerAction.class)
public class EditVeranlassungViaAAAction implements ServerAction, UserAwareServerAction, MetaServiceStore {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            EditVeranlassungViaAAAction.class);
    private static final ConnectionContext CC = ConnectionContext.create(
            AbstractConnectionContext.Category.ACTION,
            "EditVeranlassungViaAA");

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        bezeichnung, beschreibung, aaid
    }

    //~ Instance fields --------------------------------------------------------

    protected final Map<String, Object> paramsHashMap = new HashMap<>();
    private MetaService ms = null;
    private User usr = null;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void setMetaService(final MetaService service) {
        this.ms = service;
    }

    @Override
    public MetaService getMetaService() {
        return ms;
    }

    @Override
    public String getTaskName() {
        return "editVeranlassungViaAA";
    }

    @Override
    public User getUser() {
        return usr;
    }

    @Override
    public void setUser(final User user) {
        this.usr = user;
    }

    @Override
    public Object execute(final Object body, final ServerActionParameter... params) {
        String bezeichnung = null;
        String beschreibung = null;
        String aaid = null;
        boolean newBean = false;

        for (final ServerActionParameter param : params) {
            final String key = param.getKey().toLowerCase();
            final Object value = param.getValue();

            if (key.equalsIgnoreCase(ParameterType.bezeichnung.name())) {
                bezeichnung = (value != null) ? value.toString() : null;
            } else if (key.equalsIgnoreCase(ParameterType.beschreibung.name())) {
                beschreibung = (value != null) ? value.toString() : null;
            } else if (key.equalsIgnoreCase(ParameterType.aaid.name())) {
                aaid = value.toString();
            }
        }

        try {
            if (aaid instanceof String) {
                final int id = Integer.parseInt(aaid);
                CidsBean veranlassung = null;

                final MetaClass aaMc = CidsBean.getMetaClassFromTableName(
                        BelisMetaClassConstants.DOMAIN,
                        BelisMetaClassConstants.MC_ARBEITSAUFTRAG);
                final CidsBean bean = DomainServerImpl.getServerInstance()
                            .getMetaObject(getUser(), id, aaMc.getId(), CC)
                            .getBean();

                final Object protokolle = bean.getProperty("ar_protokolle");
                if (protokolle instanceof Collection) {
                    final List<CidsBean> protokollList = (List<CidsBean>)protokolle;

                    for (final CidsBean protokoll : protokollList) {
                        final String nummer = (String)protokoll.getProperty("veranlassungsnummer");

                        if (nummer != null) {
                            final Map localServers = new HashMap<>();
                            localServers.put(BelisMetaClassConstants.DOMAIN, getMetaService());
                            final VeranlassungByNummerSearch search = new VeranlassungByNummerSearch(nummer);
                            search.setActiveLocalServers(localServers);
                            search.setUser(getUser());
                            final ArrayList<MetaObject> mos = (ArrayList<MetaObject>)search.performServerSearch();

                            if ((mos != null) && (mos.size() > 0)) {
                                veranlassung = mos.get(0).getBean();
                                break;
                            }
                        }
                    }
                }

                if (veranlassung == null) {
                    veranlassung = createNewVeranlassung(bezeichnung, beschreibung);
                    newBean = true;
                } else {
                    veranlassung.setProperty("bezeichnung", bezeichnung);
                    veranlassung.setProperty("beschreibung", beschreibung);
                }

                if (veranlassung != null) {
                    if (newBean) {
                        if (protokolle instanceof Collection) {
                            final List<CidsBean> protokollList = (List<CidsBean>)protokolle;

                            for (final CidsBean protokoll : protokollList) {
                                protokoll.setProperty("veranlassungsnummer", veranlassung.getProperty("nummer"));

                                if (protokoll.getProperty("fk_abzweigdose") != null) {
                                    final Object array = veranlassung.getProperty("ar_abzweigdosen");

                                    if (array instanceof Collection) {
                                        ((List<CidsBean>)array).add(protokoll);
                                    }
                                } else if (protokoll.getProperty("fk_geometrie") != null) {
                                    final Object array = veranlassung.getProperty("ar_geometrien");

                                    if (array instanceof Collection) {
                                        ((List<CidsBean>)array).add(protokoll);
                                    }
                                } else if (protokoll.getProperty("fk_leitung") != null) {
                                    final Object array = veranlassung.getProperty("ar_leitungen");

                                    if (array instanceof Collection) {
                                        ((List<CidsBean>)array).add(protokoll);
                                    }
                                } else if (protokoll.getProperty("fk_leuchte") != null) {
                                    final Object array = veranlassung.getProperty("ar_leuchten");

                                    if (array instanceof Collection) {
                                        ((List<CidsBean>)array).add(protokoll);
                                    }
                                } else if (protokoll.getProperty("fk_mauerlasche") != null) {
                                    final Object array = veranlassung.getProperty("ar_mauerlaschen");

                                    if (array instanceof Collection) {
                                        ((List<CidsBean>)array).add(protokoll);
                                    }
                                } else if (protokoll.getProperty("fk_schaltstelle") != null) {
                                    final Object array = veranlassung.getProperty("ar_schaltstellen");

                                    if (array instanceof Collection) {
                                        ((List<CidsBean>)array).add(protokoll);
                                    }
                                } else if (protokoll.getProperty("fk_standort") != null) {
                                    final Object array = veranlassung.getProperty("ar_standorte");

                                    if (array instanceof Collection) {
                                        ((List<CidsBean>)array).add(protokoll);
                                    }
                                }
                            }
                        }
                    }

                    if (newBean) {
                        DomainServerImpl.getServerInstance()
                                .insertMetaObject(getUser(), veranlassung.getMetaObject(), CC);
                    } else {
                        DomainServerImpl.getServerInstance()
                                .updateMetaObject(getUser(), veranlassung.getMetaObject(), CC);
                    }
                    DomainServerImpl.getServerInstance().updateMetaObject(getUser(), bean.getMetaObject(), CC);

                    return "{\"nummer\":, \"" + veranlassung.getProperty("nummer") + "\"}";
                }
            }

            return "{\"error\":, \"Could not modify veranlassung\"}";
        } catch (Exception e) {
            LOG.error("Cannot edit arbeitsauftrag object", e);

            return "{\"error\":, \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  key    DOCUMENT ME!
     * @param  value  DOCUMENT ME!
     */
    protected void addParam(final String key, final Object value) {
        paramsHashMap.put(key, value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   bezeichnung   DOCUMENT ME!
     * @param   beschreibung  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public CidsBean createNewVeranlassung(final String bezeichnung, final String beschreibung) {
        try {
            final MetaClass veranlassungMc = CidsBean.getMetaClassFromTableName(
                    BelisMetaClassConstants.DOMAIN,
                    BelisMetaClassConstants.MC_VERANLASSUNG);
            final Map localServers = new HashMap<>();
            localServers.put(BelisMetaClassConstants.DOMAIN, getMetaService());
            final MetaObject veranlassungMO = veranlassungMc.getEmptyInstance(CC);
            final CidsBean veranlassung = veranlassungMO.getBean();

            veranlassung.setProperty("datum", new Date(Calendar.getInstance().getTime().getTime()));
            veranlassung.setProperty("username", getUser().getName());
            veranlassung.setProperty("bezeichnung", bezeichnung);
            veranlassung.setProperty("beschreibung", beschreibung);

            final NextVeranlassungNummerSearch nummerSearch = new NextVeranlassungNummerSearch();
            nummerSearch.setActiveLocalServers(localServers);
            nummerSearch.setUser(getUser());

            final List<Long> nextNumber = (List<Long>)nummerSearch.performServerSearch();

            final Long number = (nextNumber.isEmpty()) ? null : nextNumber.get(0);
            final DecimalFormat df = new DecimalFormat("00000000");

            veranlassung.setProperty("nummer", df.format(number));

            return veranlassung;
        } catch (Exception e) {
            LOG.error("Error while creting veranlassung", e);
        }

        return null;
    }
}
