import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Ciphererstellen {

	private Cipher privkey;
	private Key pubkey;

	public String toKey() throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(512);
		pubkey = keyGen.genKeyPair().getPublic();
		byte[] publicKey = keyGen.genKeyPair().getPublic().getEncoded();
		StringBuffer retString = new StringBuffer();
		for (int i = 0; i < publicKey.length; ++i) {
			retString.append(Integer.toHexString(
					0x0100 + (publicKey[i] & 0x00FF)).substring(1));
		}
		privkey.init(Cipher.ENCRYPT_MODE,pubkey);
		return retString.toString();
	}
	public String keyData(String data) throws IllegalBlockSizeException, BadPaddingException{
		char[] a = data.toCharArray();
		byte[] b = new byte[a.length];
		for(int i = 0;i<a.length;i++){
			b[i]=(byte) a[i];
		}
	 System.out.println(Arrays.toString(privkey.doFinal(b)));
	 return Arrays.toString(privkey.doFinal(b));
	}

}
