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

import it.alus.GPSreceiver.sentences.GSV;
import java.awt.Color;
import java.awt.Dimension;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class SatelliteRadar extends ChartPanel {
	private static final long serialVersionUID = 2785753501853018650L;
	private static final int MAX_SAT=50;
	private int azimuth[],elevation[],snr[];
	private XYSeries satSeries;
	private JFreeChart jChart=null;

	public SatelliteRadar() {
		super(null);
		azimuth=new int[MAX_SAT];
		elevation=new int[MAX_SAT];
		snr=new int[MAX_SAT];
		resetArray();
		satSeries = new XYSeries("Satellites");
		XYSeriesCollection seriescollection = new XYSeriesCollection();
		seriescollection.addSeries(satSeries);
		jChart = ChartFactory.createPolarChart("Satellites",seriescollection, true, false, false);
		jChart.setBackgroundPaint(Color.white);
		PolarPlot polarplot = (PolarPlot)jChart.getPlot();
		polarplot.setBackgroundPaint(Color.lightGray);
		polarplot.setAngleGridlinePaint(Color.white);
		polarplot.setRadiusGridlinePaint(Color.white);
		NumberAxis numberaxis = (NumberAxis)polarplot.getAxis();
		numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		super.setChart(jChart);
		super.setMouseZoomable(false);
		super.setPreferredSize(new Dimension(500, 270));
	}

	private void resetArray() {
		for(int i=0;i<MAX_SAT;i++) azimuth[i]=-1;
	}
	
	public void receiveGSVsentence(GSV gsv) {
		if(gsv.getGSVmsgSequenceNo()==1) resetArray();
		for(int i=0;i<gsv.getSatellitesNumberOfMsg();i++) {
			azimuth[gsv.getSatPRN(i)]=gsv.getSatAzimuth(i);
			elevation[gsv.getSatPRN(i)]=gsv.getSatElevation(i);
			snr[gsv.getSatPRN(i)]=gsv.getSatSNR(i);
		}
		if(gsv.isLastMsgOfSeq()) updateRadar();
	}
	
	private void updateRadar() {
		satSeries.clear();
		for(int i=0;i<MAX_SAT;i++) if(azimuth[i]!=-1) {
			satSeries.add(azimuth[i],90-elevation[i]);
		}
	}
	
}

