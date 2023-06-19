package com.bloodforge.bloodworks.Neurons.NeuronTypes;

import com.bloodforge.bloodworks.Neurons.NeuronData;
import com.bloodforge.bloodworks.Neurons.NodeTypes.FloatNode;
import com.bloodforge.bloodworks.Neurons.NodeTypes.ItemStackNode;
import com.bloodforge.bloodworks.Neurons.NodeTypes.NeuronIONode;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class NeuronMath extends NeuronData
{
    public String func;

    public NeuronMath(BlockPos position, String func)
    {
        super(position);
        this.func = func;
    }

    @Override
    public void process()
    {
        super.process();
    }

    public static double eval(final String str, NeuronMath neuronMath)
    {
        return new Object()
        {
            Object lastParsedInputNode; //for getCount and such
            int pos = -1, ch;

            void nextChar()
            {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat)
            {
                while (ch == ' ') nextChar();
                if (ch == charToEat)
                {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse()
            {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor

            double parseExpression()
            {
                double x = parseTerm();
                for (; ; )
                {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm()
            {
                double x = parseFactor();
                for (; ; )
                {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor()
            {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                Double y = Double.NaN;
                Double z = Double.NaN;
                int startPos = this.pos;
                if (eat('('))
                { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                }
                else if ((ch >= '0' && ch <= '9') || ch == '.')
                { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                }
                else if (ch >= 'a' && ch <= 'z')
                { // functions
                    while ((ch >= 'a' && ch <= 'z') || (ch >= '0' && ch <= '9') || (ch == '_')) nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('('))
                    {
                        x = parseExpression();
                        if (eat(',')) y = parseExpression();
                        if (eat(',')) z = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else
                    {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(x);
                    else if (func.equals("cos")) x = Math.cos(x);
                    else if (func.equals("tan")) x = Math.tan(x);
                    else if (func.equals("abs")) x = Math.abs(x);
                    else if (func.equals("sign")) x = Math.signum(x);
                    else if (func.equals("floor")) x = Math.floor(x);
                    else if (func.equals("ceil")) x = Math.ceil(x);
                    else if (func.equals("rad")) x = Math.toRadians(x);
                    else if (func.equals("deg")) x = Math.toDegrees(x);
                    else if (func.equals("atan")) x = Math.atan(x);
                    else if (func.equals("atan2"))
                    {
                        if(y.isNaN()) throw new RuntimeException("atan2 takes 2 arguments");
                        x = Math.atan2(x, y);
                    }
                    else if (func.equals("clamp"))
                    {
                        if(y.isNaN())
                        {
                            x = Mth.clamp(x, 0, 1);
                        }
                        else if(z.isNaN())
                        {
                            x = Mth.clamp(x, y, 1);
                        }
                        else
                        {
                            x = Mth.clamp(x, y, z);
                        }
                    }
                    else if (func.equals("stack_max")) //get item max stack
                    {
                        if(lastParsedInputNode == null) throw new RuntimeException("maxstack failed! can't find input node");
                        if(lastParsedInputNode instanceof ItemStackNode itemStackNode)
                        {
                            x = ((ItemStack)itemStackNode.data).getMaxStackSize();
                        }
                        else { throw new RuntimeException("maxstack failed! specified node was not an ItemStack node"); }
                    }
                    else if (func.equals("stack_percent")) //count / stacksize
                    {
                        if(lastParsedInputNode == null) throw new RuntimeException("maxstack failed! can't find input node");
                        if(lastParsedInputNode instanceof ItemStackNode itemStackNode)
                        {
                            x = ((ItemStack)itemStackNode.data).getCount() / (float)((ItemStack)itemStackNode.data).getMaxStackSize();
                        }
                        else { throw new RuntimeException("maxstack failed! specified node was not an ItemStack node"); }
                    }
                    else throw new RuntimeException("Unknown function: " + func);
                }
                else if (ch >= 'A' && ch <= 'Z')
                { // variables
                    String func = str.substring(startPos, this.pos);
                    int varIndex = ch - 'A';
                    if (varIndex < 0 || varIndex >= neuronMath.inputs.size()) x = 0;
                    NeuronIONode inputNode = neuronMath.inputs.get(varIndex);
                    lastParsedInputNode = inputNode;
                    if (inputNode instanceof FloatNode)
                    {
                        x = (float)inputNode.data;
                    }
                    else if (inputNode instanceof ItemStackNode)
                    {
                        x = ((ItemStack)inputNode.data).getCount();
                    }
                    else throw new RuntimeException("Unknown variable: " + func);
                }
                else
                {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
}
