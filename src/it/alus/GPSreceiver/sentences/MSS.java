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
/*=== MSS - Beacon Receiver Status ===

------------------------------------------------------------------------------
         1  2  3  4    5   6
         |  |  |  |    |   |
 $--MSS,nn,nn,fff,bbb,xxx*hh<CR><LF>
------------------------------------------------------------------------------

Field Number:

1. Signal strength (dB 1uV)
2. Signal to noise ratio (dB)
3. Beacon frequency (kHz)
4. Beacon data rate (BPS)
5. Channel Number (The channel of the beacon being used if a multi-channel beacon receiver is used)
6. Checksum

$GPMSS,0,0,0.000000,200,*5A

*/

package it.alus.GPSreceiver.sentences;

public class MSS extends Sentence {
	private int signalStrength;
	private int SNR;
	private float beaconFrequency;
	private int beaconDataRate;
	private int channel;
	
	public MSS(int talkerID, String ascii, long receivingTimestamp) {
		super(talkerID,MSS,ascii,receivingTimestamp);
		String sub=ascii.substring(7,ascii.length()-3);
		String delims="[,]";
		String[] t=sub.split(delims);
		if(t.length>=4) {
			if(t[0].isEmpty()) signalStrength=0;
			else signalStrength=Integer.parseInt(t[0]);
			if(t[1].isEmpty()) SNR=0;
			else SNR=Integer.parseInt(t[1]);
			if(t[2].isEmpty()) beaconFrequency=0;
			else beaconFrequency=Float.parseFloat(t[2]);
			if(t[3].isEmpty()) beaconDataRate=0;
			else beaconDataRate=Integer.parseInt(t[3]);
			if(t.length==5) if(t[4].isEmpty()) channel=-1;
			else channel=Integer.parseInt(t[4]);
			isWellFormed=true;
		} else isWellFormed=false;
	}
	
	@Override public String toString() {
		if(isWellFormed) return "MSS Signal strength: "+signalStrength+" dB 1uV, SNR: "+SNR+" dB,"+
		" Beacon frequency: "+beaconFrequency+" kHz, Beacon data rate: "+beaconDataRate+" B/s,"+
		" Channel: "+channel;
		else return "MSS Invalid data.";
	}

	public int getSignalStrength() {
		return signalStrength;
	}

	public int getSNR() {
		return SNR;
	}

	public float getBeaconFrequency() {
		return beaconFrequency;
	}

	public int getBeaconDataRate() {
		return beaconDataRate;
	}

	public int getChannel() {
		return channel;
	}

}
