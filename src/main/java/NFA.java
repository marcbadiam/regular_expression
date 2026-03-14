import java.util.*;

public class NFA {

    String estatInicial;
    String[] states;
    String[] estatsFinals;
    int numEFinals = 0;
    Map<String, Map<Character, Set<String>>> taulaTransicions = new HashMap<>();

    public NFA(String[] states) {
        estatsFinals = new String[states.length];
        this.states = states;
    }

    public void setInitialState(String state) {
        estatInicial = state;
    }

    public String getInitialState() {
        return estatInicial;
    }

    public void addFinalState(String state) {
        estatsFinals[numEFinals++] = state;
    }

    // Method to add a transition to the table
    public void addTransition(String state, Character input, String nextState) {
        taulaTransicions.putIfAbsent(state, new HashMap<>());
        taulaTransicions.get(state).putIfAbsent(input, new HashSet<>());
        taulaTransicions.get(state).get(input).add(nextState);
    }

    public void addEpsilonTransition(String state, String nextState) {
        addTransition(state, null, nextState);
    }

    // epsilonClosure implementation
    private Set<String> epsilonClosure(Set<String> states) {
        Set<String> closure = new HashSet<>(states);
        Stack<String> stack = new Stack<>();
        for (String state : states) {
            stack.push(state);
        }

        while (!stack.isEmpty()) {
            String current = stack.pop();
            Map<Character, Set<String>> transitions = taulaTransicions.get(current);
            if (transitions != null && transitions.containsKey(null)) {
                for (String nextState : transitions.get(null)) {
                    if (!closure.contains(nextState)) {
                        closure.add(nextState);
                        stack.push(nextState);
                    }
                }
            }
        }

        return closure;
    }

    // Outputs true if the NFA accept the input string
    // The strategy we follow is to convert the NFA into a DFA
    public boolean accept(String input) {
        return NFAtoDFA().accept(input);
    }

    public DFA NFAtoDFA() {
        Set<Character> alphabet = getAlphabet();

        List<Set<String>> dfaStates = new ArrayList<>();
        Map<Set<String>, String> dfaStateNames = new HashMap<>();
        Map<Set<String>, Map<Character, Set<String>>> dfaTransitions = new HashMap<>();

        Set<String> initialClosure = epsilonClosure(getInitialNfaState());
        dfaStates.add(initialClosure);
        dfaStateNames.put(initialClosure, generateStateName(initialClosure));

        Queue<Set<String>> unmarkedStates = new LinkedList<>();
        unmarkedStates.add(initialClosure);

        while (!unmarkedStates.isEmpty()) {
            Set<String> currentDfaState = unmarkedStates.poll();
            dfaTransitions.putIfAbsent(currentDfaState, new HashMap<>());

            for (Character c : alphabet) {
                Set<String> nextDfaState = getNextDfaState(currentDfaState, c);

                if (!nextDfaState.isEmpty()) {
                    dfaTransitions.get(currentDfaState).put(c, nextDfaState);

                    if (!dfaStates.contains(nextDfaState)) {
                        dfaStates.add(nextDfaState);
                        dfaStateNames.put(nextDfaState, generateStateName(nextDfaState));
                        unmarkedStates.add(nextDfaState);
                    }
                }
            }
        }

        return buildDfaObject(dfaStates, dfaStateNames, dfaTransitions, initialClosure);
    }

    private Set<Character> getAlphabet() {
        Set<Character> alphabet = new HashSet<>();
        for (Map<Character, Set<String>> transitions : taulaTransicions.values()) {
            for (Character c : transitions.keySet()) {
                if (c != null) {
                    alphabet.add(c);
                }
            }
        }

        return alphabet;
    }

    private Set<String> getInitialNfaState() {
        Set<String> initialNfaState = new HashSet<>();
        if (estatInicial != null) {
            initialNfaState.add(estatInicial);
        }
        return initialNfaState;
    }

    private Set<String> getNextDfaState(Set<String> currentDfaState, Character c) {
        Set<String> nextNfaStates = new HashSet<>();
        for (String srcState : currentDfaState) {
            Map<Character, Set<String>> trans = taulaTransicions.get(srcState);
            if (trans != null && trans.containsKey(c)) {
                nextNfaStates.addAll(trans.get(c));
            }
        }
        return epsilonClosure(nextNfaStates);
    }

    private DFA buildDfaObject(List<Set<String>> dfaStates, Map<Set<String>, String> dfaStateNames,
            Map<Set<String>, Map<Character, Set<String>>> dfaTransitions,
            Set<String> initialClosure) {
        // Init DFA
        String[] dfaStateNameArray = new String[dfaStates.size()];
        for (int i = 0; i < dfaStates.size(); i++) {
            dfaStateNameArray[i] = dfaStateNames.get(dfaStates.get(i));
        }

        DFA dfa = new DFA(dfaStateNameArray);
        dfa.setInitialState(dfaStateNames.get(initialClosure));

        // Get NFA final states
        Set<String> nfaFinalStates = new HashSet<>(Arrays.asList(estatsFinals).subList(0, numEFinals));

        for (Set<String> dState : dfaStates) {
            // Check intersection with NFA final states
            for (String nfaState : dState) {
                if (nfaFinalStates.contains(nfaState)) {
                    dfa.addFinalState(dfaStateNames.get(dState));
                    break;
                }
            }

            // Add transitions
            Map<Character, Set<String>> transitions = dfaTransitions.get(dState);
            if (transitions != null) {
                for (Map.Entry<Character, Set<String>> entry : transitions.entrySet()) {
                    dfa.addTransition(
                            dfaStateNames.get(dState),
                            entry.getKey(),
                            dfaStateNames.get(entry.getValue()));
                }
            }
        }

        return dfa;
    }

    private String generateStateName(Set<String> statesSet) {
        List<String> sortedStates = new ArrayList<>(statesSet);
        Collections.sort(sortedStates);
        return String.join(",", sortedStates);
    }

    public Set<Label> getAllLabels() {
        Set<Label> labels = new HashSet<>();
        for (Map<Character, Set<String>> transitions : taulaTransicions.values()) {
            for (Character c : transitions.keySet()) {
                if (c == null) {
                    labels.add(Label.createEmptyLabel());
                } else {
                    labels.add(Label.createNonEmptyLabel(c));
                }
            }
        }
        return labels;
    }
}
