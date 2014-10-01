package com.jamesrichford.brainalarm.com.jamesrichford.brainalarm.puzzles;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jamesrichford.brainalarm.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class MathPuzzle extends Puzzle {

    private Equation eq = new Equation();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout.
        setContentView(R.layout.activity_math_puzzle);

        // Display equation to solve.
        TextView tv = (TextView)findViewById(R.id.equation);
        tv.setText(eq.toString());
    }

    // Submit button touch listener.
    public void submit(View v)
    {
        // Get the solution from the View.
        Double proposedSolution = Double.parseDouble(((TextView) findViewById(R.id.solution)).getText().toString());

        // Build Toast for user feedback.
        Context context = getApplicationContext();
        CharSequence text = "Not So Correct!";
        int duration = Toast.LENGTH_LONG;

        // Test if the correct solution has been given.
        if (eq.isSolution(proposedSolution))
        {
            // Exit puzzle.
            text = "Correct!";
            finish();
        }

        // Give user feedback.
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void input(View v)
    {
        try
        {
            String numberString = ((Button)v).getText().toString();
            int number = Integer.parseInt(numberString);

            TextView solutionView = (TextView)findViewById(R.id.solution);

            String proposedSolution = solutionView.getText().toString();
            Double proposedSolutionNumber = Double.parseDouble(proposedSolution);

            if (proposedSolution.equals("0"))
            {
                proposedSolution = numberString;
            }
            else
            {
                proposedSolution += numberString;
            }

            solutionView.setText(proposedSolution);

        }
        catch (Exception e)
        {

        }
    }

    public void delete(View v)
    {
        TextView solutionView = (TextView)findViewById(R.id.solution);

        String proposedSolution = solutionView.getText().toString();
        Double proposedSolutionNumber = Double.parseDouble(proposedSolution);

        if (proposedSolution != "0")
        {
            proposedSolution = proposedSolution.substring(0, proposedSolution.length() - 1);
        }

        try
        {
            proposedSolutionNumber = Double.parseDouble(proposedSolution);
        }
        catch (Exception e)
        {
            proposedSolution = "0";
        }

        solutionView.setText(proposedSolution);
    }

    public void negate(View v)
    {
        TextView solutionView = (TextView)findViewById(R.id.solution);

        String proposedSolution = solutionView.getText().toString();
        Double proposedSolutionNumber = Double.parseDouble(proposedSolution);

        if (proposedSolutionNumber < 0)
        {
            proposedSolution = proposedSolution.substring(1);
        }
        else if (proposedSolutionNumber != 0)
        {
            proposedSolution = "-" + proposedSolution;
        }

        solutionView.setText(proposedSolution);
    }

    public void decimalise(View v)
    {
        TextView solutionView = (TextView)findViewById(R.id.solution);

        String proposedSolution = solutionView.getText().toString();

        try
        {
            int proposedSolutionNumber = Integer.parseInt(proposedSolution);
            proposedSolution += ".";
        }
        catch (Exception e)
        {

        }

        solutionView.setText(proposedSolution);
    }

    private class Equation
    {
        private ArrayList<EquationParameter> parameters = new ArrayList<EquationParameter>();
        private Double solution;

        public Equation()
        {
            Double roundedSolution = 0.0;

            // Checks that the solution to the equation is of a restricted number of decimal places.
            while(!roundedSolution.equals(solution))
            {
                // Clear equation.
                parameters.clear();

                // Add the initial term of the equation.
                parameters.add(randomTerm());

                // Add the desired amount of extra terms (and operators) to the equation.
                for (int i = 0; i < 2; i++)
                {
                    parameters.add(randomOperator());
                    parameters.add(randomTerm());
                }

                // Get the solution.
                solution = solve();

                if (solution.isNaN() || solution.isInfinite())
                {
                    // The equation is unsolvable.
                    continue;
                }

                // Create a rounded version of the solution
                DecimalFormat format = new DecimalFormat();
                format.setMaximumFractionDigits(2);
                String stringSolution = format.format(solution);
                roundedSolution = Double.parseDouble(stringSolution);
            }
        }

        @Override
        public String toString()
        {
            String equationString = "";

            // Get all the parameters characters (including a space between them).
            for (int i = 0; i < parameters.size(); i++)
            {
                equationString += parameters.get(i).toString() + " ";
            }

            // Append the question indication.
            equationString += "= ?";

            return equationString;
        }

        // Checks if the proposed solution is correct.
        private boolean isSolution(Double proposedSolution)
        {
            return proposedSolution.equals(solution);
        }

        // Solves the equation.
        private Double solve()
        {
            // Copy the equation parameters into a new ArrayList.
            ArrayList<EquationParameter> solutionParameters = new ArrayList<EquationParameter>(parameters);

            // Cycle through the process until only one Term remains.
            while (solutionParameters.size() > 1)
            {
                // Local variables for use later.
                Term firstTerm = null;
                Term secondTerm = null;
                Operator operator = null;

                // Inspect the parameters in the ArrayList.
                for(int i = 0; i < solutionParameters.size(); i++)
                {
                    // Get the class of parameter.
                    EquationParameter parameter = solutionParameters.get(i);
                    Class parameterClass = parameter.getClass();

                    // Get the next Operator.
                    if (parameterClass.getSuperclass() == Operator.class)
                    {
                        // Addition or Subtraction never gain priority over order.
                        if (operator != null && (parameterClass == Addition.class || parameterClass == Subtraction.class))
                        {
                            continue;
                        }

                        // Set the operator as the focus and the terms it will operate on.
                        operator = (Operator)parameter;
                        firstTerm = (Term)solutionParameters.get(i - 1);
                        secondTerm = (Term)solutionParameters.get(i + 1);

                        // Multiplication and Division have highest priority therefore there is no need to search further.
                        if (parameterClass == Multiplication.class || parameterClass == Division.class)
                        {
                            break;
                        }
                    }
                }

                // Find the solution to the operation.
                Term operationSolution = operator.evaluate(firstTerm, secondTerm);

                // Replace the Terms and Operator with the solution for the operation.
                solutionParameters.add(solutionParameters.indexOf(firstTerm), operationSolution);
                solutionParameters.remove(firstTerm);
                solutionParameters.remove(secondTerm);
                solutionParameters.remove(operator);
            }

            // Return the solution.
            return ((Term)solutionParameters.get(0)).termValue;
        }

        // Creates a random Term.
        private Term randomTerm()
        {
            // Random Double.
            Double termValue = Math.random();

            // Upper limit of the desired range.
            termValue = termValue * 10;

            // Lower limit of the desired range.
            termValue += 0;

            // Round the value.
            DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(0);
            termValue = Double.parseDouble(format.format(termValue));
            return new Term(termValue);
        }

        // Creates a random Operator.
        private Operator randomOperator()
        {
            Operator operator = null;

            // Takes a random number between 1 and 4.
            Double randomDouble = Math.random();
            randomDouble = randomDouble * 4;
            randomDouble = Math.ceil(randomDouble);
            DecimalFormat format = new DecimalFormat();
            format.setMaximumFractionDigits(0);
            int random = Integer.parseInt(format.format(randomDouble));

            // Selects the Operator based on the random number generated.
            switch(random)
            {
                case 1:
                {
                    operator = new Addition();
                    break;
                }
                case 2:
                {
                    operator = new Subtraction();
                    break;
                }
                case 3:
                {
                    operator = new Multiplication();
                    break;
                }
                case 4:
                {
                    operator = new Division();
                    break;
                }
            }

            return operator;
        }

        // Abstract parent class.
        private abstract class EquationParameter{}

        private abstract class Operator extends EquationParameter
        {
            // The symbol to be displayed in visual representation of the operation.
            private String symbol = null;

            // All children must evaluate.
            public abstract Term evaluate(Term firstTerm, Term secondTerm);

            @Override
            public String toString()
            {
                return symbol;
            }
        }

        private class Addition extends Operator
        {
            public Addition()
            {
                super.symbol = "+";
            }

            public Term evaluate(Term firstTerm, Term secondTerm)
            {
                return new Term(firstTerm.termValue + secondTerm.termValue);
            }
        }

        private class Subtraction extends Operator
        {
            public Subtraction()
            {
                super.symbol = "-";
            }

            public Term evaluate(Term firstTerm, Term secondTerm)
            {
                return new Term(firstTerm.termValue - secondTerm.termValue);
            }
        }

        private class Multiplication extends Operator
        {
            public Multiplication()
            {
                super.symbol = "×";
            }

            public Term evaluate(Term firstTerm, Term secondTerm)
            {
                return new Term(firstTerm.termValue * secondTerm.termValue);
            }
        }
        private class Division extends Operator
        {
            public Division()
            {
                super.symbol = "÷";
            }

            public Term evaluate(Term firstTerm, Term secondTerm)
            {
                return new Term(firstTerm.termValue / secondTerm.termValue);
            }
        }


        private class Term extends EquationParameter
        {
            private Double termValue;

            public Term(Double value)
            {
                termValue = value;
            }

            @Override
            public String toString()
            {
                DecimalFormat format = new DecimalFormat();
                return format.format(termValue);
            }
        }

    }
}
