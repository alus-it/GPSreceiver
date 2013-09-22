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
/*
=== GSA - GPS DOP and active satellites ===

------------------------------------------------------------------------------
	    1 2 3                        14 15  16  17  18
	    | | |                         |  |   |   |   |
 $--GSA,a,a,x,x,x,x,x,x,x,x,x,x,x,x,x,x,x.x,x.x,x.x*hh<CR><LF>
------------------------------------------------------------------------------
 $GPGSA,A,2,14,09,27, , , , , , , , , ,50.0,50.0,20.0*08

Field Number: 

1. Selection mode: M=Manual, forced to operate in 2D or 3D, A=Automatic, 3D/2D
2. Mode (1 = no fix, 2 = 2D fix, 3 = 3D fix)
3. ID of 1st satellite used for fix
4. ID of 2nd satellite used for fix
5. ID of 3rd satellite used for fix
6. ID of 4th satellite used for fix
7. ID of 5th satellite used for fix
8. ID of 6th satellite used for fix
9. ID of 7th satellite used for fix
10. ID of 8th satellite used for fix
11. ID of 9th satellite used for fix
12. ID of 10th satellite used for fix
13. ID of 11th satellite used for fix
14. ID of 12th satellite used for fix
15. PDOP
16. HDOP
17. VDOP
18. Checksum
*/

package it.alus.GPSreceiver.sentences;

public class GSA extends Sentence {
	public static final int MODE_NO_FIX=1;
	public static final int MODE_2D=2;
	public static final int MODE_3D=3;
	
	private boolean autoSelectionMode;
	private int mode;
	private int satellites[];
	private int numOfSatellites;
	private float pdop;
	private float hdop;
	private float vdop;
	
	public GSA(int talkerID, String ascii, long receivingTimestamp) {
		super(talkerID,GSA,ascii,receivingTimestamp);
		String sub=ascii.substring(7,ascii.length()-3);
		String delims="[,]";
		String[] t=sub.split(delims);
		if(t.length==17) {
			boolean error=false;
			if(t[0].contentEquals("A")) autoSelectionMode=true;
			else if(t[0].contentEquals("M")) autoSelectionMode=true;
			else error=true;
			mode=Integer.parseInt(t[1]);
			if(mode<1 || mode>3) error=true;
			if(!error) {
				satellites=new int[12];
				numOfSatellites=0;
				int pos=2;
				while(numOfSatellites<12) {
					if(!t[pos].isEmpty()) {
						satellites[numOfSatellites++]=Integer.parseInt(t[pos++]);
					} else break;
				}
				if(t[14].isEmpty()) pdop=0;
				else pdop=Float.parseFloat(t[14]);
				if(t[15].isEmpty()) hdop=0;
				else hdop=Float.parseFloat(t[15]);
				if(t[16].isEmpty()) vdop=0;
				else vdop=Float.parseFloat(t[16]);
				isWellFormed=true;
			}
		}
	}
	
	@Override public String toString() {
		if(isWellFormed) {
			String ret="GSA SelectionMode: ";
			if(autoSelectionMode) ret=ret+"auto";
			else ret=ret+"manual";
			ret=ret+"; Mode: ";
			switch(mode) {
				case MODE_NO_FIX: ret=ret+"no fix; "; break;
				case MODE_2D: ret=ret+"2D; "; break;
				case MODE_3D: ret=ret+"3D; "; break;
			}
			ret=ret+" NumOfSats: "+numOfSatellites+":";
			for(int i=0;i<numOfSatellites;i++) ret=ret+" "+satellites[i];
			ret=ret+"; PDOP: "+pdop+"; HDOP: "+hdop+"; VDOP: "+vdop+".";
			return ret;
		}
		else return "GSA Invalid data.";
	}
	
	public boolean isAutoSelectioMode() {
		return autoSelectionMode;
	}
	
	public int getMode() {
		if(isWellFormed) return mode;
		else return MODE_NO_FIX;
	}
	
	public int getNumOfSatellites() {
		if(isWellFormed) return numOfSatellites;
		else return 0;
	}
	
	public int getSatelliteId(int index) {
		if(isWellFormed && index>=0 && index<numOfSatellites) return satellites[index]; 
		else return 0;
	}
	
	public float getPDOP() {
		if(isWellFormed) return pdop;
		else return 0;
	}
	
	public float getHDOP() {
		if(isWellFormed) return hdop;
		else return 0;
	}
	
	public float getVDOP() {
		if(isWellFormed) return vdop;
		else return 0;
	}

}
