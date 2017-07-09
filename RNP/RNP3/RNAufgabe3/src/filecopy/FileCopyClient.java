package filecopy;

/* FileCopyClient.java
 Version 0.1 - Muss ergaenzt werden!!
 Praktikum 3 Rechnernetze BAI4 HAW Hamburg
 Autoren:
 */

import java.io.*;
import java.net.*;

import filecopy.ADTBuffer;

public class FileCopyClient extends Thread {

	// 3. ReturnTime berechnen

	// 4. wenn Ack ankommt nicht nur Packet acken (schon erledigt) sondern timer
	// stoppen, RTT neu berechnen
	// 5. getNext logik (nur WindowSize Packete gleichzeitig, nicht darüber
	// hinaus. wenn geacktes Packet = sendbase, dann alle bis zum nächsten nicht
	// geackten Packet raus aus üuffer (index verschieben)))
	//
	//--------- For Benchmarking
	public static int ACK_COUNT = 0;
	public static int SEND_COUNT = 0;
	public static long START_TIME = System.nanoTime();
	public final static boolean BENCHMARK = true;
	
	// -------- Constants
	public final static boolean TEST_OUTPUT_MODE = true;

	public final int SERVER_PORT = 23000;

	public final int UDP_PACKET_SIZE = 1008;

	// -------- Public parms
	public String servername;

	public String sourcePath;

	public String destPath;

	public int windowSize;

	public long serverErrorRate;

	// -------- Variables
	private final long MAX_TIMEOUT = 10000000000L; // 10 Sekunden
	// current default timeout in nanoseconds
	private long timeoutValue = 100000000L; // 0.1 Sekunden 
	private long expRTT = -1;
	private long jitter = -1;
	private double rtt_x = 1/4;
    private double rtt_y = rtt_x/2;
    private int[] numOfTimeOutsArr;    
    
	DatagramSocket serverSocket;
	BufferedReader in;
	PrintWriter out;

	FCpacket controlPacket;
	// Buffer mit WindowSize
	ADTBuffer buffer;
	ReceiveAckThread receiveAckThread;

	// Constructor
	public FileCopyClient(String serverArg, String sourcePathArg, String destPathArg, String windowSizeArg,
			String errorRateArg) {
		servername = serverArg;
		sourcePath = sourcePathArg;
		destPath = destPathArg;
		windowSize = Integer.parseInt(windowSizeArg);
		serverErrorRate = Long.parseLong(errorRateArg);

	}

	public void runFileCopyClient() {

		// Connect to Host

		try {
			serverSocket = new DatagramSocket();
			serverSocket.connect(InetAddress.getByName(servername), SERVER_PORT);
			serverSocket.setReuseAddress(true); // To reuse the port
			testOut("Connected to Host: " + this.servername);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			testOut("Unknown Host");
		} catch (IOException e) {
			e.printStackTrace();
			testOut("couldn't connect to Server");
		}

		// Read Document
		// Split into Packets

		// Send ControlPacket (Steuerdaten)
		controlPacket = makeControlPacket();
		// sendPacket(controlPacket);
		buffer = new ADTBuffer(windowSize, sourcePath, controlPacket);
		numOfTimeOutsArr = new int[buffer.getSize()];
		
		// send All Packets & Start ReceiveAckThread
		receiveAckThread = new ReceiveAckThread(this, serverSocket, buffer);
		receiveAckThread.start();

		// Pakete werden aus Buffer (bis while buffer.hasNext()) gesendet
		while (buffer.hasNext()) {
			FCpacket packet = buffer.getNext();
			if (packet == null) {
				// System.out.println("An Error has occured. Packet to send was empty");
			} else {
				sendPacket(packet);
			}
		}
		
		testOut("End");
		serverSocket.close(); // ReceiveAckThread wird geschlossen.
		this.receiveAckThread.interrupt();
		
		if(BENCHMARK){
			benchOut();
		}
  }
  
  private synchronized void sendPacket(FCpacket packet){
	  try {		  
		  //dataLength cannot be packet.length, because the SequenceNumber isnt included in getLength...
		  byte[] seqNumberBytesAndData = packet.getSeqNumBytesAndData();
		  int dataLength = seqNumberBytesAndData.length;
		  
		  DatagramPacket datagramPacket = new DatagramPacket(seqNumberBytesAndData, dataLength, InetAddress.getByName(servername), SERVER_PORT);
		  
		  startTimer(packet);
		  serverSocket.send(datagramPacket);
		  SEND_COUNT++;
	} catch (IOException e) {
		e.printStackTrace();
	}
  }

  /**
  * Timer Operations
  */
  public void startTimer(FCpacket packet) {
    /* Create, save and start timer for the given FCpacket */
    FC_Timer timer = new FC_Timer(timeoutValue, this, packet.getSeqNum());

    packet.setTimestamp(System.nanoTime());
    packet.setTimer(timer);
    timer.start();
  }

