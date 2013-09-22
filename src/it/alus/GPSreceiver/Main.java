//============================================================================
// GPSreceiver
// This is the main of Java demo program that parses NMEA sentences from a
// serial GPS receiver and displays live the received data.
// Author         : Alberto Realis-Luc <alberto.realisluc@gmail.com>
// Since          : July 2010
// Web            : http://www.alus.it/airnavigator/gpsreceiver/
// Git Repository : https://github.com/alus-it/GPSreceiver.git
// Version        : 0.1
// Copyright      : © 2010 Alberto Realis-Luc
// License        : GPL
// Last change    : 2 March 2011
//============================================================================

package it.alus.GPSreceiver;

import it.alus.GPSreceiver.serialPort.PortChooser;
import it.alus.GPSreceiver.serialPort.PortLister;
import it.alus.GPSreceiver.serialPort.SerialDriver;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.comm.CommPortIdentifier;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Main {
	private static boolean serialDriverStarted=false;
	private static boolean sentenceReceiverStarted=false;

	public static void main(String[] args) { //starts all the components in the right order
		if(args.length>1) { //controllo sugli argomenti
			System.out.println("USAGE: java it.alus.GPSreceiver.Main <ComPort>\n" +
					"where <ComPort> is the COM port where is attached the GPS receiver (Ex. COM1)\n");
			return;
		}
		
		//Instruments panel
		JFrame intruments = new JFrame("GPSreceiver");
		intruments.setIconImage(new ImageIcon(Main.class.getResource("altimeter.png")).getImage());
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //screen size
		intruments.setMaximumSize(screenSize); //to set the maximum size allowed
		intruments.setMinimumSize(new Dimension(640,480));
		intruments.setPreferredSize(new Dimension(800,600));
		intruments.setLocationRelativeTo(null); //centred window
		intruments.setSize(screenSize); //full screen
		InstrumentPanel pannello = new InstrumentPanel();	
		intruments.getContentPane().add(pannello,BorderLayout.CENTER);
		intruments.setVisible(true);
		intruments.setExtendedState(JFrame.MAXIMIZED_BOTH); //maximize, must be done after setVisible
		intruments.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Stop();
				System.exit(0);
			}
		});
		if(!sentenceReceiverStarted) { //Start the packet receiver
			new SentenceReceiver();
			sentenceReceiverStarted=true;
		}
		//FOR TESTING PURPOSE:
		//SentenceReceiver.receiveSentence("$GPGSV,3,3,09,31,12,310,00*43");
		//SentenceReceiver.receiveSentence("$GPGSV,3,1,09,12,69,045,00,30,62,292,27,14,40,278,34,09,34,137,30*77",System.currentTimeMillis());
		//SentenceReceiver.receiveSentence("$GPGSV,3,2,09,29,34,207,00,02,29,094,00,27,26,137,33,04,18,051,00*71",System.currentTimeMillis());
		//SentenceReceiver.receiveSentence("$GPGSV,3,3,09,31,12,310,00*4",System.currentTimeMillis());
		
		if(!serialDriverStarted) { //Start the listening thread on the serial port
			if(!PortLister.isReady()) PortLister.populate();
			CommPortIdentifier comPortId=null;
			if(args.length>0) comPortId=PortLister.getCommPortIdenfifier(args[0]);
			if(comPortId==null) { //start portChooser
				PortChooser chooser = new PortChooser(null);
				String portName = null;
				chooser.setVisible(true); // Dialog done. Get the port name.
				portName = chooser.getSelectedName();
				if(portName==null) {
					Stop(); //close all
					System.exit(0); //exit the program
				}
				comPortId = PortLister.getCommPortIdenfifier(portName); //Get the CommPortIdentifier	
			}
			try {
				new SerialDriver(comPortId);
				serialDriverStarted=true;
			} catch (IOException e) {
				serialDriverStarted=false;
				System.out.println("WARNING: UARTmanager is not started due to serial port errors: ");
				e.printStackTrace();
			}
		}
	}
	
	public static void Stop() {
		if(serialDriverStarted) {
			serialDriverStarted=false;
			SerialDriver.Stop();
		}
		if(sentenceReceiverStarted) {
			sentenceReceiverStarted=false;
			SentenceReceiver.Stop();
		}
		System.gc();
	}

	public static boolean isUartStarted() {
		return serialDriverStarted;
	}
	
	public static boolean isSentenceParserStarted() {
		return sentenceReceiverStarted;
	}
}
