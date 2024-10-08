
package fr.istic.vv;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;

public class PublicElementsPrinter extends VoidVisitorWithDefaults<Void> {
    private FileWriter writer;
    private String packageName, className;
    private List<Integer> ccValues = new ArrayList<>();

    // Constructor to initialize the BufferedWriter
    public PublicElementsPrinter(String outputFilePath) throws IOException {
        try {
            this.writer = new FileWriter(outputFilePath);
            writer.append("Package,Classe,Methode,Parametres,CC\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Close the BufferedWriter when done
    public void close() throws IOException {
        writer.close();
    }

    @Override
    public void visit(CompilationUnit unit, Void arg) {
        packageName = unit.getPackageDeclaration().map(pd -> pd.getNameAsString()).orElse("[No Package]");

        for (TypeDeclaration<?> type : unit.getTypes()) {
            type.accept(this, null);
        }
    }

    public void visitTypeDeclaration(TypeDeclaration<?> declaration, Void arg) {
        if(!declaration.isPublic()) return;
        className = declaration.getFullyQualifiedName().orElse("[Anonymous]");
        for(MethodDeclaration method : declaration.getMethods()) {
            method.accept(this, arg);
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
        try {
            System.out.println();
            // Calcul de la complexité cyclomatique
            int complexity = calculateCyclomaticComplexity(declaration);
            ccValues.add(complexity); 
            String methodName = declaration.getName().toString();
            String parameterName = declaration.getParameters().toString().replace(",","|");
            String row = String.format("%s,%s,%s,%s,%d\n",packageName, className, methodName,parameterName,complexity);
            writer.append(row);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int calculateCyclomaticComplexity(MethodDeclaration method) {
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        method.getBody().ifPresent(body -> body.accept(visitor, null)); // Assurez-vous que le corps de la méthode est bien visité
        return visitor.getComplexity();
    }
    public List<Integer> getCcValues() {
        return ccValues;
    }

    private static class CyclomaticComplexityVisitor extends VoidVisitorWithDefaults<Void> {
        private int complexity = 1;  // Complexité de base

        @Override
        public void visit(BlockStmt stmt, Void arg) {
            super.visit(stmt, arg);
            stmt.getStatements().forEach(statement -> statement.accept(this, arg)); // Visiter explicitement chaque instruction du bloc
        }

        @Override
        public void visit(IfStmt stmt, Void arg) {
            super.visit(stmt, arg);
            complexity++;
            stmt.getThenStmt().accept(this, arg);  // Visiter le bloc 'then'
            stmt.getElseStmt().ifPresent(elseStmt -> elseStmt.accept(this, arg));  // Visiter le bloc 'else'
        }

        @Override
        public void visit(ForStmt stmt, Void arg) {
            super.visit(stmt, arg);
            complexity++;
            stmt.getBody().accept(this, arg);  // Visiter le corps de la boucle
        }

        @Override
        public void visit(ForEachStmt stmt, Void arg) {
            super.visit(stmt, arg);
            complexity++;
            stmt.getBody().accept(this, arg);  // Visiter le corps de la boucle
        }

        @Override
        public void visit(WhileStmt stmt, Void arg) {
            super.visit(stmt, arg);
            complexity++;
            stmt.getBody().accept(this, arg);  // Visiter le corps de la boucle
        }

        @Override
        public void visit(DoStmt stmt, Void arg) {
            super.visit(stmt, arg);
            complexity++;
            stmt.getBody().accept(this, arg);  // Visiter le corps de la boucle
        }

        @Override
        public void visit(SwitchStmt stmt, Void arg) {
            super.visit(stmt, arg);
            complexity += stmt.getEntries().size(); // Chaque cas est un chemin indépendant
            stmt.getEntries().forEach(entry -> entry.getStatements().forEach(statement -> statement.accept(this, arg)));
        }

        @Override
        public void visit(CatchClause stmt, Void arg) {
            super.visit(stmt, arg);
            complexity++;
            stmt.getBody().accept(this, arg);  // Visiter le corps du bloc catch
        }

        public int getComplexity() {
            return complexity;
        }
    }
}
