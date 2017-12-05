import java.util.ArrayList;

public class MessageObject {
	private char type;
	private Pair publicKey;
	private String message;
	private String name;
	private ArrayList<Integer> keys;
	
	/*to add a new user*/
	public MessageObject(char t, Pair pk, String name) {
		type = t;
		publicKey = pk;
	}
	//remove
	public MessageObject(char t, Pair pk) {
		type = t;
		publicKey = pk;
	}
	//send a message
	public MessageObject(char t, Pair pk, String n, ArrayList<Integer>k, String m) {
		
	}
}
