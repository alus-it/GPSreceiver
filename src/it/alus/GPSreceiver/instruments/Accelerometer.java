//============================================================================
// This file is part of GPSreceiver: a Java demo program that parses NMEA
// sentences from a serial GPS receiver and displays live the received data.
// Author         : Alberto Realis-Luc <alberto.realisluc@gmail.com>
// Since          : July 2010
// Web            : http://www.alus.it/airnavigator/gpsreceiver/
// Git Repository : https://github.com/alus-it/GPSreceiver.git
// Version        : 0.1
// Copyright      : © 2010 Alberto Realis-Luc
// License        : GPL
// Last change    : 2 March 2011
//============================================================================

package it.alus.GPSreceiver.instruments;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialBackground;
import org.jfree.chart.plot.dial.DialCap;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialTextAnnotation;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.plot.dial.DialPointer.Pointer;
import org.jfree.data.general.DefaultValueDataset;

public class Accelerometer extends ChartPanel {
	private static final long serialVersionUID = -9175426572282645279L;
	private JFreeChart jChart;
	private DefaultValueDataset dataset;

	 public Accelerometer() {
		 super(null);
		 dataset = new DefaultValueDataset(0.0);
		 DialPlot plot = new DialPlot();
		 plot.setView(0.0,0.0,1.0,1.0);
		 plot.setDataset(0,dataset);
		 StandardDialFrame dialFrame = new StandardDialFrame();
		 dialFrame.setBackgroundPaint(Color.lightGray);
		 dialFrame.setForegroundPaint(Color.gray);
		 DialTextAnnotation titleLabel = new DialTextAnnotation("Acceleration");
		 titleLabel.setFont(new Font("Arial",1,18));
		 titleLabel.setRadius(0.45D);
		 titleLabel.setAngle(90);
		 titleLabel.setPaint(Color.lightGray);
		 plot.addLayer(titleLabel);
		 DialValueIndicator valueindicator = new DialValueIndicator(0);
		 plot.addLayer(valueindicator);
		 DialTextAnnotation annotation = new DialTextAnnotation("g");
		 annotation.setFont(new Font("Arial",1,20));
		 annotation.setRadius(0.4D);
		 annotation.setPaint(Color.lightGray);
		 plot.addLayer(annotation);
		 plot.setDialFrame(dialFrame);
		 StandardDialScale scale = new StandardDialScale(-1,3,-95,-340,1,20);
		 scale.setMajorTickPaint(Color.white);
		 scale.setMinorTickPaint(Color.lightGray);
		 scale.setFirstTickLabelVisible(true);
		 scale.setTickRadius(0.88);
		 scale.setTickLabelOffset(0.15);
		 NumberFormat formatter = new DecimalFormat("#");
		 scale.setTickLabelFormatter(formatter);
		 scale.setTickLabelFont(new Font("Arial",Font.BOLD,24));
		 scale.setTickLabelPaint(Color.white);
		 plot.addScale(0,scale);
		 plot.setBackground(new DialBackground(Color.black));
		 Pointer needle = new Pointer(0);
		 needle.setFillPaint(Color.white);
		 plot.addLayer(needle);
		 plot.mapDatasetToScale(1, 1);
		 DialCap cap = new DialCap();
		 cap.setRadius(0.10);
		 cap.setFillPaint(Color.gray);
		 plot.setCap(cap);
		 jChart= new JFreeChart(plot);
		 super.setChart(jChart);
		 super.setPreferredSize(new Dimension(400, 400));
	 }
	 
	 public void updateAcceleration(float newAccelerationG) {
		dataset.setValue(newAccelerationG);
	 }

}
