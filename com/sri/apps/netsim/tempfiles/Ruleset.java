package com.sri.apps.netsim; 


import java.util.*;

/***    XML Classes     ***/
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;

/**
 * A RouterTable class.
 *
 * Allows for high speed access of routes and for their
 * addition and deletion.
 */
public class Ruleset
{
        /**
         * The default action.
         */
    Rule defaultRule = new Rule();

    int nRules = 0;
    Rule rules[];

        /**
         * Remove all the rules.
         */
    public void removeAll()
    {
        for (int i = 0;i < nRules;i++) rules[i] = null;
        nRules = 0;
    }

        /**
         * Delete a rule at the given position.
         */
    public void deleteRule(int index)
    {
        if (index < 0) return ;
        for (int i = index;i < nRules - 1;i++) rules[i] = rules[i + 1];

        nRules--;
        rules[nRules] = null;
    }

        /**
         * Move a rule from a given position to another position.
         */
    public void moveRule(int index, int newIndex)
    {
        if (index == newIndex ||
            index < 0 || newIndex < 0 ||
            index > nRules - 1 || newIndex > nRules - 1) return ;

        Rule currRule = rules[index];

        if (index > newIndex)
        {
            for (int i = index;i > newIndex;i--) rules[i] = rules[i - 1];
        } else
        {
            for (int i = index; i < newIndex;i++) rules[i] = rules[i + 1];
        }
        rules[newIndex] = currRule;
    }

        /**
         * Insert a new rule at the given position.
         */
    public void insertRule(Rule rule, int index)
    {
        ensureCapacity(nRules + 1);

        for (int i = nRules;i > index;i--) rules[i] = rules[i - 1];

        rules[index] = rule;
        nRules++;
    }

        /**
         * Add a new rule.
         */
    public void addRule(Rule rule)
    {
        ensureCapacity(nRules + 1);
        rules[nRules++] = rule;
    }

        /**
         * Makes sure we have enough capacity in the set of rules.
         */
    public void ensureCapacity(int newCapacity)
    {
        synchronized  (this)
        {
            if (rules == null) rules = new Rule[5];

            if (rules.length < newCapacity)
            {
                Rule r2[] = rules;
                rules = null;
                rules = new Rule[newCapacity];
                System.arraycopy(r2, 0, rules, 0, nRules);
                r2 = null;
            }
        }
    }

        /**
         * Get the given rule.
         */
    public Rule getRule(int which)
    {
        return rules[which];
    }

        /**
         * Prints out the ruleset.
         */
    public void print()
    {
    }

        /**
         * Get the information that needs to be stored 
         * in the form of an XML node.
         */
    public Element getXMLNode(Document doc, String name)
    {
        Element rulesNode =
            doc.createElement(XMLParams.RULE_SET_NODE_NAME);

        rulesNode.setAttribute(XMLParams.RULE_SET_NAME, name);

            // add the routes as we need them...
        for (int i = 0;i < nRules;i++)
        {
            rules[i].ruleID = i;
            rulesNode.appendChild(rules[i].getXMLNode(doc));
        }
        return rulesNode;
    }

        /**
         * Process an XML node and read stuff
         * out of it.
         */
    public void readXMLNode(Element element, Firewall fwall)
    {
        // process the interfaces
        NodeList ruleNodes =
            element.getElementsByTagName(XMLParams.RULE_TAG_NAME);

        this.nRules = 0;
        int ruleNodeSize = ruleNodes.getLength();
        ensureCapacity(ruleNodeSize + 5);

        for (int i = 0;i < ruleNodeSize;i++)
        {
            Element ruleNode = (Element)ruleNodes.item(i);
            Rule rule = new Rule();
            rule.readXMLNode(ruleNode, fwall);
            rules[rule.ruleID] = rule;
        }
    }
}
