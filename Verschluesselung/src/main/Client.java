package main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Properties;

import comm.EncryptedReader;
import comm.EncryptedWriter;
import diffieHellman.*;

public class Client extends Thread{
	private boolean encrypted;
	private EncryptedReader br;
	private boolean run;
	public Client (EncryptedReader b, boolean encrypted){
		this.br = b;
		this.encrypted=encrypted;
		run=true;
	}
	
	public static void main(String[] args) throws IOException{
		byte[] key = new byte[8];
		Arrays.fill(key, Byte.parseByte("0"));
		String text="";
		char c=' ';
		boolean work = true;
		System.out.println("Default werte: Outgoing & Bob");
		BufferedReader a = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Incomming or Outgoing Connection?");
		
		while(text.length()==0)		
			text = a.readLine();
		c=text.charAt(0);
		int port = 0;

		Socket sock = null;
		if(c=='i'||c=='I'){
			while(work){
			System.out.println("Geben sie den Port ein");
			try{
				port = Integer.parseInt(a.readLine());
				work=false;
				}catch(NumberFormatException nfe){
					System.out.println("Please Insert a number!");
				}
			}
			work=true;
			ServerSocket ss = new ServerSocket(port);
			System.out.println("Waiting for Incomming Connection");
			sock=ss.accept();
			System.out.println("Got a connection.");
		}else{
			System.out.println("Geben sie bitte die IP ein ");
			String ip = a.readLine();
			while(work){
				System.out.println("Geben sie den Port ein");
				try{
					port = Integer.parseInt(a.readLine());
					work=false;
				}catch(NumberFormatException nfe){
					System.out.println("Please Insert a number!");
				}
			}
			work=true;
			System.out.println("Trying to connect. Press enter to abort");
			while(!a.ready()&&work){
				try{
					sock = new Socket(ip,port);
					work=false;
				}catch(ConnectException e){
					
				}catch(UnknownHostException uhe){
					System.out.println("Ip input not useable, please try again");
					ip = a.readLine();
					System.out.println("New connection attempt. Press enter to abort");
				}
			}
			if(work){
				System.out.println("Connection attempt aborted, shutting down");
				System.exit(0);
			}
		}
		Properties prop = null;
		System.out.println("Geben sie ein ob sie Alice oder Bob sein wollen");
		try{
		if(a.readLine().equals("Alice")){
			key = DiffieHellman.keyGenAlice(sock, prop);
		}else{
			 key = DiffieHellman.keyGenBob(sock, prop);

		}
		key = Arrays.copyOf(key, 8);
		System.out.println("Done");
		}catch(NumberFormatException nfe){
			System.out.println("Properties File probably wrong.");
			System.exit(37);
		}catch(IOException ioe){
			System.out.println("Connection probelm, Cleint shutting down.");
			System.exit(98);
		}
		EncryptedWriter out = null;
		EncryptedReader in=null;
		work = true;
		boolean e = false;
		while(work){
			System.out.println("Verschluesselte Uebertragung? J/N");
			text=a.readLine();
			c = text.charAt(0);
			if(c=='J'||c=='j'){
				in= new EncryptedReader(new InputStreamReader(sock.getInputStream()),key);
				out = new EncryptedWriter(new PrintWriter(sock.getOutputStream()),key);
				e=true;
				work=false;
				System.out.println("Uebertragung erfolgt verschluesselt.");
			}else if (c=='N'||c=='n'){
				in= new EncryptedReader(new InputStreamReader(sock.getInputStream()),null);
				out = new EncryptedWriter(new PrintWriter(sock.getOutputStream()),null);
				out.write(new String(key));
				out.flush();
				work=false;
				System.out.println("Uebertragung erfolgt unverschluesselt.");
				
			}
		}
		
		Client client = new Client(in,e);
		client.start();
		work=true;
		while(work){
			System.out.println("Geben sie ein welche Text sie senden wollen");
			text = a.readLine();
			if(text.equals("!end"))
				work=false;
			if(e){
				try {
					((EncryptedWriter)out).writeLineEncrypted(text);
				} catch (InvalidKeyException ike) {
					System.out.println("Key not valid");
					work=false;
				}
			}else{
				out.write(text);
				out.newLine();
				out.flush();
			}


		}
		client.end();
	}
	public void end(){
		run=false;
	}
	 @Override
	 public void run(){
		 while(run){
			 
			 try {
				 if(encrypted){

						try {
							System.out.println(br.readLineEcrypted());
						} catch (InvalidKeyException e) {

							System.out.println("Key not valid");
							run=false;
						}
				 }else{
					 System.out.println(br.readLine());
				 }
			} catch(SocketException se){
				System.out.println("Connection lost, client ending");
				end();
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		 }
	 }

}
