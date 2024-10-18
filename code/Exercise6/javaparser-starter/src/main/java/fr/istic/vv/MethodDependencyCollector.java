package fr.istic.vv;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MethodDependencyCollector extends VoidVisitorAdapter<Void> {
    
    private final Map<String, Set<String>> methodDependencies = new HashMap<>();

    @Override
    public void visit(MethodDeclaration md, Void arg) {
        super.visit(md, arg);

        String methodName = md.getNameAsString();
        Set<String> dependencies = new HashSet<>();

        // Analyser le corps de la méthode pour trouver les appels à d'autres méthodes
        md.findAll(MethodCallExpr.class).forEach(methodCall -> {
            String calledMethod = methodCall.getNameAsString();
            dependencies.add(calledMethod);
        });

        // Ajouter la méthode et ses dépendances
        methodDependencies.put(methodName, dependencies);
    }

    public Map<String, Set<String>> getMethodDependencies() {
        return methodDependencies;
    }
}
