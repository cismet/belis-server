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
public class VeranlassungToStringConverter extends WorkbenchEntityToStringConverter {

    //~ Methods ----------------------------------------------------------------

    @Override
    public String getKeyString(final CidsBean cidsBean) {
        final Collection<String> strings = new ArrayList<String>();
        strings.add("V");
        if (cidsBean.getProperty("nummer") != null) {
            strings.add((String)cidsBean.getProperty("nummer"));
        }
        final CidsBean art = (CidsBean)cidsBean.getProperty("fk_art");
        if (art != null) {
            strings.add((String)art.getProperty("schluessel"));
        } else {
            strings.add("_");
        }
        return implode(strings.toArray(new String[0]), "");
    }

    @Override
    public String getHumanReadablePosition(final CidsBean cidsBean) {
        return null;
    }

    @Override
    public String createString() {
        return "Veranlassung" + super.createString();
    }
}
