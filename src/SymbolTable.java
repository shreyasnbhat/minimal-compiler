import java.util.*;

public class SymbolTable {

    private HashMap<String, Pair<Kind, Type>> currentScope;
    private List<SymbolTable> childScopes;
    private SymbolTable parentScope;

    /*inserts an entry for x in the current scope if it is already not defined in
    it. Throws an error if the variable name is already present in the most recent scope.*/
    public void insert(String name, Kind kind, Type type) throws Exception {

        if (currentScope.containsKey(name)) throw new Exception("Identifier already present in currentscope");
        try {
            currentScope.put(name, new Pair<>(kind, type));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public SymbolTable(SymbolTable parentScope) {
        this.parentScope = parentScope;
        this.childScopes = new ArrayList<>();
        this.currentScope = new HashMap<>();
    }

    /*returns the most recent definition of name by searching the tree from leaf to root. If no match is found it throws an error.*/
    public SymbolTable lookup(String name) throws Exception {

        if (currentScope.containsKey(name)) {
            return this;
        } else if (parentScope != null) {
            return parentScope.lookup(name);
        } else throw new Exception("Identifer not found in any scope for " + name);

    }

    /* Generates a new level of nesting by creating a symbol table for the new scope. */
    public SymbolTable enter_scope() {
        SymbolTable child = new SymbolTable(this);
        childScopes.add(child);
        return child;
    }

    /*remove symbol table entries for the current scope and move back to the enclosing
      scope in the symbol table tree. */
    public SymbolTable exit_scope() {
        /*
        currentScope.clear();
        for (SymbolTable child : parentScope.getChildScopes()) {
            if (child.equals(this)) {
                parentScope.killChild(child);
            }
        }
        */
        return parentScope;
    }

    public List<SymbolTable> getChildScopes() {
        return this.childScopes;
    }

    public void killChild(SymbolTable child) {
        this.childScopes.remove(child);
    }

    @Override
    public String toString() {

        StringBuilder toString = new StringBuilder();

        toString.append("SymbolTable{ \n").append("currentScope\n");
        for (Map.Entry<String, Pair<Kind, Type>> pair : currentScope.entrySet()) {
            toString.append(pair.getKey()).append("        ").append(pair.getValue().getLeft()).append("         ").append(pair.getValue().getRight()).append("\n");
        }

        return toString.toString();
    }
}