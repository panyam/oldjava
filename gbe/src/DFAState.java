
/**
 * A DFAState class that is used in the DF Automata.
 */
public class DFAState
{
        /**
         * Used to describe a transition.
         */
    class DFATransition 
    {
            // Each item in thsi vector corresponds to one DISTINCT range
            // of inputs and the positions that contain that given
            // range.
        IntRange input;

            // this is the set for each input symbol and in which positions
            // it belongs to...
        IntSet inputSet;
        DFAState nextState;

            /**
             * Constructor.
             */
        public DFATransition(int lo, int hi)
        {
            this(lo, hi, new IntSetArrayImpl(2));
        }

            /**
             * Constructor.
             */
        public DFATransition(int lo, int hi, IntSet iSet)
        {
            input = new IntRange(lo, hi);
            inputSet = iSet;
        }

            /**
             * Makes a copy of this transition.
             */
        public DFATransition makeCopy()
        {
            return new DFATransition
                    (input.lo, input.hi, inputSet.makeCopy());
        }
    }

        /**
         * Is this a final state?
         */
    boolean finalState = false;

    int stateID;

        /**
         * These are all the positions in the expTree that belong to
         * this state.
         */
    IntSet positions;

        /**
         * Is this node marked?
         */
    boolean marked = false;

        /**
         * Number of distinct character inputs that are present.
         * Basically the number of transitions from this state will be
         * less than or equal to this number!!!
         */
    int nTrans;

        /**
         * The transitions themselves.
         */
    DFATransition trans[] = null;

        /**
         * Constructor.
         */
    public DFAState(int stateID, IntSet set, ExpNode expNodes[])
    {
        this.stateID = stateID;
        positions = set;
        marked = false;

        int size = set.size();

            // now build non-intersecting input list!!!
        for (int i = 0;i < size;i++)
        {
            int v = set.get(i);
            if (expNodes[v] == DFACreator.augExpNode)
            {
                finalState = true;
            } else if (expNodes[v] instanceof SingleCharNode)
            {
                // then add this range as a distince one
                // to the list of inputs
                addAlphabet(((SingleCharNode)expNodes[v]).getChar(),
                            ((SingleCharNode)expNodes[v]).getChar(),
                            v);
            } else if (expNodes[v] instanceof RangeCharNode)
            {
                // then add this range as a distince one
                // to the list of inputs
                addAlphabet(((RangeCharNode)expNodes[v]).loChar,
                            ((RangeCharNode)expNodes[v]).hiChar,
                            v);
            }
        }
    }

        /**
         * Reallocates the current array of inputs
         * if necessary to accomodate the extra capacity.
         */
    protected void ensureCapacity(int newCap)
    {
        if (trans == null) 
        {
            nTrans = 0;
            trans = new DFATransition[2];

                // the sentinal transition that can never be matched
            trans[nTrans++] = new DFATransition(-2, -1);
        }

        if (newCap >= trans.length)
        {
            DFATransition t2[] = trans;
            trans = new DFATransition[newCap];
            System.arraycopy(t2, 0, trans, 0, nTrans);
        }
    }

