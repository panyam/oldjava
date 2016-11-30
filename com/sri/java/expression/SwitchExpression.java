package com.sri.java.expression;

import java.io.*;
import com.sri.java.*;
import com.sri.java.types.*;


/**
 * A class for Switch statements
 */
public abstract class SwitchExpression extends VoidExpression
{
        /**
         * The expressions that are part of the
         * case statements.
         */
    Expression exps[] = null;

        /**
         * The expression for the default statement.
         */
    Expression defaultExp = null;

    int switchValues[] = null;
    int numCases = 0;

        /**
         * This determines the kind of switch command
         * we will be using in our bytecode.
         * ie whether a tableswitch or
         *            a lookupSwitch opcode.
         */
    boolean isRangeLookup = false;

        /**
         * Number of cases that we have.
         */
    public SwitchExpression(int numCases)
    {
        this.numCases = 0;
        this.switchValues = new int[numCases];
        exps = new Expression[numCases];
    }

        /**
         * Add another case value.
         */
    public void addCase(int value, Expression expr)
    {
        if (exps.length < numCases + 1)
        {
            Expression exp2[] = exps;
            exps = new Expression[numCases + 1];
            System.arraycopy(exp2, 0, exps, 0, numCases);
            exp2 = null;

            int sv[] = switchValues;
            switchValues= new int[numCases + 1];
            System.arraycopy(sv, 0, switchValues, 0, numCases);
        }
        switchValues[numCases] = value;
        exps[numCases] = expr;
        numCases++;
    }

        /**
         * Set the default expressions.
         */
    public void setDefaultExp(Expression exp)
    {
        defaultExp = exp;
    }
}
