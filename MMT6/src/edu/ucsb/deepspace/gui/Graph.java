package edu.ucsb.deepspace.gui;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

public class Graph{
	
	private final Display display;
	private final Shell shell;
	private final Button zenith;
	private final Combo scaleSelect;
	private final Button error;
	private final Button distance;
	private final Button azimuth;
	private final JFreeChart jChart;
	private String range = "";
	private final ChartComposite chartArea;
	private String scale;
	private final static List<String> scaleList = Arrays.asList("m", "mm", "micron");
	
	private final TimeSeries azimuthData = new TimeSeries("Azimuth", "Time", "Azimuth");
	private final TimeSeries zenithData = new TimeSeries("Zenith", "Time", "Zenith");
	private final TimeSeries distanceData = new TimeSeries("Distance", "Time", "Distance");
	private final TimeSeries errorData = new TimeSeries("Error", "Time", "Error");

	private final TimeSeriesCollection dataset;
	
	public static void main(String[]args) {
		Graph graph = new Graph();
		graph.show();
	}

	public Graph() {
		display = Display.getDefault();
		shell = new Shell(display, SWT.CLOSE | SWT.TITLE | SWT.MIN | SWT.MAX);
		//shell.setImage(SWTResourceManager.getImage("images/16x16.png"));
		shell.setText("MMT Control - Graph");
		shell.setSize(600, 689);

        this.dataset = new TimeSeriesCollection(errorData);
        jChart = createChart(dataset);
        
		chartArea=new ChartComposite(shell, SWT.NULL, jChart, 500, 500, 50, 50, 100, 100, false, true, true, true, true, true);
		chartArea.setBounds(0, 0, 600, 600);
		
			error = new Button(shell, SWT.RADIO | SWT.LEFT);
			error.setText("Position Error");
			error.setBounds(207, 613, 83, 32);
			error.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					range = "Position Error";
					yAxisLabelChange();
				}
			});
			
			azimuth = new Button(shell, SWT.RADIO | SWT.LEFT);
			azimuth.setText("Azimuth");
			azimuth.setBounds(12, 613, 62, 30);
			azimuth.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					range = "Azimuth";
					yAxisLabelChange();
				}
			});
			
			zenith = new Button(shell, SWT.RADIO | SWT.LEFT);
			zenith.setText("Zenith");
			zenith.setBounds(79, 613, 61, 30);
			zenith.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					range = "Zenith";
					yAxisLabelChange();
				}
			});
			
			distance = new Button(shell, SWT.RADIO | SWT.LEFT);
			distance.setText("Distance");
			distance.setBounds(140, 613, 61, 32);
			distance.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					range = "Distance";
					yAxisLabelChange();
				}
			});
			
			scaleSelect = new Combo(shell, SWT.NONE);
			scaleSelect.setBounds(416, 622, 60, 22);
			for (String s : scaleList) {
				scaleSelect.add(s);
			}
			scaleSelect.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					scale = scaleSelect.getText();
					yAxisLabelChange();
				}
			});
			
		this.range = "Position Error";
        this.scale = "m";
        error.setSelection(true);
        scaleSelect.setText("m");
	}
	
    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            "Distance vs Time Graph", 
            "Time", 
            "Distance",
            dataset, 
            true, 
            true, 
            false
        );
        final XYPlot plot = result.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis.setFixedAutoRange(60000.0);  // 60 seconds
        axis = plot.getRangeAxis();
        axis.setRange(3.89, 3.892);
        return result;
    }

	public void show() {
		shell.open();

		while (!shell.isDisposed())
			if (!display.readAndDispatch())
				display.sleep();	
	}

	public boolean isDisposed() {
		return shell.isDisposed();
	}
	
	public void setRange(String range) {
		this.range = range;
	}
	
	public void yAxisLabelChange() {
		String temp = scale;
		if (range.equals("Azimuth") || range.equals("Zenith")) {
			temp = "deg";
		}
		jChart.getXYPlot().getRangeAxis().setLabel(range + " (" + temp + ")");
		dataset.removeAllSeries();
		dataset.addSeries(changeScale());
	}
	
	public String getRange() {
		return this.range;
	}
	
	public void addData(double azi, double zen, double dist, double error) {
		RegularTimePeriod now = new Millisecond();
		azimuthData.addOrUpdate(now, azi);
		zenithData.addOrUpdate(now, zen);
		distanceData.addOrUpdate(now, dist);
		errorData.addOrUpdate(now, error);
		
		double factor = makeFactor();
		
		double val = 0;
		if (range.equals("Azimuth")) {
			val = azi;
		}
		else if (range.equals("Zenith")) {
			val = zen;
		}
		else if (range.equals("Distance")) {
			val = dist*factor;
		}
		else if (range.equals("Position Error")) {
			val = error*factor;
		}
		((TimeSeries) dataset.getSeries().get(0)).addOrUpdate(now, val);
	}
	
	private double makeFactor() {
		if (scale.equals("m")) {
			return 1.0;
		}
		else if (scale.equals("mm")) {
			return 1000;
		}
		else if (scale.equals("micron")) {
			return 1000000;
		}
		return 1;
	}
	
	public TimeSeries changeScale() {
		double factor = makeFactor();
		
		TimeSeries temp = null;
		try {
			if (range.equals("Azimuth")) {
				temp = (TimeSeries) azimuthData.clone();
			}
			else if (range.equals("Zenith")) {
				temp = (TimeSeries) zenithData.clone();
			}
			else if (range.equals("Distance")) {
				temp = (TimeSeries) distanceData.clone();
			}
			else if (range.equals("Position Error")) {
				temp = (TimeSeries) errorData.clone();
			}
		} catch (CloneNotSupportedException ignored) {}
		
		for (int i = 0; i < temp.getItemCount(); i++) {
			double num = (Double) temp.getDataItem(i).getValue();
			temp.update(i, num*factor);
		}
		return temp;
	}

}