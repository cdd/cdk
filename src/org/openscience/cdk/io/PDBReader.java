/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.io;

import org.openscience.cdk.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.tools.LoggingTool;
import java.io.*;
import java.util.*;
import javax.vecmath.*;

/**
 * Reads the contents of a PDBFile.
 *
 * <p>A description can be found at <a href="http://www.rcsb.org/pdb/docs/format/pdbguide2.2/guide2.2_frame.html">
 * http://www.rcsb.org/pdb/docs/format/pdbguide2.2/guide2.2_frame.html</a>.
 *
 * @author     Edgar Luttmann
 * @author     Bradley Smith (bradley@baysmith.com)
 * @created    2001-08-06 
 *
 * @keyword file format, PDB
 */
public class PDBReader extends DefaultChemObjectReader {

  private org.openscience.cdk.tools.LoggingTool logger;
  private BufferedReader _oInput; // The internal used BufferedReader

  /**
   *
   * Contructs a new PDBReader that can read Molecules from a given
   * InputStream.
   *
   * @param oIn  The InputStream to read from
   *
   */
  public PDBReader(InputStream oIn) {
    logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    _oInput = new BufferedReader(new InputStreamReader(oIn));
  }

  /**
   *
   * Contructs a new PDBReader that can read Molecules from a given
   * Reader.
   *
   * @param oIn  The Reader to read from
   *
   */
  public PDBReader(Reader oIn) {
    logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    _oInput = new BufferedReader(oIn);
  }

  /**
   *
   * Takes an object which subclasses ChemObject, e.g. Molecule, and will
   * read this (from file, database, internet etc). If the specific
   * implementation does not support a specific ChemObject it will throw
   * an Exception.
   *
   * @param oObj  The object that subclasses ChemObject
   * @return      The ChemObject read  
   * @exception   CDKException  
   *
   */
  public ChemObject read(ChemObject oObj) throws CDKException {
    if (oObj instanceof ChemFile) {
      return (ChemObject)readChemFile();
    } else {
      throw new CDKException("Only supported is reading of ChemFile objects.");
    }
  }

