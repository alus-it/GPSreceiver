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
=== VTG - Track made good and Ground speed ===

------------------------------------------------------------------------------
         1  2  3  4  5	6  7  8 9   10
         |  |  |  |  |	|  |  | |   |
 $--VTG,x.x,T,x.x,M,x.x,N,x.x,K,m,*hh<CR><LF>
------------------------------------------------------------------------------
 $GPVTG,   ,T,   ,M,   ,N,   ,K   *4E
 $GPVTG,   ,T,   ,M,   ,N,   ,K,N *2C
 
Field Number: 

1. Track Degrees
2. T = True
3. Track Degrees
4. M = Magnetic
5. Speed Knots
6. N = Knots
7. Speed Kilometers Per Hour
8. K = Kilometers Per Hour
9. FAA mode indicator (NMEA 2.3 and later)
10. Checksum

Note: in some older versions of NMEA 0183, the sentence looks like this:

------------------------------------------------------------------------------
         1  2  3   4  5
         |  |  |   |  |
 $--VTG,x.x,x,x.x,x.x,*hh<CR><LF>
------------------------------------------------------------------------------

Field Number: 

1. True course over ground (degrees) 000 to 359
2. Magnetic course over ground 000 to 359
3. Speed over ground (knots) 00.0 to 99.9
4. Speed over ground (kilometers) 00.0 to 99.9
5. Checksum

The two forms can be distinguished by field 2, which will be
the fixed text 'T' in the newer form.  The new form appears
to have been introduced with NMEA 3.01 in 2002.

Some devices, such as those described in [GLOBALSAT], leave the
magnetic-bearing fields 3 and 4 empty.
*/

package it.alus.GPSreceiver.sentences;

public class VTG extends Sentence {
	private float trueTrack;
	private float magneticTrack;
	private float groundSpeedKnots;
	private float groundSpeedKmh;
	private int faa;
	
	public VTG(int talkerID, String ascii, long receivingTimestamp) {
		super(talkerID,VTG,ascii,receivingTimestamp);
		String sub=ascii.substring(7,ascii.length()-3);
		String delims="[,]";
		String[] t=sub.split(delims);
		if(t.length>=7) {
			if(t[1].contentEquals("T") && t[3].contentEquals("M") && t[5].contentEquals("N") && t[7].contentEquals("K")) {
				if(t[0].isEmpty()) trueTrack=0;
				else trueTrack=Float.parseFloat(t[0]);
				if(t[2].isEmpty()) magneticTrack=0;
				else magneticTrack=Float.parseFloat(t[2]);
				if(t[4].isEmpty()) groundSpeedKnots=0;
				else groundSpeedKnots=Float.parseFloat(t[4]);
				if(t[6].isEmpty()) groundSpeedKmh=0;
				else groundSpeedKmh=Float.parseFloat(t[6]);
				isWellFormed=true;
				if(t.length>=9) {
					if(t[8].length()!=1) faa=Sentence.FAA_NOTVAL;
					faa=Sentence.getFAAcode(t[8].charAt(0));
				} else faa=Sentence.FAA_ABSENT;
			} else isWellFormed=false;
		} else if(t.length==4) { //old version
			if(t[0].isEmpty()) trueTrack=0;
			else trueTrack=Float.parseFloat(t[0]);
			if(t[1].isEmpty()) magneticTrack=0;
			else magneticTrack=Float.parseFloat(t[1]);
			if(t[2].isEmpty()) groundSpeedKnots=0;
			else groundSpeedKnots=Float.parseFloat(t[2]);
			if(t[3].isEmpty()) groundSpeedKmh=0;
			else groundSpeedKmh=Float.parseFloat(t[3]);
			faa=Sentence.FAA_ABSENT;
			isWellFormed=true;
		} else isWellFormed=false;
	}
	
	@Override public String toString() {
		if(isWellFormed) return "VTG True Track: "+trueTrack+"°; Magnetic track: "+magneticTrack+"°; Ground speed: "+groundSpeedKnots+" Knots, "+groundSpeedKmh+" Km/h";
		else return "VTG Invalid data.";
	}
	
	public float getTrueTrack() {
		if(isWellFormed) return trueTrack;
		else return 0;
	}
	
	public float getMagneticTrack() {
		if(isWellFormed) return magneticTrack;
		else return 0;
	}
	
	public float getGroundSpeedKnots() {
		if(isWellFormed) return groundSpeedKnots;
		else return 0;
	}
	
	public float getGroundSpeedKmh() {
		if(isWellFormed) return groundSpeedKmh;
		else return 0;
	}
	
	public int getFAAindicator() {
		return faa;
	}

}
