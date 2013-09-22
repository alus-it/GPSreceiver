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
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.chart.plot.dial.DialPointer.Pointer;
import org.jfree.data.general.DefaultValueDataset;

public class Speedometer extends ChartPanel {
	private static final long serialVersionUID = 6627577229648985779L;
	private DefaultValueDataset groundSpeedDataset, totalSpeedDataset;
	private float currentGroundSpeedKmh;
	private JFreeChart jChart;
	
	private int Vx; //velocità di salita ripida
	private int Vy; //velocità di salita rapida
	private int Vs0; //velocità di stallo con flap completamente estesi (inizio arco bianco)
	private int Vs; //velocità di stallo con flap completamente retratti (inizio arco verde)
	private int Vfe; //velocità massima con flap completamente estesi (fine arco bianco)
	private int Va; //velocità di manovra 
	private int Vno; //velocità massima (inizio arco giallo)
	private int Vne; //never exceed speed (limite rosso)
	private int endScale; //fondoscala
	
	public Speedometer(int Vs0, int Vfe, int Vs, int Vno, int Vne, int endScaleKmh) {
		super(null);
		currentGroundSpeedKmh=0;
		if(!setArcs(Vs0,Vfe,Vs,Vno,Vne,endScaleKmh)) {
			this.Vx=90;
			this.Vy=100;
			this.Vs0=45;
			this.Vs=55;
			this.Vfe=86;
			this.Va=135;
			this.Vno=160;
			this.Vne=180;
			this.endScale=185;
		}
		groundSpeedDataset = new DefaultValueDataset(0);
		totalSpeedDataset = new DefaultValueDataset(0);
		DialPlot dialplot = new DialPlot();
		dialplot.setDataset(0,groundSpeedDataset);
		dialplot.setDataset(1,totalSpeedDataset);
		StandardDialFrame dialFrame = new StandardDialFrame();
		dialFrame.setBackgroundPaint(Color.lightGray);
		dialFrame.setForegroundPaint(Color.gray);
		DialBackground db = new DialBackground(Color.black);
		dialplot.setBackground(db);
		dialplot.setDialFrame(dialFrame);

		DialTextAnnotation dialtextannotation = new DialTextAnnotation("Km/h");
		dialtextannotation.setFont(new Font("Arial",1,14));
		dialtextannotation.setRadius(0.4D);
		dialtextannotation.setPaint(Color.lightGray);
		dialplot.addLayer(dialtextannotation);
		
		//DialValueIndicator dialvalueindicator = new DialValueIndicator(0);
		//dialplot.addLayer(dialvalueindicator);
		DialValueIndicator groundIndicator = new DialValueIndicator(0);
		groundIndicator.setFont(new Font("Dialog", 0, 10));
		groundIndicator.setOutlinePaint(Color.green);
		groundIndicator.setRadius(0.3);
		groundIndicator.setAngle(-110D);
        dialplot.addLayer(groundIndicator);
        DialValueIndicator realIndicator = new DialValueIndicator(1);
        realIndicator.setFont(new Font("Dialog", 0, 10));
        realIndicator.setOutlinePaint(Color.cyan);
        realIndicator.setRadius(0.3);
        realIndicator.setAngle(-70);
        dialplot.addLayer(realIndicator);
		StandardDialScale scale = new StandardDialScale(0,endScale,90,-350,10,5);
		scale.setFirstTickLabelVisible(true);
		scale.setTickRadius(0.9D);
		scale.setTickLabelOffset(0.14999999999999999D);
		NumberFormat formatter = new DecimalFormat("#");
		scale.setTickLabelFormatter(formatter);
		scale.setTickLabelFont(new Font("Arial",Font.BOLD,16));
		scale.setMajorTickPaint(Color.white);
		scale.setMinorTickPaint(Color.lightGray);
		scale.setTickLabelPaint(Color.white);
		dialplot.addScale(0,scale);
		dialplot.addPointer(new org.jfree.chart.plot.dial.DialPointer.Pin());
		DialCap dialcap = new DialCap();
		dialcap.setRadius(0.10);
		dialcap.setFillPaint(Color.lightGray);
		dialplot.setCap(dialcap);
		jChart= new JFreeChart(dialplot);
		StandardDialRange standarddialrange = new StandardDialRange(this.Vne,endScale,Color.red);
		standarddialrange.setInnerRadius(0.54D);
		standarddialrange.setOuterRadius(0.56D);
		dialplot.addLayer(standarddialrange);
		StandardDialRange standarddialrange1 = new StandardDialRange(this.Vno,this.Vne,Color.yellow);
		standarddialrange1.setInnerRadius(0.54D);
		standarddialrange1.setOuterRadius(0.56D);
		dialplot.addLayer(standarddialrange1);
		StandardDialRange standarddialrange2 = new StandardDialRange(this.Vs,this.Vno,Color.green);
		standarddialrange2.setInnerRadius(0.54D);
		standarddialrange2.setOuterRadius(0.56D);
		dialplot.addLayer(standarddialrange2);
		StandardDialRange standarddialrange3 = new StandardDialRange(this.Vs0,this.Vfe,Color.white);
		standarddialrange3.setInnerRadius(0.50D);
		standarddialrange3.setOuterRadius(0.52D);
		dialplot.addLayer(standarddialrange3);
		//dialplot.removePointer(0);
		Pointer realPointer = new Pointer(1);
		realPointer.setFillPaint(Color.cyan);
		dialplot.addPointer(realPointer);
		Pointer groundPointer = new Pointer(0);
		groundPointer.setFillPaint(Color.green);
		dialplot.addPointer(groundPointer);
		
		/* PER NASCONDERE GLI INDICATORI
		groundIndicator.setVisible(false);
		realIndicator.setVisible(false);
		groundPointer.setVisible(false);
		realPointer.setVisible(false);
		*/
		
		
		
		super.setChart(jChart);
		super.setPreferredSize(new Dimension(400, 400));
	}

	public void updateGroundSpeedKmh(float speedKmh) {
		currentGroundSpeedKmh=speedKmh;
		groundSpeedDataset.setValue(speedKmh);
	}
	
	public float getCurrentGroundSpeedKmh() {
		return currentGroundSpeedKmh;
	}
	
	public void updateRealSpeedKmh(float speedKmh) {
		totalSpeedDataset.setValue(speedKmh);
	}
	
	public boolean setArcs(int Vs0, int Vfe, int Vs, int Vno, int Vne, int endScale) {
		if(endScale>=Vne && Vne>Vno && Vno>Vs && Vfe>Vs0) {
			this.endScale=endScale;
			this.Vs0=Vs0;
			this.Vs=Vs;
			this.Vfe=Vfe;
			this.Vno=Vno;
			this.Vne=Vne;
			return true;
		} else return false;
	}
	
	public void setVxVy(int Vx, int Vy) {
		this.Vx=Vx;
		this.Vy=Vy;
	}
	
	public void setVa(int Va) {
		this.Va=Va;
	}

}
