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
== Physical protocol layer ==

The NMEA specification requires a physical-level protocol compatible
with RS422 at 4800bps, 8N1 or 7N2.  It is RS422 rather than RS232
because NMEA expects many navigational devices to feed a common serial
bus.  The darta encoding is ASCII with the high data bit not used and zeroed.

Consumer-grade GPS sensors normally report over an RS232 port or a USB
port emulating an RS232 serial device. Baud rate is variable, with
9600 probably the most common.  Most deviveces use 8N1; there are 
rare exceptions that use 7N2 (San Jose Navigation) or even 8O1 (Trimble).


== NMEA Encoding Conventions ==

An NMEA sentence consists of a start delimiter, followed by a
comma-separated sequence of fields, followed by the character '\*'
(ASCII 42), followed by a CRC32 checksum expressed as two hexadecimal
digits, followed by an end-of-line marker.

The start delimiter is normally '$' (ASCII 36). Packets of AIVDM/AIVDO
data, which are otherwise formatted like NMEA, use '!'.  It is
possible that recent revisions of NMEA may allow other exceptions;
we do not know.
*/

package it.alus.GPSreceiver.serialPort;

import it.alus.GPSreceiver.SentenceReceiver;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TooManyListenersException;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

public class SerialDriver extends Thread implements SerialPortEventListener {
	private static InputStream inputStream;
	private static OutputStream outputStream;
	protected static SerialPort port;
	private static final int READBUFFER_SIZE = 4096;
	private static final int TIMEOUT_MAX = 5000;
	private static final int TIMEOUT_MILLIS = 2000;
	private static boolean receiving,trasmitting;
	private static Queue<byte[]> outgoingQueue;
	private static boolean isUARTmanagerActive;
	private static boolean lineFeedExpected;
	private static byte[] sentence;
	private static int rcvdBytesOfSentence;
	private static long timestamp;
	
	public SerialDriver(CommPortIdentifier comPortId) throws IOException {
		isUARTmanagerActive=false;
		receiving=false;
		trasmitting=false;
		sentence=new byte[READBUFFER_SIZE];
		rcvdBytesOfSentence=0;
		lineFeedExpected=false;
		outgoingQueue = new LinkedList<byte []>();
		if(!openSerialPort(comPortId)) {
			System.out.println("Can't operate on port "+comPortId.getName()+" closing SerialDriver...");
			System.gc();
		}
	}

