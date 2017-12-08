import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.HashMap;

import javax.swing.*;
/*
 * prime number function reference:
 * https://stackoverflow.com/questions/1538644/c-determine-if-a-number-is-prime
 * */


public class Client extends JFrame implements ActionListener{
	
/*======================PRIVATE VARIABLES AND SOCKET VARIABLES====================*/	
	private JPanel    clientsInfo;
	public Pair getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(Pair publicKey) {
		this.publicKey = publicKey;
	}

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
	
	ObjectOutputStream objectOut = null;
    ObjectInputStream objectIn = null;
    RSAEncryption encryptionVal;
	
	  
	JTextArea  chatHistory;
	private JTextField message;
	private JList<String> clientList;
	HashMap<String, Pair> nameAndKey;
	
	DefaultListModel<String> names;
/*====================================CONSTRUCTOR====================================*/		
	public Client() {
		setLayout(new BorderLayout());
		
		connected = false;

		/*GET USER INFO*/
		name        = new JTextField("sarah");
		pField      = new JTextField("16189");
		qField      = new JTextField("16381");
		portInfo    = new JTextField("62197");
		machineInfo = new JTextField("10.5.219.126");
 
		
		/*ADD THE CLIENT LIST ON THE SIDE*/
		add(new JScrollPane(clientList()), BorderLayout.EAST);
		
		chatHistory = new JTextArea(10, 40);
		chatHistory.setEditable(false);	
		
		message = new JTextField("");
		message.addActionListener(this);
		
		sendButton = new JButton("Send Message");
		sendButton.setEnabled(false);
		sendButton.addActionListener(this);
		
		nameAndKey = new HashMap<String, Pair>();

		
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
				
				encryptionVal = new RSAEncryption(p, q);
				encryptionVal.setMessage("aaaa aaa aaa");
				
				publicKey = encryptionVal.getPublicKey();
				privateKey = encryptionVal.getPrivateKey();

			}
			else {
				result = JOptionPane.showConfirmDialog(null, message, "Enter Server info", JOptionPane.OK_CANCEL_OPTION);
			}
			/*ESTABLISH CONNECTION WITH SERVER*/
			try {
				clientSocket = new Socket(machineName, portNum);

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
	          objectOut.close();
	          objectIn.close();
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
            MessageObject msg = new MessageObject('M', clientName, message.getText());
            chatHistory.insert("Me: "+ message.getText() + "\n", 0);
            message.setText("");
            objectOut.writeObject(msg);
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
		quitChat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(connected) {
		            try {
		            		MessageObject delete = new MessageObject('D', clientName);
		            		objectOut.writeObject(delete);
						objectOut.flush();
						
						objectOut.close();
						objectIn.close();
						clientSocket.close();
						
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				dispose();
				
			}
		});
	
		
		connect.add(startConnection);
		quit.add(quitChat);
		
		
		menuBar.add(about);
		menuBar.add(help);
		menuBar.add(connect);
		menuBar.add(quit);
		
		
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
                        client.nameAndKey.put(name, inputObject.getPublicKey());
                        
                        client.chatHistory.insert("User added: " + name + "\n", 0);
                        client.names.addElement(name);

                        break;

                    case 'D':
                        name = inputObject.getName();
                        client.chatHistory.insert("User Deleted: " + name + "\n", 0);
                        client.names.removeElement(name);
                        break;


                    case 'M':
                        name = inputObject.getName();
                        message = inputObject.getMessage();
                        client.chatHistory.insert("Message from user " + name + ": " + message + "\n", 0);
                        break;
                        
                    case 'U':
                    		name = inputObject.getName();
                    		client.nameAndKey.put(name, inputObject.getPublicKey());
                    		client.names.addElement(name);
                    		break;
                    		

                }
			}
			objectIn.close();
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


