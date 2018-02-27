//============================================================================
// This file is part of GPSreceiver: a Java demo program that parses NMEA
// sentences from a serial GPS receiver and displays live the received data.
// Author         : Alberto Realis-Luc <alberto.realisluc@gmail.com>
// Since          : July 2010
// Web            : http://www.alus.it/airnavigator/gpsreceiver/
// Git repository : https://github.com/alus-it/GPSreceiver.git
// Version        : 0.1
// Copyright      : © 2010-2018 Alberto Realis-Luc
// License        : GPL
//============================================================================

package it.alus.GPSreceiver.instruments;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import org.jfree.ui.TextAnchor;

public class Compass extends ChartPanel {
	private static final long serialVersionUID = 6212084781783666549L;
	private JFreeChart jChart;
	private DefaultValueDataset dataset;
	private StandardDialScale realScale;
	private StandardDialScale dispScale;
	private DialTextAnnotation N,S,W,E;
	private boolean isTrueTrack;

	 public Compass(boolean trueTrack) {
		 super(null);
		 isTrueTrack=trueTrack;
		 dataset = new DefaultValueDataset(0.0);
		 DialPlot plot = new DialPlot();
		 //Image background=Toolkit.getDefaultToolkit().getImage(getClass().getResource("heading.png"));
		 //plot.setBackgroundImage(background);
		 plot.setView(0.0,0.0,1.0,1.0);
		 plot.setDataset(0,dataset);
		 StandardDialFrame dialFrame = new StandardDialFrame();
		 dialFrame.setBackgroundPaint(Color.lightGray);
		 dialFrame.setForegroundPaint(Color.gray);
		 N=new DialTextAnnotation("N");
		 N.setFont(new Font("Arial",1,32));
		 N.setPaint(Color.white);
		 N.setAnchor(TextAnchor.CENTER);
		 plot.addLayer(N);
		 S=new DialTextAnnotation("S");
		 S.setFont(new Font("Arial",1,32));
		 S.setPaint(Color.white);
		 S.setAnchor(TextAnchor.CENTER);
		 plot.addLayer(S);
		 W=new DialTextAnnotation("W");
		 W.setFont(new Font("Arial",1,32));
		 W.setPaint(Color.white);
		 W.setAnchor(TextAnchor.CENTER);
		 plot.addLayer(W);
		 E=new DialTextAnnotation("E");
		 E.setFont(new Font("Arial",1,32));
		 E.setPaint(Color.white);
		 E.setAnchor(TextAnchor.CENTER);
		 plot.addLayer(E);
		 repositionDirectionsLabels(90);
		 DialValueIndicator valueindicator = new DialValueIndicator(0);
		 plot.addLayer(valueindicator);
		 plot.setDialFrame(dialFrame);
		 realScale = new StandardDialScale(0,360,90,-360,30,6);
		 realScale.setVisible(false);
		 realScale.setFirstTickLabelVisible(false);
		 plot.addScale(0,realScale);
		 dispScale = new StandardDialScale(0,36,90,-360,3,15);
		 dispScale.setFirstTickLabelVisible(false);
		 dispScale.setMajorTickPaint(Color.white);
		 dispScale.setMinorTickPaint(Color.lightGray);
		 dispScale.setFirstTickLabelVisible(true);
		 dispScale.setTickRadius(0.88);
		 dispScale.setTickLabelOffset(0.15);
		 NumberFormat formatter = new DecimalFormat("#");
		 dispScale.setTickLabelFormatter(formatter);
		 dispScale.setTickLabelFont(new Font("Arial",Font.BOLD,22));
		 dispScale.setTickLabelPaint(Color.white);
		 plot.addScale(1,dispScale);
		 plot.setBackground(new DialBackground(Color.black));
		 Pointer needle = new Pointer(0);
		 needle.setFillPaint(Color.red);
		 needle.setRadius(0.8);
		 plot.addLayer(needle);
		 plot.mapDatasetToScale(1,1);
		 DialCap cap = new DialCap();
		 cap.setRadius(0.05);
		 cap.setFillPaint(Color.gray);
		 plot.setCap(cap);
		 jChart=new JFreeChart(plot);
		 //jChart.setBackgroundImage(background);
		 super.setChart(jChart);
		 super.setPreferredSize(new Dimension(400,400));
	 }
	 
	 public void updateDirection(float trueTrack,float magneticTrack) {
		 float newDirection;
		 if(isTrueTrack) newDirection=trueTrack;
		 else newDirection=magneticTrack;
		 if(newDirection>=0 && newDirection<=360) {
			 float northAngle=newDirection+90;
			 realScale.setStartAngle(northAngle);
			 dispScale.setStartAngle(northAngle);
			 dispScale.setFirstTickLabelVisible(false);
			 repositionDirectionsLabels(northAngle);
			 dataset.setValue(newDirection);
		 }
	 }
	 
	 private void repositionDirectionsLabels(float angle) {
		 float base=0.56F;
		 N.setAngle(angle);
		 N.setRadius(base);
		 angle=angle+90;
		 W.setAngle(angle);
		 W.setRadius(base);
		 angle=angle+90;
		 S.setAngle(angle);
		 S.setRadius(base);
		 angle=angle+90;
		 E.setAngle(angle);
		 E.setRadius(base);
	 }

	public void setTrueTrack(boolean isTrueTrack) {
		this.isTrueTrack = isTrueTrack;
	}

	public boolean isTrueTrack() {
		return isTrueTrack;
	}

}
