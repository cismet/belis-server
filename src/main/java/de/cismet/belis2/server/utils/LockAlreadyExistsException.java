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
package de.cismet.belis2.server.utils;

import Sirius.server.middleware.types.MetaObjectNode;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DOCUMENT ME!
 *
 * @author   spuhl
 * @version  $Revision$, $Date$
 */
@XmlRootElement
public class LockAlreadyExistsException extends Exception implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private final Collection<MetaObjectNode> alreadyExisingLocks = new ArrayList<MetaObjectNode>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LockAlreadyExistsException object.
     *
     * @param  message               DOCUMENT ME!
     * @param  alreadyExistingLocks  DOCUMENT ME!
     */
    public LockAlreadyExistsException(final String message,
            final Collection<MetaObjectNode> alreadyExistingLocks) {
        super(message);
        if (alreadyExistingLocks != null) {
            alreadyExisingLocks.addAll(alreadyExistingLocks);
        }
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<MetaObjectNode> getAlreadyExisingLocks() {
        return alreadyExisingLocks;
    }
}
