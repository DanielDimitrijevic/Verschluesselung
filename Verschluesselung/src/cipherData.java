import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;


public class cipherData {
	
	public static void main(String[] args) throws IOException{
		System.out.println("Geben sie ein welche Text sie verschlüsseln wollen");
		BufferedReader a = new BufferedReader(new InputStreamReader(System.in));
		Ciphererstellen cip = new Ciphererstellen();
		String aus = null;
		aus = a.readLine();
		try {
			System.out.println(cip.keyData(aus));
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(aus);
		
	}

}
