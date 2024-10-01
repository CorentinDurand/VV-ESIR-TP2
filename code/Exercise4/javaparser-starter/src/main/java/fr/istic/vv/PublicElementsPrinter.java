package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;


// This class visits a compilation unit and prints all public classes, their private fields without public getters
public class PublicElementsPrinter extends VoidVisitorWithDefaults<Void> {

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        String packageName = unit.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("[No Package]");

        for (TypeDeclaration<?> type : unit.getTypes()) {
            System.out.println("Package: " + packageName);
            type.accept(this, null);
        }
    }

    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        if (!declaration.isPublic()) return;

        System.out.println("  Class: " + declaration.getFullyQualifiedName().orElse("[Anonymous]"));

        // Check for private fields without public getters
        for (FieldDeclaration field : declaration.getFields()) {
            for (VariableDeclarator variable : field.getVariables()) {
                if (field.isPrivate() && !hasPublicGetter(declaration, variable.getNameAsString())) {
                    System.out.println("    Private field without public getter: " + variable.getNameAsString());
                }
            }
        }

        // Printing nested types in the top level
        for (BodyDeclaration<?> member : declaration.getMembers()) {
            if (member instanceof TypeDeclaration)
                member.accept(this, arg);
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(EnumDeclaration declaration, Void arg) {
        visitTypeDeclaration(declaration, arg);
    }

    @Override
    public void visit(MethodDeclaration declaration, Void arg) {
        if (declaration.isPublic()) {
            System.out.println("  " + declaration.getDeclarationAsString(true, true));
        }
    }

    // Helper method to check if there is a public getter for the field
    private boolean hasPublicGetter(TypeDeclaration<?> declaration, String fieldName) {
        String getterName = "get" + capitalize(fieldName);

        for (MethodDeclaration method : declaration.getMethods()) {
            if (method.isPublic() &&
                (method.getNameAsString().equals(getterName))) {
                return true;
            }
        }
        return false;
    }

    // Helper method to capitalize the field name for getter convention
    private String capitalize(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
