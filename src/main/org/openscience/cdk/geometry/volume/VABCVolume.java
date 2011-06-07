/* Copyright (C) 2011  Egon Willighagen <egonw@users.sf.net>
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA. 
 */
package org.openscience.cdk.geometry.volume;

import java.util.HashMap;
import java.util.Map;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

/**
 * Calculates the Vanderwaals volume using the method proposed
 * in {@cdk.cite Zhao2003}. The method is limited to molecules
 * with the following elements: H, C, N, O, F, Cl, Br, I,
 * P, S, As, B, Si, Se, and Te.
 *
 * @cdk.module   standard
 * @cdk.keywords volume, molecular
 */
@TestClass("org.openscience.cdk.geometry.volume.VABCVolumeTest")
public class VABCVolume {

    @SuppressWarnings("serial")
    private static Map<String,Double> bondiiVolumes = new HashMap<String, Double>() {{
        put("H", 7.24);
        put("C", 20.58);
    }};

    private static AtomTypeFactory atomTypeList = AtomTypeFactory.getInstance(
        "org/openscience/cdk/dict/data/cdk-atom-types.owl",
        NoNotificationChemObjectBuilder.getInstance()
    );

    /**
     * Calculates the volume for the given {@link IMolecule}. This methods assumes
     * that atom types have been perceived.
     *
     * @param  molecule {@link IMolecule} to calculate the volume of.
     * @return          the volume in cubic &Aring;ngstr&ouml;m.
     */
    public static double calculate(IMolecule molecule) throws CDKException {
        double sum = 0.0;
        int totalHCount = 0;
        for (IAtom atom : molecule.atoms()) {
            Double bondiiVolume = bondiiVolumes.get(atom.getSymbol());
            if (bondiiVolume == null)
                throw new CDKException("Unsupported element.");

            sum += bondiiVolume;

            // add volumes of implicit hydrogens?
            IAtomType type =  atomTypeList.getAtomType(atom.getAtomTypeName());
            int hCount = type.getFormalNeighbourCount() -
                molecule.getConnectedAtomsCount(atom);
            sum += (hCount * bondiiVolumes.get("H"));
            totalHCount += hCount;
        }
        sum -= 5.92 * (molecule.getBondCount() + totalHCount);
        return sum;
    }

}