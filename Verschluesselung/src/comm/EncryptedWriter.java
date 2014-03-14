package comm;import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;


public class EncryptedWriter extends BufferedWriter {

	private byte[] key;
	public EncryptedWriter(Writer arg0, byte[]key) {
		super(arg0);
		this.key=key;//TODO Check key?
	}

	public EncryptedWriter(Writer arg0, int arg1, byte[] key) {
		super(arg0, arg1);
		this.key=key;
	}

	/**
	 * 
	 * writeLineEncrypted
	 * @param line Line to write, encrypted to be decrypted by an EncryptedReader
	 *
	 * @author Thomas Traxler
	 * @version 26.02.2014
	 * @throws IOException 
	 * @throws InvalidKeyException 
	 */
	public void writeLineEncrypted(String in) throws IOException, InvalidKeyException{
		boolean more = false;
		String line;
		if(in.length()>40){
			line=in.substring(0, 40);
			more=true;
		}else{
			line=in;
		}
		DESKeySpec desKeySpec = new DESKeySpec(key);
		SecretKeySpec keySpec = new SecretKeySpec (desKeySpec.getKey(),"DES");
		Cipher cipher;
		try {
//			cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			String write = new sun.misc.BASE64Encoder().encode((cipher.doFinal(line.getBytes())));
			System.out.println(write);
			super.write(write);
			super.newLine();
			super.flush();
			if(more){
				writeLineEncrypted(in.substring(41));
			}
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
		
	}
}
