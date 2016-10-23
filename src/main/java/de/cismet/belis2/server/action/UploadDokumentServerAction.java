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
package de.cismet.belis2.server.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Collection;
import java.util.Properties;

import de.cismet.belis2.server.utils.BelisServerResources;

import de.cismet.cids.server.actions.ServerAction;

import de.cismet.cids.utils.serverresources.ServerResourcesLoader;

import de.cismet.commons.security.WebDavClient;
import de.cismet.commons.security.WebDavHelper;

import de.cismet.netutil.Proxy;

import de.cismet.tools.PasswordEncrypter;

/**
 * DOCUMENT ME!
 *
 * @author   jruiz
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ServerAction.class)
public class UploadDokumentServerAction extends AddDokumentServerAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            UploadDokumentServerAction.class);

    private static final String FILE_PREFIX = "DOC-";
    private static final String WEB_DAV_USER;
    private static final String WEB_DAV_PASSWORD;
    private static final String WEB_DAV_DIRECTORY;

    static {
        String pass = null;
        String user = null;
        String webDavRoot = null;
        try {
            final Properties properties = ServerResourcesLoader.getInstance()
                        .loadPropertiesResource(BelisServerResources.WEBDAV.getValue());
            pass = properties.getProperty("password");
            user = properties.getProperty("username");
            webDavRoot = properties.getProperty("url");

            if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
                pass = PasswordEncrypter.decryptString(pass);
            }
        } catch (final Exception ex) {
        } finally {
            WEB_DAV_PASSWORD = pass;
            WEB_DAV_USER = user;
            WEB_DAV_DIRECTORY = webDavRoot;
        }
    }

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        UPLOAD_INFO
    }

    //~ Instance fields --------------------------------------------------------

    private final WebDavClient webDavClient = new WebDavClient(Proxy.fromPreferences(), WEB_DAV_USER, WEB_DAV_PASSWORD);

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Object processExecution() throws Exception {
        for (final String dokumentInfo
                    : (Collection<String>)getListParam(ParameterType.UPLOAD_INFO.toString(), String.class)) {
            FileOutputStream fos = null;
            try {
                final String[] dokumentInfoArray = dokumentInfo.split("\\n");
                final String endung = dokumentInfoArray[0];
                final String beschreibung = dokumentInfoArray[1];

                final File tempFile = File.createTempFile(beschreibung, "." + endung);
                final String webFileName = WebDavHelper.generateWebDAVFileName(FILE_PREFIX, tempFile);

                fos = new FileOutputStream(tempFile);
                fos.write((byte[])getBody());

                WebDavHelper.uploadFileToWebDAV(
                    webFileName,
                    tempFile,
                    WEB_DAV_DIRECTORY,
                    webDavClient,
                    null);

                addParam(AddDokumentServerAction.ParameterType.DOKUMENT_URL.toString().toLowerCase(),
                    WEB_DAV_DIRECTORY
                            + webFileName
                            + "\n"
                            + beschreibung);
            } catch (final Exception ex) {
                LOG.fatal(ex, ex);
                throw ex;
            } finally {
                try {
                    fos.close();
                } catch (IOException ex) {
                    LOG.fatal(ex, ex);
                }
            }
        }

        return super.processExecution();
    }

    @Override
    public String getTaskName() {
        return "UploadDokument";
    }
}
