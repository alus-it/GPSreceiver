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

package it.alus.GPSreceiver;

import it.alus.GPSreceiver.instruments.SatelliteRadar;
import it.alus.GPSreceiver.sentences.*;

public class SentenceReceiver {
	private static boolean systemStarted;
	private static SatelliteRadar radar;
	public static float speedKmh, speedKnots, totalSpeedMs, accelerationMs2;
	public static float altMt, altFt, climbFtMin;
	public static float trueTrack, magneticTrack, magneticVariation, turnRateDegMin, turnRateDegSec;
	private static boolean isMagVarToEast;
	public static float altTimestamp,posTimestamp,dirTimestamp,accTimestamp;
	private static int day,month,year,hour,minute,timezone;
	private static float second;
	private static int latDeg, latMin, lonDeg, lonMin;
	public static float latMinDecimal, lonMinDecimal, latSec, lonSec;
	private static boolean isLatN, isLonE;
	private static float pdop,hdop,vdop,beaconFrequency;
	private static int activeSats,satsInView,modeFix,mode2d3d;
	private static int signalStrength,SNR,beaconDataRate,channel;
	
	public SentenceReceiver() {
		systemStarted=true;
		altTimestamp=0;
		posTimestamp=0;
		dirTimestamp=0;
		accTimestamp=0;
		pdop=50;
		hdop=50;
		vdop=50;
		radar = new SatelliteRadar();
	}
	
	public static void receiveSentence(String ascii,long timestamp) {
		if(ascii==null || ascii.length()<1) return;
		//System.out.println(ascii);//// DEBUG
		Sentence sentence=Sentence.buildSentence(ascii,timestamp);
		if(sentence==null) return;
		if(!sentence.isWellFormed()) {
			System.out.println("Invalid sentence: "+ascii);
			return;
		}
		switch(sentence.getTypeCode()) { //switch of various types of sentences received
			case Sentence.GGA: GGA gga=(GGA)sentence;
				System.out.println(gga.toString());
				updateTime(gga.getTimeHour(),gga.getTimeMin(),gga.getTimeSec());
				if(gga.getQuality()!=GGA.Q_NO_FIX) {
					updateAltitude(gga.getAltitude(),gga.getAltitudeUnits(),gga.getTimestamp());
					updatePosition(gga.getLatitudeGrades(),gga.getLatitudeMinutes(),gga.isLatitudeNorth(),gga.getLongitudeGrades(),gga.getLongitudeMinutes(),gga.isLongitudeEast(),gga.getTimestamp());
					updateHdiluition(gga.gethDilutionPrecision());
					updateNumOfTotalSats(gga.getNumOfSatellites());
					updateFixMode(gga.getQuality());
				}
				break;
			case Sentence.RMC: RMC rmc=(RMC)sentence;
				System.out.println(rmc.toString());
				updateDate(rmc.getTimeDay(),rmc.getTimeMonth(),rmc.getTimeYear());
				updateTime(rmc.getTimeHour(),rmc.getTimeMin(),rmc.getTimeSec());
				if(rmc.isValid()) {
					updateSpeed(rmc.getGroundSpeedKnots(),rmc.getTimestamp());
					updateDirection(rmc.getTrueTrack(),rmc.getMagneticVariation(),rmc.isMagneticVariationToEast(),rmc.getTimestamp());
					updatePosition(rmc.getLatitudeGrades(),rmc.getLatitudeMinutes(),rmc.isLatitudeNorth(),rmc.getLongitudeGrades(),rmc.getLongitudeMinutes(),rmc.isLongitudeEast(),rmc.getTimestamp());
				}
				break;
			case Sentence.MSS: MSS mss=(MSS)sentence;
				System.out.println(mss.toString());
				updateSignalInfo(mss.getSignalStrength(),mss.getSNR(),mss.getBeaconFrequency(),mss.getBeaconDataRate(),mss.getChannel());
				break;
			case Sentence.GSA: GSA gsa=(GSA)sentence;
				if(gsa.getMode()!=GSA.MODE_NO_FIX) {
					update2D3Dmode(gsa.getMode());
					updateDiluition(gsa.getPDOP(),gsa.getHDOP(),gsa.getVDOP());
					updateNumOfActiveSats(gsa.getNumOfSatellites());
				}
				System.out.println(gsa.toString());
				break;
			case Sentence.ZDA: ZDA zda=(ZDA)sentence;
				System.out.println(zda.toString());
				updateDate(zda.getTimeDay(),zda.getTimeMonth(),zda.getTimeYear());
				updateTime(zda.getTimeHour(),zda.getTimeMin(),zda.getTimeSec());
				break;
			case Sentence.GSV: GSV gsv=(GSV)sentence;
				if(gsv.isLastMsgOfSeq()) updateNumOfTotalSats(gsv.getTotalNumberOfSatellitesInView());
				radar.receiveGSVsentence(gsv);
				System.out.println(gsv.toString());
				break;
			case Sentence.VTG: VTG vtg=(VTG)sentence;
				System.out.println(vtg.toString());
				if(vtg.getFAAindicator()!=Sentence.FAA_NOTVAL)
					updateGroundSpeedAndDirection(vtg.getGroundSpeedKmh(),vtg.getGroundSpeedKnots(),vtg.getTrueTrack(),vtg.getMagneticTrack());
			break;
			case Sentence.GLL: GLL gll=(GLL)sentence;
				System.out.println(gll.toString());
				if(gll.isValid()) {
					updatePosition(gll.getLatitudeGrades(),gll.getLatitudeMinutes(),gll.isLatitudeNorth(),gll.getLongitudeGrades(),gll.getLongitudeMinutes(),gll.isLongitudeEast(),gll.getTimestamp());
					updateTime(gll.getTimeHour(),gll.getTimeMin(),gll.getTimeSec());
				}
				break;
			case Sentence.GBS: GBS gbs=(GBS)sentence;
				//At the moment we never see any GPS receiver able to send this kind of sentence
				System.out.println(gbs.toString());
				break;
			default: System.out.println(ascii);
		}
	}

