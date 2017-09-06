import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RegexParser {

    /**
     * Parse C statements of the following type:
     * 1) Split string into lines at the following breakpoints: a) '{' b) '}' c) ;
     * 2) There are three types of' lines': 1) Function declaration line
     * 2) Variable declaration line
     * 3) Variable assignment line
     * 4) Function call
     * <p>
     * Example: void f() {
     * int x;
     * int y;
     * x = 7;
     * y = 5;
     * }
     */

    public static List<String> parseIntoLines(String x) {
        x = x.replace('\n', ' ');
        x = x.replace("{", "{ \n");
        x = x.replace("}", "} \n");
        String[] lines = x.split("(?=[{}])");
        List<String> lines2 = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
            for (String line : lines[i].split("[;\n]"))
                lines2.add(line.trim());
        }
        return lines2;
    }

    public static Pair<Integer, String[]> classifyLine(String y) throws Exception {


        /**
         * This section pertains to any function call / definition
         * Case 1 would be a function definition
         * Case 2 would be a function call
          */
        if (y.contains("(")) {
            assert y.contains(")");

            // Strips the brackets from the function call definitions
            String z2 = y.split("\\(\\s*[\\w]*\\s*[\\w]*\\s*(,\\s*[\\w]*\\s*[\\w]*\\s*)?\\)")[0];

            // This stores the final list of tokens including function_name and parameters
            ArrayList<String> tokens = new ArrayList<>();

            String[] tokens1 = z2.split(" ");

            int left = 0;

            /**
             * Case 1
             * function_name ( )
             * Tokens : [function_name]  Function Call
             * < 4 , function_name param1 param2 >
             *
             * Case 2
             * return_type function_name ( type1 param1 , type2 param2 )
             * Tokens : [return_type function_name , type1 param1 type2 param2]
             * Second token might be null for no parameters
             * < 1 , return_type function_name type1 param1 type2 param2 >
             * Otherwise throw Exception
             */
            if (tokens1.length == 1) {
                //String[] ret = {tokens1[0], tokens};
                left = 4;
                tokens.add(tokens1[0]);
            } else if (tokens1.length == 2) {
                left = 1;
                tokens.add(tokens1[0]);
                tokens.add(tokens1[1]);
            } else {
                throw new Exception("Syntax error with " + y);
            }

            /**
             * Regex pattern to retrieve contents inside the brackets from the function call / declaration
             * Example : void foo ( int x , int y ) ---->  ( int x , int y )
             * Example : g () ----> ()
             */
            Pattern p = Pattern.compile("\\(\\s*[\\w]*\\s*[\\w]*\\s*(,\\s*[\\w]*\\s*[\\w]*\\s*)?\\)");
            Matcher m = p.matcher(y);

            if (m.find()) {
                String z = m.group();
                tokens1 = z.split("\\(\\s*|\\s*\\)|\\s*,\\s*|\\s+");
                if (tokens1.length > 1) {
                    for (int i = 1; i < tokens1.length; i++) {
                        tokens.add(tokens1[i]);
                    }
                }
            } else
                throw new Exception("Syntax error with " + y);
            String[] ret = new String[tokens.size()];
            return new Pair<>(left, tokens.toArray(ret));
        }
        /**
         *  This handle the cases of variable initialization and declaration
         *  Case 1 :
         *  variable_type variable_name
         *  Tokens : [variable_type , variable_name ]  Variable Declaration
         *  < 2 , variable_type variable_name >
         *
         * Case 2 :
         *  variable_name = value
         *  Tokens : [variable_name , value ]  Variable Initialization
         *  < 3 , variable_name value >
         *
         *  These cases are executed if no () are found. A simple split across spaces occurs to get all tokens
         */
        else {
            String[] tokens = y.split("\\s+");
            if (tokens.length >=2 && tokens.length <=3 ) {
                if (tokens.length == 3 && !tokens[1].equals("=")){
                    throw new Exception("Variable Assignment expected at " + y );
                }
                for (int i = 0; i < tokens.length; i++) {
                    tokens[i] = tokens[i].trim();
                }
                return new Pair<>(tokens.length, tokens);
            } else {
                throw new Exception("Syntax error with " + y);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<String> lines = parseIntoLines("void f(){int e;} int main(int f,int h){ int a; a = 6; { int b; int c; int d; int e;} int x;}");

        for (String line : lines) {
            if (line.equals("{") | line.equals("}")) continue;
            Pair<Integer, String[]> p = classifyLine(line);
            System.out.print(p.getLeft() + ":  ");
            String[] parsed = p.getRight();
            for (String x : parsed)
                System.out.print(x + " ");
            System.out.println();
        }
    }

}
