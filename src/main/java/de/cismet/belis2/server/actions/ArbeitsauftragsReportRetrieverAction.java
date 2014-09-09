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
package de.cismet.belis2.server.actions;

import Sirius.server.newuser.User;

import org.openide.util.Exceptions;

import java.io.IOException;

import java.util.Properties;

import de.cismet.cids.server.actions.DownloadFileAction;
import de.cismet.cids.server.actions.ServerAction;
import de.cismet.cids.server.actions.ServerActionParameter;
import de.cismet.cids.server.actions.UserAwareServerAction;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ServerAction.class)
public class ArbeitsauftragsReportRetrieverAction extends DownloadFileAction implements UserAwareServerAction {

    //~ Static fields/initializers ---------------------------------------------

    public static final String TASK_NAME = "arbeitsauftragsReportRetriever";

    private static final String FILENAME_PROPERTIES = "arbeitsauftragsReportRetriever.properties";

    private static final transient org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            ArbeitsauftragsReportRetrieverAction.class);

    private static final Properties serviceProperties = new Properties();
    private static final String PATH_REPORTS;
    private static final String BIN_JAVA;
    private static final String LAUNCHER_STRING;
    private static final String JAR_BUNDLED;

    static {
        try {
            serviceProperties.load(ArbeitsauftragsReportRetrieverAction.class.getResourceAsStream(FILENAME_PROPERTIES));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        PATH_REPORTS = serviceProperties.getProperty("pathReports");
        BIN_JAVA = serviceProperties.getProperty("binJava");
        LAUNCHER_STRING = serviceProperties.getProperty("launcherString");
        JAR_BUNDLED = serviceProperties.getProperty("jarBundled");
    }

    //~ Instance fields --------------------------------------------------------

    private User user;

    //~ Methods ----------------------------------------------------------------

    @Override
    public Object execute(final Object body, final ServerActionParameter... params) {
        final String auftragsnummer = (String)body;

        try {
//            final MetaClass mcArbeitsauftrag = CidsBean.getMetaClassFromTableName(
//                    BelisMetaClassConstants.DOMAIN,
//                    BelisMetaClassConstants.MC_ARBEITSAUFTRAG);
//            final String query = ""
//                        + "SELECT " + mcArbeitsauftrag.getID() + ", " + mcArbeitsauftrag.getTableName() + "."
//                        + mcArbeitsauftrag.getPrimaryKey() + " "
//                        + " FROM " + mcArbeitsauftrag.getTableName()
//                        + " WHERE " + ArbeitsauftragPropertyConstants.PROP__NUMMER + " like '" + auftragsnummer + "'";
//            final MetaObject[] mos = DomainServerImpl.getServerInstance().getMetaObject(getUser(), query);
//            final CidsBean aaBean = mos[0].getBean();

//            final String aenderungsdatum = (String)aaBean.getProperty("letzte Aenderung");

            final String filepath = PATH_REPORTS + auftragsnummer + ".pdf";

            try {
                final byte[] fileContent = (byte[])super.execute(filepath);
                if (fileContent != null) {
                    return fileContent;
                }
            } catch (final Exception ex) {
                LOG.info(body, ex);
            }

            final Runtime rt = Runtime.getRuntime();
            final String launcher = String.format(LAUNCHER_STRING, auftragsnummer, filepath);
            final Process p = rt.exec(BIN_JAVA + " -classpath " + JAR_BUNDLED + " " + launcher);
            final int ret = p.waitFor();

            if (ret == 0) {
                return super.execute(filepath);
            } else {
                return new Exception("Aborted with exit code " + ret);
            }
        } catch (final Exception ex) {
            LOG.warn(ex, ex);
            return ex;
        }
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(final User user) {
        this.user = user;
    }
}
