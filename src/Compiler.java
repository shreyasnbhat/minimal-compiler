import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * There are three types of' lines': 1) Function declaration line
 * 2) Variable declaration line
 * 3) Variable assignment line
 * 4) Function call with only variables and not values
 */
public class Compiler {

    public static void compile(String code) throws Exception {
        List<String> lines = RegexParser.parseIntoLines(code);
        Boolean flag = false;
        SymbolTable currentScope = new SymbolTable(null);

        ArrayList<Kind> paramKindList = new ArrayList<>();

        for (String line : lines) {

            if (line.equals("{")) {
                if (!flag) {
                    currentScope = enter(currentScope);
                } else {
                    flag = false;
                }
            } else if (line.equals("}")) {
                currentScope = exit(currentScope);
                flag = false;
                //paramKindList.clear();
            } else {
                Pair<Integer, String[]> lineFormat = RegexParser.classifyLine(line);
                String[] codeLine = lineFormat.getRight();
                switch (lineFormat.getLeft()) {
                    case 1:
                        if (codeLine.length % 2 == 0) {
                            paramKindList.add(Utils.getKind(codeLine[0]));
                            switch (codeLine.length) {
                                case 6:
                                    paramKindList.add(Utils.getKind(codeLine[4]));
                                case 4:
                                    paramKindList.add(Utils.getKind(codeLine[2]));
                                    break;
                                default:
                                    break;
                            }
                            currentScope.insert(codeLine[1], paramKindList, Utils.getType("fun"));
                            paramKindList.clear();
                            flag = true;
                            // For the parameters we need to go a level below to add the parameters
                            currentScope = enter(currentScope);
                            switch (codeLine.length) {
                                case 6:
                                    currentScope.insert(codeLine[5], getKindListFromKind(codeLine[4]), Utils.getType("par"));
                                case 4:
                                    currentScope.insert(codeLine[3], getKindListFromKind(codeLine[2]), Utils.getType("par"));
                                    break;
                                case 2:
                                    break;
                                default:
                                    throw new Exception("Function can have a maximum of two arguments only!");
                            }
                        } else {
                            throw new Exception("Compilation Failed!");
                        }
                        break;
                    case 2:
                        currentScope.insert(codeLine[1], getKindListFromKind(codeLine[0]), Utils.getType("var"));
                        break;
                    case 3:
                        currentScope.lookup(codeLine[0]);
                        break;
                    case 4:
                        //Type checking is don
                        SymbolTable functionSymbolTable = currentScope.lookup(codeLine[0]);
                        HashMap<String, Pair<Type, ArrayList<Kind>>> functionScope = functionSymbolTable.getCurrentScope();

                        if (!functionScope.get(codeLine[0]).getLeft().equals(Utils.getType("fun"))) {
                            throw new Exception("No function " + codeLine[0] + " is defined!");
                        } else {
                            System.out.println("Symbol Table for Function " + codeLine[0]);
                            System.out.println(functionSymbolTable);
                            ArrayList<Kind> kindList = functionScope.get(codeLine[0]).getRight();

                            // Check if parameter 1 exists
                            try {
                                SymbolTable param1SymbolTable = currentScope.lookup(codeLine[1]);
                                HashMap<String, Pair<Type, ArrayList<Kind>>> param1Scope = param1SymbolTable.getCurrentScope();
                                System.out.println("Symbol Table for param1 " + codeLine[1]);
                                System.out.println(param1SymbolTable);

                                Kind param1KindDefined = kindList.get(1);
                                Kind param1KindPassed = param1Scope.get(codeLine[1]).getRight().get(0);

                                System.out.println(param1KindDefined + " " + param1KindPassed);

                            } catch (ArrayIndexOutOfBoundsException e) {
                                System.out.print("");
                            }

                            //  Check if parameter 2 exists
                            try {
                                SymbolTable param2SymbolTable = currentScope.lookup(codeLine[2]);
                                HashMap<String, Pair<Type, ArrayList<Kind>>> param2Scope = param2SymbolTable.getCurrentScope();
                                System.out.println("Symbol Table for param2 " + codeLine[2]);
                                System.out.println(param2SymbolTable);

                                Kind param2KindDefined = kindList.get(2);
                                Kind param2KindPassed = param2Scope.get(codeLine[2]).getRight().get(0);

                                System.out.println(param2KindDefined + " " + param2KindPassed);

                            } catch (ArrayIndexOutOfBoundsException e) {
                                System.out.print("");
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public static ArrayList<Kind> getKindListFromKind(String kind) {
        ArrayList<Kind> kindList = new ArrayList<>();
        try {
            kindList.add(Utils.getKind(kind));
        } catch (Exception e) {
            System.err.print(e.getMessage());
        }
        return kindList;
    }

    public static SymbolTable enter(SymbolTable currentScope) {
        return currentScope.enter_scope();
    }

    public static SymbolTable exit(SymbolTable currentScope) {
        System.out.println(currentScope);
        SymbolTable parentScope = currentScope.exit_scope();
        parentScope.killChild(currentScope);
        return parentScope;
    }

    public static void main(String[] args) throws Exception {
        Compiler.compile("{void function1(int m,int c){int e;} int main(int f,int h){ int a; f = 6; { int b; { int b;} int c; int d; int e;} int x; function1(a,f);}}");
    }
}
