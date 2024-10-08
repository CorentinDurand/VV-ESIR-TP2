package fr.istic.vv;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

// This class visits a compilation unit and prints all public classes, their private fields without public getters
public class PublicElementsPrinter extends VoidVisitorWithDefaults<Void> {
    private BufferedWriter writer;

    // Constructor to initialize the BufferedWriter
    public PublicElementsPrinter(String outputFilePath) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(outputFilePath));
    }

    // Close the BufferedWriter when done
    public void close() throws IOException {
        writer.close();
    }
    @Override
    public void visit(CompilationUnit unit, Void arg) {
        try {
            String packageName = unit.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("[No Package]");

            for (TypeDeclaration<?> type : unit.getTypes()) {
                writer.write("Package: " + packageName + "\n");
                type.accept(this, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        try {
            if (!declaration.isPublic()) return;

            writer.write("  Class: " + declaration.getFullyQualifiedName().orElse("[Anonymous]") + "\n");

            // Check for private fields without public getters
            for (FieldDeclaration field : declaration.getFields()) {
                for (VariableDeclarator variable : field.getVariables()) {
                    if (field.isPrivate() && !hasPublicGetter(declaration, variable.getNameAsString())) {
                        writer.write("    Private field without public getter: " + variable.getNameAsString() + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
