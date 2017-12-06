/*
 * reference for is coPrime:
 * https://stackoverflow.com/questions/28575416/finding-out-if-two-numbers-are-relatively-prime
 * */
import java.math.*;
import java.util.Random;
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
	
	private static int blockSize = 2;
	
	private String message;
	private char[] charMessage;
	
	private Vector<BigInteger> blockValues;
	private Vector<BigInteger> encryptValues;
	private Vector<BigInteger> decryptValues;
	
	public RSAEncryption(int pVal, int qVal, int nVal) {
		
		blockValues = new Vector<BigInteger>();
		encryptValues = new Vector<BigInteger>();
		decryptValues = new Vector<BigInteger>();
		
		p   = pVal;
		q   = qVal;
		n   = nVal;
		phi = createPhi();
		
		e   = createE();
		System.out.println("E is: " + e);
		d   = createD();
		System.out.println("D is: " + d);
		

		publicKey  = new Pair(e, n);
		privateKey = new Pair(d, n);
	}
/*============================GET PUBLIC AND PRIVATE KEY========================================*/
	public Pair getPublicKey() {
		return new Pair(e, n);
	}
	public Pair getPrivateKey() {
		return new Pair(d, n);
	}
	
	/*CREATE PHI VAL*/ 
	private int createPhi() {
		 return (p-1) * (q-1);
	}
/*===============================CREATE D AND E================================================*/
	private long createE() {
		long i = 2;
		long count = 0;
		Random rand = new Random();
		int x = rand.nextInt(100) + 100;
		while (i < n){
	        if (gcd(i, phi)==1) {
	            count++;
	            if(count == x) {
	            		break;
	            }
	            else
	            		i++;
	        }
	        else
	           i++;
		 }
		 return i;
	}
	private long createD() {
		long k = 2;
		long dVal;
		
		while((1 + (k*phi)) % e != 0) {
			k++;
		}
		dVal = (1 + (k*phi)) / e;
		System.out.println("K is: " + k + ", phi is: " + phi + "\n");
		//System.out.println("D is : " + dVal);
		return dVal;
		

	}
/*============================GRAB MESSAGE AND ENCRYPT IT===================================*/	
	public void setMessage(String m) {
		message = m;
		charMessage = explodeMessage();
		blockMessage();
		setDecryptValues();
	}
	private char[] explodeMessage() {
		int addAmount = message.length() % blockSize;
		if(addAmount != 0) {
			for(int i = 0; i < blockSize - addAmount; i++) {
				message = message + 0;
			}
		}
		return message.toCharArray();
	}
	private void blockMessage() {
		BigInteger val = new BigInteger("0");
		char[] block = new char[blockSize];
		for(int i = 0; i < charMessage.length; i++) {
			if((i % blockSize == 0) && i != 0) {
				//System.out.println("Val is: " + val);
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
		System.out.println(block);
		System.out.println("Val is: ");
		System.out.println(val);
		BigInteger C = new BigInteger("0");
		
		C = val.pow((int) e);		
		C = C.mod(new BigInteger(Integer.toString(n)));

		encryptValues.addElement(C);
	
	}
	public Vector<BigInteger> getEncryptedMessage(){
		return encryptValues;
	}
/*==================================DECRYPT MESSAGE======================================*/		
	private void setDecryptValues() {
		System.out.println("In decrypt()");
		BigInteger M = new BigInteger("0");
		for(BigInteger C: encryptValues) {
			System.out.println("In encryptValues");
			M = C.pow((int)d).mod(new BigInteger(Integer.toString(n)));
			decryptValues.addElement(M);
		}
		printDecryptValues();
		
	}
	private void printDecryptValues() {
		for(BigInteger M: decryptValues) {
			System.out.println(M);
		}
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

	
	
}
