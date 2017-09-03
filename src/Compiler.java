import java.util.List;

/**
        *       There are three types of' lines': 1) Function declaration line
        *                                      2) Variable declaration line
        *                                      3) Variable assignment line
        *                                      4) Function call
*/
 public class Compiler {

    public static Kind getKind(String kind) throws Exception{
        switch (kind) {
            case "void": return Kind.Void;
            case "int": return Kind.Int;
            case "double": return Kind.Double;
            case "bool": return Kind.Bool;
            case "float": return Kind.Float;
            default: throw new Exception("Invalid argument to getKind()");
        }
    }

    public static Type getType(String type) throws Exception{
        switch (type) {
            case "par": return Type.par;
            case "fun": return Type.fun;
            case "var": return Type.var;
            default: throw new Exception("Invalid argument to getType()");
        }
    }

    public static void compile(String code) throws Exception{
        List<String> lines = RegexParser.parseIntoLines(code);
        SymbolTable currentScope = new SymbolTable(null);
        for(String line : lines ) {
            if(line.equals("{")) {
                currentScope = currentScope.enter_scope();
            }
            else if(line.equals("}")) {
                SymbolTable parentScope = currentScope.exit_scope();
                parentScope.killChild(currentScope);
                currentScope = parentScope;
            }
            else {
                Pair<Integer, String[]> p = RegexParser.classifyLine(line);
                String[] codeLine = p.getRight();
                switch(p.getLeft()) {
                    case 1:
                        if(codeLine.length % 2 == 0) {
                            currentScope.insert(codeLine[1], getKind(codeLine[0]), getType("fun"));
                            switch(codeLine.length) {
                                case 6: currentScope.insert(codeLine[5], getKind(codeLine[4]), getType("par"));
                                case 4: currentScope.insert(codeLine[3], getKind(codeLine[2]), getType("par")); break;
                                case 2: break;
                                default:
                                    throw new Exception("Line type 1. codeLine length isn't correct. Can have a" +
                                            " Maximum of two arguments");
                            }
                        }
                        else {
                            throw new Exception("Parser couldn't parse");
                        }
                        break;
                    case 2:
                        // Check if the variable is already defined
                        currentScope.insert(codeLine[1], getKind(codeLine[0]), getType("var"));
                        break;
                    case 3:
                        // Check if the variable is already defined
                        currentScope.lookup(codeLine[0]);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception{
        Compiler.compile("int g; void f() {    { int     y; }      int x;          int y;         x = 7;          y = 5;      g(x, y); } void h(int x, int y) {    { int z; }      int x; }");
    }
}
