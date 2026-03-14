import java.util.*;

/*For simplicity, this  class does not allow for the construction of a regular expression
* that accepts the empty sequence. The reason being that we have no symbol to 
* denote the empty sequence.  
*/
public class RegularExpression {

    private NFA nfa;
    private static int stateCounter = 0;
    
    // Helper method to generate unique state names
    private static String newState() {
        return "s" + (stateCounter++);
    }

    public RegularExpression(NFA engine, String re) {
        this.nfa = engine;
    }

    /*An empty regular expression - accepts only the empty string*/
    public RegularExpression() {
        String s0 = newState();
        String s1 = newState();
        this.nfa = new NFA(new String[]{s0, s1});
        this.nfa.setInitialState(s0);
        this.nfa.addFinalState(s1);
        this.nfa.addEpsilonTransition(s0, s1);
    }

    /* A regular expression consisting of a single character*/
    public RegularExpression(char a) {
        String s0 = newState();
        String s1 = newState();
        this.nfa = new NFA(new String[]{s0, s1});
        this.nfa.setInitialState(s0);
        this.nfa.addFinalState(s1);
        this.nfa.addTransition(s0, a, s1);
    }

    public RegularExpression(char[] chard) {
        // Creates an NFA that accepts any character in the array (union of single chars)
        String s0 = newState();
        String s1 = newState();
        this.nfa = new NFA(new String[]{s0, s1});
        this.nfa.setInitialState(s0);
        this.nfa.addFinalState(s1);
        for (char c : chard) {
            this.nfa.addTransition(s0, c, s1);
        }
    }

    public RegularExpression(char ini, char end) {
        // Creates an NFA that accepts any character in the range [ini-end]
        String s0 = newState();
        String s1 = newState();
        List<String> stateList = new ArrayList<>();
        stateList.add(s0);
        stateList.add(s1);
        this.nfa = new NFA(stateList.toArray(new String[0]));
        this.nfa.setInitialState(s0);
        this.nfa.addFinalState(s1);
        for (char c = ini; c <= end; c++) {
            this.nfa.addTransition(s0, c, s1);
        }
    }

    public static RegularExpression createUnionRE(RegularExpression left, RegularExpression right) {
        // Thompson's construction for union (alternation)
        String newStart = newState();
        String newEnd = newState();
        
        // Collect all states from both NFAs plus new start and end
        Set<String> allStates = new HashSet<>();
        allStates.add(newStart);
        allStates.add(newEnd);
        allStates.addAll(left.nfa.getStates());
        allStates.addAll(right.nfa.getStates());
        
        NFA result = new NFA(allStates.toArray(new String[0]));
        result.setInitialState(newStart);
        result.addFinalState(newEnd);
        
        // Add all transitions from left NFA
        result.addNFA(left.nfa);
        // Add all transitions from right NFA
        result.addNFA(right.nfa);
        
        // Epsilon from new start to both NFAs' initial states
        result.addEpsilonTransition(newStart, left.nfa.getInitialState());
        result.addEpsilonTransition(newStart, right.nfa.getInitialState());
        
        // Epsilon from both NFAs' final states to new end
        for (String finalState : left.nfa.getFinalStates()) {
            result.addEpsilonTransition(finalState, newEnd);
        }
        for (String finalState : right.nfa.getFinalStates()) {
            result.addEpsilonTransition(finalState, newEnd);
        }
        
        return new RegularExpression(result, null);
    }

    public static RegularExpression createSequentialRE(RegularExpression[] res) {
        if (res.length == 0) {
            return new RegularExpression();
        }
        if (res.length == 1) {
            return res[0];
        }
        
        // Thompson's construction for concatenation
        // Connect res[i]'s final states to res[i+1]'s initial state
        
        Set<String> allStates = new HashSet<>();
        for (RegularExpression re : res) {
            allStates.addAll(re.nfa.getStates());
        }
        
        NFA result = new NFA(allStates.toArray(new String[0]));
        result.setInitialState(res[0].nfa.getInitialState());
        result.addFinalState(res[res.length - 1].nfa.getFinalStates().iterator().next());
        
        // Add all transitions from all NFAs
        for (RegularExpression re : res) {
            result.addNFA(re.nfa);
        }
        
        // Connect consecutive NFAs with epsilon transitions
        for (int i = 0; i < res.length - 1; i++) {
            for (String finalState : res[i].nfa.getFinalStates()) {
                result.addEpsilonTransition(finalState, res[i + 1].nfa.getInitialState());
            }
        }
        
        return new RegularExpression(result, null);
    }

    public static RegularExpression createStartRE(RegularExpression re) {
        // Thompson's construction for Kleene star (zero or more repetitions)
        String newStart = newState();
        String newEnd = newState();
        
        Set<String> allStates = new HashSet<>();
        allStates.add(newStart);
        allStates.add(newEnd);
        allStates.addAll(re.nfa.getStates());
        
        NFA result = new NFA(allStates.toArray(new String[0]));
        result.setInitialState(newStart);
        result.addFinalState(newEnd);
        
        // Add all transitions from the original NFA
        result.addNFA(re.nfa);
        
        // Epsilon from new start to new end (accepts empty string)
        result.addEpsilonTransition(newStart, newEnd);
        
        // Epsilon from new start to re's initial state
        result.addEpsilonTransition(newStart, re.nfa.getInitialState());
        
        // Epsilon from re's final states to new end
        for (String finalState : re.nfa.getFinalStates()) {
            result.addEpsilonTransition(finalState, newEnd);
        }
        
        // Epsilon from re's final states back to re's initial state (loop for repetition)
        for (String finalState : re.nfa.getFinalStates()) {
            result.addEpsilonTransition(finalState, re.nfa.getInitialState());
        }
        
        return new RegularExpression(result, null);
    }


    public static RegularExpression createUnionRE(RegularExpression[] disj) {
        if (disj.length == 0) {
            return new RegularExpression();
        }
        if (disj.length == 1) {
            return disj[0];
        }
        
        // Build union incrementally
        RegularExpression result = disj[0];
        for (int i = 1; i < disj.length; i++) {
            result = createUnionRE(result, disj[i]);
        }
        return result;
    }

    public Boolean accept(String string) {
        return nfa.accept(string);
    }

}