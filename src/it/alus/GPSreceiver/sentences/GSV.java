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
=== GSV - Satellites in view ===

These sentences describe the sky position of a UPS satellite in view.
Typically they're shipped in a group of 2 or 3.

------------------------------------------------------------------------------
	    1 2 3 4 5 6 7     n
	    | | | | | | |     |
 $--GSV,x,x,x,x,x,x,x,...*hh<CR><LF>
------------------------------------------------------------------------------
 $GPGSV,3,1,09,12,69,045,00,30,62,292,27,14,40,278,34,09,34,137,30*77
 $GPGSV,3,2,09,29,34,207,00,02,29,094,00,27,26,137,33,04,18,051,00*71
 $GPGSV,3,3,09,31,12,310,00*43

Field Number: 

1. total number of GSV messages to be transmitted in this group
2. 1-origin number of this GSV message  within current group
3. total number of satellites in view (leading zeros sent)
4. satellite PRN number (leading zeros sent)
5. elevation in degrees (00-90) (leading zeros sent)
6. azimuth in degrees to true north (000-359) (leading zeros sent)
7. SNR in dB (00-99) (leading zeros sent)
   more satellite info quadruples like 4-7
   n) checksum

Example:
    $GPGSV,3,1,11,03,03,111,00,04,15,270,00,06,01,010,00,13,06,292,00*74
    $GPGSV,3,2,11,14,25,170,00,16,57,208,39,18,67,296,40,19,40,246,00*74
    $GPGSV,3,3,11,22,42,067,42,24,14,311,43,27,05,244,00,,,,*4D
    
    $GPGSV,3,3,12,06,04,000,00,24,01,000,,12,01,000,,14,01,000,*79
    
    $GPGSV,3,1,12,20,00,000,  ,10,00,000,,25,00,000,,27,00,000,*79
    $GPGSV,3,2,12,03,00,000,  ,31,00,000,,24,00,000,,15,00,000,*78
    $GPGSV,3,3,12,16,00,000,  ,05,00,000,,01,00,000,,26,00,000,*7D

Some GPS receivers may emit more than 12 quadruples (more than three
GPGSV sentences), even though NMEA-0813 doesn't allow this.  (The
extras might be WAAS satellites, for example.) Receivers may also
report quads for satellites they aren't tracking, in which case the
SNR field will be null; we don't know whether this is formally allowed
or not.
*/

package it.alus.GPSreceiver.sentences;

public class GSV extends Sentence {
	private static final int PRN=0; //satellite PRN number
	private static final int ELEVATION=1; //elevation in degrees (00-90)
	private static final int AZIMUTH=2; //azimuth in degrees to true north (000-359)
	private static final int SNR=3; //SNR in dB (00-99)
	
	private int numOfGSVmsg, GSVmsgSeqNo;
	private int totalSatInView,numSatOfThisMsg;
	private int[][] satellites;
	
	public GSV(int talkerID, String ascii, long receivingTimestamp) {
		super(talkerID,GSV,ascii,receivingTimestamp);
		String sub=ascii.substring(7,ascii.length()-3);
		String delims="[,]";
		String[] t=sub.split(delims);
		if(t.length>=3) {
			boolean error=false;
			numOfGSVmsg=Integer.parseInt(t[0]);
			GSVmsgSeqNo=Integer.parseInt(t[1]);
			totalSatInView=Integer.parseInt(t[2]);
			if(numOfGSVmsg!=GSVmsgSeqNo) numSatOfThisMsg=(t.length-3)/4;
			else {
				if(t.length>4) {
					numSatOfThisMsg=0;
					for(int i=3;i<t.length;i=i+4) if(!t[i].isEmpty()) numSatOfThisMsg++;
				}
				else error=true;
			}
			if(!error) {
				satellites=new int[numSatOfThisMsg][4];
				for(int i=0;i<numSatOfThisMsg;i++) {
					satellites[i][PRN]=Integer.parseInt(t[3+i*4+PRN]);
					satellites[i][ELEVATION]=Integer.parseInt(t[3+i*4+ELEVATION]);
					satellites[i][AZIMUTH]=Integer.parseInt(t[3+i*4+AZIMUTH]);
					int pos=3+i*4+SNR;
					if(i==numSatOfThisMsg-1 && pos>=t.length) satellites[i][SNR]=0;
					else {
						String snr=t[pos];
						if(snr.isEmpty()) satellites[i][SNR]=0;
						else satellites[i][SNR]=Integer.parseInt(t[pos]);
					}
				}
				isWellFormed=true;
			}
		}
	}
	
	@Override public String toString() {
		if(isWellFormed) {
			String ret="GSV NumOfMsg: "+numOfGSVmsg+"; SeqNo: "+GSVmsgSeqNo+"; TotalSatInView: "+totalSatInView+"; Sats: ";
			for(int i=0;i<numSatOfThisMsg;i++) ret=ret+"PNR: "+satellites[i][PRN]+
				", Elev: "+satellites[i][ELEVATION]+
				"°, Azimuth: "+satellites[i][AZIMUTH]+
				"°, SNR: "+satellites[i][SNR]+" dB; ";
			return ret;
		}
		else return "GSV Invalid data.";
	}
	
	public int getSatPRN(int index) {
		if(index>=0 && index<numSatOfThisMsg) return satellites[index][PRN];
		else return 0;
	}
	
	public int getSatElevation(int index) {
		if(index>=0 && index<numSatOfThisMsg) return satellites[index][ELEVATION];
		else return 0;
	}
	
	public int getSatAzimuth(int index) {
		if(index>=0 && index<numSatOfThisMsg) return satellites[index][AZIMUTH];
		else return 0;
	}
	
	public int getSatSNR(int index) {
		if(index>=0 && index<numSatOfThisMsg) return satellites[index][SNR];
		else return 0;
	}

	public int getGSVmsgSequenceNo() {
		return GSVmsgSeqNo;
	}

	public int getNumberOfGSVmsgs() {
		return numOfGSVmsg;
	}

	public int getTotalNumberOfSatellitesInView() {
		return totalSatInView;
	}

	public int getSatellitesNumberOfMsg() {
		return numSatOfThisMsg;
	}
	
	public boolean isLastMsgOfSeq() {
		if(GSVmsgSeqNo!=numOfGSVmsg) return false;
		else return true;
	}

}