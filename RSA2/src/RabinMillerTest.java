import java.lang.reflect.GenericArrayType;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RabinMillerTest {
	
	private static final BigInteger ZERO = BigInteger.ZERO;
	private static final BigInteger ONE = BigInteger.ONE;
	private static final BigInteger TWO = new BigInteger("2");
	private static final BigInteger THREE = new BigInteger("3");
	
	//static int id = 0;
	int num;

	SecureRandom generator;
	List<BigInteger> primes = new ArrayList<>();
	
	public RabinMillerTest(int num, int bitLength) {
		this.num = num;
		generator = new SecureRandom();

		List<GeneratorThread> threadList = new ArrayList<>();

		for (int i = 0; i < num; i++) {
			threadList.add(new GeneratorThread(generator, bitLength));
		}

		for (int i = 0; i < num; i++) {
			threadList.get(i).start();
		}
	}

	List<BigInteger> getPrimes() {
		while (primes.size() < num) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return primes;
	}

	/*
	 * watki dodaja znalezione liczby prawd. pierwsze
	 */
	synchronized void addPrime(BigInteger number) {
		primes.add(number);
	}
	
	private class GeneratorThread extends Thread {
		Random random;
		int bitLength;

		public GeneratorThread(Random random, int bitLength) {
			this.random = random;
			this.bitLength = bitLength;
		}

		public void run() {
			BigInteger number = BigInteger.probablePrime(bitLength, random);
			while (probablyPrime(number, 40)==false) {
				number = BigInteger.probablePrime(bitLength, random);
			}
			primes.add(number);
		}
	}


	/* 
	 * precision - ile razy sprawdzic czy liczba jest pierwsza 
	 */
	public static boolean probablyPrime(BigInteger n, int precision) {
		
		/*
		 * jesli 1,2 lub 3
		 */
		if (n.compareTo(THREE) < 0)
			return true;
		
		int s = 0;
		BigInteger d = n.subtract(ONE);
		
		while (d.mod(TWO).equals(ZERO)) {
			s++;
			d = d.divide(TWO);
		}
		
		for (int i = 0; i < precision; i++) {
			BigInteger a = witness(TWO, n.subtract(ONE));
			BigInteger x = a.modPow(d, n);
			if (x.equals(ONE) || x.equals(n.subtract(ONE)))
				continue;
			int r = 1;
			for (; r < s; r++) {
				x = x.modPow(TWO, n);
				if (x.equals(ONE))
					return false;
				if (x.equals(n.subtract(ONE)))
					break;
			}
			if (r == s) 
				return false;
		}
		return true;
	}

	/*
	 * losuj a - swiadka testu
	 */
	private static BigInteger witness(BigInteger bottom, BigInteger top) {
		Random rnd = new Random();
		BigInteger res;
		do {
			res = new BigInteger(top.bitLength(), rnd);
		} while (res.compareTo(bottom) < 0 || res.compareTo(top) > 0);
		return res;
	}

}