
package com.sri.java.expression;

import java.io.*;
import com.sri.java.*;
import com.sri.java.types.*;
import com.sri.java.utils.*;

/**
 * This class if for return throw and break unary expressions.
 */
public class IfElseExpression extends VoidExpression
{
        /**
         * The match expression.
         */
    BooleanExpression matchExp;

        /**
         * The expression that is run if the match expression
         * is met.
         */
    public Expression matchStmt= null;

        /**
         * The else statement.
         */
    public Expression elseStmt = null;

        /**
         * Constructor.
         */
    protected IfElseExpression()
    {
    }

        /**
         * Constructor.
         */
    public IfElseExpression(BooleanExpression matchExp, Expression stmt)
    {
        this(matchExp, stmt, null);
    }

        /**
         * Constructor.
         */
    public IfElseExpression(BooleanExpression matchExp,
                                 Expression stmt, 
                                 Expression elseStmt)
    {
        this.matchExp = matchExp;
        this.matchStmt = stmt;
        this.elseStmt = elseStmt;
    }
}
