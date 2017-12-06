/*
 * reference for is coPrime:
 * https://stackoverflow.com/questions/28575416/finding-out-if-two-numbers-are-relatively-prime
 * */
import java.math.*;
import java.util.Vector;

public class RSAEncryption {
	private int p;
	private int q;
	private int n;
	
	private long phi;
	private long e;
	private long d;
	
	private Pair publicKey;
	private Pair privateKey;
	
	private static int blockSize = 4;
	
	private String message;
	private char[] charMessage;
	
	private Vector<BigInteger> blockValues;
	private Vector<BigInteger> encryptValues;
	
	public RSAEncryption(int pVal, int qVal, int nVal) {
		
		blockValues = new Vector<BigInteger>();
		encryptValues = new Vector<BigInteger>();
		
		p   = pVal;
		q   = qVal;
		n   = nVal;
		phi = createPhi();
		
		e   = createE();
		d   = createD();
		
		
		//publicKey  = new Pair(e, n);
		//privateKey = new Pair(d, n);
	}
/*============================GET PUBLIC AND PRIVATE KEY========================================*/
//	public Pair getPublicKey() {
//		return new Pair(e, n);
//	}
//	public Pair getPrivateKey() {
//		return new Pair(d, n);
//	}
	/*CREATE PHI VAL*/ 
	private int createPhi() {
		 return (p-1) * (q-1);
	}
/*===============================CREATE D AND E================================================*/
	private long createE() {

		for(int i = 2; i < n; i++) {
			if(isCoPrime(i, phi)) {
				
				System.out.println("e is:  " + i);
				return i;
			}
		}
		System.out.println("Didn't generate 'e' ");
		return -1;
	}
	
	private long createD() {
		long k = 2;
		long dVal;
		while((1 + (k*phi)) % e != 0) {
			System.out.println("K is: " + k);
			k++;
		}
		dVal = (1 + (k*phi)) / e;
		System.out.println("D is : " + dVal);
		return dVal;
		

	}
/*============================GRAB MESSAGE AND ENCRYPT IT===================================*/	
	public void setMessage(String m) {
		message = m;
		charMessage = explodeMessage();
		blockMessage();
	}
	private char[] explodeMessage() {
		int addAmount = message.length() % blockSize;
		if(addAmount != 0) {
			for(int i = 0; i < blockSize - addAmount; i++) {
				message = message + "/0";
			}
		}
		return message.toCharArray();
	}
	private void blockMessage() {
		BigInteger val = new BigInteger("0");
		char[] block = new char[blockSize];
		if(charMessage.length % blockSize != 0) {
			
		}
		for(int i = 0; i < charMessage.length; i++) {
			if((i % blockSize == 0) && i != 0) {
				System.out.println("Val is: " + val);
				encryptMessage(block, val);
				blockValues.addElement(val);
				val = new BigInteger("0");
				block[i % blockSize] = charMessage[i];
			}
			else {
				block[i % blockSize] = charMessage[i];
				int powVal = charMessage[i] *(int)Math.pow(128, (i % blockSize));
				BigInteger charVal = new BigInteger(Integer.toString(powVal));
				val = val.add(charVal);
				
			}
		}
		encryptMessage(block, val);

	}
	private void encryptMessage(char[] block, BigInteger val) {
		System.out.println("Block is: ");
		System.out.println(block);
		BigInteger C = new BigInteger("0");
		C = val.pow((int) e);
		
		C = C.mod(new BigInteger(Integer.toString(n)));
		System.out.println("C is ");
		System.out.println(C);
		encryptValues.addElement(C);
	
	}
	private Vector<BigInteger> getEncryptedMessage(){
		return encryptValues;
	}
	private decryptMessage() {
		
	}
	
/*============================CHECK IF VAL IS COPRIME======================================*/
	
	private static long gcd(long val1, long val2) {
	    long t;
	    while(val2 != 0){
	        t = val1;
	        val1 = val2;
	        val2 = t % val2;
	    }
	    return val1;
	}
	private static boolean isCoPrime(int a, long b) {
		return (gcd(a, b) == 1);
	}
	
	
}
