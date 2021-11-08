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

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.Base64;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;

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
        final int objectId = (Integer)getParam(AddDokumentServerAction.ParameterType.OBJEKT_ID.toString(),
                Integer.class);
        final String className = (String)getParam(AddDokumentServerAction.ParameterType.OBJEKT_TYP.toString(),
                String.class);

        try {
            final String endung = (String)getParam(ParameterType.Ending.toString(), String.class);
            final String beschreibung = (String)getParam(ParameterType.Description.toString(), String.class);
            final Long ts = (Long)getParam(ParameterType.TS.toString(), Long.class);

            final String documentUrl = writeImage(dev, ts, beschreibung, endung, imageData, objectId, className);

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
     * @param   objectId     DOCUMENT ME!
     * @param   className    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static String writeImage(final String prefix,
            final Long ts,
            final String description,
            final String ending,
            final String imageData,
            final Integer objectId,
            final String className) throws Exception {
        FileOutputStream fos = null;
        final String tsString = dateFromTimestamp(ts);

        try {
            if ((prefix == null) || !prefix.toLowerCase().equals("dev")) {
                final String pre = ((prefix == null) ? FILE_PREFIX : prefix);
                final String webFileName = createFileName(pre, className, objectId, ending); //
                                                                                             // WebDavHelper.generateWebDAVFileName(pre,
                                                                                             // tempFile);
                final BelisWebdavProperties properties = BelisWebdavProperties.load();
                final String webDavRoot = properties.getUrl();

                final File tempFile = uploadToWebDav(imageData, null, webFileName, ending);

                if (ending.equals("jpg") || ending.equals("png")) {
                    final byte[] bytes = createThumbnail(tempFile, ending);

                    uploadToWebDav(imageData, bytes, webFileName, ending);
                }

                return webDavRoot + webFileName + "\n" + description + tsString;
            } else {
                final UploadConfig config = ServerResourcesLoader.getInstance()
                            .loadJson(new TextServerResource("/imageUpload/config.json"),
                                UploadConfig.class);

                if (!config.useDefaultWebdav) {
                    final String webFileName = createFileName(LOCAL_FILE_PREFIX, className, objectId, ending);
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
                    final String webFileName = createFileName(pre, className, objectId, ending);
                    final BelisWebdavProperties properties = BelisWebdavProperties.load();
                    final String webDavRoot = properties.getUrl();

                    final File tempFile = uploadToWebDav(imageData, null, webDavPath + webFileName, ending);

                    if (ending.equals("jpg") || ending.equals("png")) {
                        final byte[] bytes = createThumbnail(tempFile, ending);

                        uploadToWebDav(imageData, bytes, webDavPath + webFileName + ".thumbnail." + ending, ending);
                    }

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
     * @param   prefix  DOCUMENT ME!
     * @param   type    DOCUMENT ME!
     * @param   id      DOCUMENT ME!
     * @param   ending  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String createFileName(final String prefix,
            final String type,
            final Integer id,
            final String ending) {
        // ${prefix}.${featuretype des fachobjektes}.${id des fachobjektes}.${zufallsekram}.${endung}
        return prefix + "." + type + "." + id + "." + System.currentTimeMillis() + "." + ending;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   imageData     DOCUMENT ME!
     * @param   imageAsBytes  DOCUMENT ME!
     * @param   webFileName   DOCUMENT ME!
     * @param   ending        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static File uploadToWebDav(final String imageData,
            final byte[] imageAsBytes,
            final String webFileName,
            final String ending) throws Exception {
        final File tempFile = File.createTempFile("file", "." + ending);
        FileOutputStream fos = null;
        WebDavClient webDavClient = null;
        byte[] imageDataAsByteA = imageAsBytes;

        if (imageDataAsByteA == null) {
            imageDataAsByteA = convertFileDataToBytes(imageData);
        }

        try {
            fos = new FileOutputStream(tempFile);
            fos.write(imageDataAsByteA);
            fos.close();
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

            final int httpStatusCode = WebDavHelper.uploadFileToWebDAV(
                    webFileName,
                    tempFile,
                    webDavRoot,
                    webDavClient,
                    null);

            if ((int)(httpStatusCode / 100) != 2) {
                throw new Exception("Cannot upload image. Status code = " + httpStatusCode);
            }
        } finally {
            if (fos != null) {
                fos.close();
            }
        }

        return tempFile;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   tempFile  DOCUMENT ME!
     * @param   ending    DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static byte[] createThumbnail(final File tempFile, final String ending) throws Exception {
        final Image img = ImageIO.read(tempFile);
        int height = img.getHeight(null);
        int width = img.getWidth(null);

        if (height > width) {
            if (height > 600) {
                width = (int)(width * 600.0 / height);
                height = 600;
            }
        } else {
            if (width > 600) {
                height = (int)(height * 600.0 / width);
                width = 600;
            }
        }

        final BufferedImage imgThumb = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        imgThumb.createGraphics().drawImage(img.getScaledInstance(width, height, Image.SCALE_SMOOTH),
            0,
            0,
            null);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(imgThumb, ending, os);

        return os.toByteArray();
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
