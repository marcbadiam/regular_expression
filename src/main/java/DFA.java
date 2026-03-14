/*A class that models DFAs. States are just strings while labels
 * are characters. 
 * We shall specify DFAs without the requirement that the transition 
 * function is total. In other words, we will let the programmer specify 
 * DFAs with transitions, then assume those transitions all go to a trap state. 
 */

import java.util.HashMap;
import java.util.Map;

public class DFA {

    String estatInicial;
    String[] states;
    String[] estatsFinals;
    int numEFinals = 0;
    Map<String, Map<Character, String>> taulaTransicions = new HashMap<>();

    /*A constructor that builds a DFA with the set of state names 
     * given as arguments. 
     */
    public DFA(String[] states) {
        estatsFinals = new String[states.length];
        this.states = states;
    }

    /*Establish the initial state  */
    public void setInitialState(String state) {
        estatInicial = state;
    }

    /*Mark a state in the DFA as final*/
    public void addFinalState(String state) {
        estatsFinals[numEFinals++] = state;
    }

    // Method that adds a transition. 
    public void addTransition(String state, Character input, String nextState) {
        taulaTransicions.putIfAbsent(state, new HashMap<>());
        taulaTransicions.get(state).put(input, nextState);  
    }

    /*Given an input string, this method outputs true if the DFA accepts it.
     * Otherwise it outputs false. 
     */
    public boolean accept(String input) {

        String estatActual = this.estatInicial; 

        for (int i = 0; i < input.length(); i++) {
            char simbol = input.charAt(i);
            Map<Character, String> transitions = taulaTransicions.get(estatActual);
            if (transitions != null && transitions.containsKey(simbol)) {
                estatActual = transitions.get(simbol);
            } else {
                return false; //no hi ha transició per aquest símbol
            }
        }

        for (int i = 0; i < numEFinals; i++) {
            if (estatActual.equals(estatsFinals[i])) {
                return true; // em arribat a estat final
            }
        }

        return false;
    }

}