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

package it.alus.GPSreceiver;

import java.util.Calendar;

public class Converter {
	private static final float KmhOfKnots=1.852F;  //1 Knots = 1.852 Km/h
	private static final float KmOfMile=1.609344F; //1 Mile = 1.609344 Km
	private static final float mOfFt=0.3048F;      //1 Ft = 0.3048 m
	private static final float msOfFtMin=0.00508F; //1 Ft/min = 0.00508 m/s
	
	public static float Kmh2Knots(float valueKmh) {
		return valueKmh/KmhOfKnots;
	}
	
	public static float Knots2Kmh(float valueKnots) {
		return valueKnots*KmhOfKnots;
	}
	
	public static float Km2Miles(float valueKm) {
		return valueKm/KmOfMile;
	}
	
	public static float Miles2Km(float valueMiles) {
		return valueMiles*KmOfMile;
	}
	
	public static float m2Ft(float valueMt) {
		return valueMt/mOfFt;
	}
	
	public static float Ft2m(float valueFt) {
		return valueFt*mOfFt;
	}
	
	public static float Kmh2ms(float valueKmh) {
		return valueKmh/3.6F;
	}
	
	public static float ms2Kmh(float valueMs) {
		return valueMs*3.6F;
	}
	
	public static float FtMin2ms(float valueFtMin) {
		return valueFtMin*msOfFtMin;
	}
	
	public static float ms2FtMin(float valueMs) {
		return valueMs/msOfFtMin;
	}
		
	public static String TimeMillisToString(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		return(String.format("%1$ta %1$te/%1$tb/%1$tY %1$tH:%1$tM:%1$tS.%1$tL",cal));
	}
	
	public static String TimeCalendarToString(Calendar timeCal) {
		return(String.format("%1$ta %1$te/%1$tb/%1$tY %1$tH:%1$tM:%1$tS.%1$tL",timeCal));
	}
	
	public static Calendar TimeMillisToCalendar(long timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		return(cal);
	}
	
	public static long DateTimeToMillis(int day, int month, int year, int hour, int min, int sec, int millis) {
		Calendar cal = Calendar.getInstance();
		cal.set(year,month-1,day,hour,min,sec);
		cal.set(Calendar.MILLISECOND,millis);
		return(cal.getTimeInMillis());
	}

}
