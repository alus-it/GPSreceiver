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

package it.alus.GPSreceiver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import it.alus.GPSreceiver.instruments.*;
import it.alus.GPSreceiver.sentences.GGA;
import it.alus.GPSreceiver.sentences.GSA;

public class InstrumentPanel extends JPanel  {
	private static final long serialVersionUID = 1213340551279637577L;
	private static JTextField date,time,position,numOfSats,mode,diluition;
	private static Speedometer speed;
	private static Compass heading;
	private static Altimeter alt;
	private static Variometer climb;
	private static Turnometer turn;
	private static SatelliteRadar radar;
	private static Accelerometer acc;

	public InstrumentPanel() {
		super(new BorderLayout());
		JPanel displayPanel=new JPanel();
		displayPanel.setLayout(new GridLayout(1,3));
		displayPanel.setPreferredSize(new Dimension(1000,35));
		date=new javax.swing.JTextField("01/01/2000");
		date.setBackground(Color.black);
		date.setForeground(Color.green);
		date.setFont(new Font("Arial",Font.BOLD,20));
		displayPanel.add(date);
		time=new javax.swing.JTextField("00:00:00");
		time.setBackground(Color.black);
		time.setForeground(Color.green);
		time.setFont(new Font("Arial",Font.BOLD,20));
		displayPanel.add(time);
		position=new javax.swing.JTextField("0° 0' 0.0\" N  0° 0' 0.0\" E");
		position.setBackground(Color.black);
		position.setForeground(Color.green);
		position.setFont(new Font("Arial",Font.PLAIN,18));
		displayPanel.add(position);
		numOfSats=new javax.swing.JTextField("Sats: 0/0");
		numOfSats.setBackground(Color.black);
		numOfSats.setForeground(Color.green);
		numOfSats.setFont(new Font("Arial",Font.BOLD,20));
		numOfSats.setSize(100,34);
		displayPanel.add(numOfSats);
		mode=new javax.swing.JTextField("NO Fix");
		mode.setBackground(Color.black);
		mode.setForeground(Color.green);
		mode.setFont(new Font("Arial",Font.PLAIN,20));
		displayPanel.add(mode);
		diluition=new javax.swing.JTextField("Dop p:50 h:50 v:50");
		diluition.setBackground(Color.black);
		diluition.setForeground(Color.green);
		diluition.setFont(new Font("Arial",Font.PLAIN,18));
		displayPanel.add(diluition);
		this.add(displayPanel,BorderLayout.NORTH);
		JPanel meterPanel=new JPanel();
		meterPanel.setLayout(new GridLayout(2,3));
		speed = new Speedometer(45,86,55,160,180,185); //speeds for ICP Savannah
		speed.setVxVy(90,100);
		speed.setVa(135);
		alt = new Altimeter(0);
		climb = new Variometer();
		heading=new Compass(true);
		turn = new Turnometer();
		//acc = new Accelerometer();
		radar=new SatelliteRadar();
		meterPanel.add(speed);
		meterPanel.add(alt);
		meterPanel.add(climb);
		meterPanel.add(turn);
		meterPanel.add(heading);
		//meterPanel.add(acc);
		meterPanel.add(radar);
		this.add(meterPanel,BorderLayout.CENTER);
	}
	
	public static void updateGroundSpeedKmh(float newGroundSpeedKmh) {
		speed.updateGroundSpeedKmh(newGroundSpeedKmh);
	}
	
	public static void updateTotalSpeedKmh(float newTotalSpeedKmh) {
		if(newTotalSpeedKmh>3) speed.updateRealSpeedKmh(newTotalSpeedKmh);
	}
	
	public static void updateAcceleration(float newAccelerationG) {
		acc.updateAcceleration(newAccelerationG);
	}
	
	public static void updateAltitude(float newAltitudeMt,float newAltitudeFt) {
		alt.updateAltitudeFt(newAltitudeFt);
	}
	
	public static void updateVariometer(float newVerticalSpeedFtMin) {
		climb.updateVerticalSpeed(newVerticalSpeedFtMin);
	}
	
	public static void updateCompass(float trueTrack,float magneticTrack) {
		heading.updateDirection(trueTrack, magneticTrack);
	}
	
	public static void updateTurnometer(float turnRateDegMin) {
		turn.updateTurnRate(turnRateDegMin);
	}
	
	public static void updateDate(int day,int month,int year) {
		date.setText(day+"/"+month+"/"+year);
	}
	
	public static void updateTime(int hour, int min, float sec) {
		time.setText(hour+":"+min+":"+sec);
	}

	public static void updatePosition(int latDeg, int latMin, float latSec, boolean isLatN, int lonDeg, int lonMin, float lonSec, boolean isLonE) {
		String text=latDeg+"°"+latMin+"'"+String.format("%.1f",latSec)+"\"";
		if(isLatN) text=text+"N";
		else text=text+"S";
		text=text+" "+lonDeg+"°"+lonMin+"'"+String.format("%.1f",lonSec)+"\"";
		if(isLonE) text=text+"E";
		else text=text+"W";
		position.setText(text);
	}
	
	public static void updateNumOfSats(int activeSats,int totalSats) {
		numOfSats.setText("Sats: "+activeSats+"/"+totalSats);
	}
	
	public static void updateDiluitions(float pDiluition, float hDiluition, float vDiluition) {
		diluition=new javax.swing.JTextField("Dop p:"+pDiluition+" h:"+hDiluition+" v:"+vDiluition);
	}
	
	public static void updateFixMode(int fixMode, int mode2d3d) {
		mode.setText(getFixModeText(fixMode)+get2D3DmodeText(mode2d3d));
	}
	
	private static String getFixModeText(int modeFix) {
		String text;
		switch(modeFix) {
			case GGA.Q_GPS_FIX: text="GPS Fix"; break;
			case GGA.Q_DIFF_FIX: text="Diff. Fix"; break; //Differential GPS fix
			case GGA.Q_PPS_FIX: text="PPS Fix"; break; //PPS fix
			case GGA.Q_RTK_FIX: text="RTK Fix"; break; //Real Time Kinematic
			case GGA.Q_FRTK_FIX: text="Float RTK Fix"; break; //Float RTK
			case GGA.Q_EST_FIX: text="Estim. Fix"; break; //estimated (dead reckoning)
			case GGA.Q_MAN_FIX: text="Manual Fix"; break; //Manual input mode
			case GGA.Q_SIM_FIX: text="Simul. Fix"; break; //Simulation mode
			default: text="NO Fix";
		}
		return text;
	}
	
	private static String get2D3DmodeText(int mode2d3d) {
		String text;
		switch(mode2d3d) {
			case GSA.MODE_2D: text=" (2D)"; break;
			case GSA.MODE_3D: text=" (3D)"; break;
			case GSA.MODE_NO_FIX: text=""; break;
			default: text="";
		}
		return text;
	}

}