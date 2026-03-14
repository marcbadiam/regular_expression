
# Llenguatges Formals — Practical Assessment Task 3 (Regular Expressions)

# Project overview

This project implements a Regular Expression parser and recognizer in Java, based on the provided ANTLR4 template.

The implemented work focuses on:

- Completing `src/main/java/DFA.java`:
  - constructor and basic state management
  - transition handling
  - input acceptance logic with implicit trap state
- Completing `src/main/java/NFA.java`:
  - constructor and basic state management
  - epsilon closure computation
  - NFA to DFA conversion algorithm (subset construction)
  - input acceptance logic
  - transition handling (regular and epsilon)
  - helper methods for NFA merging and state access
- Completing `src/main/java/RegularExpression.java`:
  - constructors for single characters, character ranges, and character arrays
  - Thompson's construction for union (alternation)
  - Thompson's construction for concatenation
  - Thompson's construction for Kleene star
  - NFA-based string acceptance
- Providing the corresponding Regular Expression specifications on:
  - `re-member1.txt` (binary strings containing substring "101")
  - `re-member2.txt` (binary strings containing "11" or "00")
  - `re-member3.txt` (binary strings with at least two zeros)
  - `re-member4.txt` (binary strings starting with 1 and having at least three 1s)

# Contribution of each member

- Marc Badia
  - Implementation of `DFA.java`: constructor, `setInitialState()`, `addFinalState()`, `addTransition()`, and `accept()` methods.
  - Implementation of `NFA.java`: epsilon closure computation (`epsilonClosure()`), `move()` method, and `accept()` method.
  - Co-implementation of `NFAtoDFA()` conversion algorithm using subset construction.
  - Implementation of helper methods in `NFA.java`: `getStates()`, `getFinalStates()`, `addState()`, `addNFA()`.
  - `re-member1.txt`

- Pol Caballé
  - Implementation of `RegularExpression.java` constructors: empty RE, single character, character array, and character range.
  - Implementation of `createUnionRE()` methods using Thompson's construction.
  - Implementation of `createSequentialRE()` method for concatenation.
  - Co-implementation of NFA merging logic in `RegularExpression.java`.
  - `re-member2.txt`

- Adrian Stefan Rusu
  - Implementation of `createStartRE()` method (Kleene star) using Thompson's construction.
  - Implementation of `accept()` method in `RegularExpression.java`.
  - Implementation of `NFABuilder.java` integration methods for parser visitor pattern.
  - Co-implementation of `NFAtoDFA()` DFA initialization and final state determination.
  - `re-member3.txt`

- Team Member 4
  - Testing and validation of all RegularExpression operations.
  - Documentation and code review.
  - Preparation of test cases and expected outputs for member specifications.
  - `re-member4.txt`

- Each member has contributed to a final review and submission checks and preparation of this document.
