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
/*
=== RMC - Recommended Minimum Navigation Information ===

------------------------------------------------------------------------------
                                                                       12
        1          2 3         4 5          6  7    8      9      10 11|  13
        |          | |         | |          |  |    |      |      |  | |   |
 $--RMC,hhmmss.ss, A,llll.ll  ,a,yyyyy.yy  ,a,x.x ,x.x   ,xxxx  ,x.x,a,m,*hh<CR><LF>
------------------------------------------------------------------------------
 $GPRMC,141705.490,A,4527.2716,N,00752.1632,E,0.16,227.32,250710,       ,*08
 $GPRMC,163337.459,V,0000.0000,N,00000.0000,E,    ,      ,161102,       ,*1A
 $GPRMC,000045.037,V,         , ,          , ,    ,      ,110905,     , ,N*44
 $GPRMC,062850.904,V,4508.3856,N,00746.1907,E,    ,      ,090910,       ,*1A

Field Number:

1. UTC Time
2. Status, V=Navigation receiver warning A=Valid
3. Latitude
4. N or S
5. Longitude
6. E or W
7. Speed over ground, knots
8. Track made good, degrees true
9. Date, ddmmyy
10. Magnetic Variation, degrees
11. E or W
12. FAA mode indicator (NMEA 2.3 and later)
13. Checksum

A status of V means the GPS has a valid fix that is below an internal
quality threshold, e.g. because the dilution of precision is too high 
or an elevation mask test failed.
*/

package it.alus.GPSreceiver.sentences;
//import java.util.Calendar;
//import java.util.TimeZone;

public class RMC extends Sentence {
	private int timeHour,timeMin,timeDay,timeMonth,timeYear;
	private float timeSec;
	private float timestamp;
	private boolean isValid;
	private int latGra;
	private float latMin;
	private boolean latNorth;
	private int lonGra;
	private float lonMin;
	private boolean lonEast;
	private float groundSpeedKnots;
	private float trueTrack;
	private float magneticVariation;
	private boolean magneticVariationToEast;
	private int faa;
	
	public RMC(int talkerID, String ascii, long receivingTimestamp) {
		super(talkerID,RMC,ascii,receivingTimestamp);
		String sub=ascii.substring(7,ascii.length()-3);
		String delims="[,]";
		String[] t=sub.split(delims);
		if(t.length>=9) {
			boolean error=false;
			timeHour=Integer.parseInt(t[0].substring(0,2));
			timeMin=Integer.parseInt(t[0].substring(2,4));
			timeSec=Float.parseFloat(t[0].substring(4,t[0].length()));
			timestamp=timeHour*3600+timeMin*60+timeSec;
			if(t[1].contentEquals("A")) isValid=true;
			else if(t[1].contentEquals("V")) isValid=false;
			else error=true;
			if(t[2].length()>=4) {
				latGra=Integer.parseInt(t[2].substring(0,2));
				latMin=Float.parseFloat(t[2].substring(2,t[2].length()));
			}
			else {
				latGra=0;
				latMin=0;
			}
			if(t[3].contentEquals("N")) latNorth=true;
			else if(t[3].contentEquals("S")) latNorth=false;
			else error=true;
			if(t[4].length()>=4) {
				lonGra=Integer.parseInt(t[4].substring(0,3));
				lonMin=Float.parseFloat(t[4].substring(3,t[4].length()));
			}
			else {
				lonGra=0;
				lonMin=0;
			}
			if(t[5].contentEquals("E")) lonEast=true;
			else if(t[5].contentEquals("W")) lonEast=false;
			else error=true;
			if(t[6].isEmpty()) groundSpeedKnots=0;
			else groundSpeedKnots=Float.parseFloat(t[6]);
			if(t[7].isEmpty()) trueTrack=0;
			else trueTrack=Float.parseFloat(t[7]);
			if(t[8].length()==6) {
				timeDay=Integer.parseInt(t[8].substring(0,2));
				timeMonth=Integer.parseInt(t[8].substring(2,4));
				timeYear=Integer.parseInt(t[8].substring(4,6));
				//int sec=(int)timeSec;
				//int millis=(int)((timeSec-sec)*1000);
				//Calendar cal=Calendar.getInstance();
				//cal.setTimeZone(TimeZone.getTimeZone("UTC"));
				//cal.set(timeYear,timeMonth-1,timeDay,timeHour,timeMin,sec);
				//cal.set(Calendar.MILLISECOND,millis);
				//timestamp=cal.getTimeInMillis();
			} else error=true;
			if(t.length>=11) {
				if(t[9].isEmpty()) magneticVariation=-1;
				else magneticVariation=Float.parseFloat(t[9]);
				if(t[10].contentEquals("E")) magneticVariationToEast=true;
				else if(t[10].contentEquals("W")) magneticVariationToEast=false;
				else error=true;
			} else magneticVariation=-1;
			if(!error) isWellFormed=true;
			if(t.length>=12) {
				if(t[11].length()!=1) faa=Sentence.FAA_NOTVAL;
				faa=Sentence.getFAAcode(t[11].charAt(0));
			} else faa=Sentence.FAA_ABSENT;
		}
	}
	
	@Override public String toString() {
		if(isWellFormed) {
			String ret="RMC "+timeDay+"/"+timeMonth+"/"+timeYear+
				" "+timeHour+":"+String.format("%02d",timeMin)+":"+String.format("%02.3f",timeSec)+
				"; "+latGra+"° "+String.format("%02.4f",latMin)+"' ";
			if(latNorth) ret=ret+"N , ";
			else ret=ret+"S , ";
			ret=ret+lonGra+"° "+String.format("%02.4f",lonMin)+"' ";
			if(lonEast) ret=ret+"E ; ";
			else ret=ret+"W ; ";
			ret=ret+"Direction: "+trueTrack+"°; Speed: "+groundSpeedKnots+"Knots";
			return ret;
		} else return "RMC Invalid data.";
	}
	
	public float getTrueTrack() {
		if(isWellFormed) return trueTrack;
		else return 0;
	}
	
	public float getGroundSpeedKnots() {
		if(isWellFormed) return groundSpeedKnots;
		else return 0;
	}

	public boolean isValid() {
		return isValid;
	}

	public float getMagneticVariation() {
		return magneticVariation;
	}

	public boolean isMagneticVariationToEast() {
		return magneticVariationToEast;
	}

	public int getTimeHour() {
		return timeHour;
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

	public int getTimeMin() {
		return timeMin;
	}

	public float getTimeSec() {
		return timeSec;
	}

	public int getLatitudeGrades() {
		return latGra;
	}

	public float getLatitudeMinutes() {
		return latMin;
	}

	public boolean isLatitudeNorth() {
		return latNorth;
	}

	public int getLongitudeGrades() {
		return lonGra;
	}

	public float getLongitudeMinutes() {
		return lonMin;
	}

	public boolean isLongitudeEast() {
		return lonEast;
	}

	public float getTimestamp() {
		return timestamp;
	}

	public int getFAAindicator() {
		return faa;
	}

}