        /**
         * Add a new inputsymbol to the list.
         * The following cases are possible:
         *
         * The new range is totally non-intersecting from
         * one of the existing ranges - Then add it in the right
         * position in sorted order.
         *
         * The new range is a superset of an existing range then break
         * the new range and update the table and then try adding the
         * new range again.
         *
         * the new range is a subset of an existing range, then break
         * the existing range and add v as an entry in the new range
         * but dont add the new range.
         */
    protected void addAlphabet(int loChar, int hiChar, int pos)
    {
        int loIndex = 0;
        int hiIndex = 0;
        boolean foundPos = false, splitRange = false;
        IntRange currRange;

        ensureCapacity(nTrans + 1);

        while (loIndex < nTrans && ! foundPos)
        {
            currRange = trans[loIndex].input;
            if (loChar >= currRange.lo && loChar <= currRange.hi)
            {
                foundPos = true;
                splitRange = true;
            } else if (loChar < currRange.lo)
            {
                foundPos = true;
            }
            if (! foundPos) loIndex ++;
        }

            // now we have lo.
            // Two possibilities...
            // to split or not to split!!!
        if (! foundPos)
        {
            // if position wasnt found then insert at the end of the
            // list..
            trans[nTrans] = new DFATransition(loChar, hiChar);
            trans[nTrans].inputSet.add(pos);
            nTrans++;
            return ;
        }

            // insert where the "lo" char first
        if (splitRange)
        {
                // only insert if the lo is not a subset of 
                // this range ie does not lie on the boundary!!!
            if (loChar > trans[loIndex].input.lo)
            {
                DFATransition newTrans = trans[loIndex].makeCopy();
                for (int i = nTrans + 1;i > loIndex;i--)
                    trans[i] = trans[i - 1];

                trans[loIndex] = newTrans;
                trans[loIndex].input.hi = loChar - 1;
                trans[loIndex + 1].input.lo = loChar;
                loIndex ++;
                nTrans++;
            }
        } else
        {
            // if no range is split then add a new range from
            // loChar to min(trans[loIndex].lo - 1, hiChar)
            for (int i = nTrans + 1;i > loIndex;i--)
                trans[i] = trans[i - 1];
            if (hiChar < trans[loIndex].input.lo)
            {
                    // this is the case where lo and hi are in the
                    // same "range"
                trans[loIndex] = new DFATransition(loChar, hiChar);
                trans[loIndex].inputSet.add(pos);
                return ;
            } else
            {
                trans[loIndex] = new DFATransition(
                                loChar, trans[loIndex + 1].input.lo - 1);
            }
        }

            // now for the "hi" part of the range...
            // same deal, but unlike loIndex (which gives the node
            // BEFORE which the isnertion is to be made), hiIndex says
            // AFTER which node, the insertion is to be made...
        hiIndex = nTrans - 1;

        foundPos = false;
        splitRange = false;
        while (hiIndex > 0 && !foundPos)
        {
            currRange = trans[hiIndex].input;
            if (hiChar >= currRange.lo && hiChar <= currRange.hi)
            {
                foundPos = true;
                splitRange = true;
            } else if (hiChar > currRange.hi)
            {
                foundPos = true;
            }
            if (! foundPos) hiIndex --;
        }

            // in this case there is no "foundPos situation" it will
            // always be after SOME position becuase all other cases
            // are taken care of by all the loIndex jazz...
            //
            // So we either split the range or add a new range
        if (splitRange)
        {
                // only insert if the lo is not a subset of 
                // this range ie does not lie on the boundary!!!
            if (hiChar < trans[hiIndex].input.hi)
            {
                DFATransition newTrans = trans[hiIndex].makeCopy();
                for (int i = nTrans + 1;i > hiIndex;i--)
                    trans[i] = trans[i - 1];

                trans[hiIndex] = newTrans;
                trans[hiIndex].input.hi = hiChar;
                trans[hiIndex + 1].input.lo = hiChar + 1;
            }
        } else
        {
            // if no range is split then add a new range from
            // loChar to min(trans[loIndex].lo - 1, hiChar)
            hiIndex ++;
            for (int i = nTrans + 1;i > hiIndex;i--)
                trans[i] = trans[i - 1];

            trans[hiIndex] = new DFATransition(
                            trans[hiIndex - 1].input.hi + 1, hiChar);
            /*if (loChar > trans[hiIndex].input.hi)
            {
                    // this is the case where lo and hi are in the
                    // same "range"
                trans[loIndex] = new DFATransition(loChar, hiChar);
                trans[loIndex].inputSet.add(pos);
                return ;
            } else
            {
            }*/
        }

            // now add pos as a state in each list betweenloIndex and
            // hiIndex inclusive
        for (int i = loIndex;i <= hiIndex;i++)
        {
            trans[i].inputSet.add(pos);
        }
    }
}
