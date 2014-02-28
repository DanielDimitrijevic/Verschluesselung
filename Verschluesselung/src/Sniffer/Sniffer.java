package Sniffer;



    
    import java.util.ArrayList;  
import java.util.Arrays;
import java.util.Date;  
import java.util.List;  
      



    import org.jnetpcap.Pcap;  
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;  
import org.jnetpcap.packet.PcapPacket;  
import org.jnetpcap.packet.PcapPacketHandler;  
import org.jnetpcap.protocol.tcpip.Tcp;
      
    
    public class Sniffer {  
      
        /** 
         * Main Methode
         *   
         */  
        public static void main(String[] args) {  
            List<PcapIf> alldevs = new ArrayList<PcapIf>();   
            StringBuilder errbuf = new StringBuilder();   
      
            // Alle Geräte finden
            int r = Pcap.findAllDevs(alldevs, errbuf);  
            if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
                System.err.printf("Konnte die Device list nicht lesen %s", errbuf  
                    .toString());  
                return;  
            }  
      
            System.out.println("Network devices gefunden:");  
      
            int i = 0;  
            for (PcapIf device : alldevs) {  
                String description =  
                    (device.getDescription() != null) ? device.getDescription()  
                        : "No description available";  
                System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);  
            }  
      
            PcapIf device = alldevs.get(0);   
            System.out  
                .printf("\n '%s' wurde ausgewählt:\n",  
                    (device.getDescription() != null) ? device.getDescription()  
                        : device.getName());  
      
            // Öffnen des Geräts
            int snaplen = 64 * 1024;             
            int flags = Pcap.MODE_PROMISCUOUS;   
            int timeout = 10 * 1000;
            Pcap pcap =  
                Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);  
          
        	PcapBpfProgram program = new PcapBpfProgram();
        	String expression = "host 10.0.0.1";//"host 10.0.0.1";
        	int optimize = 0; // 0 = false
        	int netmask = 0xFFFFFF00; // 255.255.255.0
        	if (pcap.compile(program, expression, optimize, netmask) != Pcap.OK) {
        	System.err.println(pcap.getErr());
        	return;
        	}
        	if (pcap.setFilter(program) != Pcap.OK) {
        	System.err.println(pcap.getErr());
        	return;	
        	} 
            if (pcap == null) {  
                System.err.printf("Fehler beim capturen des Geräts: "  
                    + errbuf.toString());  
                return;  
            }  
      
            // Lesen der Packete in einer Loop
            PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() {  
      
                public void nextPacket(PcapPacket packet, String user) {  
                	Tcp a = new Tcp();
                	
                    System.out.printf("Packet %s caplen=%-4d len=%-4d %s\n",  
                        new Date(packet.getCaptureHeader().timestampInMillis()),   
                        packet.getCaptureHeader().caplen(),  // Length actually captured  
                        packet.getCaptureHeader().wirelen(), // Original length   
                        user                                 // User supplied object  
                        );  
                    if (packet.hasHeader(a)) {
                    	System.out.printf("Payload length=%d\n", a.getLength());
                    	byte[] payloadContent = a.getByteArray(0, a.size()); // offset, length
                    	String strPayloadContent = new String(payloadContent);
                    	System.out.println("Payload Inhalt = [" + strPayloadContent + "]");
                    	System.out.println(a.toHexdump());
                    	}
                }  
            };  
     
            pcap.loop(1000, jpacketHandler,"");  
            pcap.close();  
        }  
    }  