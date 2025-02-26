//Gift Emmanuel code U23PE1035
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class BasicCalculator {
    private JFrame frame;
    private JTextField textField;
    private StringBuilder currentInput;
    private String lastAnswer = "";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BasicCalculator().createAndShowGUI());
    }

    public BasicCalculator() {
        currentInput = new StringBuilder();
    }

    public void createAndShowGUI() {
        frame = new JFrame("Basic Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 800);
        frame.setLayout(new BorderLayout());

        // Create a larger display for the JTextField with right alignment
        textField = new JTextField();
        textField.setEditable(false);
        textField.setFont(new Font("Arial", Font.PLAIN, 150)); // Default font size
        textField.setHorizontalAlignment(SwingConstants.RIGHT); // Right-align text
        textField.setBackground(Color.BLACK);
        textField.setForeground(Color.WHITE); // Set white text on black background for better visibility
        textField.setCaretColor(Color.WHITE); // Make cursor white
        frame.add(textField, BorderLayout.NORTH);

        // Layout panel for calculator buttons using GridLayout for 6x4 arrangement
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 4, 5, 5)); // 6 rows, 4 columns, with padding between buttons

        // Array of buttons: basic operations, including new operators
        String[] buttons = {
                "C", "SQRT", "%", "DEL",
                "^", "(", ")","Ans",
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+"

        };

        // Add buttons to the panel
        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.PLAIN, 24)); // Set a consistent font size
            button.addActionListener(new ButtonClickListener());
            button.setPreferredSize(new Dimension(60, 60)); // Increase button size for visibility
            panel.add(button);
        }

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("=")) {
                try {
                    String result = evaluate(currentInput.toString());
                    textField.setText(result);
                    lastAnswer = result; // Save the last answer for recall
                    currentInput.setLength(0);
                    currentInput.append(result);
                } catch (Exception ex) {
                    textField.setText("Error");
                    currentInput.setLength(0);
                }
            } else if (command.equals("C")) {
                // Clear the input when the "C" button is pressed
                currentInput.setLength(0);
                textField.setText("");
            } else if (command.equals("DEL")) {
                // Backspace: Remove the last character
                if (currentInput.length() > 0) {
                    currentInput.deleteCharAt(currentInput.length() - 1);
                    textField.setText(currentInput.toString());
                }
            } else if (command.equals("Ans")) {
                // Recall the last answer
                currentInput.append(lastAnswer);
                textField.setText(currentInput.toString());
            } else if (command.equals("SQRT")) {
                // Square root: Calculate square root of the number
                try {
                    double number = Double.parseDouble(currentInput.toString());
                    if (number < 0) {
                        textField.setText("Error");
                    } else {
                        double result = Math.sqrt(number);
                        currentInput.setLength(0);
                        currentInput.append(result);
                        textField.setText(currentInput.toString());
                    }
                } catch (NumberFormatException ex) {
                    textField.setText("Error");
                }
            } else if (command.equals("%")) {
                // Percentage: Calculate percentage of the number
                try {
                    double number = Double.parseDouble(currentInput.toString());
                    double result = number / 100;
                    currentInput.setLength(0);
                    currentInput.append(result);
                    textField.setText(currentInput.toString());
                } catch (NumberFormatException ex) {
                    textField.setText("Error");
                }
            } else {
                currentInput.append(command);
                textField.setText(currentInput.toString());
            }

            // Adjust font size based on input length for display clarity
            adjustTextFieldFontSize();
        }
    }

    private void adjustTextFieldFontSize() {
        int length = currentInput.length();

        // Adjust the font size depending on the length of the input
        if (length > 20) {
            textField.setFont(new Font("Arial", Font.PLAIN, 24)); // Smaller font for longer input
        } else if (length > 10) {
            textField.setFont(new Font("Arial", Font.PLAIN, 30)); // Medium font for medium-length input
        } else {
            textField.setFont(new Font("Arial", Font.PLAIN, 36)); // Default font for short input
        }

        // Keep the alignment right
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
    }

    private String evaluate(String expression) {
        try {
            return String.valueOf(evalBasicExpression(expression));
        } catch (Exception e) {
            return "Error";
        }
    }

    private double evalBasicExpression(String expression) {
        // Basic stack-based evaluation
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();
        Stack<Integer> parentheses = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--; // Move back after the number is processed
                values.push(Double.parseDouble(sb.toString()));
            } else if (c == '(') {
                parentheses.push(values.size());
            } else if (c == ')') {
                while (!parentheses.isEmpty() && values.size() > parentheses.peek()) {
                    values.pop();
                }
                parentheses.pop();
            } else if (c == '+' || c == '-' || c == '*' || c == '/' || c == '^') {
                while (!operators.isEmpty() && hasPrecedence(c, operators.peek())) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(c);
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private boolean hasPrecedence(char operator1, char operator2) {
        if (operator2 == '+' || operator2 == '-') {
            return !(operator1 == '*' || operator1 == '/' || operator1 == '^');
        }
        return false;
    }

    private double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return a / b;
            case '^': return Math.pow(a, b);
            default: return 0;
        }
    }
}