	private synchronized boolean openSerialPort(CommPortIdentifier portId) {
		if(portId.getPortType()!=CommPortIdentifier.PORT_SERIAL) {
			System.out.println("Error: port: "+portId.getName()+" is not a serial port!");
			return false;
		}
		try {
			port=(SerialPort)portId.open("GPSreceiver",TIMEOUT_MAX);
		} catch (PortInUseException e) {
			System.out.println("Warning: The port "+portId.getName()+" was already in use by: "+ e.currentOwner);
			return false;
		} 
		catch (RuntimeException e) {
			System.out.println("Error: Runtime Exception while opening port "+portId.getName());
			return false;
		}
		if(port==null) {
			System.out.println("Error: port request timed out");
			return false;
		}
		try {
			port.enableReceiveTimeout(TIMEOUT_MILLIS);
		} catch (UnsupportedCommOperationException e) {
			System.out.println("ERROR: Impossibile settare il timeout sulla seriale.");
			e.printStackTrace();
		}
		try {
			inputStream = port.getInputStream();
		} catch (IOException e1) {
			System.out.println("Error while opening the input stream to serial port "+portId.getName());
			e1.printStackTrace();
			port.close();
			return false;
		}
		try {
			outputStream = port.getOutputStream();
		} catch (IOException e1) {
			System.out.println("Error while opening the output stream to serial port "+portId.getName());
			e1.printStackTrace();
			port.close();
			try {
				inputStream.close();
			} catch (IOException e) {
				System.out.println("Error while reclosing input stream to serial port "+portId.getName());
				e.printStackTrace();
			}
			return false;
		}
		try {
			port.addEventListener(this); //c'è un this quindi questo metodo non può essere static
		} catch (TooManyListenersException e) {
			System.out.println("Too many listeners on port "+portId.getName()+": "+e.getMessage());
			try {
				inputStream.close();
				outputStream.close();
			} catch (IOException e1) {
				System.out.println("Error while reclosing IO streams on serial port "+portId.getName());
				e1.printStackTrace();
			}
			port.close();
			return false;
		}
		port.notifyOnDataAvailable(true);
		try {
			port.setSerialPortParams(4800,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
			port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
		} catch (UnsupportedCommOperationException e) {
			System.out.println("Operation not supported on port "+portId.getName()+": "+e.getMessage());
			port.close();
			try {
				inputStream.close();
				outputStream.close();
			} catch (IOException e1) {
				System.out.println("Error while reclosing IO streams on serial port "+portId.getName());
				e1.printStackTrace();
			}
			return false;
		}
		this.start();
		isUARTmanagerActive=true;
		return true;
	}
	
	private static int getCRCintValue() {
		if(rcvdBytesOfSentence<9) return -1;
		try {
			int checksum=Integer.parseInt(""+((char)sentence[rcvdBytesOfSentence-2])+((char)sentence[rcvdBytesOfSentence-1]),16);
			return checksum;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public synchronized void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI: //Break Interrupt
		case SerialPortEvent.OE: //Overrun Error
		case SerialPortEvent.FE: //Framing Error
		case SerialPortEvent.PE: //Parity Error
		case SerialPortEvent.CD: //Carrier Detect
		case SerialPortEvent.DSR: //Data Sent Ready
		case SerialPortEvent.RI: //Ring Indicator
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY: //Dati trasmessi output buffer vuoto
		case SerialPortEvent.CTS: break; //Clear To Send
		case SerialPortEvent.DATA_AVAILABLE: //Dati ricevuti nel buffer da leggere
			receiving=true;
			timestamp=System.currentTimeMillis();
			byte[] readBuffer = new byte[READBUFFER_SIZE];
			int redBytes=0,i; // number of bytes read from serial port
			try { // states machine implementation
				while(inputStream.available()>0) {
					redBytes=inputStream.read(readBuffer); //leggo il buffer
					for(i=0;i<redBytes;i++) switch(readBuffer[i]) {
						case '$': if(rcvdBytesOfSentence==0) sentence[rcvdBytesOfSentence++]='$'; break;
						case '\r': if(rcvdBytesOfSentence>0) lineFeedExpected=true; break;
						case '\n': if(lineFeedExpected) {
								if(rcvdBytesOfSentence>3) { //to avoid overflow errors
									if(sentence[rcvdBytesOfSentence-3]=='*') {  //verifica CRC
										int checksum=0;
										for(int j=1;j<rcvdBytesOfSentence-3;j++) checksum=checksum^sentence[j];
										if(getCRCintValue()==checksum) SentenceReceiver.receiveSentence(new String(sentence,0,rcvdBytesOfSentence,"ASCII"),timestamp);
									} else SentenceReceiver.receiveMessage(new String(sentence,0,rcvdBytesOfSentence,"ASCII"),timestamp); //line without checksum
								}
								rcvdBytesOfSentence=0;
								lineFeedExpected=false;
							}
							break;
						default: if(rcvdBytesOfSentence>0)
									if(rcvdBytesOfSentence<READBUFFER_SIZE) sentence[rcvdBytesOfSentence++]=readBuffer[i];
									else { //to avoid buffer overflow
										rcvdBytesOfSentence=0;
										lineFeedExpected=false;
									}
					} //end of or each byte of just received sequence
				} // end of the 'while' loop
			} catch (Exception e) { //catch, warn, ignore better to catch ANY exception to avoid ANY kind of error
				e.printStackTrace();
				System.out.println(e.getClass().getName()+" caugth in the source: "+e.getMessage());
			}
			receiving=false;
			goAhead();
			break; //end of data available
		} // end of switch
	}
	
	private static synchronized void sendDataToUARTnow(byte[] dataToSend) { //invia immediatamente
		trasmitting=true;
		try {
			//System.out.println("Data to UART: "+Converter.Vector2HexString(dataToSend));
			outputStream.write(dataToSend);
		} catch (IOException e) {
			e.printStackTrace();
		}
		trasmitting=false;
	}
	
	public static void sendDataToUART(byte[] dataToSend) { //mette in coda per inviare
		if(!trasmitting && !receiving && isUARTmanagerActive) {
			System.out.println("####senData trasm:"+trasmitting+" rec:"+receiving+" isActive:"+isUARTmanagerActive);
			if(!outgoingQueue.isEmpty()) {
				//System.out.println("Data to QUEUE: "+Converter.Vector2HexString(dataToSend));
				outgoingQueue.add(dataToSend);
			} else sendDataToUARTnow(dataToSend);
		}
		else {
			//System.out.println("Data to QUEUE: "+Converter.Vector2HexString(dataToSend));
			outgoingQueue.add(dataToSend);
		}
	}
	
	public static void goAhead() {
		if(!trasmitting && !receiving && isUARTmanagerActive)
			if(!outgoingQueue.isEmpty()) {
				System.out.println("####GOAHEAD!! trasm:"+trasmitting+" rec:"+receiving+" isActive:"+isUARTmanagerActive+" queueEmpty:"+outgoingQueue.isEmpty());
				sendDataToUARTnow(outgoingQueue.poll());
			}
	}
	
	public static synchronized void Stop() {
		if(isUARTmanagerActive) {
			isUARTmanagerActive=false;
			outgoingQueue.clear();
			try {
				outputStream.close();
				inputStream.close();
			} catch (IOException e) {
				System.out.println("Error while reclosing IO streams on serial port.");
				e.printStackTrace();
			}
			trasmitting=false;
			receiving=false;
			rcvdBytesOfSentence=0;
			lineFeedExpected=false;
			port.close();
		}
	}

	public static boolean isActive() {
		return isUARTmanagerActive;
	}

}
