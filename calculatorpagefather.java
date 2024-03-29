import tt2.Stack;
import java.util.*;

public class CaluclatorSamFather {
    
    private final String expression;
    
    private ArrayList<String> tokens;
    
    
    private ArrayList<String> reverse_polish;
    
    private Double result = 0.0;

    // Helper definition for supported operators
    private final Map<String, Integer> OPERATORS = new HashMap<>();
    {
        // Map<"token", precedence>
        OPERATORS.put("sqrt", 2);
        OPERATORS.put("^", 2);
        OPERATORS.put("*", 3);
        OPERATORS.put("/", 3);
        OPERATORS.put("%", 3);
        OPERATORS.put("+", 4);
        OPERATORS.put("-", 4);
    }

    // Helper definition for supported operators
    private final Map<String, Integer> SEPARATORS = new HashMap<>();
    {
        // Map<"separator", not_used>
        SEPARATORS.put(" ", 0);
        SEPARATORS.put("(", 0);
        SEPARATORS.put(")", 0);
    }

    // Create a 1 argument constructor expecting a mathematical expression
    public CaluclatorSamFather(String expression) {
        // original input
        this.expression = expression;

        // parse expression into terms
        this.termTokenizer();

        // place terms into reverse polish notation
        this.tokensToReversePolishNotation();

        // calculate reverse polish notation
        this.rpnToResult();
    }

    // Test if token is an operator
    private boolean isOperator(String token) {
        // find the token in the hash map
        return OPERATORS.containsKey(token);
    }

    // Test if token is an seperator
    private boolean isSeperator(String token) {
        // find the token in the hash map
        return SEPARATORS.containsKey(token);
    }

    // Compare precedence of operators.
    private Boolean isPrecedent(String token1, String token2) {
        // token 1 is precedent if it is greater than token 2
        return (OPERATORS.get(token1) - OPERATORS.get(token2) >= 0) ;
    }

    // Term Tokenizer takes original expression and converts it to ArrayList of tokens
    private void termTokenizer() {
        // contains final list of tokens
        this.tokens = new ArrayList<>();

        int start = 0;  // term split starting index
        StringBuilder multiCharTerm = new StringBuilder();    // term holder
        for (int i = 0; i < this.expression.length(); i++) {
            Character c = this.expression.charAt(i);
            if ( isOperator(c.toString() ) || isSeperator(c.toString())  ) {
                // 1st check for working term and add if it exists
                if (multiCharTerm.length() > 0) {
                    tokens.add(this.expression.substring(start, i));
                }
                // Add operator or parenthesis term to list
                if (c != ' ') {
                    tokens.add(c.toString());
                }
                // Get ready for next term
                start = i + 1;
                multiCharTerm = new StringBuilder();
            } else {
                // multi character terms: numbers, functions, perhaps non-supported elements
                // Add next character to working term
                multiCharTerm.append(c);
            }

        }
        // Add last term
        if (multiCharTerm.length() > 0) {
            tokens.add(this.expression.substring(start));
        }
    }

    // Takes tokens and converts to Reverse Polish Notation (RPN), this is one where the operator follows its operands.
    private void tokensToReversePolishNotation () {
        // contains final list of tokens in RPN
        this.reverse_polish = new ArrayList<>();

        // stack is used to reorder for appropriate grouping and precedence
        Stack tokenStack = new Stack();
        for (String token : tokens) {
            switch (token) {
                // If left bracket push token on to stack
                case "(":
                    tokenStack.push(token);
                    break;
                case ")":
                    while (tokenStack.peek() != null && !tokenStack.peek().equals("("))
                    {
                        reverse_polish.add( (String)tokenStack.pop() );
                    }
                    tokenStack.pop();
                    break;
                case "+":
                case "-":
                case "*":
                case "/":
                case "%":
                case "^":
                case "sqrt":
                    // While stack
                    // not empty AND stack top element
                    // and is an operator
                    while (tokenStack.peek() != null && isOperator((String) tokenStack.peek()))
                    {
                        if ( isPrecedent(token, (String) tokenStack.peek() )) {
                            reverse_polish.add((String)tokenStack.pop());
                            continue;
                        }
                        break;
                    }
                    // Push the new operator on the stack
                    tokenStack.push(token);
                    break;
                default:    // Default should be a number, there could be test here
                    this.reverse_polish.add(token);
            }
        }
        // Empty remaining tokens
        while (tokenStack.peek() != null) {
            reverse_polish.add((String)tokenStack.pop());
        }

    }

