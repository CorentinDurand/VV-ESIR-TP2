package fr.istic.vv;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.util.List;

public class CyclomaticComplexityChart {

    public static void displayHistogram(List<Integer> ccValues) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < ccValues.size(); i++) {
            dataset.addValue(ccValues.get(i), "Complexité Cyclomatique", "Méthode " + (i + 1));
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Répartition des valeurs de Complexité Cyclomatique",
                "Méthodes",
                "Complexité",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));

        JFrame frame = new JFrame();
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}