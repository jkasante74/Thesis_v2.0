package gui;
/* --------------------
 * BarChart3DDemo4.java
 * --------------------
 * (C) Copyright 2005-2008, by Object Refinery Limited.
 *
 */
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;
import org.jfree.ui.TextAnchor;

import historicalInfo.HistoricalInfoMgr;

/**
 * This chart shows how to override the getItemPaint() method to set a color
 * that depends on the data value.
 */
public class BarChart3DDemo4 extends JFrame {

	// Parameter variables 
	private static final long serialVersionUID = 1L;
	public static DefaultCategoryDataset dataset = new DefaultCategoryDataset();
	private static int expNum; // requested experiment index
	public static ArrayList<String> agentStrategies = new ArrayList<String>();

	
    /**
     * A custom renderer that returns a different color for each item in a
     * single series.
     */
    class CustomBarRenderer3D extends BarRenderer3D {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
         * Creates a new renderer.
         */
        public CustomBarRenderer3D() {
        }

     }
    
    
    
    /**
     * Creates a new demo.
     *
     * @param title  the frame title.
     */
    public BarChart3DDemo4(String title) {
        super(title);
        CategoryDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1200, 770));
        setContentPane(chartPanel);
    }

    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
   private static CategoryDataset createDataset() {
     
	   dataset.clear();
		
	   // Query HIM to submit data for bar chart display 
	   HistoricalInfoMgr.getChartDataset(expNum);
		return dataset;

     }

    /**
     * Creates a chart.
     *
     * @param dataset  the dataset.
     *
     * @return The chart.
     */
    private static JFreeChart createChart(CategoryDataset dataset) {

        JFreeChart chart = ChartFactory.createBarChart3D(
            "Experiment "+ (expNum + 1),      // chart title
            "Tournament",               // domain axis label
            "Cummulative Pay-Off",                  // range axis label
            dataset,                  // data
            PlotOrientation.VERTICAL, // orientation
            true,                     // include legend
            true,                     // tooltips
            false                     // urls
        );
        LegendTitle legend = chart.getLegend();
        Font labelFont = new Font("Arial", Font.BOLD, 12);
        legend.setItemFont(labelFont);
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        
        renderer.setBaseItemLabelGenerator(
                new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setItemLabelAnchorOffset(10.0);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
     //   plot.setRenderer(renderer);
      //  renderer.setBaseItemLabelsVisible(true);
//        renderer.setMaximumBarWidth(0.05);

        
        renderer.getLegendItems();
        // set the color (r,g,b) or (r,g,b,a)
        Color color = new Color(79, 129, 189);
 
        
        // Set colors depending on agent strategies
        for (int i = 0; i < dataset.getRowCount(); i++){
            String s = dataset.getRowKey(i).toString(); 
        	int agentId = Integer.parseInt(s.substring(6,7));
        
        	switch(agentStrategies.get(agentId-1)){
        	
        	case "Advanced_C": 
            	color = new Color(248,171,38);
            	break;
        	
        	case "Naive_C": 
            	color = new Color(252,197,90);
            	break;
            	
        	case "Naive_D": 
            	color = new Color(254,251,188);
            	break;
            	
        	case "Advanced_D": 
            	color = new Color(30,104,94);
            	break;
            
        	case "Advanced_E": 
            	color = new Color(023,129,179);
            	break;
            
        	}
            renderer.getLegendItems();
            renderer.setSeriesPaint(i, color);
            
        }
        

        return chart;
        
    }

    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     *
     * @return A panel.
     */
    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        return new ChartPanel(chart);
    }

    
   
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {
		expNum = Integer.parseInt(args[0]);

        BarChart3DDemo4 demo = new BarChart3DDemo4(
                "Bar Chart of Agents Pay-off vrs Tournament");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}