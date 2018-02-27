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
/* === GBS - GPS Satellite Fault Detection ===

------------------------------------------------------------------------------
            1      2   3   4   5   6   7   8   9
            |      |   |   |   |   |   |   |   |
 $--GBS,hhmmss.ss,x.x,x.x,x.x,x.x,x.x,x.x,x.x*hh<CR><LF>
------------------------------------------------------------------------------

Field Number: 

1. UTC time of the GGA or GNS fix associated with this sentence
2. Expected error in latitude (meters)
3. Expected error in longitude (meFters)
4. Expected error in altitude (meters)
5. PRN of most likely failed satellite
6. Probability of missed detection for most likely failed satellite
7. Estimate of bias in meters on most likely failed satellite
8. Standard deviation of bias estimate
9. Checksum

Note: Source [MX521] describes a proprietary extension of GBS with
a 9th data field. The 8-field version is in NMEA 3.0.

*/

package it.alus.GPSreceiver.sentences;

public class GBS extends Sentence {
	private int timeHour,timeMin;
	private float timeSec;
	private float timestamp;
	private float latitudeError;
	private float longitudeError;
	private float altitudeError;
	private int mostFailedSatPRN;
	private float missedDetectionProb;
	private float estBiasOfFailedSat;
	private float stdDevOfBias;

	public GBS(int talkerID, String ascii, long receivingTimestamp) {
		super(talkerID,GBS,ascii,receivingTimestamp);
		String sub=ascii.substring(7,ascii.length()-3);
		String delims="[,]";
		String[] t=sub.split(delims);
		if(t.length==8) {
			timeHour=Integer.parseInt(t[0].substring(0,2));
			timeMin=Integer.parseInt(t[0].substring(2,4));
			timeSec=Float.parseFloat(t[0].substring(4,t[0].length()));
			timestamp=timeHour*3600+timeMin*60+timeSec;
			if(t[1].isEmpty()) latitudeError=-1;
			else latitudeError=Float.parseFloat(t[1]);
			if(t[2].isEmpty()) longitudeError=-1;
			else longitudeError=Float.parseFloat(t[2]);
			if(t[3].isEmpty()) altitudeError=-1;
			else altitudeError=Float.parseFloat(t[3]);
			if(t[4].isEmpty()) mostFailedSatPRN=-1;
			else mostFailedSatPRN=Integer.parseInt(t[4]);
			if(t[5].isEmpty()) missedDetectionProb=-1;
			else missedDetectionProb=Float.parseFloat(t[5]);
			if(t[6].isEmpty()) estBiasOfFailedSat=-1;
			else estBiasOfFailedSat=Float.parseFloat(t[6]);
			if(t[7].isEmpty()) stdDevOfBias=-1;
			else stdDevOfBias=Float.parseFloat(t[7]);
			isWellFormed=true;
		}
	}

	@Override public String toString() {
		if(isWellFormed) {
			String ret="GBS "+timeHour+":"+String.format("%02d",timeMin)+":"+String.format("%02.3f",timeSec)+
			"; "+ascii; //TODO: put here all the others fields...
			return ret;
		} else return "GGA Invalid data.";
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

	public float getLatitudeErrorMt() {
		return latitudeError;
	}

	public float getLongitudeErrorMt() {
		return longitudeError;
	}

	public float getAltitudeErrorMt() {
		return altitudeError;
	}

	public int getMostFailedSatPRN() {
		return mostFailedSatPRN;
	}

	public float getMissedDetectionProb() {
		return missedDetectionProb;
	}

	public float getEstBiasOfFailedSat() {
		return estBiasOfFailedSat;
	}

	public float getStandardDeviationOfBias() {
		return stdDevOfBias;
	}

}
