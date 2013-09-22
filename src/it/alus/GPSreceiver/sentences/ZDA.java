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
/*=== ZDA - Time & Date - UTC, day, month, year and local time zone ===

------------------------------------------------------------------------------
    	1         2  3  4     5  6  7
        |         |  |  |     |  |  |
 $--ZDA,hhmmss.ss,xx,xx,xxxx, xx,xx*hh<CR><LF>
 $GPZDA,000351.99,10,11,2002,+00,03*48
------------------------------------------------------------------------------

Field Number:

1. UTC time (hours, minutes, seconds, may have fractional subsecond)
2. Day, 01 to 31
3. Month, 01 to 12
4. Year (4 digits)
5. Local zone description, 00 to +- 13 hours
6. Local zone minutes description, apply same sign as local hours
7. Checksum

Example: $GPZDA,160012.71,11,03,2004,-1,00*7D
*/

package it.alus.GPSreceiver.sentences;

public class ZDA extends Sentence {
	private int timeHour,timeMin,timeDay,timeMonth,timeYear;
	private float timeSec,timestamp;
	private int localZone, localZoneMin;

	public ZDA(int talkerID, String ascii, long receivingTimestamp) {
		super(talkerID,ZDA,ascii,receivingTimestamp);
		String sub=ascii.substring(7,ascii.length()-3);
		String delims="[,]";
		String[] t=sub.split(delims);
		if(t.length==6) {
			//boolean error=false;
			timeHour=Integer.parseInt(t[0].substring(0,2));
			timeMin=Integer.parseInt(t[0].substring(2,4));
			timeSec=Float.parseFloat(t[0].substring(4,t[0].length()));
			timestamp=timeHour*3600+timeMin*60+timeSec;
			timeDay=Integer.parseInt(t[1]);
			timeMonth=Integer.parseInt(t[2]);
			timeYear=Integer.parseInt(t[3]);
			boolean positive=true;
			String timeZone;
			if(t[4].charAt(0)=='+') timeZone=t[4].substring(1);
			else if(t[4].charAt(0)=='-') {
				positive=false;
				timeZone=t[4].substring(1);
			} else timeZone=t[4];
			localZone=Integer.parseInt(timeZone);
			if(!positive) localZone=0-localZone;
			localZoneMin=Integer.parseInt(t[5]);
			//TODO: implement Calendar support
			//int sec=(int)timeSec;
			//int millis=(int)((timeSec-sec)*1000);
			//Calendar cal=Calendar.getInstance();
			//cal.setTimeZone(TimeZone.getTimeZone("UTC"));
			//cal.set(timeYear,timeMonth-1,timeDay,timeHour,timeMin,sec);
			//cal.set(Calendar.MILLISECOND,millis);
			//timestamp=cal.getTimeInMillis();
			/*if(!error) */isWellFormed=true;
		}
	}

	@Override public String toString() {
		if(isWellFormed) {
			String ret="ZDA "+timeDay+"/"+timeMonth+"/"+timeYear+" "+timeHour+":"+
			String.format("%02d",timeMin)+":"+String.format("%02.3f",timeSec)+
			" Local Zone: "+localZone+":"+localZoneMin;
			return ret;
		} else return "ZDA Invalid data.";
	}

	public int getTimeYear() {
		return timeYear;
	}

	public int getTimeMonth() {
		return timeMonth;
	}

	public int getTimeDay() {
		return timeDay;
	}

	public float getTimestamp() {
		return timestamp;
	}

	public int getTimeHour() {
		return timeHour;
	}

	public int getTimeMin() {
		return timeMin;
	}

	public float getTimeSec() {
		return timeSec;
	}

	public int getLocalZoneMin() {
		return localZoneMin;
	}

	public int getLocalZone() {
		return localZone;
	}

}
