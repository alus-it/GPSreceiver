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
import java.awt.Image;
import java.awt.Toolkit;
//import java.awt.Image;
//import java.awt.Toolkit;
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

public class Altimeter extends ChartPanel {
	private static final long serialVersionUID = -3627503745675193082L;
	private JFreeChart jChart;
	private float altitude;
	private float groundAltitude;
	private DefaultValueDataset hoursDataset;
	private DefaultValueDataset minutesDataset;

	 public Altimeter(float groundAltitudeFt) {
		 super(null);
		 altitude=0;
		 if(groundAltitudeFt<0) groundAltitude=0;
		 else groundAltitude=groundAltitudeFt;
		 hoursDataset = new DefaultValueDataset(0.0);
		 minutesDataset = new DefaultValueDataset(0.0);
		 DialPlot plot = new DialPlot();
		 plot.setView(0.0, 0.0, 1.0, 1.0);
		 Image background=Toolkit.getDefaultToolkit().getImage(getClass().getResource("altimeter.png"));
		 plot.setDataset(0,hoursDataset);
		 plot.setDataset(1,minutesDataset);
		 StandardDialFrame dialFrame = new StandardDialFrame();
		 dialFrame.setBackgroundPaint(Color.lightGray);
		 dialFrame.setForegroundPaint(Color.gray);
		 DialTextAnnotation annotation = new DialTextAnnotation("Feet");
		 annotation.setFont(new Font("Arial",1,14));
		 annotation.setRadius(0.4D);
		 annotation.setPaint(Color.lightGray);
		 plot.addLayer(annotation);
		 DialValueIndicator valueindicator = new DialValueIndicator(0);
		 NumberFormat formatter = new DecimalFormat("#");
		 plot.addLayer(valueindicator);
		 plot.setDialFrame(dialFrame);
		 StandardDialScale scale = new StandardDialScale(0,10,90,-360,1,10);
		 scale.setMajorTickPaint(Color.white);
		 scale.setMinorTickPaint(Color.lightGray);
		 scale.setFirstTickLabelVisible(false);
		 scale.setTickRadius(0.88);
		 scale.setTickLabelOffset(0.15);
		 scale.setTickLabelFormatter(formatter);
		 scale.setTickLabelFont(new Font("Arial",Font.BOLD,24));
		 scale.setTickLabelPaint(Color.white);
		 plot.addScale(3,scale);
		 plot.setBackground(new DialBackground(Color.black));
		 StandardDialScale hourScale = new StandardDialScale(0,10000,90,-360,1000,10);
		 hourScale.setVisible(false);
		 hourScale.setTickRadius(0.88);
		 hourScale.setTickLabelOffset(0.15);
		 plot.addScale(0, hourScale);
		 StandardDialScale minScale = new StandardDialScale(0,1000,90,-360,0,0);
		 minScale.setVisible(false);
		 minScale.setMajorTickIncrement(5.0);
		 minScale.setTickRadius(0.68);
		 plot.addScale(1,minScale);
		 Pointer hourNeedle = new Pointer(0);
		 hourNeedle.setRadius(0.55);
		 hourNeedle.setFillPaint(Color.lightGray);
		 plot.addLayer(hourNeedle);
		 plot.mapDatasetToScale(1, 1);
		 Pointer minNeedle = new Pointer(1);
		 minNeedle.setFillPaint(Color.white);
		 plot.addLayer(minNeedle);
		 DialCap cap = new DialCap();
		 cap.setRadius(0.10);
		 cap.setFillPaint(Color.lightGray);
		 plot.setCap(cap);
		 jChart= new JFreeChart(plot);
		 jChart.setBackgroundImage(background);
		 super.setChart(jChart);
		 super.setPreferredSize(new Dimension(400,400));
	 }

	 public void updateAltitudeFt(float altFt) {
		 altitude=altFt;
		 if(altitude>=groundAltitude) altFt=altitude-groundAltitude;
		 hoursDataset.setValue(altFt);
		 int migliaia=(int)altFt/1000;
		 float minPart=altFt-migliaia*1000;
		 minutesDataset.setValue((int)minPart);
	 }
	 
	 public void setGraoundAltitudeFt(float groundAltitudeFt) {
		 groundAltitude=groundAltitudeFt;
	 }
	 
	 public float getAltitudeFt() {
		 return altitude;
	 }
	 
	 public float getGroundAltitudeFt() {
		 return groundAltitude;
	 }
	 
	 public float getAltitudeFromGroundFt() {
		 return altitude-groundAltitude;
	 }
}
