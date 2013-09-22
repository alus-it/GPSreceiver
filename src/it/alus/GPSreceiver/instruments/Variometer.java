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

public class Variometer extends ChartPanel {
	private static final long serialVersionUID = 5923210898165025281L;
	private JFreeChart jChart;
	private DefaultValueDataset dataset;

	 public Variometer() {
		 super(null);
		 dataset = new DefaultValueDataset(0.0);
		 DialPlot plot = new DialPlot();
		 plot.setView(0.0,0.0,1.0,1.0);
		 plot.setDataset(0,dataset);
		 StandardDialFrame dialFrame = new StandardDialFrame();
		 dialFrame.setBackgroundPaint(Color.lightGray);
		 dialFrame.setForegroundPaint(Color.gray);
		 DialTextAnnotation titleLabel = new DialTextAnnotation("Vertical speed");
		 titleLabel.setFont(new Font("Arial",1,18));
		 titleLabel.setRadius(0.45D);
		 titleLabel.setAngle(90);
		 titleLabel.setPaint(Color.lightGray);
		 plot.addLayer(titleLabel);
		 DialTextAnnotation unitLabel = new DialTextAnnotation("100 Ft/min");
		 unitLabel.setFont(new Font("Arial",1,18));
		 unitLabel.setRadius(0.3D);
		 unitLabel.setAngle(90);
		 unitLabel.setPaint(Color.white);
		 plot.addLayer(unitLabel);
		 DialValueIndicator valueindicator = new DialValueIndicator(0);
		 plot.addLayer(valueindicator);
		 DialTextAnnotation annotation = new DialTextAnnotation("Ft/min");
		 annotation.setFont(new Font("Arial",1,14));
		 annotation.setRadius(0.4D);
		 annotation.setPaint(Color.lightGray);
		 plot.addLayer(annotation);
		 plot.setDialFrame(dialFrame);
		 StandardDialScale realScale = new StandardDialScale(-3000,3000,-10,-340,100,10);
		 realScale.setVisible(false);
		 plot.addScale(0,realScale);
		 StandardDialScale dispScale = new StandardDialScale(-30,30,-10,-340,5,10);
		 dispScale.setMajorTickPaint(Color.white);
		 dispScale.setMinorTickPaint(Color.lightGray);
		 dispScale.setFirstTickLabelVisible(true);
		 dispScale.setTickRadius(0.88);
		 dispScale.setTickLabelOffset(0.15);
		 NumberFormat formatter = new DecimalFormat("#");
		 dispScale.setTickLabelFormatter(formatter);
		 dispScale.setTickLabelFont(new Font("Arial",Font.BOLD,24));
		 dispScale.setTickLabelPaint(Color.white);
		 plot.addScale(1,dispScale);
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
	 
	 public void updateVerticalSpeed(float newVerticalSpeedFtMin) {
		dataset.setValue(newVerticalSpeedFtMin);
	 }

}
