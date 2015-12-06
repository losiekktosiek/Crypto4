import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/*
 * Wersja druga zadania - CTR za pomoca watkow
 * @author: AG
 */
public class RSA_CRT {
	
	List<BigInteger> primes, modifyPrimes, threadsResults;
	private BigInteger n, e, d;
	int primerNumber;

	public void init(int primerNumber, int bitlen) {
		n = BigInteger.ONE;

		this.primerNumber = primerNumber;
		this.primes = primes;
		threadsResults = new ArrayList<>();
		generate(primerNumber, bitlen);

	}

	/*
	 * metoda do watku, umieszcza wyniki w liscie
	 */
	synchronized void add(BigInteger bi) {
		threadsResults.add(bi);
	}

	/*
	 * generuje liczby potrzebne do zaszyfrowania/odszyfrowania
	 * m.in. klucz publiczny i prywatny
	 */
	public void generate(int primerNumber, int bitlen) {

		/*
		 * generuje liczby pierwsze 
		 */
		RabinMillerTest test = new RabinMillerTest(primerNumber, bitlen);
		primes = test.getPrimes();
		
		/*
		 * wyswietl liczby pierwsze p1, p2, ... pn
		 */
		for(int i = 0; i < primerNumber; i++)
			System.out.println("Liczba pierwsza ("+ i +") = "+primes.get(i));
		
		/* 
		 * zawiera reszty z dzielenia d przez (p-1) dla kazdego p
		 */
		modifyPrimes = new ArrayList<>();

		/*
		 * n = p1 * p2 * ... * pn
		 */
		for (BigInteger number : primes) {
			n = n.multiply(number);
		}

		BigInteger m = BigInteger.ONE;
		for (BigInteger number : primes) {
			BigInteger primeSubOne = number.subtract(BigInteger.ONE);  // p - 1
			m = m.multiply(primeSubOne);
			modifyPrimes.add(primeSubOne);
		}

		/*
		 * wyznaczamy e, wzglednie pierwsze z m
		 */
		e = new BigInteger("3");
		while (m.gcd(e).intValue() > 1) {
			e = e.add(new BigInteger("2"));
		}
		
		// d - klucz prywatny
		d = e.modInverse(m);

		/*
		 * na koniec liczymy d%(p-1) dla kazdego p
		 */
		for (int i = 0; i < primerNumber; i++) {
			modifyPrimes.set(i, d.remainder(modifyPrimes.get(i)));
		}
		
		System.out.println("n = "+n + "\ne= "+e + "\nd="+d);
	}

	public synchronized String encrypt(BigInteger message) {
		BigInteger encrypted= message.modPow(e,n);
		return encrypted.toString();
	//	return (new BigInteger(message.getBytes())).modPow(e, n).toString();
	}

	String decryptCRT(BigInteger message) {
		
		//BigInteger message = new BigInteger(message2);
		
		//System.out.println(message);
		//System.out.println(n);
		for (int i = 0; i < primerNumber; i++) {
			new CRT_Thread(message.modPow(modifyPrimes.get(i), primes.get(i)), n, primes.get(i)).start();
		}

		BigInteger sum = BigInteger.ZERO;
		/*
		 * kazdy watek deszyfruje osobna wiadomosc z uzyciem pi
		 */
		while (threadsResults.size() < primerNumber) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		for (BigInteger b : threadsResults) {
			sum = sum.add(b);
		}

		threadsResults.clear();

		return new String(sum.mod(n).toByteArray());
	}

	public synchronized String decrypt(String message) {
		return new String((new BigInteger(message)).modPow(d, n).toByteArray());
	}


	private class CRT_Thread extends Thread {
		BigInteger ai, n, ni;

		public CRT_Thread(BigInteger ai, BigInteger n, BigInteger ni) {
			this.ai = ai;
			this.n = n;
			this.ni = ni;
		}

		public void run() {
			BigInteger t = n.divide(ni);
			add(ai.multiply(t).multiply(t.modInverse(ni)));
		}
	}

	
}