/**
 * Created by Shreyas on 9/9/2017.
 */
public class Utils {

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
}
