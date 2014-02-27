package diffieHellman;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Properties;
/**
 * 
 * Class that generates a Key via DiffieHellman
 * Everything it needs is provided per an PropertiesObject
 * 
 * @author Thomas Traxler
 * @version 26.02.2014
 */
public class DiffieHellman {

	/**Key for the Properties of the used shared Prime*/
	private static final String prime ="dh.p";
	/**Key for the Properties for the corresponding Primitiveroot to the Prime*/
	private static final String root = "dh.g";
	/**Key for the Properties of the used minimum value*/
	private static final String min ="dh.min";
	
	/**
	 * 
	 * keyGenAlice
	 * generate a Key with Deffie Hellman if there's a keyGenBob on the other end.
	 * @param s  Socket tho the Other party of the KeyGen
	 * @param p Properties for the Encryption (should be  ident on both ends)
	 * @return genereated key
	 * @throws IOException
	 *
	 * @author Thomas Traxler
	 * @version 26.02.2014
	 */
	public static byte[] keyGenAlice (Socket s, Properties p) throws IOException, NumberFormatException{
		PrintWriter  pw = new PrintWriter(new BufferedOutputStream(s.getOutputStream()));
		BufferedReader br = new BufferedReader(new InputStreamReader (s.getInputStream()));
		pw.print("ready\n");
		while(!br.ready());
		while(!br.readLine().equals("ready")){
			//XXX Staff to sync, fix if it causes Problems!
		}
		//TODO Check Properties
		BigInteger prime = BigInteger.valueOf(Integer.parseInt(p.getProperty(DiffieHellman.prime)));
		BigInteger g = primitveRoot(p);
		BigInteger a = BigInteger.valueOf((int) (Math.random()*Math.pow(10, 10)+Integer.parseInt(p.getProperty(DiffieHellman.min))));
		BigInteger A = calcA(g, a, prime);
		pw.println(prime+" "+g+" "+A);
		while(!br.ready());
		String one = br.readLine();
		BigInteger B =  BigInteger.valueOf(Integer.parseInt(one));
		
		return calcK(B, a, prime);
	}
	
	/**
	 * 
	 * keyGenBob
	 * generate a Key with Deffie Hellman if there's a keyGenAlice on the other end.
	 * @param s  Socket tho the Other party of the KeyGen
	 * @param p Properties for the Encryption (should be  ident on both ends)
	 * @return genereated key
	 * @throws IOException
	 * @throws NumberFormatException
	 *
	 * @author Thomas Traxler
	 * @version 26.02.2014
	 */
	public static byte[] keyGenBob (Socket s, Properties p) throws IOException, NumberFormatException{
		PrintWriter  pw = new PrintWriter(new BufferedOutputStream(s.getOutputStream()));
		BufferedReader br = new BufferedReader(new InputStreamReader (s.getInputStream()));
		while(!br.ready());
		while(!br.readLine().equals("ready")){
			//XXX Staff to sync, fix if it causes Problems!
		}
		pw.print("ready\n");
		while(!br.ready());
		String[] one = br.readLine().split("\\s++");
		BigInteger prime = BigInteger.valueOf(Integer.parseInt(one[0]));
		BigInteger g = BigInteger.valueOf(Integer.parseInt(one[1]));
		BigInteger A = BigInteger.valueOf(Integer.parseInt(one[2]));BigInteger a = BigInteger.valueOf((int) (Math.random()*Math.pow(10, 10)+Integer.parseInt(p.getProperty(DiffieHellman.min))));
		BigInteger b = BigInteger.valueOf((int) (Math.random()*Math.pow(10, 10)+Integer.parseInt(p.getProperty(DiffieHellman.min))));
		BigInteger B = calcA(g, b, prime);
		pw.println(B+"");
		
		return calcK(a, B, prime);
	}
	/**
	 * 
	 * Calculates the primitveRoot of a given Prime
	 * @param prime
	 * @return primitveRoot
	 *
	 * @author Thomas Traxler
	 * @version 26.02.2014
	 * @throws NumberFormatException
	 */
	private static BigInteger primitveRoot (Properties p) throws NumberFormatException{
		return BigInteger.valueOf(Long.parseLong(p.getProperty(DiffieHellman.root))); 
	}
	/**
	 * 
	 * calcA
	 * @param g
	 * @param a
	 * @param prime
	 * @return A
	 *
	 * @author Thomas Traxler
	 * @version 26.02.2014
	 */
	private static BigInteger calcA (BigInteger g, BigInteger a, BigInteger prime){
		return BigInteger.valueOf(((g.pow(a.intValue()).intValue())%prime.intValue()));
	}
	
	private static byte[] calcK (BigInteger A, BigInteger b, BigInteger prime){
		return ((A.pow(b.intValue()).intValue()%prime.intValue())+"").getBytes();
	}
}
