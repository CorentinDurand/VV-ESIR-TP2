package fr.istic.vv;

import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.io.BufferedWriter;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Should provide the path to the source code");
            System.exit(1);
        }

        File file = new File(args[0]);
        if (!file.exists() || !file.isDirectory() || !file.canRead()) {
            System.err.println("Provide a path to an existing readable directory");
            System.exit(2);
        }

        SourceRoot root = new SourceRoot(file.toPath());
        TCCCalculator tccCalculator = new TCCCalculator();
        root.parse("", (localPath, absolutePath, result) -> {
            result.ifSuccessful(unit -> unit.accept(tccCalculator, null));
            return SourceRoot.Callback.Result.DONT_SAVE;
        });

        // Generate the CSV report
        List<TCCResult> results = tccCalculator.getResults();
        generateCSVReport(results, "tcc_report.csv");

        generateHistogram(results, "tcc_histogram.png");

        // Générer le graphe DOT des dépendances entre les méthodes
        MethodDependencyCollector dependencyCollector = new MethodDependencyCollector();
        root.parse("", (localPath, absolutePath, result) -> {
            result.ifSuccessful(unit -> unit.accept(dependencyCollector, null));
            return SourceRoot.Callback.Result.DONT_SAVE;
        });

        Map<String, Set<String>> methodDependencies = dependencyCollector.getMethodDependencies();
        generateMethodDependencyDotGraph(methodDependencies, "method_dependency_graph.dot");
    }

    private static void generateCSVReport(List<TCCResult> results, String filePath) throws IOException {
        FileWriter csvWriter = new FileWriter(filePath);

        // Write the header
        csvWriter.append("Package,Class,TCC\n");

        // Write the data
        for (TCCResult result : results) {
            csvWriter.append(result.getPackageName())
                    .append(',')
                    .append(result.getClassName())
                    .append(',')
                    .append(String.valueOf(result.getTccValue()))
                    .append('\n');
        }

        csvWriter.flush();
        csvWriter.close();
        System.out.println("CSV Report generated at: " + filePath);
    }

    private static void generateHistogram(List<TCCResult> results, String filePath) throws IOException {
        // Préparer les données pour l'histogramme
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (TCCResult result : results) {
            // Utilise le nom de la classe comme clé pour chaque valeur TCC
            dataset.addValue(result.getTccValue(), "TCC", result.getClassName());
        }

        // Créer le graphique histogramme
        JFreeChart histogram = ChartFactory.createBarChart(
                "TCC Histogram",
                "Classes",
                "TCC",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        // Sauvegarder l'histogramme en tant qu'image PNG
        File histogramFile = new File(filePath);
        ChartUtils.saveChartAsPNG(histogramFile, histogram, 800, 600);

        System.out.println("Histogram generated at: " + filePath);
    }

    private static void generateMethodDependencyDotGraph(Map<String, Set<String>> methodDependencies, String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        writer.write("digraph G {\n");

        // Ajouter les nœuds et les dépendances entre méthodes
        for (Map.Entry<String, Set<String>> entry : methodDependencies.entrySet()) {
            String methodName = entry.getKey();
            Set<String> dependencies = entry.getValue();

            for (String dependency : dependencies) {
                writer.write(String.format("\t\"%s\" -> \"%s\";\n", methodName, dependency));
            }
        }

        writer.write("}\n");
        writer.close();
        System.out.println("Method dependency graph generated at: " + filePath);
    }
}
