import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import javax.swing.*;

public class Client extends JFrame implements ActionListener{
	private JPanel    clientsInfo;
	private JTextField machineInfo;
	private JTextField portInfo;
	
	private JTextField pField;
	private JTextField qField;
	private JTextField name;
	private Pair publicKey;
	private Pair privateKey;
	
	private int p;
	private int q;
	private int n;
	private String clientName;
	
	String machineName;
	JButton sendButton;
	int portNum = -1;
	boolean connected;
	
	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	
	  
	JTextArea  chatHistory;
	private JTextField message;
	private JList<String> clientList;
	DefaultListModel<String> names;
	
	public Client() {
		setLayout(new BorderLayout());
		
		connected = false;
		machineInfo = new JTextField("10.107.210.254");
		portInfo = new JTextField();
		pField = new JTextField();
		qField = new JTextField();
		name = new JTextField();
		
		add(new JScrollPane(clientList()), BorderLayout.EAST);
		
		chatHistory = new JTextArea(10, 40);
		chatHistory.setEditable(false);	
		
		message = new JTextField("");
		message.addActionListener(this);
		
		sendButton = new JButton("Send Message");
		sendButton.setEnabled(false);
		sendButton.addActionListener(this);
		
		add(new JScrollPane(chatHistory),BorderLayout.CENTER);
		add(messagePanel(), BorderLayout.SOUTH);
		
		setJMenuBar(createMenuBar());
		
		
		
		setSize(500, 250);
		setVisible(true);
	}
	private JPanel messagePanel() {
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BorderLayout());


		messagePanel.add(message, BorderLayout.CENTER);
		messagePanel.add(sendButton, BorderLayout.EAST);
		
		return messagePanel;
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sendButton ||
		   e.getSource() == message) {
			sendMessage();
		}
		else {
			
			manageConnection();
		}
	}
	public void manageConnection() {
		if(connected == false) {
			Object[] message = {
					"Machine Info:", machineInfo,
					"Port Info:", portInfo,
					"Private Key:", p,
					"Public Key:", q,
					"Name:", name,
			};
			int result = JOptionPane.showConfirmDialog(null, message, "Enter Server info", JOptionPane.OK_CANCEL_OPTION);
			if(result == JOptionPane.OK_OPTION) {
				machineName = machineInfo.getText();
				portNum = Integer.parseInt(portInfo.getText());
				p = Integer.parseInt(pField.getText());
				q = Integer.parseInt(qField.getText());
				clientName = name.getText( );
				n = p*q;
				publicKey = new Pair(p, n);
				privateKey = new Pair(q, n);
				
			}
			
			try {
				clientSocket = new Socket(machineName, portNum);
				out = new PrintWriter(clientSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				sendButton.setEnabled(true);			
				connected = true;
				new ClientCommunicationThread(in, this);

			}
			catch(NumberFormatException nfe) {
				System.out.println("Server Port must be integer");
				
			}
			catch(UnknownHostException uhe) {
				System.out.println("Don't know host");
			}
			catch(IOException ioe) {
				System.out.println("Couldn't get I/O");
			}
		}
		else {
			try 
	        {
	          out.close();
	          in.close();
	          clientSocket.close();
	          sendButton.setEnabled(false);
	          connected = false;

	        }
	        catch (IOException e) 
	        {
	            chatHistory.insert ("Error in closing down Socket ", 0);
	        }
		}
		
		
	}

	public void sendMessage() {
		try {
			out.println(message.getText());
			message.setText("");
		}
		catch(Exception e) {
			chatHistory.insert("Error processing message", 0);
		}
	}
	
	
	
	private JPanel clientList() {
		clientsInfo = new JPanel();
		clientsInfo.setLayout(new BorderLayout());
		
		JLabel connectedClients = new JLabel("Connected Clients");
		connectedClients.setMaximumSize(new Dimension(150, 100));
		
		clientsInfo.add(connectedClients, BorderLayout.NORTH);
		
		names = new DefaultListModel<>();

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
		startConnection.addActionListener(this);
		
		connect.add(startConnection);
		
		menuBar.add(about);
		menuBar.add(help);
		menuBar.add(connect);
		
		
		return menuBar;
	}
	
}

class ClientCommunicationThread extends Thread {
	private Client client;
	private BufferedReader in;
	
	public ClientCommunicationThread(BufferedReader br, Client c) {
		in = br;
		client = c;
		start();
		
	}
	public void run() {
		System.out.println("New Client Communication Thread");
		
		try {
			String inputLine;
			while((inputLine = in.readLine()) != null) {
				System.out.println("Client: "+ inputLine);
				client.chatHistory.insert("From Server:" + inputLine + 
						"\n", 0);
				if(inputLine.equals("Bye."))
					break;
			}
			in.close();
		}
		catch(IOException e){
			System.err.println("Problem with Client Read");
		}
	}
	
}


