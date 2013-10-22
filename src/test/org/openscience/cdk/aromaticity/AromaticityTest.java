package org.openscience.cdk.aromaticity;

import org.junit.Test;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.graph.Cycles;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author John May
 * @cdk.module test-standard
 */
public class AromaticityTest {

    private final Aromaticity cdk      = new Aromaticity(ElectronDonation.cdk(),
                                                         Cycles.all());
    private final Aromaticity cdkExo   = new Aromaticity(ElectronDonation.cdkAllowingExocyclic(),
                                                         Cycles.all());
    private final Aromaticity daylight = new Aromaticity(ElectronDonation.daylight(),
                                                         Cycles.all());

    @Test public void benzene() throws Exception {
        assertThat(cdk.findBonds(type(smiles("C1=CC=CC=C1"))).size(),
                   is(6));
        assertThat(daylight.findBonds(smiles("C1=CC=CC=C1")).size(),
                   is(6));
    }

    @Test public void furan() throws Exception {
        assertThat(cdk.findBonds(type(smiles("C1=CC=CO1"))).size(),
                   is(5));
        assertThat(daylight.findBonds(smiles("C1=CC=CO1")).size(),
                   is(5));
    }

    @Test public void quinone() throws Exception {
        assertThat(cdk.findBonds(type(smiles("O=C1C=CC(=O)C=C1"))).size(),
                   is(0));
        assertThat(cdkExo.findBonds(type(smiles("O=C1C=CC(=O)C=C1"))).size(),
                   is(6));
        assertThat(daylight.findBonds(smiles("O=C1C=CC(=O)C=C1")).size(),
                   is(0));
    }

    @Test public void azulene() throws Exception {
        assertThat(cdk.findBonds(type(smiles("C1=CC2=CC=CC=CC2=C1"))).size(),
                   is(10));
        assertThat(daylight.findBonds(smiles("C1=CC2=CC=CC=CC2=C1")).size(),
                   is(10));
    }

    // 4-oxo-1H-pyridin-1-ide
    @Test public void oxypyridinide() throws Exception {
        assertThat(cdk.findBonds(type(smiles("O=C1C=C[N-]C=C1"))).size(),
                   is(0));
        assertThat(cdkExo.findBonds(type(smiles("O=C1C=C[N-]C=C1"))).size(),
                   is(0));
        assertThat(daylight.findBonds(smiles("O=C1C=C[N-]C=C1")).size(),
                   is(6));
    }

    // 2-Pyridone
    @Test public void pyridinone() throws Exception {
        assertThat(cdk.findBonds(type(smiles("O=C1NC=CC=C1"))).size(),
                   is(0));
        assertThat(cdkExo.findBonds(type(smiles("O=C1C=C[N-]C=C1"))).size(),
                   is(0));
        assertThat(daylight.findBonds(smiles("O=C1NC=CC=C1")).size(),
                   is(6));
    }

    @Test public void clearFlags_cyclobutadiene() throws Exception {
        IAtomContainer cyclobutadiene = smiles("c1ccc1");
        daylight.apply(cyclobutadiene);
        for (IBond bond : cyclobutadiene.bonds())
            assertFalse(bond.getFlag(CDKConstants.ISAROMATIC));
        for (IAtom atom : cyclobutadiene.atoms())
            assertFalse(atom.getFlag(CDKConstants.ISAROMATIC));
    }

    @Test public void clearFlags_quinone() throws Exception {
        IAtomContainer quinone = smiles("O=c1ccc(=O)cc1");
        daylight.apply(quinone);
        for (IBond bond : quinone.bonds())
            assertFalse(bond.getFlag(CDKConstants.ISAROMATIC));
        for (IAtom atom : quinone.atoms())
            assertFalse(atom.getFlag(CDKConstants.ISAROMATIC));
    }

    @Test public void validSum() throws Exception {
        // aromatic
        assertTrue(Aromaticity.validSum(2));
        assertTrue(Aromaticity.validSum(6));
        assertTrue(Aromaticity.validSum(10));
        assertTrue(Aromaticity.validSum(14));
        assertTrue(Aromaticity.validSum(18));

        // anti-aromatic
        assertFalse(Aromaticity.validSum(4));
        assertFalse(Aromaticity.validSum(8));
        assertFalse(Aromaticity.validSum(12));
        assertFalse(Aromaticity.validSum(16));
        assertFalse(Aromaticity.validSum(20));

        // other numbers
        assertFalse(Aromaticity.validSum(0));
        assertFalse(Aromaticity.validSum(1));
        assertFalse(Aromaticity.validSum(3));
        assertFalse(Aromaticity.validSum(5));
        assertFalse(Aromaticity.validSum(7));
        assertFalse(Aromaticity.validSum(9));
        assertFalse(Aromaticity.validSum(11));
        assertFalse(Aromaticity.validSum(13));
        assertFalse(Aromaticity.validSum(15));
    }

    @Test public void electronSum() throws Exception {
        assertThat(Aromaticity.electronSum(new int[]{0, 1, 2, 3, 0},
                                           new int[]{1, 1, 1, 1}),
                   is(4));
    }

    // make sure negative values are checked
    @Test public void electronSum_negative() throws Exception {
        assertThat(Aromaticity.electronSum(new int[]{0, 1, 2, 3, 0},
                                           new int[]{1, 1, -1, 1}),
                   is(0));
    }

    static IAtomContainer smiles(String smi) throws Exception {
        return new SmilesParser(SilentChemObjectBuilder.getInstance()).parseSmiles(smi);
    }

    static IAtomContainer type(IAtomContainer molecule) throws Exception {
        AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molecule);
        return molecule;
    }
}
