import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;
import java.util.Scanner;
/*
 * Wersja pierwsza zadania - standardowa
 * @author: AG
 * 
 */
public class RSA {
	/*
	 * n = p1 * ... * pn
	 * d - klucz prywatny
	 * e - klucz publiczny
	 */
	private BigInteger n = BigInteger.ONE, e, d;

	public RSA(int numPrimes, int bitlen) {
		generateData(numPrimes, bitlen);
	}

	public RSA(BigInteger n, BigInteger e, BigInteger d) {
		this.n = n;
		this.e = e;
		this.d = d;
	}

	public void generateData(int numPrimes, int bitlen) {
		
		RabinMillerTest test = new RabinMillerTest(numPrimes, bitlen);
		List<BigInteger> primes = test.getPrimes();

		/*
		 * wykonujemy n = p1* p2 * ... * pn
		 */
		for (BigInteger number : primes) {
			n = n.multiply(number);
		}

		/*
		 * wyliczamy pomocnicze m
		 */
		BigInteger m = BigInteger.ONE;
		for (BigInteger number : primes) {
			m = m.multiply(number.subtract(BigInteger.ONE));
		}
		/*
		 * znajdujemy e wzglednie pierwsze z m
		 */
		e = new BigInteger("3");
		while (m.gcd(e).intValue() > 1) {
			e = e.add(new BigInteger("2"));
		}
		
		// wyliczamy klucz prywatny
		d = e.modInverse(m);
	}

	public synchronized String encrypt(BigInteger message) {
		//BigInteger encrypted= message.modPow(e,n);
		//return encrypted.toString();
		return message.modPow(e, n).toString();
	}

	public synchronized String decrypt(BigInteger message) {
		//BigInteger decrypted= message.modPow(e,n);
		//return new String(decrypted.toByteArray());
		return new String(message.modPow(d, n).toByteArray());
	}


}