	private static float calculateTotalSpeedMs(float hSpeedMs,float vSpeedMs) {
		return (float)Math.sqrt(Math.pow(hSpeedMs,2)+Math.pow(vSpeedMs,2));
	}
	
	private static void updateAltitude(float newAltitude,char altUnit,float timestamp) {
		float newAltitudeMt,newAltitudeFt;
		if(altUnit=='M') {
			newAltitudeMt=newAltitude;
			newAltitudeFt=Converter.m2Ft(newAltitude);
		} else if(altUnit!='F') {
			newAltitudeFt=newAltitude;
			newAltitudeMt=Converter.Ft2m(newAltitude);
		} else {
			System.err.println("ERROR: Unknown altitude unit: "+altUnit);
			return;
		}
		InstrumentPanel.updateAltitude(newAltitudeMt,newAltitudeFt);
		if(altTimestamp!=0) {
			 float deltaT;
			 if(timestamp>altTimestamp) deltaT=timestamp-altTimestamp;
			 else if(timestamp!=altTimestamp) deltaT=timestamp+86400-altTimestamp;
			 else return;
			 float deltaH=newAltitudeFt-altFt;
			 climbFtMin=deltaH/(deltaT/60);
			 InstrumentPanel.updateVariometer(climbFtMin);
		}
		altTimestamp=timestamp;
		altMt=newAltitudeMt;
		altFt=newAltitudeFt;
		float newTotalSpeedMs=calculateTotalSpeedMs(Converter.Kmh2ms(speedKmh),Converter.FtMin2ms(climbFtMin));
		InstrumentPanel.updateTotalSpeedKmh(Converter.ms2Kmh(newTotalSpeedMs));
		if(accTimestamp!=0) {
			 float deltaT;
			 if(timestamp>accTimestamp) deltaT=timestamp-accTimestamp;
			 else if(timestamp!=accTimestamp) deltaT=timestamp+86400-accTimestamp;
			 else return;
			 float deltaV=newTotalSpeedMs-totalSpeedMs;
			 accelerationMs2=deltaV/deltaT;
			 InstrumentPanel.updateAcceleration((float) (accelerationMs2/9.80665));
		 }
		 accTimestamp=timestamp;
		 totalSpeedMs=newTotalSpeedMs;
	}
	
	private static void updateDate(int newDay,int newMonth,int newYear) {
		day=newDay;
		month=newMonth;
		year=newYear;
		InstrumentPanel.updateDate(day,month,year);
	}
	
	private static void updateTime(int newHour, int newMin, float newSec) {
		hour=newHour;
		minute=newMin;
		second=newSec;
		InstrumentPanel.updateTime(hour,minute,second);
	}

	private static void updatePosition(int newlatDeg, float newlatMin, boolean newisLatN, int newlonDeg, float newlonMin, boolean newisLonE, float timestamp) {
		posTimestamp=timestamp;
		latDeg=newlatDeg;
		lonDeg=newlonDeg;
		latMinDecimal=newlatMin;
		lonMinDecimal=newlonMin;
		latMin=(int)newlatMin;
		lonMin=(int)newlonMin;
		latSec=(newlatMin-latMin)*60;
		lonSec=(newlonMin-lonMin)*60;
		isLatN=newisLatN;
		isLonE=newisLonE;
		InstrumentPanel.updatePosition(latDeg,latMin,latSec,isLatN,lonDeg,lonMin,lonSec,isLonE);
	}
	
