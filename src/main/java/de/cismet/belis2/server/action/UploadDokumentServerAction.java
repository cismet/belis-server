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

    private WebDavClient webDavClient = null;

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

                final Properties properties = ServerResourcesLoader.getInstance()
                            .loadProperties(BelisServerResources.WEBDAV.getValue());
                final String webDavRoot = properties.getProperty("url");
                if (webDavClient == null) {
                    final String user = properties.getProperty("username");
                    String pass = properties.getProperty("password");
                    if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
                        pass = PasswordEncrypter.decryptString(pass);
                    }
                    webDavClient = new WebDavClient(Proxy.fromPreferences(), user, pass);
                }

                WebDavHelper.uploadFileToWebDAV(
                    webFileName,
                    tempFile,
                    webDavRoot,
                    webDavClient,
                    null);

                addParam(AddDokumentServerAction.ParameterType.DOKUMENT_URL.toString().toLowerCase(),
                    webDavRoot
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
