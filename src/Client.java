import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class Client extends JFrame{
	private JPanel    clientsInfo;
	private JButton   sendButton;
	private JButton   connectButton;
	
	private JList<String> clientList;
	DefaultListModel<String> names;
	
	public Client() {
		setLayout(new BorderLayout());
		add(clientList(), BorderLayout.EAST);
		setJMenuBar(createMenuBar());
		
		
		setSize(500, 250);
		setVisible(true);
	}
	
	
	
	
	private JPanel clientList() {
		clientsInfo = new JPanel();
		clientsInfo.setLayout(new BorderLayout());
		
		JLabel connectedClients = new JLabel("Connected Clients");
		connectedClients.setMaximumSize(new Dimension(150, 100));
		
		clientsInfo.add(connectedClients, BorderLayout.NORTH);
		
		names = new DefaultListModel<>();
		names.addElement("Zakee Jabbar");
		names.addElement("Sarah Kazi");
		
		clientList = new JList<>(names);
		clientList.setBounds(100, 100, 75, 35);
		clientsInfo.add(clientList, BorderLayout.CENTER);
		
		return clientsInfo;
	}
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		
		JMenu about   = new JMenu("About");
		JMenu help    = new JMenu("Help");
		JMenu connect = new JMenu("Connect");
		
		JMenuItem startConnection = new JMenuItem("Start Connection");
		startConnection.addActionListener(new Connection());
		
		connect.add(startConnection);
		
		menuBar.add(about);
		menuBar.add(help);
		menuBar.add(connect);
		
		
		return menuBar;
	}
	
}
class Connection implements ActionListener{
	private JTextField machineInfo = new JTextField();
	private JTextField portInfo = new JTextField();
	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	String machineName;
	int portNum = -1;
	
	Object[] message = {
			"Machine Info:", machineInfo,
			"Port Info:", portInfo,
	};
	public void actionPerformed(ActionEvent e) {
		int result = JOptionPane.showConfirmDialog(null, message, "Enter Server info", JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.OK_OPTION) {
			machineName = machineInfo.getText();
			portNum = Integer.parseInt(portInfo.getText());
			manageConnection();
		}
	}
	public void manageConnection() {
		try {
			clientSocket = new Socket(machineName, portNum);
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		}
		catch(NumberFormatException e) {
			System.out.println("Server Port must be integer");
			
		}
		catch(UnknownHostException e) {
			System.out.println("Don't know host");
		}
		catch(IOException e) {
			System.out.println("Couldn't get I/O");
		}
		
		try {
			out.close();
			in.close();
			clientSocket.close();
		}
		catch(IOException e) {
			System.out.println("Couldn't close socket");
		}
	}
}

