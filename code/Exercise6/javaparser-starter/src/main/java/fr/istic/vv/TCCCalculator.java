package fr.istic.vv;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

public class TCCCalculator extends VoidVisitorAdapter<Void> {

    private final List<TCCResult> results = new ArrayList<>();

    @Override
    public void visit(ClassOrInterfaceDeclaration n, Void arg) {
        super.visit(n, arg);

        List<FieldDeclaration> fields = n.findAll(FieldDeclaration.class);
        List<MethodDeclaration> methods = n.findAll(MethodDeclaration.class);

        double tcc = calculateClassTCC(methods, fields);

        // Store the result (assuming package name is available via n.getFullyQualifiedName())
        String packageName = n.findCompilationUnit().flatMap(cu -> cu.getPackageDeclaration().map(pd -> pd.getNameAsString())).orElse("");
        results.add(new TCCResult(packageName, n.getNameAsString(), tcc));
    }

    private double calculateClassTCC(List<MethodDeclaration> methods, List<FieldDeclaration> fields) {
        int totalPairs = methods.size() * (methods.size() - 1) / 2; // Total method pairs
        int connectedPairs = 0;

        // Compare each pair of methods
        for (int i = 0; i < methods.size(); i++) {
            for (int j = i + 1; j < methods.size(); j++) {
                if (methodsShareField(methods.get(i), methods.get(j), fields)) {
                    connectedPairs++;
                }
            }
        }

        // Return 1.0 if there are no pairs, otherwise return the ratio of connected pairs
        return totalPairs == 0 ? 1.0 : (double) connectedPairs / totalPairs;
    }

    private boolean methodsShareField(MethodDeclaration m1, MethodDeclaration m2, List<FieldDeclaration> fields) {
        // Get the accessed fields for each method
        Set<String> m1Fields = getAccessedFields(m1);
        Set<String> m2Fields = getAccessedFields(m2);

        // Check if they share at least one field
        for (String field : m1Fields) {
            if (m2Fields.contains(field)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> getAccessedFields(MethodDeclaration method) {
        Set<String> accessedFields = new HashSet<>();

        // Collect field accesses via NameExpr and FieldAccessExpr
        method.findAll(NameExpr.class).forEach(nameExpr -> accessedFields.add(nameExpr.getNameAsString()));
        method.findAll(FieldAccessExpr.class).forEach(fieldAccess -> accessedFields.add(fieldAccess.getNameAsString()));

        return accessedFields;
    }

    public List<TCCResult> getResults() {
        return results;
    }
}
