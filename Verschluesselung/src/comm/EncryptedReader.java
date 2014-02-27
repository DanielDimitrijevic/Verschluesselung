package comm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * EnryptedReader, Decrypts Messaages encrypted by an EncryptedWriter, only works with readLineEcrypted() at the current state!!
 * 
 * @author Thomas Traxler
 * @version 26.02.2014
 */
public class EncryptedReader extends BufferedReader {
	
	private byte[] key;

	public EncryptedReader(Reader arg0, byte[] key) {
		super(arg0);
		this.key=key;//TODO Check key?
	}

	public EncryptedReader(Reader arg0, int arg1, byte []key) {
		super(arg0, arg1);
		this.key=key;
	}
	
	
	public String readLineEcrypted () throws IOException, InvalidKeyException{
		
		DESKeySpec desKeySpec = new DESKeySpec(key);
		SecretKeySpec keySpec = new SecretKeySpec (desKeySpec.getKey(),"DES");
		Cipher cipher;
		try {
//			cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			String out = super.readLine();
			return new String(cipher.doFinal(new sun.misc.BASE64Decoder().decodeBuffer(out)));
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// Shpuld not be possible to reach
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// Housten we have a problem
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;//Something went wrong :)
	}

}
