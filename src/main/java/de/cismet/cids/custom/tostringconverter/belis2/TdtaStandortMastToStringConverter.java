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

import java.util.ArrayList;
import java.util.Collection;

import de.cismet.cids.dynamics.CidsBean;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class TdtaStandortMastToStringConverter extends WorkbenchEntityToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getKeyString(final CidsBean cidsBean) {
        final Collection<String> strings = new ArrayList<String>();
        final CidsBean masttyp = (CidsBean)cidsBean.getProperty("fk_masttyp");
        if ((masttyp != null) && (masttyp.getProperty("masttyp") != null)) {
            strings.add((String)masttyp.getProperty("masttyp"));
        }
        final CidsBean mastart = (CidsBean)cidsBean.getProperty("fk_mastart");
        if ((mastart != null) && (mastart.getProperty("mastart") != null)) {
            strings.add((String)mastart.getProperty("mastart"));
        }
        return implode(strings.toArray(new String[0]), ", ");
    }

    @Override
    public String getHumanReadablePosition(final CidsBean cidsBean) {
        final CidsBean strassenshluessel = (CidsBean)cidsBean.getProperty("fk_strassenschluessel");
        final String strasse = (strassenshluessel != null) ? (String)strassenshluessel.getProperty("strasse") : "";
        return strasse;
    }

    @Override
    public String createString() {
        return "Mast " + ((Integer)cidsBean.getProperty("lfd_nummer")).toString() + super.createString();
    }
}
