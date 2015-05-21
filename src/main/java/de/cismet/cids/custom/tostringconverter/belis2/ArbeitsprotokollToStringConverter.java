/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 *  Copyright (C) 2010 thorsten
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cismet.cids.custom.tostringconverter.belis2;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class ArbeitsprotokollToStringConverter extends WorkbenchEntityToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getKeyString(final CidsBean cidsBean) {
        final String type;
        final CidsBean entity;
        if (cidsBean.getProperty("fk_abzweigdose") != null) {
            entity = (CidsBean)cidsBean.getProperty("fk_abzweigdose");
            type = "Abzweigdose";
        } else if (cidsBean.getProperty("fk_leitung") != null) {
            entity = (CidsBean)cidsBean.getProperty("fk_leitung");
            type = "Leitung";
        } else if (cidsBean.getProperty("fk_leuchte") != null) {
            entity = (CidsBean)cidsBean.getProperty("fk_leuchte");
            type = "Leuchte";
        } else if (cidsBean.getProperty("fk_mauerlasche") != null) {
            entity = (CidsBean)cidsBean.getProperty("fk_mauerlasche");
            type = "Mauerlasche";
        } else if (cidsBean.getProperty("fk_schaltstelle") != null) {
            entity = (CidsBean)cidsBean.getProperty("fk_schaltstelle");
            type = "Schaltstelle";
        } else if (cidsBean.getProperty("fk_geometrie") != null) {
            entity = (CidsBean)cidsBean.getProperty("fk_geometrie");
            type = "Geometrie";
        } else if (cidsBean.getProperty("fk_standort") != null) {
            final CidsBean standort = (CidsBean)cidsBean.getProperty("fk_standort");
            entity = standort;

            final boolean isStandortMast = ((cidsBean.getProperty("fk_mastart") != null)
                            || (cidsBean.getProperty("fk_masttyp") != null)
                            || (cidsBean.getProperty("mastanstrich") != null)
                            || (cidsBean.getProperty("mastschutz") != null)
                            || (cidsBean.getProperty("ist_virtueller_standort") == null)
                            || ((cidsBean.getProperty("ist_virtueller_standort") != null)
                                && !((Boolean)cidsBean.getProperty("ist_virtueller_standort"))));

            if (isStandortMast) {
                type = "Mast";
            } else {
                type = "Standort";
            }
        } else {
            return "";
        }

        final String prefix = ((entity.getProperty("is_deleted") != null) && (Boolean)entity.getProperty("is_deleted"))
            ? "<html><strike>" : "";
        final String suffix;
        if (cidsBean.getProperty("veranlassungsschluessel") != null) {
            suffix = " (" + (String)cidsBean.getProperty("veranlassungsschluessel") + ")";
        } else {
            suffix = "";
        }
        return prefix + type + suffix;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   cidsBean  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public String getHumanReadablePosition(final CidsBean cidsBean) {
        if (cidsBean.getProperty("fk_abzweigdose") != null) {
            return
                new AbzweigdoseToStringConverter().convert(((CidsBean)cidsBean.getProperty("fk_abzweigdose"))
                            .getMetaObject());
        } else if (cidsBean.getProperty("fk_leitung") != null) {
            return new LeitungToStringConverter().convert(((CidsBean)cidsBean.getProperty("fk_leitung"))
                            .getMetaObject());
        } else if (cidsBean.getProperty("fk_leuchte") != null) {
            return
                new TdtaLeuchtenToStringConverter().convert(((CidsBean)cidsBean.getProperty("fk_leuchte"))
                            .getMetaObject());
        } else if (cidsBean.getProperty("fk_mauerlasche") != null) {
            return
                new MauerlascheToStringConverter().convert(((CidsBean)cidsBean.getProperty("fk_mauerlasche"))
                            .getMetaObject());
        } else if (cidsBean.getProperty("fk_schaltstelle") != null) {
            return
                new SchaltstelleToStringConverter().convert(((CidsBean)cidsBean.getProperty("fk_schaltstelle"))
                            .getMetaObject());
        } else if (cidsBean.getProperty("fk_standort") != null) {
            return
                new TdtaStandortMastToStringConverter().convert(((CidsBean)cidsBean.getProperty("fk_standort"))
                            .getMetaObject());
        } else {
            return "";
        }
    }
}
