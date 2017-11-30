import javax.swing.JFrame;

public class Chat {

	public static void main(String[] args) {
		CentralServer server  = new CentralServer();
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Client client = new Client();
		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