    // Takes RPN and produces a final result
    private void rpnToResult()
    {
        // Stack used to hold calculation while process RPN
        Stack stack = new Stack();

        // for loop to process RPN
        for (String token: this.reverse_polish) {
            // If the token is a number
            if (!isOperator(token)) {
                //Push number to stack
                stack.push(token)
            }
            //else
            else {
                // Pop the two top entries
                Double x1 = valueOf((String)stack.pop());
                Double x0 = valueOf((String)stack.pop());

                // Based off of Token operator calculate result
                Double result;
                switch (token) {
                    case "+":
                        result = x0 + x1;
                        break;
                    case "-":
                        result = x0 - x1;
                        break;
                    case "*":
                        result = x0 * x1;
                        break;
                    case "/":
                        result = x0 / x1;
                        break;
                    case "%":
                        result = x0 % x1;
                        break;
                    case "^":
                        result = Math.pow(x0, x1);
                        break;
                    case "sqrt":
                        result = Math.sqrt(x0);
                        break;
                    default:
                        result = 0.0;
                }

                // Push result back onto the stack
                push(String.valueOf(result));
            }
        }
        // Pop final result and set as final result for expression
        result = Double.valueOf((String)());
    }

    // Print the expression, terms, and result
    public String toString() {
        return ("Original expression: " + this.expression + "\n" +
                "Tokenized expression: " + this.tokens.toString() + "\n" +
                "Reverse Polish Notation: " +this.reverse_polish.toString() + "\n" +
                "Final result: " + String.format("%.2f", this.result));
    }

    // Tester method
    public static void main(String[] args) {
        // Random set of test cases
        CaluclatorSamFather simpleMath = new CaluclatorSamFather("100 + 200  * 3");
        System.out.println("Simple Math\n" + simpleMath);

        System.out.println();

        CaluclatorSamFather parenthesisMath = new CaluclatorSamFather("(100 + 200)  * 3");
        System.out.println("Parenthesis Math\n" + parenthesisMath);

        System.out.println();

        CaluclatorSamFather fractionMath = new CaluclatorSamFather("100.2 - 99.3");
        System.out.println("Fraction Math\n" + fractionMath);

        System.out.println();

        CaluclatorSamFather moduloMath = new CaluclatorSamFather("300 % 200");
        System.out.println("Modulo Math\n" + moduloMath);

        System.out.println();

        CaluclatorSamFather divisionMath = new CaluclatorSamFather("300/200");
        System.out.println("Division Math\n" + divisionMath);

        System.out.println();

        CaluclatorSamFather multiplicationMath = new CaluclatorSamFather("300 * 200");
        System.out.println("Multiplication Math\n" + multiplicationMath);

        System.out.println();

        CaluclatorSamFather allMath = new CaluclatorSamFather("200 % 300 + 5 + 300 / 200 + 1 * 100");
        System.out.println("All Math\n" + allMath);

        System.out.println();

        CaluclatorSamFather allMath2 = new CaluclatorSamFather("200 % (300 + 5 + 300) / 200 + 1 * 100");
        System.out.println("All Math2\n" + allMath2);

        System.out.println();

        CaluclatorSamFather allMath3 = new CaluclatorSamFather("200%(300+5+300)/200+1*100");
        System.out.println("All Math3\n" + allMath3);

        System.out.println();

        CaluclatorSamFather expMath = new CaluclatorSamFather("8 ^ 4");
        System.out.println("Exponential Math\n" + expMath);

        System.out.println();

        String userInput;
        Scanner input = new Scanner(System.in);

        System.out.println("Give the equation you would like to calculate");

        userInput = input.next();
        CaluclatorSamFather test = new CaluclatorSamFather(userInput);
        System.out.print("Result:\n" + test);


        /** working on bug
        CaluclatorSamFather sqrtMath = new CaluclatorSamFather("sqrt9");
        System.out.println("Square Root Math\n" + sqrtMath);
        System.out.println();*/
    }
}
