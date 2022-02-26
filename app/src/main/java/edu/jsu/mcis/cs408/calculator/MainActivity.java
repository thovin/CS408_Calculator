package edu.jsu.mcis.cs408.calculator;



import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.HashMap;

import edu.jsu.mcis.cs408.calculator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private State currState = State.LP;
    private StringBuilder lp = new StringBuilder();
    private String op;
    private StringBuilder rp = new StringBuilder();
    private BigDecimal total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        ClickHandler clickHandler = new ClickHandler();
        ConstraintLayout layout = binding.layout;

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);

            if (child instanceof Button) {
                child.setOnClickListener(clickHandler);
            }
        }
    }


    private void buttonClicked(String tag) {
        if (currState == State.TOTAL) {
            currState = State.LP;
            lp.setLength(0);
            rp.setLength(0);
            op = "";
        }

        HashMap<String, String> tagTypes = generateTypeList();
        String tagType = tagTypes.get(tag);

        if (tagType.equals("clear")) {
            currState = State.LP;
            lp.setLength(0);
            rp.setLength(0);
            op = "";
        } else if (tagType.equals("decimal")) {
            if (currState == State.LP && lp.indexOf(".") == -1) {
                lp.append(".");
            } else if (currState == State.RP && rp.indexOf(".") == -1){
                rp.append(".");
            }
        } else if (tagType.equals("numeric")) {
            int num = Integer.parseInt(tag);
            if (currState == State.LP) {
                lp.append(num);
            } else if (currState == State.RP || currState == State.OP) {
                currState = State.RP;
                rp.append(num);
            }
        } else if (tagType.equals("mutator")) {
            if (tag.equals("\u00B1")) { //sin
                if (currState == State.LP) {
                    String temp = new BigDecimal(lp.toString()).multiply(new BigDecimal(-1)).toString();
                    lp.setLength(0);
                    lp.append(temp);
                } else if (currState == State.RP) {
                    String temp = new BigDecimal(rp.toString()).multiply(new BigDecimal(-1)).toString();
                    rp.setLength(0);
                    rp.append(temp);
                }
            } else if (tag.equals("\u221A")) { //square root
                if (currState == State.LP) {
                    String temp = String.valueOf(Math.sqrt(Double.parseDouble(lp.toString())));
                    lp.setLength(0);
                    lp.append(temp);
                } else if (currState == State.RP) {
                    String temp = String.valueOf(Math.sqrt(Double.parseDouble(rp.toString())));
                    rp.setLength(0);
                    rp.append(temp);
                }
            } else if (tag.equals("%")) {
                if (currState == State.LP) {
                    String temp = new BigDecimal(lp.toString()).divide(new BigDecimal(100)).toString();
                    lp.setLength(0);
                    lp.append(temp);
                } else if (currState == State.RP) {
                    String temp = new BigDecimal(rp.toString()).divide(new BigDecimal(100)).toString();
                    rp.setLength(0);
                    rp.append(temp);
                }
            }
        } else if (tagType.equals("operand")) {
            currState = State.OP;
            op = tag;
        } else if (tagType.equals("calculate")) {
            if (currState == State.RP) {
                currState = State.TOTAL;

                BigDecimal bdlp = new BigDecimal(lp.toString());
                BigDecimal bdrp = new BigDecimal(rp.toString());
                switch (op) {
                    case "\u00F7":
                        total = bdlp.divide(bdrp);
                        break;
                    case "\u00D7":
                        total = bdlp.multiply(bdrp);
                        break;
                    case "+":
                        total = bdlp.add(bdrp);
                        break;
                    case "-":
                        total = bdlp.subtract(bdrp);
                        break;
                }
            }
        }

        TextView display = binding.displayText;
        StringBuilder outputText = new StringBuilder();
        if (currState == State.TOTAL) {
            outputText.append(total.toString());
        } else if (currState == State.RP) {
            outputText.append(lp).append(' ').append(op).append(' ').append(rp);
        } else if (currState == State.OP) {
            outputText.append(lp).append(' ').append(op);
        } else if (currState == State.LP) {
            outputText.append(lp);
        }

        display.setText(outputText);
    }

    private HashMap<String, String> generateTypeList() {
        HashMap<String, String> typeList = new HashMap<>();
        typeList.put("0", "numeric");
        typeList.put("1", "numeric");
        typeList.put("2", "numeric");
        typeList.put("3", "numeric");
        typeList.put("4", "numeric");
        typeList.put("5", "numeric");
        typeList.put("6", "numeric");
        typeList.put("7", "numeric");
        typeList.put("8", "numeric");
        typeList.put("9", "numeric");

        typeList.put("C", "clear");

        typeList.put("\u221A", "mutator");
        typeList.put("%", "mutator");
        typeList.put("\u00B1", "mutator"); //sin

        typeList.put("\u00F7", "operand"); //division
        typeList.put("\u00D7", "operand"); //multiplication
        typeList.put("+", "operand");
        typeList.put("-", "operand");

        typeList.put(".", "decimal");

        typeList.put("=", "calculate");

        return typeList;

    }


    class ClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String tag = ((Button) view).getTag().toString();
            buttonClicked(tag);
        }
    }
}



