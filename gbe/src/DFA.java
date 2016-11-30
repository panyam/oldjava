
/**
 * The DFA class.
 */
public class DFA
{
    public DFAState dfaStates [] = null;
    public int nStates;

        /**
         * Constructor.
         */
    public DFA(DFAState states[], int nStates)
    {
        dfaStates = states;
    }

        /**
         * Apply minimisation techniques to reduce the number of states.
         */
    public void minimiseDFA()
    {
        IntSet f = new IntSet(), smf = new IntSet();
        for (int i = 0;i < nStates;i++)
        {
            if (dfaStates[i].finalState)
            {
                f.add(i);
            } else
            {
                smf.add(i);
            }
        }
    }
}
