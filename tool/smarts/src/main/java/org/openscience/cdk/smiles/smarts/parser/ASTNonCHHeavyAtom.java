/* Generated By:JJTree: Do not edit this line. ASTNonCHHeavyAtom.java */

package org.openscience.cdk.smiles.smarts.parser;

/**
 * An AST node. It represents any non-C heavy atom.
 *
 * This is not specified in the original Daylight specification, but
 * is support by MOE
 *
 * @author Rajarshi Guha
 * @cdk.created 2008-10-14
 * @cdk.module smarts
 * @cdk.githash
 * @cdk.keyword SMARTS AST
 */

class ASTNonCHHeavyAtom extends SimpleNode {

    public ASTNonCHHeavyAtom(int id) {
        super(id);
    }

    public ASTNonCHHeavyAtom(SMARTSParser p, int id) {
        super(p, id);
    }

    /** Accept the visitor. **/
    @Override
    public Object jjtAccept(SMARTSParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
