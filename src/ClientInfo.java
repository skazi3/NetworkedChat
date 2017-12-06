
public class ClientInfo {
	private Pair publicKey;
	private String username;
	
	public ClientInfo(Pair pk, String name) {
		publicKey = pk;
		username = name;
	}

	public Pair getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(Pair publicKey) {
		this.publicKey = publicKey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
