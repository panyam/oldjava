import java.util.*;

public class DFACreator
{
        /**
         * Number of nodes in the tree.
         */
    protected int nNodes;

        /**
         * Number of leaf nodes.
         */
    protected int nLeafNodes;

        // now to calculate the nullable, firstpos and lastpos
        // values for all nodes...
    protected IntSet firstPos[] = null;
    protected IntSet lastPos[] = null;
    protected IntSet followPos[] = null;
    protected LeafNode leafNodes[] = null;
    protected ExpNode expNodes[] = null;

        /**
         * This is the set ofall the possible "inputs" that can arise.
         * Basically this determines how an output edge occurs.
         */
    protected Object inputSet;

        /**
         * This is the augmented node
         * (exp) # 
         */
    protected ExpNode augNode;

    public final static ExpNode augExpNode = new ZeroExpNode();

        /**
         * Constructor.
         */
    public DFACreator(ExpNode exp)
    {
        setExpNode(exp);
    }

        /**
         * Set the expression node.
         */
    public void setExpNode(ExpNode exp)
    {
        synchronized(this)
        {
            ExpNode augNode = new BinaryExpNode(ExpNodeType.CONCAT_NODE,
                                                exp, augExpNode);

                // now number all the leaf nodes in this tree...
            nNodes = nLeafNodes = 0;
            numberTree(augNode);

            if (firstPos == null || firstPos.length < nNodes)
            {
                firstPos = new IntSet[nNodes];
                lastPos = new IntSet[nNodes];
                expNodes = new ExpNode[nNodes];
            }

            for (int i = 0;i < nNodes;i++)
            {
                if (firstPos[i] == null) firstPos[i] = createIntSet();
                if (lastPos[i] == null) lastPos[i] = createIntSet();

                firstPos[i].clearSet();
                lastPos[i].clearSet();
            }

            if (followPos == null || followPos.length < nLeafNodes)
            {
                followPos = new IntSet[nLeafNodes];
                leafNodes = new LeafNode[nLeafNodes];
            }

            for (int i = 0;i < nLeafNodes;i++)
            {
                if (followPos[i] == null) followPos[i] = createIntSet();
                followPos[i].clearSet();
            }
        }
    }

        /**
         * Creates an instance of an intset.
         */
    protected static IntSet createIntSet()
    {
        return new IntSetArrayImpl();
    }

        /**
         * Given an expression node creates a DFA and returns it.
         */
    public DFA createDFA()
    {
        DFAState states[];
        synchronized (this)
        {
            System.out.println("Generating DFA: ");

                // create the augmented regular expression (r)#
            evaluateLists(augNode);

                // finally to calculate the followpos lists for each.
                // this only applies to leaf nodes however it will be
                // propogated "via" non-leaf nodes...
                //
            Hashtable stateTable = createStates();

            states = new DFAState[stateTable.size()];
                // now that the states are here, return it...
            for (Enumeration e = stateTable.elements();e.hasMoreElements();)
            {
                DFAState state = (DFAState)(e.nextElement());
                states[state.stateID] = state;
            }

                // now that this is done, try and minimise the DFA...
        }

        DFA dfa = new DFA(states, states.length);
        return null;
    }

        /**
         * Creates the states for the DFA.
         * Precondition: Followpos, firstpos and lastpos are all evaluated.
         * The algorithm is (as of The dragon book):
         *  Initially the only unmarked state in Dstates is firspos(root),
         *  where root is the root of the augmented tree
         *
         *      while there is an unmarked state T in Dstates
         *          mark T
         *          for each input symbol a 
         *              let U be the set of positions that are in 
         *                  followpos(p) for some position p in T, such
         *                  that the symbol at position p is a; 
         *              if U is not empty and is not in Dstates then add U
         *                  as an unmarked state to Dstates; 
         *              Dtran[T, a] := U
         */
    protected Hashtable createStates()
    {
        int stateID = 0;

            // the current states...
        Hashtable stateTable = new Hashtable();

        Stack umStates = new Stack();
        IntSet tempSet = createIntSet();

        DFAState rootState = new DFAState(stateID,
                                          firstPos[augNode.nodeID],
                                          expNodes);
        umStates.push(rootState);

        while ( ! umStates.isEmpty())
        {
            DFAState curr = (DFAState)umStates.pop();

                // mark this state and put it in the table..
            curr.marked = true;
            stateTable.put(curr.positions, curr);

                // now
            IntSet T = curr.positions;
            int ts = T.size();

            for (int i = 0;i < curr.nTrans;i++)
            {
                tempSet.clearSet();

                for (int p = 0;p < ts;p++)
                {
                        // id of node P in T
                    int nodeP = T.get(p);
                    ExpNode node = expNodes[nodeP];
                    if (node instanceof CharClassNode)
                    {
                        CharClassNode ccNode = (CharClassNode)node;

                        if (ccNode.matchesRange(curr.trans[i].input.lo,
                                                curr.trans[i].input.hi))
                        {
                            tempSet.unionWith(followPos[nodeP]);
                        }
                    }
                }

                    // if this kind of set is already on the table then
                    // dont add it...
                DFAState nextState = (DFAState)stateTable.get(tempSet);
                if ( nextState == null)
                {
                    nextState = new DFAState(stateID++,
                                             tempSet.makeCopy(),
                                             expNodes);
                    nextState.marked = false;
                        // add it to the table
                    stateTable.put(tempSet, nextState);
                }
                    // also add a transition...
                curr.trans[i].nextState = nextState;
            }
        }
        return stateTable;
    }