	private static void updateGroundSpeedAndDirection(float newSpeedKmh,float newSpeedKnots,float trueTrack,float magneticTrack) {
		if(newSpeedKmh==0) if(speedKmh>20) return;
		InstrumentPanel.updateGroundSpeedKmh(speedKmh);
		if(speedKmh>3) InstrumentPanel.updateCompass(trueTrack,magneticTrack);
	}
	
	private static void updateSpeed(float newSpeedKnots, float timestamp){
		speedKnots=newSpeedKnots;
		speedKmh=Converter.Knots2Kmh(newSpeedKnots);
		InstrumentPanel.updateGroundSpeedKmh(speedKmh);
		float newTotalSpeedMs=calculateTotalSpeedMs(Converter.Kmh2ms(speedKmh),Converter.FtMin2ms(climbFtMin));
		InstrumentPanel.updateTotalSpeedKmh(Converter.ms2Kmh(newTotalSpeedMs));
		if(accTimestamp!=0) {
			 float deltaT;
			 if(timestamp>accTimestamp) deltaT=timestamp-accTimestamp;
			 else if(timestamp!=accTimestamp) deltaT=timestamp+86400-accTimestamp;
			 else return;
			 float deltaV=newTotalSpeedMs-totalSpeedMs;
			 accelerationMs2=deltaV/deltaT;
			 InstrumentPanel.updateAcceleration((float) (accelerationMs2/9.80665));
		 }
		 accTimestamp=timestamp;
		 totalSpeedMs=newTotalSpeedMs;
	}
	
	private static void updateDirection(float newTrueTrack, float magneticVar, boolean isVarToEast, float timestamp) {
		magneticVariation=magneticVar;
		isMagVarToEast=isVarToEast;
		if(speedKmh>3) {
			if(newTrueTrack<90 && newTrueTrack>270) { //sono sopra
				if(isVarToEast) magneticTrack=newTrueTrack+magneticVar;
				else magneticTrack=newTrueTrack-magneticVar;
			} else { //sono sotto
				if(isVarToEast) magneticTrack=newTrueTrack-magneticVar;
				else magneticTrack=newTrueTrack+magneticVar;
			}
			InstrumentPanel.updateCompass(newTrueTrack,magneticTrack);
			if(dirTimestamp!=0) {
				float deltaT;
				if(timestamp>dirTimestamp) deltaT=timestamp-dirTimestamp;
				else if(timestamp!=dirTimestamp) deltaT=timestamp+86400-dirTimestamp;
				else return;
				float deltaA=newTrueTrack-trueTrack;
				turnRateDegSec=deltaA/deltaT;
				turnRateDegMin=turnRateDegSec*60;
				InstrumentPanel.updateTurnometer(turnRateDegMin);
			}
			dirTimestamp=timestamp;
			trueTrack=newTrueTrack;
		}
	}
	
	private static void updateNumOfTotalSats(int totalSats) {
		satsInView=totalSats;
		InstrumentPanel.updateNumOfSats(activeSats,satsInView);
	}
	
	private static void updateNumOfActiveSats(int workingSats) {
		activeSats=workingSats;
		InstrumentPanel.updateNumOfSats(activeSats,satsInView);
	}
	
	private static void updateHdiluition(float hDiluition) {
		hdop=hDiluition;
		InstrumentPanel.updateDiluitions(pdop,hdop,vdop);
	}
	
	private static void updateDiluition(float pDiluition, float hDiluition, float vDiluition) {
		pdop=pDiluition;
		hdop=hDiluition;
		vdop=vDiluition;
		InstrumentPanel.updateDiluitions(pdop,hdop,vdop);
	}
	
	private static void updateFixMode(int fixMode) {
		modeFix=fixMode;
		InstrumentPanel.updateFixMode(modeFix,mode2d3d);
	}
	
	private static void update2D3Dmode(int mode23D) {
		mode2d3d=mode23D;
		InstrumentPanel.updateFixMode(modeFix,mode2d3d);
	}
	
	private static void updateSignalInfo(int newSignalStrength, int newSNR,float newBeaconF, int newBeaconDataRate, int newChannel) {
		signalStrength=newSignalStrength;
		SNR=newSNR;
		beaconFrequency=newBeaconF;
		beaconDataRate=newBeaconDataRate;
		channel=newChannel;
	}
	
	public static boolean isSystemStarted() {
		return systemStarted;
	}

	public static void Stop() {
		systemStarted=false;
	}

	public static void receiveMessage(String ascii,long timestamp) {
		//TODO: do something with the timestamp
		System.out.println("Not NMEA msg: "+ascii);
	}

}