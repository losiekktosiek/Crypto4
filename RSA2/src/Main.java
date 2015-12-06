import java.math.BigInteger;


public class Main {
	public static void main(String[] args) {
		
		String message = "Tekst do zaszyfrowania!!!";
		BigInteger toEncrypt, toDecrypt;
		String encrypted, decrypted;
		/*
		 * wersja standardowa
		 */
		System.out.println("---- Wersja standardowa ");

		RSA rsa = new RSA(8, 128);

		toEncrypt = new BigInteger(message.getBytes());	
		
		encrypted = rsa.encrypt(toEncrypt);
		
		toDecrypt = new BigInteger(encrypted);	
		decrypted = rsa.decrypt(toDecrypt);
		
		System.out.println("Zdeszyfrowano: "+decrypted);
		/*
		 * wersja CTR 
		 */
		
		System.out.println("--- Wersja CTR: ");

		RSA_CRT rsa_ctr = new RSA_CRT();
		rsa_ctr.init(8, 32);
		
		toEncrypt = new BigInteger(message.getBytes());	
		
		encrypted = rsa_ctr.encrypt(toEncrypt);
		
		toDecrypt = new BigInteger(encrypted);	
		decrypted = rsa_ctr.decryptCRT(toDecrypt);
		
		System.out.println("Zdeszyfrowano: "+decrypted);
		
	}
}
