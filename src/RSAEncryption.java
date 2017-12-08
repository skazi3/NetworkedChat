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
	
	private static int blockSize = 4;
	private BigInteger num = new BigInteger("128");
	
	private String message;
	private char[] charMessage;
	
	private Vector<BigInteger> blockValues;
	private Vector<BigInteger> encryptValues;
	private Vector<BigInteger> decryptValues;
	
	public RSAEncryption(int pVal, int qVal) {
		
		blockValues = new Vector<BigInteger>();
		encryptValues = new Vector<BigInteger>();
		decryptValues = new Vector<BigInteger>();
		
		p   = pVal;
		q   = qVal;
		n   = p * q;
		phi = createPhi();
		
		e   = createE();
		d   = createD();

		

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
		return dVal;
		

	}
/*============================GRAB MESSAGE AND ENCRYPT IT===================================*/	
	public void setMessage(String m) {
		message = m;
		charMessage = explodeMessage();
	}
	public Vector<BigInteger> getEncryptValues(long e, long n) {
		blockMessage(e, n);
		return encryptValues;
		
		
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
	private void blockMessage(long e, long n) {

		char[] block = new char[blockSize];
		for(int i = 0; i < charMessage.length; i+=blockSize) {
			BigInteger val = new BigInteger("0");
			for(int j = i; j < i + blockSize; j++) {
				
				long powVal = charMessage[j] *(int)Math.pow(128, (j % blockSize));
				val = val.add(new BigInteger(Long.toString(powVal)));
				
			} 
			encryptMessage(val, e, n);
		}
	

	}
	private void encryptMessage(BigInteger val, long e, long n) {
		BigInteger C = new BigInteger("0");
		BigInteger exp = new BigInteger(Long.toString(e));
		BigInteger mod = new BigInteger(Long.toString(n));
		C = val.modPow(exp, mod);
		//System.out.println(C);
		encryptValues.addElement(C);
	}

	private Vector<BigInteger> getEncryptedMessage(){
		return encryptValues;
	}
/*==================================DECRYPT MESSAGE======================================*/		
	private void setDecryptValues() {
		BigInteger M = new BigInteger("0");
		
		for(BigInteger C: encryptValues) {
			M = C.modPow(new BigInteger(Long.toString(d)), new BigInteger(Integer.toString(n)));
			decryptValues.addElement(M);
		}
		decryptMessage();
		//printDecryptValues();
		
	}
	private String decryptMessage() {
		String msg = new String();

		for(BigInteger M: decryptValues) {
			while(true) {
				if(M.intValue() == 0) 
				{
					break;
				}
				int val = M.mod(num).intValue();
				
				char letter = (char)val;
				M = M.shiftRight(7);	
				if(letter != '0') 		
					msg += letter;
				
			}
		}
		return msg;
		
	}
	
	private void printDecryptValues() {
		System.out.println("in decrypt");
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