  public void cancelTimer(FCpacket packet) {
    /* Cancel timer for the given FCpacket 
     * Wird nur aufgerufen wenn ACK fuer packet kam.
     * */
    testOut("Cancel Timer for packet" + packet.getSeqNum());

    if(numOfTimeOutsArr[(int) packet.getSeqNum()] == 0){
    	computeTimeoutValue(System.nanoTime() - packet.getTimestamp());
    }
    
    packet.getTimer().interrupt();
  }

  /**
   * Implementation specific task performed at timeout
   */
  public void timeoutTask(long seqNum) {
	  // Wird aufgerufen wenn Timer auslaeuft und er NICHT interrupted wurde.
	  numOfTimeOutsArr[(int) seqNum] = numOfTimeOutsArr[(int) seqNum] + 1;
	  buffer.resendPacket(seqNum);
	  
	  this.timeoutValue *= 2;
  }


  /**
   *
   * Computes the current timeout value (in nanoseconds)
   */
  public void computeTimeoutValue(long sampleRTT) {
		if(expRTT == -1) expRTT = sampleRTT;
		if(jitter == -1) jitter = expRTT/2;
		
		expRTT = (long) ((1 - rtt_y) * expRTT + rtt_y * sampleRTT);
		jitter = (long) ((1 - rtt_x) * jitter + rtt_x * Math.abs(sampleRTT - expRTT));
		
		long timeout = expRTT + 4L * jitter;
		if(timeout > MAX_TIMEOUT){
			timeout = MAX_TIMEOUT;
		}
		
		this.timeoutValue = timeout;
  }


  /**
   *
   * Return value: FCPacket with (0 destPath;windowSize;errorRate)
   */
  public FCpacket makeControlPacket() {
   /* Create first packet with seq num 0. Return value: FCPacket with
     (0 destPath ; windowSize ; errorRate) */
    String sendString = destPath + ";" + windowSize + ";" + serverErrorRate;
    testOut(sendString);
    
    byte[] sendData = null;
    try {
      sendData = sendString.getBytes("UTF-8");
      FileCopyClient.testOut(String.valueOf(sendData));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return new FCpacket(0, sendData, sendData.length);
  }

  public static void testOut(String out) {
    if (TEST_OUTPUT_MODE) {
      System.err.printf("%,d %s: %s\n", System.nanoTime(), Thread
          .currentThread().getName(), out);
    }
  }
  
  private void benchOut(){
		/*
		 * 1. Zeit
		 * 2. Anzahl Timerabläufe
		 * 3. Anzahl Wiederholungen
		 * 4. Anzahl Acks
		 * 5. RTT
		 * 6. Timeout
		 * 7. Windowssize
		 * 8. Error rate
		 */
		int timerCount = 0, i = 0, size = this.buffer.getSize();
		while(i < size){
			timerCount += numOfTimeOutsArr[i];
			i++;
		}
		
		System.out.println("Benoetigte Zeit: "+ (System.nanoTime() - START_TIME) + " (ns)");
		System.out.println("Benoetigte Timer: " + timerCount);
		System.out.println("Benoetigte Wiederholte Uebertragungen: " + SEND_COUNT);
		System.out.println("Benoetigte ACKs: " + ACK_COUNT);
		System.out.println("Ermittelte RTT: "+ this.expRTT + " (ns)");
		System.out.println("Ermittelter Timeout: " + this.timeoutValue + " (ns)");
		System.out.println("Genutzte Windowssize: " + this.windowSize);
		System.out.println("Genutzte Error-Rate: " + this.serverErrorRate);	
  }

  public static void main(String argv[]) {
//    FileCopyClient myClient = new FileCopyClient(argv[0], argv[1], argv[2], argv[3], argv[4]);
	  
	  	String servername = "localhost";
	    String sourcePath = "106MB .mp3";
	    String destPath = "EsLeuft.mp3";
	    String windowSize = "1000";
	    String serverErrorRate = "1001";

	    FileCopyClient myClient = new FileCopyClient(servername, sourcePath, destPath, windowSize, serverErrorRate);
	    myClient.runFileCopyClient();
	    
//	    String[] sizes = new String[]{"1","10","100"};
//	    String[] rates = new String[]{"10","100","1000"};
//	    int i1 = 0, i2 = 0, i3 = 1;
//	    ArrayList<FileCopyClient> clients = new ArrayList<>();
//	    
//	    while(i1 < sizes.length && BENCHMARK){
//	    	while(i2 < rates.length){
//	    	    clients.add(new FileCopyClient(servername, sourcePath, destPath, sizes[i1], rates[i2]));
//	    		i2++;
//	    	}
//	    	i1++;
//	    }
//	    
//	    FileCopyClient actClient = clients.get(0);
//	    actClient.runFileCopyClient();
//	    while(){
//		    FileCopyClient actClient = clients.get(i3);
//		    actClient.runFileCopyClient();
//		    i3++;
//	    }
  }

}
