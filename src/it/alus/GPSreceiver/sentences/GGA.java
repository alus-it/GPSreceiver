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
=== GGA - Global Positioning System Fix Data ===

Time, Position and fix related data for a GPS receiver.

------------------------------------------------------------------------------
        1          2         3 4          5 6 7  8    9    10  11 12 13  14   15
        |          |         | |          | | |  |    |     |  |   | |   |    |
 $--GGA,hhmmss.ss ,llll.ll,  a,yyyyy.yy  ,a,x,xx,x.x ,x.x  ,M,x.x ,M,x.x,xxxx*hh<CR><LF>
------------------------------------------------------------------------------

 $GPGGA,141736.488,4527.2834,N,00752.1733,E,1,03,50.0,244.4,M,48.1,M,0.0,0000*42
 $GPGGA,141743.488,4527.2822,N,00752.1722,E,1,03,50.0,243.6,M,48.1,M,0.0,0000*42
 $GPGGA,163330.460,0000.0000,N,00000.0000,E,0,00,50.0,  0.0,M, 0.0,M,0.0,0000*70
 $GPGGA,000045.037,         , ,          , ,0,00,    ,     ,M, 0.0,M,   ,0000*53


Field Number: 

1. Universal Time Coordinated (UTC)
2. Latitude
3. N or S (North or South)
4. Longitude
5. E or W (East or West)
6. GPS Quality Indicator,
     - 0 - fix not available,
     - 1 - GPS fix,
     - 2 - Differential GPS fix
           (values above 2 are 2.3 features)
     - 3 = PPS fix
     - 4 = Real Time Kinematic
     - 5 = Float RTK
     - 6 = estimated (dead reckoning)
     - 7 = Manual input mode
     - 8 = Simulation mode
7. Number of satellites in view, 00 - 12
8. Horizontal Dilution of precision (meters)
9. Antenna Altitude above/below mean-sea-level (geoid) (in meters)
10. Units of antenna altitude, meters
11. Geoidal separation, the difference between the WGS-84 earth
     ellipsoid and mean-sea-level (geoid), "-" means mean-sea-level
     below ellipsoid
12. Units of geoidal separation, meters
13. Age of differential GPS data, time in seconds since last SC104
     type 1 or 9 update, null field when DGPS is not used
14. Differential reference station ID, 0000-1023
15. Checksum 
*/

package it.alus.GPSreceiver.sentences;

public class GGA extends Sentence {
	public static final int Q_NO_FIX  =0; //fix not available,
	public static final int Q_GPS_FIX =1; //GPS fix,
	public static final int Q_DIFF_FIX=2; //Differential GPS fix
	public static final int Q_PPS_FIX =3; //PPS fix
	public static final int Q_RTK_FIX =4; //Real Time Kinematic
	public static final int Q_FRTK_FIX=5; //Float RTK
	public static final int Q_EST_FIX =6; //estimated (dead reckoning)
    public static final int Q_MAN_FIX =7; //Manual input mode
    public static final int Q_SIM_FIX =8; //Simulation mode
	
	private int timeHour,timeMin;
	private float timeSec;
	private float timestamp;
	private int latGra;
	private float latMin;
	private boolean latNorth;
	private int lonGra;
	private float lonMin;
	private boolean lonEast;
	private int quality;
	private int numOfSatellites;
	private float hDilutionPrecision;
	private float alt;
	private char altUnit;
	private float geoidalSeparation;
	private char geoidalUnit;
	private float diffAge;
	private int diffRef;
	
	public GGA(int talkerID, String ascii, long receivingTimestamp) {
		super(talkerID,GGA,ascii,receivingTimestamp);
		String sub=ascii.substring(7,ascii.length()-3);
		String delims="[,]";
		String[] t=sub.split(delims);
		if(t.length==14) {
			boolean error=false;
			timeHour=Integer.parseInt(t[0].substring(0,2));
			timeMin=Integer.parseInt(t[0].substring(2,4));
			timeSec=Float.parseFloat(t[0].substring(4,t[0].length()));
			timestamp=timeHour*3600+timeMin*60+timeSec;
			if(t[1].length()>=4) {
				latGra=Integer.parseInt(t[1].substring(0,2));
				latMin=Float.parseFloat(t[1].substring(2,t[1].length()));
			}
			else {
				latGra=0;
				latMin=0;
			}
			if(t[2].contentEquals("N")) latNorth=true;
			else if(t[2].contentEquals("S")) latNorth=false;
			else error=true;
			if(t[3].length()>=4) {
				lonGra=Integer.parseInt(t[3].substring(0,3));
				lonMin=Float.parseFloat(t[3].substring(3,t[3].length()));
			}
			else {
				lonGra=0;
				lonMin=0;
			}
			if(t[4].contentEquals("E")) lonEast=true;
			else if(t[4].contentEquals("W")) lonEast=false;
			else error=true;
			quality=Integer.parseInt(t[5]);
			numOfSatellites=Integer.parseInt(t[6]);
			if(t[7].isEmpty()) hDilutionPrecision=50;
			else hDilutionPrecision=Float.parseFloat(t[7]);
			if(t[8].isEmpty()) alt=0;
			else alt=Float.parseFloat(t[8]);
			altUnit=t[9].charAt(0);
			geoidalSeparation=Float.parseFloat(t[10]);
			geoidalUnit=t[11].charAt(0);
			if(t[12].isEmpty()) diffAge=0;
			else diffAge=Float.parseFloat(t[12]);
			diffRef=Integer.parseInt(t[13]);
			if(!error) isWellFormed=true;
		}
	}
	
	@Override public String toString() {
		if(isWellFormed) {
			String ret="GGA "+timeHour+":"+String.format("%02d",timeMin)+":"+String.format("%02.3f",timeSec)+
				"; "+latGra+"° "+String.format("%02.4f",latMin)+"' ";
			if(latNorth) ret=ret+"N , ";
			else ret=ret+"S , ";
			ret=ret+lonGra+"° "+String.format("%02.4f",lonMin)+"' ";
			if(lonEast) ret=ret+"E ; ";
			else ret=ret+"W ; ";
			ret=ret+"Alt: "+alt+" "+altUnit;
			return ret;
		} else return "GGA Invalid data.";
	}

	public int getQuality() {
		return quality;
	}

	public int getNumOfSatellites() {
		return numOfSatellites;
	}

	public float gethDilutionPrecision() {
		return hDilutionPrecision;
	}

	public float getGeoidalSeparation() {
		return geoidalSeparation;
	}

	public char getGeoidalSeparationUnit() {
		return geoidalUnit;
	}

	public float getAgeOfDifferentialGPSdata() {
		return diffAge;
	}

	public int getDifferentialReferenceStationID() {
		return diffRef;
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

	public float getAltitude() {
		return alt;
	}

	public char getAltitudeUnits() {
		return altUnit;
	}

}