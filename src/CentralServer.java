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
	Vector<PrintWriter> outStreamList;


	
	boolean serverContinue;
	ServerSocket serverSocket;
	
	public CentralServer() {
		super("Central Server");
		
		running = false;
		
		Container container = getContentPane();
		container.setLayout(new FlowLayout());
		
		serverButton = new JButton("Start Listening");
		serverButton.addActionListener(this);
		outStreamList = new Vector<PrintWriter>();
		
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
          new CommunicationThread (centralServer.serverSocket.accept(), centralServer, centralServer.outStreamList); 
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
private Vector<PrintWriter> outStreamList;



public CommunicationThread (Socket clientSoc, CentralServer cs, Vector<PrintWriter> osl)
  {
   clientSocket = clientSoc;
   centralServer = cs;
   centralServer.history.insert ("Communicating with Port " + clientSocket.getLocalPort()+"\n", 0);
   outStreamList = osl;
   start();
  }

public void run()
  {
   System.out.println ("New Communication Thread Started");

   try { 
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), 
                                     true); 
        outStreamList.add(out);
        
        BufferedReader in = new BufferedReader( 
                new InputStreamReader( clientSocket.getInputStream())); 

        String inputLine; 

        while ((inputLine = in.readLine()) != null) 
            { 
             System.out.println ("Server: " + inputLine); 
             centralServer.history.insert (inputLine+"\n", 0);
             
             for (PrintWriter out1: outStreamList)
             {
               System.out.println ("Sending Message");
               out1.println (inputLine);
             }
             
             if (inputLine.equals("Bye.")) 
                 break; 

             if (inputLine.equals("End Server.")) 
            	 	centralServer.serverContinue = false; 
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
   }
} 







