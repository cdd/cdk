/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.test.qsar;

import org.openscience.cdk.qsar.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.qsar.result.*;
import java.io.*;

/**
 * @cdk.module test
 */
public class VdWRadiusDescriptorTest extends TestCase {
	
	public  VdWRadiusDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(VdWRadiusDescriptorTest.class);
	}
	
	public void testVdWRadiusDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={1.7};
		Descriptor descriptor = new VdWRadiusDescriptor();
		Object[] params = {new Integer(1)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("NCCN(C)(C)"); 
		double retval = ((DoubleResult)descriptor.calculate(mol)).doubleValue();

		assertEquals(testResult[0], retval, 0.01);
	}
}

