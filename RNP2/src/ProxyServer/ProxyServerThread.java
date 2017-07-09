package ProxyServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import Models.Mail;
import Models.User;
import ProxyMain.ProxyMainThread;
import ProxyServer.RequestHandler;

public class ProxyServerThread implements Runnable{

	private static final int MAX_NUMBER_OF_THREADS = 2;
	private int nextHandlerID = 1;
	private ArrayList<User> userList;
	private ArrayList<RequestHandler> handlerList = new ArrayList<>();
	private int port;
	private ServerSocket serverSocket;
	
	
	public ProxyServerThread(ArrayList<User> userList, int port){
		this.userList = userList;
		this.port = port;
		
		try {
			this.serverSocket = new ServerSocket(port);
			this.serverSocket.setReuseAddress(true);
		} catch (IOException e) {
			System.out.println("Server can't be started!");
		}
	}

	@Override
	public void run() {
		
		while(!Thread.currentThread().isInterrupted()){
			System.out.println("Server listening on Port: "+port);
			Socket clientSocket = null;
			
			
			
			if(handlerList.size() < MAX_NUMBER_OF_THREADS){
				System.out.println("Server is running with "+ handlerList.size() + " Threads!");
				
				try{
					clientSocket = serverSocket.accept();
					handleRequest(clientSocket);
				} catch (IOException e){
					System.out.println("ProxyServerThread: couldn't accept ClientSocket!");
				}
			}	
//Auskommentiert von Michael 25.11.16 / 12Uhr	
			// else {
//				
//				// TODO: was genau passiert hier Jerom?
//				ArrayList<Thread> removeBatch = new ArrayList<>();
//				for(Thread thread : threadList){
//					if(thread.isInterrupted()){
//						removeBatch.add(thread);
//						System.out.println("ProxyServerThread: Handler removed from List");
//					}
//				}
//				threadList.removeAll(removeBatch);
//			}
		}
		
		//cleanup: serverSocket is closed after Session. Only if not null
		if (serverSocket != null) {
			try {
				serverSocket.close();
				System.out.println("Socket is closed (ServerMain)!");
			} catch (IOException e) {
				System.out.println("Could not close Socket!");
			}
		}
	}

	private void handleRequest(Socket clientSocket) {
		RequestHandler handler = new RequestHandler(nextHandlerID,this, clientSocket);
		++nextHandlerID;
		Thread handlerThread = new Thread(handler);
		handlerList.add(handler);
		handlerThread.start();
	}
	
	public void handlerIsDone(RequestHandler handler){
		if(this.handlerList.contains(handler)){
			this.handlerList.remove(handler);
		} else {
			System.out.println("ServerThread: Handler not in List!");
		}
	}
	
	/*//////////////////
	 * GETTER & SETTER
	 *//////////////////
	public ArrayList<User> getUserList(){return this.userList;}

}
