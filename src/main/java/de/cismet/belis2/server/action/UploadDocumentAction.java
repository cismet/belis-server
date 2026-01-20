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

import Sirius.server.newuser.User;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import de.cismet.belis2.server.utils.BelisWebdavProperties;

import de.cismet.cids.dynamics.CidsBean;

import de.cismet.cids.server.actions.ServerAction;
import de.cismet.cids.server.actions.ServerActionParameter;
import de.cismet.cids.server.actions.UserAwareServerAction;

import de.cismet.commons.security.WebDavClient;
import de.cismet.commons.security.WebDavHelper;

import de.cismet.connectioncontext.AbstractConnectionContext;
import de.cismet.connectioncontext.ConnectionContext;

import de.cismet.netutil.ProxyHandler;

import de.cismet.tools.ExifReader;
import de.cismet.tools.PasswordEncrypter;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
@org.openide.util.lookup.ServiceProvider(service = ServerAction.class)
public class UploadDocumentAction implements ServerAction, UserAwareServerAction {

    //~ Static fields/initializers ---------------------------------------------

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UploadDocumentAction.class);
    private static final ConnectionContext CC = ConnectionContext.create(
            AbstractConnectionContext.Category.ACTION,
            "UploadTzbAction");
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    //~ Enums ------------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public enum ParameterType {

        //~ Enum constants -----------------------------------------------------

        data, name
    }

    //~ Instance fields --------------------------------------------------------

    protected final Map<String, Object> paramsHashMap = new HashMap<>();
    private User usr = null;

    //~ Methods ----------------------------------------------------------------

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
        String data = null;
        String name = null;
        String resultUrlAsString = null;

        for (final ServerActionParameter param : params) {
            final String key = param.getKey().toLowerCase();
            final Object value = param.getValue();

            if (key.equalsIgnoreCase(ParameterType.data.name())) {
                data = value.toString();
            } else if (key.equalsIgnoreCase(ParameterType.name.name())) {
                name = value.toString();
            }
        }

        try {
            if (data instanceof String) {
                final String imageData = (String)data;

                if (imageData != null) {
                    try {
                        resultUrlAsString = writeImage(imageData, name);

                        final CidsBean url = AbstractBelisServerActionV3.createDmsURLFromLink(resultUrlAsString, name);

                        return url.toJSONString(true);
                    } catch (final Exception ex) {
                        LOG.error(ex, ex);
                        return "{\"error\":, \"" + ex.getMessage() + "\"}";
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Cannot create tzb_tree_action cids bean", e);

            return "{\"error\":, \"" + e.getMessage() + "\"}";
        }

        return "{\"error\":, \"no data\"}";
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
     * @param   key    DOCUMENT ME!
     * @param   clazz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected Object getParam(final String key, final Class clazz) {
        final Collection values = getListParam(key, clazz);
        if ((values == null) || values.isEmpty()) {
            return null;
        } else {
            return values.iterator().next();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   key    DOCUMENT ME!
     * @param   clazz  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  UnsupportedOperationException  DOCUMENT ME!
     */
    protected Collection getListParam(final String key, final Class clazz) {
        final Collection objects = new ArrayList();
        if (paramsHashMap.containsKey(key.toLowerCase())) {
            for (final Object val : (List)paramsHashMap.get(key.toLowerCase())) {
                Object object = null;

                if (val instanceof String) {
                    final String value = (String)val;

                    if (Date.class.equals(clazz)) {
                        final long timestamp = Long.parseLong(value);
                        object = new Date(timestamp);
                    } else if (java.sql.Date.class.equals(clazz)) {
                        final long timestamp = Long.parseLong(value);
                        object = new java.sql.Date(timestamp);
                    } else if (Timestamp.class.equals(clazz)) {
                        final long timestamp = Long.parseLong(value);
                        object = new Timestamp(timestamp);
                    } else if (Integer.class.equals(clazz)) {
                        object = Integer.parseInt(value);
                    } else if (Float.class.equals(clazz)) {
                        object = Float.parseFloat(value);
                    } else if (Long.class.equals(clazz)) {
                        object = Long.parseLong(value);
                    } else if (Double.class.equals(clazz)) {
                        object = Double.parseDouble(value);
                    } else if (Boolean.class.equals(clazz)) {
                        if ("ja".equals(value.toLowerCase())) {
                            object = true;
                        } else if ("nein".equals(value.toLowerCase())) {
                            object = false;
                        } else {
                            throw new UnsupportedOperationException("wrong boolean value");
                        }
                    } else if (String.class.equals(clazz)) {
                        object = value;
                    } else {
                        throw new UnsupportedOperationException("this class is not supported");
                    }
                } else {
                    if (val == null) {
                        object = null;
                    } else if (ArrayList.class.equals(clazz)) {
                        object = val;
                    }
                }

                objects.add(object);
            }
        } else {
            return objects;
        }
        return objects;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   imageData  DOCUMENT ME!
     * @param   name       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static String writeImage(final String imageData, final String name) throws Exception {
        final FileOutputStream fos = null;
        try {
            final BelisWebdavProperties properties = BelisWebdavProperties.load();
            String ending = "";

            if (name.lastIndexOf(".") != -1) {
                ending = name.substring(name.lastIndexOf(".") + 1);
            }

            final String webDavRoot = properties.getUrl();
            final String webFileName = createFileName(ending);

            final File tempFile = uploadToWebDav(
                    webDavRoot,
                    properties.getUsername(),
                    properties.getPassword(),
                    imageData,
                    null,
                    webFileName,
                    ending);

            if (ending.equals("jpg") || ending.equals("png")) {
                final byte[] bytes = createThumbnail(tempFile, ending);

                uploadToWebDav(
                    webDavRoot,
                    properties.getUsername(),
                    properties.getPassword(),
                    imageData,
                    bytes,
                    webFileName
                            + ".thumbnail."
                            + ending,
                    ending);
            }

            return webDavRoot + webFileName;
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
     * @param   ending  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String createFileName(final String ending) {
        final Random rand = new Random();
        return "IMG-" + rand.nextInt() + "_" + System.currentTimeMillis() + "." + ending;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   webDavRoot    DOCUMENT ME!
     * @param   user          DOCUMENT ME!
     * @param   passwd        DOCUMENT ME!
     * @param   imageData     DOCUMENT ME!
     * @param   imageAsBytes  DOCUMENT ME!
     * @param   webFileName   DOCUMENT ME!
     * @param   ending        DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static File uploadToWebDav(final String webDavRoot,
            final String user,
            final String passwd,
            final String imageData,
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

            if (webDavClient == null) {
                String pass = passwd;

                if ((pass != null) && pass.startsWith(PasswordEncrypter.CRYPT_PREFIX)) {
                    pass = PasswordEncrypter.decryptString(passwd);
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
     * @param   src      tempFile DOCUMENT ME!
     * @param   degrees  ending DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  IllegalArgumentException  Exception DOCUMENT ME!
     */
// private static byte[] createThumbnail(final File tempFile, final String ending) throws Exception {
// final Image img = ImageIO.read(tempFile);
// final int height = img.getHeight(null);
// final int width = img.getWidth(null);
// final int longestSide = Math.max(width, height);
// double scale = 1;
//
// // set longest side to 600 if it is longer
// if (longestSide > 600) {
// scale = 600.0 / longestSide;
// }
//
// final BufferedImage imgThumb = new BufferedImage((int)(width * scale),
// (int)(height * scale),
// BufferedImage.TYPE_INT_RGB);
//
// imgThumb.createGraphics()
// .drawImage(img.getScaledInstance((int)(width * scale), (int)(height * scale), Image.SCALE_SMOOTH),
// 0,
// 0,
// null);
// final ByteArrayOutputStream os = new ByteArrayOutputStream();
// ImageIO.write(imgThumb, ending, os);
//
// return os.toByteArray();
// }

    private static BufferedImage rotate(final BufferedImage src, final int degrees) {
        if ((degrees % 360) == 0) {
            return src;
        }

        final double rads = Math.toRadians(degrees);
        final int srcW = src.getWidth();
        final int srcH = src.getHeight();

        final int dstW = ((degrees == 90) || (degrees == 270)) ? srcH : srcW;
        final int dstH = ((degrees == 90) || (degrees == 270)) ? srcW : srcH;

        final BufferedImage dst = new BufferedImage(dstW, dstH, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = dst.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // Transformation:
        // erst verschieben, dann rotieren
        switch (degrees) {
            case 90: {
                g.translate(dstW, 0);
                break;
            }
            case 180: {
                g.translate(dstW, dstH);
                break;
            }
            case 270: {
                g.translate(0, dstH);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported rotation: " + degrees);
            }
        }

        g.rotate(rads);
        g.drawImage(src, 0, 0, null);
        g.dispose();

        return dst;
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
    public static byte[] createThumbnail(final File tempFile, final String ending) throws Exception {
        final BufferedImage imgOrig = ImageIO.read(tempFile);

        final ExifReader reader = new ExifReader(tempFile);
        Double rotation = reader.getOrientationRotation();

        if (rotation == null) {
            rotation = 0.0;
        }

        final BufferedImage orientedImg = rotate(imgOrig, rotation.intValue());

        final int height = orientedImg.getHeight(null);
        final int width = orientedImg.getWidth(null);
        final int longestSide = Math.max(width, height);
        double scale = 1;

        // set longest side to 600 if it is longer
        if (longestSide > 600) {
            scale = 600.0 / longestSide;
        }

        final int swidth = (int)Math.round(width * scale);
        final int sheight = (int)Math.round(height * scale);

        final BufferedImage imgThumb = new BufferedImage(swidth, sheight, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = imgThumb.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(orientedImg, 0, 0, swidth, sheight, null);
        g.dispose();

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
        try {
            final byte[] thumb = createThumbnail(new File("/home/therter/Downloads/IMG-887392315_1768496235662.jpg"),
                    "jpg");

            final FileOutputStream fos = new FileOutputStream(
                    "/home/therter/Downloads/IMG-887392315_1768496235662.jpg.t.jpg");

            fos.write(thumb);

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
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
        return "uploadBelisDocument";
    }
}
