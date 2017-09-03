import javafx.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shreyas on 9/3/2017.
 */
public class Executor {

    public static void main(String[] args) {
        String code = " {int ax=1; {int b = 7;} {int c = 84;} { int b = 4; {int m=2;{int l = 5;}}} int f = 3; } ";

        SymbolTable table = new SymbolTable(null);

        Pattern expression = Pattern.compile("(\\s*[{]\\s*|\\s*\\w+\\s*|\\s*[a-zA-Z]+\\s*|\\s*\\d+\\s*|\\s*[;]\\s*|\\s*[}]\\s*)");
        Matcher m = expression.matcher(code);
        ArrayList<String> tokens = new ArrayList<>();
        while (m.find()) {
            tokens.add(m.group().replace(" ", ""));
        }

        System.out.print(tokens.toString());
        Parser p = new Parser();
        p.generateTable(tokens, table);

        System.out.print('d');
    }
}

