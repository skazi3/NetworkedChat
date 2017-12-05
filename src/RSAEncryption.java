/*
 * reference for is coPrime:
 * https://stackoverflow.com/questions/28575416/finding-out-if-two-numbers-are-relatively-prime
 * */

public class RSAEncryption {
	private int p;
	private int q;
	private int n;
	
	private int phi;
	private int e;
	private int d;
	
	private Pair publicKey;
	private Pair privateKey;
	private static int blockSize = 4;
	
	
	public RSAEncryption(int pVal, int qVal, int nVal) {
		p = pVal;
		q = qVal;
		n = nVal;
		phi = createPhi();
		e = createE();
		d = createD();
		publicKey = new Pair(e, n);
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
	private int createD() {
		int k = 0;
		while(true) {
			int val = (1 + k * phi) / e;
			if(val == 0) {
				return val;
			}
			else {
				System.out.println("Could not generate 'd' ");
				k++;
			}
		}
	}
	private int createE() {
		int i = 0;
		while(i < n) {
			if(isCoPrime(i, phi)) {
				return i;
			}
			else {
				System.out.println("Couldn't generate 'e' ");
				i++;
			}
		}
		return -1;
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
