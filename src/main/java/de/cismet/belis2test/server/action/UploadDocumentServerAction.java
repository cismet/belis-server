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
package de.cismet.belis2test.server.action;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Base64;
import java.util.GregorianCalendar;

import de.cismet.belis2.server.utils.BelisWebdavProperties;

import de.cismet.cids.server.actions.ServerAction;

import de.cismet.cids.utils.serverresources.ServerResourcesLoader;
import de.cismet.cids.utils.serverresources.TextServerResource;

import de.cismet.commons.security.WebDavClient;
import de.cismet.commons.security.WebDavHelper;

import de.cismet.netutil.ProxyHandler;

import de.cismet.tools.PasswordEncrypter;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ServerAction.class)
public class UploadDocumentServerAction extends AddDokumentServerAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(
            UploadDocumentServerAction.class);

    private static final String FILE_PREFIX = "DOC-";
    private static final String LOCAL_FILE_PREFIX = "DEV-";

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        ImageData, PREFIX, Ending, Description, TS
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected Object processExecution() throws Exception {
        final String imageData = (String)getParam(ParameterType.ImageData.toString(), String.class);
        final String dev = (String)getParam(ParameterType.PREFIX.toString(), String.class);

        try {
            final String endung = (String)getParam(ParameterType.Ending.toString(), String.class);
            final String beschreibung = (String)getParam(ParameterType.Description.toString(), String.class);
            final Long ts = (Long)getParam(ParameterType.TS.toString(), Long.class);

            final String documentUrl = writeImage(dev, ts, beschreibung, endung, imageData);

            addParam(AddDokumentServerAction.ParameterType.DOKUMENT_URL.toString().toLowerCase(), documentUrl);
        } catch (final Exception ex) {
            LOG.fatal(ex, ex);
            throw ex;
        }

        return super.processExecution();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   prefix       DOCUMENT ME!
     * @param   ts           DOCUMENT ME!
     * @param   description  DOCUMENT ME!
     * @param   ending       DOCUMENT ME!
     * @param   imageData    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static String writeImage(final String prefix,
            final Long ts,
            final String description,
            final String ending,
            final String imageData) throws Exception {
        WebDavClient webDavClient = null;
        FileOutputStream fos = null;
        final String tsString = dateFromTimestamp(ts);
        final File tempFile = File.createTempFile(description, "." + ending);

        try {
            if ((prefix == null) || !prefix.toLowerCase().equals("dev")) {
                final String pre = ((prefix == null) ? FILE_PREFIX : prefix);
                final String webFileName = WebDavHelper.generateWebDAVFileName(pre, tempFile);
                fos = new FileOutputStream(tempFile);
                fos.write(convertFileDataToBytes(imageData));

                final BelisWebdavProperties properties = BelisWebdavProperties.load();
                final String webDavRoot = properties.getUrl();

                if (webDavClient == null) {
                    final String user = properties.getUsername();
                    String pass = properties.getPassword();

                    if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
                        pass = PasswordEncrypter.decryptString(pass);
                    }
                    webDavClient = new WebDavClient(ProxyHandler.getInstance().getProxy(), user, pass);
                }

                WebDavHelper.uploadFileToWebDAV(
                    webFileName,
                    tempFile,
                    webDavRoot,
                    webDavClient,
                    null);

                return webDavRoot + webFileName + "\n" + description + tsString;
            } else {
                final UploadConfig config = ServerResourcesLoader.getInstance()
                            .loadJson(new TextServerResource("/imageUpload/config.json"),
                                UploadConfig.class);

                if (!config.useDefaultWebdav) {
                    final String webFileName = WebDavHelper.generateWebDAVFileName(LOCAL_FILE_PREFIX, tempFile);
                    String rootPath = config.getPath();

                    if (!rootPath.endsWith("/")) {
                        rootPath += "/";
                    }

                    fos = new FileOutputStream(new File(rootPath, webFileName));
                    fos.write(convertFileDataToBytes(imageData));

                    return rootPath + webFileName + "\n" + description + tsString;
                } else {
                    final String pre = ((prefix == null) ? LOCAL_FILE_PREFIX : prefix);
                    final String webDavPath = (config.getPath().endsWith("/") ? config.getPath()
                                                                              : (config.getPath() + "/"));
                    final String webFileName = WebDavHelper.generateWebDAVFileName(pre, tempFile);
                    fos = new FileOutputStream(tempFile);
                    fos.write(convertFileDataToBytes(imageData));

                    final BelisWebdavProperties properties = BelisWebdavProperties.load();
                    final String webDavRoot = properties.getUrl();

                    if (webDavClient == null) {
                        final String user = properties.getUsername();
                        String pass = properties.getPassword();

                        if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
                            pass = PasswordEncrypter.decryptString(pass);
                        }
                        webDavClient = new WebDavClient(ProxyHandler.getInstance().getProxy(), user, pass);
                    }

                    WebDavHelper.uploadFileToWebDAV(
                        webDavPath
                                + webFileName,
                        tempFile,
                        webDavRoot,
                        webDavClient,
                        null);

                    return webDavRoot + webFileName + "\n" + description + tsString;
                }
            }
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                LOG.fatal(ex, ex);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  args  DOCUMENT ME!
     */
    public static void main(final String[] args) {
        System.out.println("time:" + dateFromTimestamp(1634234133025L));

//        try {
//            final String file = "/home/therter/tmp/image.base";
//            final BufferedReader fis = new BufferedReader(new FileReader(file));
//            String tmp;
//            final StringBuilder base64String = new StringBuilder();
//
//            while ((tmp = fis.readLine()) != null) {
//                base64String.append(tmp);
//            }
//
//            fis.close();
//
//            final FileOutputStream fout = new FileOutputStream("/home/therter/tmp/image.jpg");
//
//            fout.write(convertFileDataToBytes(base64String.toString()));
//
//            fout.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   ts  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String dateFromTimestamp(final Long ts) {
        if (ts == null) {
            return "";
        } else {
            final GregorianCalendar gc = new GregorianCalendar();
            final StringBuilder sb = new StringBuilder(13);

            gc.setTimeInMillis(ts);
            sb.append(" (")
                    .append(gc.get(GregorianCalendar.DAY_OF_MONTH))
                    .append(".")
                    .append(gc.get(GregorianCalendar.MONTH) + 1)
                    .append(".")
                    .append(gc.get(GregorianCalendar.YEAR))
                    .append(")");

            return sb.toString();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   imageData  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static byte[] convertFileDataToBytes(final String imageData) {
        String base64String;

        if (imageData.indexOf("base64,") != -1) {
            base64String = imageData.substring(imageData.indexOf("base64,") + "base64,".length());
        } else {
            base64String = imageData;
        }

        final Base64.Decoder decoder = Base64.getDecoder();

        return decoder.decode(base64String);
    }

    @Override
    public String getTaskName() {
        return "uploadDocument";
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    @Getter
    @Setter
    private static class UploadConfig {

        //~ Instance fields ----------------------------------------------------

        boolean useDefaultWebdav;
        String path;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new UploadConfig object.
         */
        public UploadConfig() {
        }
    }
}
