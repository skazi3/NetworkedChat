import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultListModel;

public class MessageObject implements Serializable
{
	private char type;
	private Pair publicKey;
	private String message;
	private String name;
	private ArrayList<Pair> keys;
	
	
	/*to add a new user*/
	public MessageObject(char t, Pair pk, String n) {
		type = t;
		publicKey = pk;
		this.name = n;
	}
	//remove 
	public MessageObject(char t, Pair pk) {
		type = t;
		publicKey = pk;
	}
	//send a message
	public MessageObject(char t, Pair pk, String n, ArrayList<Pair> k, String m)
	{
		type = t;
		publicKey = pk;
		name = n;
		keys = k;
		message = m;
	}
	public MessageObject(char t, String n , String m)
	{
		type = t;
		name = n;
		message = m;
	}
	public MessageObject(char t, String n) {
		type = t;
		name = n;
	}

	public char getType()
	{
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public Pair getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(Pair publicKey) {
		this.publicKey = publicKey;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message; 
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Pair> getKeys() {
		return keys;
	}

	public void setKeys(ArrayList<Pair> keys) {
		this.keys = keys;
	}
}