  /**
   * Read a <code>ChemFile</code> from a file in PDB format. The molecules
   * in the file are stored as <code>BioPolymer</code>s in the
   * <code>ChemFile</code>. The residues are the monomers of the
   * <code>BioPolymer</code>, and their names are the concatenation of the
   * residue, chain id, and the sequence number. Separate chains (denoted by
   * TER records) are stored as separate <code>BioPolymer</code> molecules.
   *
   * <p>Connectivity information is not currently read.
   *
   * @return The ChemFile that was read from the PDB file.
   */
  private ChemFile readChemFile() 	{
    // initialize all containers
    ChemFile oFile = new ChemFile();
    ChemSequence oSeq = new ChemSequence();
    ChemModel oModel = new ChemModel();
    SetOfMolecules oSet = new SetOfMolecules();
		
    // some variables needed
    String cLine;
    String cCol;
    Atom oAtom;
    BioPolymer oBP = new BioPolymer();
    Object oObj;
    Monomer oMonomer;
    String cRead;
		
    // do the reading of the Input		
    try {
      do {
        cRead = _oInput.readLine();
        logger.debug(cRead);
        if (cRead != null) {
          cLine = cRead;
          // check the first column to decide what to do
          if (cLine.startsWith("ATOM  ") || cLine.startsWith("HETATM")) {
            // read an atom record
            oAtom = readAtom(cLine.toString());
						
            // construct a string describing the residue
            String resName, chainID, resSeq;
            resName = (String)oAtom.getProperty("pdb.resName");
            if (resName == null)
              resName = "";
            chainID = (String)oAtom.getProperty("pdb.chainID");
            if (chainID == null)
              chainID = "";
            resSeq = (String)oAtom.getProperty("pdb.resSeq");
            if (resSeq == null)
              resSeq = "";
            String cResidue = resName + chainID + resSeq;
						
            // search for the existing monomer or create a new one
            oMonomer = oBP.getMonomer(cResidue);
            if (oMonomer == null) {
              oMonomer = new Monomer();
              oMonomer.setMonomerName(cResidue);
              oMonomer.setMonomerType((String)oAtom.getProperty("pdb.resName"));
            }
						
            // add the atom
            oBP.addAtom(oAtom, oMonomer);
          } else if (cLine.startsWith("TER   ")) {
            // finish the molecule and construct a new one
            oSet.addMolecule(oBP);
            oBP = new BioPolymer();
          } else if (cLine.startsWith("END   ")) {
            // finish the molecule and construct a new one
            if (oBP.getAtomCount() != 0) {
              oSet.addMolecule(oBP);
            }
            oBP = new BioPolymer();					
            //				} else if (cLine.startsWith("USER  ")) {
            //						System.out.println(cLine);
            //				} else if (cLine.startsWith("MODEL ")) {
            //					System.out.println(cLine);
            //				} else if (cLine.startsWith("ENDMDL")) {
            //					System.out.println(cLine);
          } else if (cLine.startsWith("HELIX ") ||
                     cLine.startsWith("SHEET ") ||
                     cLine.startsWith("TURN  ")) {
            Vector t = (Vector)oModel.getProperty("pdb.structure.records");
            if (t == null)
              oModel.setProperty("pdb.structure.records", t = new Vector());
            t.addElement(cLine);
          }
        }
      } while (_oInput.ready());
    } catch (Exception e) {
      e.printStackTrace();
    }

    // try to close the Input
    try {
      _oInput.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
	
    // Set all the dependencies
    oModel.setSetOfMolecules(oSet);
    oSeq.addChemModel(oModel);
    oFile.addChemSequence(oSeq);
		
    return oFile;
  }

  /**
   * Creates an <code>Atom</code> and sets properties to their values from
   * the ATOM record. If the line is shorter than 80 characters, the information
   * past 59 characters is treated as optional. If the line is shorter than 59
   * characters, a <code>RuntimeException</code> is thrown.
   *
   * @param cLine  the PDB ATOM record.
   * @return the <code>Atom</code> created from the record.
   * @throws RuntimeException if the line is too short (less than 59 characters).
   */
  private Atom readAtom(String cLine) {
    if (cLine.length() < 59) {
      throw new RuntimeException("PDBReader error during readAtom(): line too short");
    }
    String elementSymbol = cLine.substring(12, 14).trim();
    if (elementSymbol.length() == 2) {
      // ensure that the second char is lower case
      elementSymbol = elementSymbol.charAt(0) + elementSymbol.substring(1).toLowerCase();
    }
    Atom oAtom = new Atom(elementSymbol, 
                          new Point3d(new Double(cLine.substring(30, 38)).doubleValue(),
                                      new Double(cLine.substring(38, 46)).doubleValue(),
                                      new Double(cLine.substring(46, 54)).doubleValue()));
    oAtom.setProperty("pdb.record", cLine);
    oAtom.setProperty("pdb.serial", new Integer(cLine.substring(6, 11).trim()));
    oAtom.setProperty("pdb.name", (new String(cLine.substring(12, 16))).trim());
    oAtom.setProperty("pdb.altLoc", (new String(cLine.substring(16, 17))).trim());
    oAtom.setProperty("pdb.resName", (new String(cLine.substring(17, 20))).trim());
    oAtom.setProperty("pdb.chainID", (new String(cLine.substring(21, 22))).trim());
    oAtom.setProperty("pdb.resSeq", (new String(cLine.substring(22, 26))).trim());
    oAtom.setProperty("pdb.iCode", (new String(cLine.substring(26, 27))).trim());
    if (cLine.length() >= 59) {
      oAtom.setProperty("pdb.occupancy", new Double(cLine.substring(54, 60)));
    }
    if (cLine.length() >= 65) {
      oAtom.setProperty("pdb.tempFactor", new Double(cLine.substring(60, 66)));
    }
    if (cLine.length() >= 75) {
      oAtom.setProperty("pdb.segID", (new String(cLine.substring(72, 76))).trim());
    }
    if (cLine.length() >= 78) {
      oAtom.setProperty("pdb.element", (new String(cLine.substring(76, 78))).trim());
    }
    if (cLine.length() >= 79) {
      oAtom.setProperty("pdb.charge", (new String(cLine.substring(78, 80))).trim());
    }
  
    return oAtom;
  }

  public void close() throws IOException {
    _oInput.close();
  }
}
