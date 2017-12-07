import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

import javax.swing.*;
/*
 * prime number function reference:
 * https://stackoverflow.com/questions/1538644/c-determine-if-a-number-is-prime
 * */


public class Client extends JFrame implements ActionListener{
	
/*======================PRIVATE VARIABLES AND SOCKET VARIABLES====================*/	
	private JPanel    clientsInfo;
	private JTextField machineInfo;
	private JTextField portInfo;
	
	private JTextField pField;
	private JTextField qField;
	private JTextField name;
	
	private int p;
	private int q;
	
	private Pair publicKey;
	private Pair privateKey;
	
	private String clientName;
	
	String machineName;
	JButton sendButton;
	int portNum = -1;
	boolean connected;
	
	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	ObjectOutputStream objectOut = null;
    ObjectInputStream objectIn = null;
	
	  
	JTextArea  chatHistory;
	private JTextField message;
	private JList<String> clientList;
	DefaultListModel<String> names;
/*====================================CONSTRUCTOR====================================*/		
	public Client() {
		setLayout(new BorderLayout());
		
		connected = false;

		/*GET USER INFO*/
		name        = new JTextField("sarah");
		pField      = new JTextField("16189");
		qField      = new JTextField("16381");
		portInfo    = new JTextField("61553");
		machineInfo = new JTextField("10.18.198.74");

		
		/*ADD THE CLIENT LIST ON THE SIDE*/
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
	/*END CONSTRUCTOR*/

	private JPanel messagePanel() {
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new BorderLayout());

		messagePanel.add(message, BorderLayout.CENTER);
		messagePanel.add(sendButton, BorderLayout.EAST);
		
		return messagePanel;
	}
	/*IMPLEMENT ACTIONLISTENER*/
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sendButton ||
		   e.getSource() == message) {
			sendMessage();
		}
		else {
			
			manageConnection();
		}
	}
	/*=======================SET UP SOCKET/CONNECTION=========================*/
	public void manageConnection() {
		if(connected == false) {
			Object[] message = {
					"Machine Info:", machineInfo,
					"Port Info:", portInfo,
					"P:", pField,
					"Q:", qField,
					"Name:", name,
			};
			int result = JOptionPane.showConfirmDialog(null, message, "Enter Server info", JOptionPane.OK_CANCEL_OPTION);

			/*STORE USER INFO INTO VARIABLES*/
			if(result == JOptionPane.OK_OPTION) {
				machineName = machineInfo.getText();
				portNum = Integer.parseInt(portInfo.getText());
				p = Integer.parseInt(pField.getText());
				q = Integer.parseInt(qField.getText());
				clientName = name.getText( );
				
				RSAEncryption encryptionVal = new RSAEncryption(p, q);
				encryptionVal.setMessage("aaaa aaa aaa");
				
				publicKey = encryptionVal.getPublicKey();
				privateKey = encryptionVal.getPrivateKey();
				

                //initMessage = new MessageObject('A', encrypt.getPublicKey(), name.getText());

				//add = new MessageObject('A', encrypt.getPublicKey(), name.getText());

			}
			else {
				result = JOptionPane.showConfirmDialog(null, message, "Enter Server info", JOptionPane.OK_CANCEL_OPTION);
			}
			/*ESTABLISH CONNECTION WITH SERVER*/
			try {
				clientSocket = new Socket(machineName, portNum);

//				out = new PrintWriter(clientSocket.getOutputStream(), true);
//				in  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				objectOut = new ObjectOutputStream(clientSocket.getOutputStream());
		        objectIn  = new ObjectInputStream(clientSocket.getInputStream());

				connected = true;
				sendButton.setEnabled(true);
		        
				new ClientCommunicationThread(objectIn, this, objectOut);

			}
			catch(IllegalArgumentException iae) {
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
/*============================CLOSE CONNECTION=======================================*/
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
/*===================================SEND MESSAGE TO SERVER==========================*/
	public void sendMessage() {
		try {
            MessageObject mes = new MessageObject('M', clientName, message.getText());
            message.setText("");
            objectOut.writeObject(mes);
            objectOut.flush();
        }

		catch(Exception e) {
			chatHistory.insert("Error processing message", 0);
			e.printStackTrace();
		}
	}

    public String getClientName() {
        return clientName;
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
		JMenu quit    = new JMenu("Quit");
		
		JMenuItem startConnection = new JMenuItem("Start Connection");
		JMenuItem quitChat = new JMenuItem("Quit");
		
		startConnection.addActionListener(this);
	
		
		connect.add(startConnection);
		quit.add(quitChat);
		
		menuBar.add(about);
		menuBar.add(help);
		menuBar.add(connect);
		
		
		return menuBar;
	}
	
	private boolean isPrime(int num) {
		for (int i=2; i<num; i++) {
		    if (num % i == 0 && i != num) 
		    		return false;  
		}
		return true;
	}
	
}

class ClientCommunicationThread extends Thread {
	private Client client;
	private BufferedReader in;
	private ObjectInputStream objectIn;
	private ObjectOutputStream objectOut;
	
	public ClientCommunicationThread(ObjectInputStream br, Client c, ObjectOutputStream outR) {
		objectIn = br;
		objectOut = outR;
		client = c;
		start();
		
	}
	public void run() {
		System.out.println("New Client Communication Thread");
        MessageObject initMessage = new MessageObject('A', client.getClientName(), "Added");
		
		try {

            objectOut.writeObject(initMessage);
            objectOut.flush();
			MessageObject inputObject;

			while((inputObject = (MessageObject) objectIn.readObject()) != null)
            {
                char messageType = inputObject.getType();
                String name;
                String message;
                switch(messageType) {
                    case 'A':
                        name = inputObject.getName();
                        Pair publickey = inputObject.getPublicKey();
                        client.chatHistory.insert("User added: " + name + "\n", 0);
                        //client.chatHistory.insert("Val 1: " + publickey.getVal1() + "Val 2: " + publickey.getVal2(), 0);
//                        for(ClientInfo c: inputObject.getExistingClients()) {
//                        		client.names.add(0, c.getUsername()); 
//                        }
                        break;

                    case 'D':
                        name = inputObject.getName();
                        client.chatHistory.insert("User Deleted: " + name + "\n", 0);
                        break;


                    case 'M':
                        name = inputObject.getName();
                        message = inputObject.getMessage();
                        client.chatHistory.insert("Message from user " + name + ": " + message + "\n", 0);
                        break;

                }
			}
			in.close();
		}
		catch(IOException e){
			System.err.println("Problem with Client Read");
		}
		catch(ClassNotFoundException eC)
        {
            System.err.println("Class does not exist");
        }
	}
	
}


