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
/* Other possibles infos sent by the GPS receiver:
 * $Version 231.000.000_A2
 * $TOW: 0       
 * $WK:  1192
 * $POS: 6378137  0        0       
 * $CLK: 96000   
 * $CHNL:12
 * $Baud rate: 4800  System clock: 12.277MHz
 * $HW Type: S2AM
 * $Asic Version: 0x23
 * $Clock Source: GPSCLK
 * $Internal Beacon: None */

package it.alus.GPSreceiver.sentences;

public class Sentence {
	//Talker IDs
	public static final int GP=1; //Global Positioning System receiver
	public static final int LC=2; //Loran-C receiver
	public static final int II=3; //Integrated Instrumentation
	public static final int IN=4; //Integrated Navigation
	public static final int EC=5; //Electronic Chart Display & Information System (ECDIS)
	public static final int CD=6; //Digital Selective Calling (DSC)
	public static final int GL=7; //GLONASS, according to IEIC 61162-1
	public static final int GN=8; //Mixed GPS and GLONASS data, according to IEIC 61162-1
	
	//Type codes
	public static final int GSA=1;
	public static final int GSV=2;
	public static final int GGA=3;
	public static final int RMC=4;
	public static final int VTG=5;
	public static final int GLL=6;
	public static final int ZDA=7;
	public static final int GBS=8;
	public static final int MSS=9;
	
	//FAA Mode Indicator
	public static final int FAA_ABSENT=0; //Previous version of NMEA 2.3 without FAA 
    public static final int FAA_AUTO  =1; //Autonomous mode
    public static final int FAA_DIFF  =2; //Differential Mode
    public static final int FAA_ESTIM =3; //Estimated (dead-reckoning) mode
    public static final int FAA_MANUAL=4; //Manual Input Mode
    public static final int FAA_SIMUL= 5; //Simulated Mode
    public static final int FAA_NOTVAL=6; //Data Not Valid
	
	protected int talker;
	protected int type;
	protected String ascii;
	protected boolean isWellFormed;
	protected long receivingTimestamp;
	
	protected Sentence(int talkerID,int typeID,String ascii,long timestamp) {
		this.talker=talkerID;
		this.type=typeID;
		this.ascii=ascii;
		this.isWellFormed=false;
		this.receivingTimestamp=timestamp;
	}

	public int getTypeCode() {
		return this.type;
	}
	
	public int getTalkerId() {
		return this.talker;
	}

	public static Sentence buildSentence(String ascii,long timestamp) {
		if(ascii.length()<6) {
			System.out.println("Received too short sentence: "+ascii);
			return null;
		}
		if(ascii.charAt(0)!='$') System.out.println("$ expected as first char but first char is: "+ascii.charAt(0));
		int talkerID;
		if(ascii.startsWith("$GP")) talkerID=GP;
		else if(ascii.startsWith("$LC")) talkerID=LC;
		else if(ascii.startsWith("$IN")) talkerID=IN;
		else if(ascii.startsWith("$II")) talkerID=II;
		else if(ascii.startsWith("$EC")) talkerID=EC;
		else if(ascii.startsWith("$CD")) talkerID=CD;
		else if(ascii.startsWith("$GL")) talkerID=GL;
		else if(ascii.startsWith("$GN")) talkerID=GN;
		else { //tutti gli altri casi
			talkerID=0;
			System.out.println("Received unexpected TalkerID: "+ascii.substring(1,3));
		}
		String typecode=ascii.substring(3,6);
		if(typecode.contentEquals("GGA")) return new GGA(talkerID,ascii,timestamp);
		else if(typecode.contentEquals("RMC")) return new RMC(talkerID,ascii,timestamp);
		else if(typecode.contentEquals("MSS")) return new MSS(talkerID,ascii,timestamp);
		else if(typecode.contentEquals("GSA")) return new GSA(talkerID,ascii,timestamp);
		else if(typecode.contentEquals("ZDA")) return new ZDA(talkerID,ascii,timestamp);
		else if(typecode.contentEquals("GSV")) return new GSV(talkerID,ascii,timestamp);
		else if(typecode.contentEquals("VTG")) return new VTG(talkerID,ascii,timestamp);
		else if(typecode.contentEquals("GLL")) return new GLL(talkerID,ascii,timestamp);
		else if(typecode.contentEquals("GBS")) return new GBS(talkerID,ascii,timestamp);
		else { //others sentences
			System.out.println("Received unexpected sentence: "+ascii);
			return null;
		}
	}
	
	public boolean isWellFormed() {
		return isWellFormed;
	}
	
	public String toString() {
		return ascii;
	}
	
	public byte[] getASCII() {
		return ascii.getBytes();
	}
	
	public long getReceivingTimestamp() {
		return receivingTimestamp;
	}
	
	protected static int getFAAcode(char faa) {
		switch(faa) {
			case 'A': return FAA_AUTO; //Autonomous mode
			case 'D': return FAA_DIFF; //Differential Mode
			case 'E': return FAA_ESTIM; //Estimated (dead-reckoning) mode
			case 'M': return FAA_MANUAL; //Manual Input Mode
			case 'S': return FAA_SIMUL; //Simulated Mode
			case 'N': return FAA_NOTVAL; //Data Not Valid
			default: return FAA_NOTVAL;
		}
	}

}
