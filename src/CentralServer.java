import java.net.*;
import java.util.Vector;
import java.io.*; 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class CentralServer extends JFrame implements ActionListener{
	JButton serverButton;
	JLabel machineInfo;
	JLabel portInfo;
	JTextArea history;
	private boolean running;
	
	Vector<ObjectOutputStream> outStreamList;
	Vector<ClientInfo> clientInfo;
	

	
	boolean serverContinue;
	ServerSocket serverSocket;
	
	public CentralServer() {
		super("Central Server");
		
		running = false;
		
		Container container = getContentPane();
		container.setLayout(new FlowLayout());
		
		serverButton = new JButton("Start Listening");
		serverButton.addActionListener(this);
		outStreamList = new Vector<>();
		clientInfo = new Vector<ClientInfo>();
		
		container.add(serverButton);
		String machineAddress = null;
		try {
			InetAddress addr = InetAddress.getLocalHost();
			machineAddress = addr.getHostAddress();
		}
		catch(UnknownHostException e) {
			machineAddress = "127.0.0.1";
		}
		machineInfo = new JLabel(machineAddress);
		container.add(machineInfo);
		portInfo = new JLabel("Not Listening");
		container.add(portInfo);
		
		history = new JTextArea(10, 40);
		history.setEditable(false);
		container.add(new JScrollPane(history));
		
		setSize(500, 250);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(running == false) {
			new ConnectionThread(this);
		}
		else {
			serverContinue = false;
			serverButton.setText("Start Listening");
			portInfo.setText("Not Listening");
		}
	}

}

class ConnectionThread extends Thread
{
  CentralServer centralServer;
  
  public ConnectionThread (CentralServer cs)
  {
	centralServer = cs;
    start();
  }
  
  public void run()
  {
	  centralServer.serverContinue = true;
    
    try 
    { 
    	  centralServer.serverSocket = new ServerSocket(0); 
    	  centralServer.portInfo.setText("Listening on Port: " + centralServer.serverSocket.getLocalPort());
      System.out.println ("Connection Socket Created");
      try { 
        while (centralServer.serverContinue)
        {
          System.out.println ("Waiting for Connection");
          centralServer.serverButton.setText("Stop Listening");
          new CommunicationThread (centralServer.serverSocket.accept(), centralServer, centralServer.outStreamList, centralServer.clientInfo); 
        }
      } 
      catch (IOException e) 
      { 
        System.err.println("Accept failed."); 
        System.exit(1); 
      } 
    } 
    catch (IOException e) 
    { 
      System.err.println("Could not listen on port: 10008."); 
      System.exit(1); 
    } 
    finally
    {
      try {
    	  	centralServer.serverSocket.close(); 
      }
      catch (IOException e)
      { 
        System.err.println("Could not close port: 10008."); 
        System.exit(1); 
      } 
    }
  }
}


class CommunicationThread extends Thread
{ 

    private Socket clientSocket;
    private CentralServer centralServer;
    private Vector<ObjectOutputStream> outStreamList;
    private Vector<ClientInfo> clientInfo;



    public CommunicationThread (Socket clientSoc, CentralServer cs, Vector<ObjectOutputStream> osl, Vector<ClientInfo> info)
    {
        clientSocket = clientSoc;
        centralServer = cs;
        centralServer.history.insert("Communicating with Port " + clientSocket.getLocalPort() + "\n", 0);
        outStreamList = osl;
        clientInfo = info;
        start();
    }

    public void run()
    {
       System.out.println ("New Communication Thread Started");

       try
       {
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            outStreamList.add(out);

            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            MessageObject inputObject;

            while ((inputObject = (MessageObject) in.readObject()) != null)
            {
                 char messageType = inputObject.getType();
                 String message;
                 String name;
                 switch(messageType)
                 {
                 case 'A':
                     name = inputObject.getName();
                     Pair publicKey = inputObject.getPublicKey();
                     clientInfo.addElement(new ClientInfo(publicKey, name, out));
                     inputObject.setExistingClients(clientInfo);
                     centralServer.history.insert("User added: " + name + "\n", 0);
                     //centralServer.history.insert("Val 1: " + publickey.getVal1() + "Val 2: " + publickey.getVal2(), 0);
                     for (ClientInfo c: clientInfo)
                     {
                    	 	if(c.getUsername() != name) {
	                         System.out.println ("Sending Message"); 
	                         c.getOut().writeObject (inputObject);
	                         c.getOut().flush();
                    	 	}
                     }

                         break;


                     case 'D':
                         for (ObjectOutputStream out1: outStreamList)
                         {
                             System.out.println ("Sending Message");
                             out1.writeObject (inputObject);
                             out1.flush();
                         }

                         break;


                     case 'M':
                         System.out.println("Received message!");
                         message = inputObject.getMessage();
                         name = inputObject.getName();

                         for (ObjectOutputStream out1: outStreamList)
                         {
                             System.out.println ("Sending Message");
                             centralServer.history.insert("Message from user " + name + ": "  + message + "\n", 0);
                             out1.writeObject (inputObject);
                             out1.flush();
                         }

                         break;

                 }
            }

            outStreamList.remove(out);
            out.close();
            in.close();
            clientSocket.close();
       }
       catch (IOException e)
       {
            System.err.println("Problem with Communication Server");
            //System.exit(1);
       }
       catch(ClassNotFoundException cE)
       {
           System.err.println("Object class not found!");
       }
    }
} 







