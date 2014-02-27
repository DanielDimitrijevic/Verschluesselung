package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Properties;

import diffieHellman.*;

public class Client {
	public static void main(String[] args) throws IOException{
		byte[] key = null;
		System.out.println("Geben sie ein welche Text sie verschlüsseln wollen");
		BufferedReader a = new BufferedReader(new InputStreamReader(System.in));
		String text = a.readLine();
		System.out.println("Geben sie bitte die IP ein ");
		String ip = a.readLine();
		System.out.println("Geben sie den Port ein");
		int port = Integer.parseInt(a.readLine());
		Socket sock = new Socket(ip,port);
		Properties prop = new Properties();
		System.out.println("Geben sie ein ob sie Alice oder Bob sein wollen");
		if(a.readLine().equals("Alice")){
			key = DiffieHellman.keyGenAlice(sock, prop);
		}else{
			 key = DiffieHellman.keyGenBob(sock, prop);
		}
		OutputStream out = sock.getOutputStream();
		out.write(key);
		out.write(text.getBytes());
		
	}

}