        /**
         * Evaluate all the lists starting from the root node.
         * Basically, nullable, firstPos and lastPos are evaluated 
         * at each node by an inorder DF traversal of the tree.  This would
         * work since the result at any node only depends on its children
         * node.
         */
    protected void evaluateLists(ExpNode expNode)
    {
        expNodes[expNode.nodeID] = expNode;
        if (expNode instanceof LeafNode)
        {
            expNode.nullable = false;

            leafNodes[((LeafNode)expNode).position] = (LeafNode)expNode;
            firstPos[expNode.nodeID].add(expNode.nodeID);
            lastPos[expNode.nodeID].add(expNode.nodeID);
        } else if (expNode.expType == ExpNodeType.STAR_NODE)
        {
            expNode.nullable = true;

            ExpNode child = ((UnaryExpNode)expNode).child;

                // calculate children first
            evaluateLists(child);

            firstPos[expNode.nodeID] = firstPos[child.nodeID];
            lastPos[expNode.nodeID] = lastPos[child.nodeID];

                // also, calculate followPos here...
                // if n is a starnode and i is a position in lastpos(n),
                // then all positions infirstpos(n) are in followpos(i)
                //
                // for a starnode, n, 
                //  for each position i in lastpos(n)
                //      followpos(i) = followpos(i) U firstpos(n)
            int nLP = lastPos[expNode.nodeID].size();

            for (int i = 0;i < nLP;i++)
            {
                followPos[lastPos[expNode.nodeID].get(i)].
                                unionWith(firstPos[expNode.nodeID]);
            }
        } else if (expNode instanceof BinaryExpNode)
        {
            ExpNode left = ((BinaryExpNode)expNode).left;
            ExpNode right = ((BinaryExpNode)expNode).right;

            evaluateLists(left);
            evaluateLists(right);

            if (expNode.expType == ExpNodeType.CONCAT_NODE)
            {
                expNode.nullable = left.nullable && right.nullable;

                if (left.nullable)
                {
                    firstPos[expNode.nodeID] = firstPos[left.nodeID].
                                                union(firstPos[right.nodeID]);
                } else 
                {
                    firstPos[expNode.nodeID] = firstPos[left.nodeID];
                }

                if (right.nullable)
                {
                    lastPos[expNode.nodeID] = lastPos[right.nodeID].
                                                union(lastPos[left.nodeID]);
                } else 
                {
                    lastPos[expNode.nodeID] = lastPos[right.nodeID];
                }

                    // if N is  cat-node with left child c1 and right child
                    // 2, and i is a position in lastpos(c1), then all
                    // positions in firstpos(c2) are in followPos(i)
                int nLP = lastPos[left.nodeID].size();
                for (int i = 0;i < nLP;i++)
                {
                    followPos[lastPos[left.nodeID].get(i)].
                                unionWith(firstPos[right.nodeID]);
                }
            } else if (expNode.expType == ExpNodeType.UNION_NODE)
            {
                expNode.nullable = left.nullable || right.nullable;

                firstPos[expNode.nodeID] = firstPos[left.nodeID].
                                            union(firstPos[right.nodeID]);
                lastPos[expNode.nodeID] = lastPos[right.nodeID].
                                            union(lastPos[left.nodeID]);
                    // nothing happens to follow pos here!!!
            }
        }
    }

        /**
         * Number the tree in an inorder DF traversal and return the number
         * of nodeIDs generated.
         */
    protected void numberTree(ExpNode root)
    {
        if (root instanceof LeafNode) 
        {
            ((LeafNode)root).position = nLeafNodes++;
            root.nodeID = nNodes++;
        } else if (root instanceof BinaryExpNode)
        {
            numberTree(((BinaryExpNode)root).left);
            root.nodeID = nNodes++;
            numberTree(((BinaryExpNode)root).right);
        } else if (root instanceof UnaryExpNode)
        {
            numberTree(((UnaryExpNode)root).child);
            root.nodeID = nNodes++;
        }
    }
}
