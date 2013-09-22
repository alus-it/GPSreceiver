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
package it.alus.GPSreceiver.serialPort;

import javax.comm.*;
import java.util.*;

public class PortLister {
	private static final long serialVersionUID = 1L;
	private static HashMap<String, CommPortIdentifier> map = new HashMap<String, CommPortIdentifier>(); //mapping from names to CommPortIdentifiers.
	private static List<String> list=new LinkedList<String>();
	private static int numOfPorts=0;

	public static void populate() {
		String osname = System.getProperty("os.name","").toLowerCase();
		if(osname.startsWith("linux")) try { //inizializzazione del driver per linux
			CommDriver driver=(CommDriver) Class.forName("com.sun.comm.LinuxDriver").newInstance();
			driver.initialize(); //ATT: può portare ad avere doppie porte se viene ricaricato
		} catch(Exception e) {
			System.out.println("ERROR while initializing the serial driver: "+e.getMessage());
		}
		else if(osname.startsWith("windows")) try { //inizializzazione del driver per windows
			CommDriver driver = (CommDriver) Class.forName("com.sun.comm.Win32Driver").newInstance();
			driver.initialize(); //ATT: può portare ad avere doppie porte se viene ricaricato
		} catch (Exception e) {
			System.out.println("ERROR while initializing the serial driver: "+e.getMessage());
		}
	    else {
	    	System.out.println("Sorry, your operating system is not supported");
	        return;
	    }		
		Enumeration<?> pList = CommPortIdentifier.getPortIdentifiers();
		while (pList.hasMoreElements()) {
			CommPortIdentifier cpi = (CommPortIdentifier)pList.nextElement();
			if(!map.containsKey(cpi.getName()) && cpi.getPortType()==CommPortIdentifier.PORT_SERIAL) { 
				map.put(cpi.getName(), cpi);
				list.add(cpi.getName());
				numOfPorts++;
			}
		}
	}
	
	public static CommPortIdentifier getCommPortIdenfifier(String comPortName) {
		if(map.containsKey(comPortName)) return map.get(comPortName);
		else return null;
	}

	public static boolean isReady() {
		if(numOfPorts>0) return true;
		else return false;
	}
	
	public static List<String> getComPortsNames() {
		return list;
	}
}
