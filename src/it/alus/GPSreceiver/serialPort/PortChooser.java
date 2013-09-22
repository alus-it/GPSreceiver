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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PortChooser extends JDialog implements ItemListener {
	private static final long serialVersionUID = 1L;
	protected String selectedPortName; /** The name of the choice the user made. */
	protected JComboBox serialPortsChoice; /** The JComboBox for serial ports */
	protected JLabel choice; /** To display the chosen */
	protected final int PAD = 5; /** Padding in the GUI */
	
	/** This will be called from either of the JComboBoxen when the user selects any given item.*/
	public void itemStateChanged(ItemEvent e) {
		selectedPortName = (String)((JComboBox)e.getSource()).getSelectedItem(); //Get the name
		choice.setText(selectedPortName); // Display the name.
	}

	public String getSelectedName() { //The public "getter" to retrieve the chosen port by name
		return selectedPortName;
	}

	public PortChooser(JFrame parent) {
		super(parent, "Port Chooser", true);
		makeGUI();
		populate();
		setSize(200,100);
		serialPortsChoice.addItemListener(this);
		pack();
		setResizable(false);
		setAlwaysOnTop(true);
		//setLocationRelativeTo(null);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    setLocation((screenSize.width-200)/2,(screenSize.height-100)/2);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	}

	protected void makeGUI() {
		Container cp = getContentPane();
		JPanel centerPanel = new JPanel();
		cp.add(BorderLayout.CENTER, centerPanel);
		centerPanel.setLayout(new GridLayout(0,2, PAD, PAD));
		centerPanel.add(new JLabel("Serial Ports", JLabel.RIGHT));
		serialPortsChoice = new JComboBox();
		centerPanel.add(serialPortsChoice);
		serialPortsChoice.setEnabled(false);
		centerPanel.add(new JLabel("Your choice:", JLabel.RIGHT));
		centerPanel.add(choice = new JLabel());
		JButton okButton;
		cp.add(BorderLayout.SOUTH, okButton = new JButton("OK"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PortChooser.this.dispose();
			}
		});

	}

	protected void populate() {
		List<String> portNames = PortLister.getComPortsNames();
		for(String currentPortName:portNames) {
			serialPortsChoice.addItem(currentPortName);
		}
		serialPortsChoice.setEnabled(true);
		serialPortsChoice.setSelectedIndex(-1);
	}

}
