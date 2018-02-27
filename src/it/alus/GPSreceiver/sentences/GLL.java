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
/*=== GLL - Geographic Position - Latitude/Longitude ===

------------------------------------------------------------------------------
	    1       2 3        4 5         6 7   8
    	|       | |        | |         | |   |
 $--GLL,llll.ll,a,yyyyy.yy,a,hhmmss.ss,a,m,*hh<CR><LF>
------------------------------------------------------------------------------

Field Number: 

1. Latitude
2. N or S (North or South)
3. Longitude
4. E or W (East or West)
5. Universal Time Coordinated (UTC)
6. Status A - Data Valid, V - Data Invalid
7. FAA mode indicator (NMEA 2.3 and later)
8. Checksum
*/

package it.alus.GPSreceiver.sentences;

public class GLL extends Sentence {
	private int timeHour,timeMin;
	private float timeSec;
	private float timestamp;
	private int latGra;
	private float latMin;
	private boolean latNorth;
	private int lonGra;
	private float lonMin;
	private boolean lonEast;
	private boolean isValid;
	private int faa;

	public GLL(int talkerID, String ascii, long receivingTimestamp) {
		super(talkerID,GLL,ascii,receivingTimestamp);
		String sub=ascii.substring(7,ascii.length()-3);
		String delims="[,]";
		String[] t=sub.split(delims);
		if(t.length>=6) {
			boolean error=false;
			if(t[0].length()>=4) {
				latGra=Integer.parseInt(t[0].substring(0,2));
				latMin=Float.parseFloat(t[0].substring(2,t[0].length()));
			}
			else {
				latGra=0;
				latMin=0;
			}
			if(t[1].contentEquals("N")) latNorth=true;
			else if(t[1].contentEquals("S")) latNorth=false;
			else error=true;
			if(t[2].length()>=4) {
				lonGra=Integer.parseInt(t[2].substring(0,3));
				lonMin=Float.parseFloat(t[2].substring(3,t[2].length()));
			}
			else {
				lonGra=0;
				lonMin=0;
			}
			if(t[3].contentEquals("E")) lonEast=true;
			else if(t[3].contentEquals("W")) lonEast=false;
			else error=true;
			timeHour=Integer.parseInt(t[4].substring(0,2));
			timeMin=Integer.parseInt(t[4].substring(2,4));
			timeSec=Float.parseFloat(t[4].substring(4,t[4].length()));
			timestamp=timeHour*3600+timeMin*60+timeSec;
			if(t[5].contentEquals("A")) isValid=true;
			else if(t[5].contentEquals("V")) isValid=false;
			else error=true;
			if(t.length>=7) {
				if(t[6].length()!=1) faa=Sentence.FAA_NOTVAL;
				faa=Sentence.getFAAcode(t[6].charAt(0));
			} else faa=Sentence.FAA_ABSENT;
			if(!error) isWellFormed=true;
		}
	}

	@Override public String toString() {
		if(isWellFormed) {
			String ret="GLL "+timeHour+":"+String.format("%02d",timeMin)+":"+String.format("%02.3f",timeSec)+
			"; "+latGra+"° "+String.format("%02.4f",latMin)+"' ";
			if(latNorth) ret=ret+"N , ";
			else ret=ret+"S , ";
			ret=ret+lonGra+"° "+String.format("%02.4f",lonMin)+"' ";
			if(lonEast) ret=ret+"E ; ";
			else ret=ret+"W ; ";
			return ret;
		} else return "GLL Invalid data.";
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
	
	public boolean isValid() {
		return isValid;
	}
	
	public int getFAAindicator() {
		return faa;
	}

}
