
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shreyas on 9/3/2017.
 */
public class Parser {

    public Parser() {
    }

    /**
     * All operations in the codebase are restricted:
     * 1) int a = 5; Variable creation
     * 2) a = 8; Variable Assignment
     * 3) f() No arguements Function call
     *
     * Need to add multiple scopes between { }
     */

    public void generateTable(ArrayList<String> tokens, SymbolTable table) {

        SymbolTable newScope = null;
        String identifier = "None", kind = "None", type = "None", value = "None";
        int counter = 0;
        while (counter < tokens.size()) {

            String token = tokens.get(counter);
            // Enter into a newscope if initial character is  {
            if (token.equals("{") && counter == 0) {
                newScope = table.enter_scope();
                counter += 1;
            } else if (token.equals("{") && counter != 0) {
                // We have a subscope now

                int leftBracketCounter = 0;
                int rightBracketCounter = 0;
                int tempCounter = counter;
                ArrayList<String> subScopeTokenList = new ArrayList<>();
                do {
                    if(tokens.get(tempCounter).equals("{")){
                        leftBracketCounter+=1;
                    }
                    else if(tokens.get(tempCounter).equals("}")){
                        rightBracketCounter+=1;
                    }
                    subScopeTokenList.add(tokens.get(tempCounter));
                    tempCounter += 1;
                }while(leftBracketCounter!=rightBracketCounter);
                //Pass in the subScope tokens and the generated symbol table
                generateTable(subScopeTokenList, newScope);
                counter  = tempCounter ;
            } else if (token.equals("int")) {
                kind = "var";
                type = token;
                counter += 1;
            } else if (token.matches("[a-zA-Z]+")) {
                identifier = token;
                counter += 1;
            } else if (token.matches("[0-9]+")) {
                value = token;
                counter += 1;
            } else if (token.equals(";")) {
                System.out.println(" " + identifier + " " + kind + " " + type + " " + value);
                try {
                    newScope.insert(identifier, getKindEnum(kind), getTypeEnum(type));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                identifier = "None";
                kind = "None";
                type = "None";
                counter += 1;
            } else if (token.equals("}")) {
                counter+=1;
                System.out.println("End");
            }
        }
    }

    @Nullable
    public  Kind getKindEnum(String kind){
        if (kind.contains("int")) {
            return Kind.Int;
        } else if(kind.contains("float")) {
            return Kind.Float;
        }
        return Kind.Int;
    }

    @Nullable
    public  Type getTypeEnum(String type){
        if (type.equals("var")) {
            return Type.var;
        } else if(type.equals("fun")) {
            return Type.fun;
        }
        return Type.var;
    }

}
