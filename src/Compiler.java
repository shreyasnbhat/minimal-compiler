import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.List;

/**
 * There are three types of' lines': 1) Function declaration line
 * 2) Variable declaration line
 * 3) Variable assignment line
 * 4) Function call
 */
public class Compiler {

    public static Kind getKind(String kind) throws Exception {
        switch (kind) {
            case "void":
                return Kind.Void;
            case "int":
                return Kind.Int;
            case "double":
                return Kind.Double;
            case "bool":
                return Kind.Bool;
            case "float":
                return Kind.Float;
            default:
                throw new Exception("Invalid argument to getKind() for " + kind);
        }
    }

    public static Type getType(String type) throws Exception {
        switch (type) {
            case "par":
                return Type.par;
            case "fun":
                return Type.fun;
            case "var":
                return Type.var;
            default:
                throw new Exception("Invalid argument to getType() for " + type);
        }
    }

    public static void compile(String code) throws Exception {
        List<String> lines = RegexParser.parseIntoLines(code);

        Boolean flag = false;

        SymbolTable currentScope = new SymbolTable(null);

        SymbolTable x = currentScope;

        for (String line : lines) {

            if (line.equals("{")) {
                if (!flag) {
                    currentScope = enter(currentScope);
                } else {
                    flag = false;
                }
            } else if (line.equals("}")) {
                System.out.println(currentScope);

                currentScope = exit(currentScope);
                flag = false;
            } else {

                Pair<Integer, String[]> p = RegexParser.classifyLine(line);
                String[] codeLine = p.getRight();
                switch (p.getLeft()) {
                    case 1:
                        if (codeLine.length % 2 == 0) {
                            currentScope.insert(codeLine[1], getKind(codeLine[0]), getType("fun"));
                            flag = true;
                            // For the parameters we need to go a level below to add the parameters
                            currentScope = enter(currentScope);
                            switch (codeLine.length) {
                                case 6:
                                    currentScope.insert(codeLine[5], getKind(codeLine[4]), getType("par"));
                                case 4:
                                    currentScope.insert(codeLine[3], getKind(codeLine[2]), getType("par"));
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
                        currentScope.insert(codeLine[1], getKind(codeLine[0]), getType("var"));
                        break;
                    case 3:
                        currentScope.lookup(codeLine[0]);
                        break;
                    case 4:
                        //currentScope.lookup(codeLine[0]);
                        break;
                    default:
                        break;
                }
            }


        }
    }

    public static SymbolTable enter(SymbolTable currentScope) {
        return currentScope.enter_scope();
    }

    public static SymbolTable exit(SymbolTable currentScope) {
        SymbolTable parentScope = currentScope.exit_scope();
        parentScope.killChild(currentScope);
        return parentScope;
    }

    public static void main(String[] args) throws Exception {
        Compiler.compile("void f(){int e;} int main(int f,int h){ int a; f = 6; { int b; { int b;} int c; int d; int e;} int x;}");
    }
}
