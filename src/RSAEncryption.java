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
	
	private int phi;
	private int e;
	private long d;
	
	private Pair publicKey;
	private Pair privateKey;
	
	private static int blockSize = 4;
	
	private String message;
	private char[] charMessage;
	
	private Vector<BigInteger> blockValues;
	
	public RSAEncryption(int pVal, int qVal, int nVal) {
		p   = pVal;
		q   = qVal;
		n   = nVal;
		phi = createPhi();
		
		e   = createE();
		d   = createD();
		
		
		publicKey  = new Pair(e, n);
		//privateKey = new Pair(d, n);
		
		blockValues = new Vector<BigInteger>();
	}
/*============================GET PUBLIC AND PRIVATE KEY========================================*/
	public Pair getPublicKey() {
		return new Pair(e, n);
	}
//	public Pair getPrivateKey() {
//		return new Pair(d, n);
//	}
	/*CREATE PHI VAL*/ 
	private int createPhi() {
		 return (p-1) * (q-1);
	}
/*===============================CREATE D AND E================================================*/
	private int createE() {

		for(int i = n-1; i > 0; i--) {
			if(isCoPrime(i, phi)) {
				
				System.out.println("e is:  " + i);
				return i;
			}
		}
		System.out.println("Didn't generate 'e' ");
		return -1;
	}
	
	private long createD() {
		long k = 0;
		while(true) {
			long val = (1 + k * phi) % e;
			if(val == 0) {
				System.out.println("d is: " + (1 + k * phi) / e);
				return (1 + k * phi) / e;
			}
			else {
				k++;
			}
		}
		

	}
/*============================GRAB MESSAGE AND ENCRYPT IT===================================*/	
	public void setMessage(String m) {
		message = m;
		charMessage = explodeMessage();
		blockMessage();
	}
	private char[] explodeMessage() {
		System.out.println(message);
		return message.toCharArray();
	}
	private void blockMessage() {
		BigInteger val = new BigInteger("0");
		
		for(int i = 0; i < charMessage.length; i++) {
			if(i % (blockSize-1) == 0) {
				blockValues.addElement(val);
				val = new BigInteger("0");
			}
			else {
				
				int powVal = charMessage[i] *(int)Math.pow(128, (i % blockSize));
				BigInteger charVal = new BigInteger(Integer.toString(powVal));
				val = val.add(charVal);
				System.out.println("Val: " + val);
				
			}
		}
		for(BigInteger b: blockValues) {
			System.out.println(b);
		}
	}
	
	
/*============================CHECK IF VAL IS COPRIME======================================*/
	
	private static int gcd(int val1, int val2) {
	    int t;
	    while(val2 != 0){
	        t = val1;
	        val1 = val2;
	        val2 = t % val2;
	    }
	    return val1;
	}
	private static boolean isCoPrime(int a, int b) {
		return (gcd(a, b) == 1);
	}
	
	
}
