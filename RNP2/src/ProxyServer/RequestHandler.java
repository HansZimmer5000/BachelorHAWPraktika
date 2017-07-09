package ProxyServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import Models.Mail;
import Models.User;
import ProxyMain.ProxyMainThread;

class RequestHandler implements Runnable {
	// Nur ProxyServerThread braucht hierauf Zugriff

	private ProxyServerThread proxyServer;
	private Socket clientSocket;
	private boolean running;
	private int id;
	// ENUM: AUTHENTIFICATION, TRANSACTION, UPDATE (States). Implement
	// dispatchMessage();
	States state;

	public RequestHandler(int id, ProxyServerThread proxyServer, Socket clientSocket) {
		this.id = id;
		this.proxyServer = proxyServer;
		this.clientSocket = clientSocket;
		this.running = true;
		this.state = new States(this, proxyServer.getUserList());
	}

	@Override
	public void run() {
		sendAnswer("+OK Server Ready!");
		while (!Thread.currentThread().isInterrupted()) {
			String message = receiveMessage();
			if (!message.equals("")) {
				state.dispatchmessage(message);
			}
		}

		System.out.println("RequestHandler died!");
	}

	private String receiveMessage() {
		// Command = 4 Chars
		// Input = max 40 Chars
		String message = "";
		try {
			BufferedReader in = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
			message = in.readLine();
		} catch (IOException e) {
			System.out.println("RequestHandler: couldn't receive message!");
			quitConnection();
		}
		if (message == null) {
			// TODO: close Socket
			try {
				clientSocket.close();
			} catch (IOException e) {
				System.out.println("RequestHandler: Socket couldn't be closed!");
			}
			return "";
		}
		// message = message.replace("\n", "");
		System.out.println("ReceiveMessage: -" + message + "-");

		return message;
	}

	public void sendAnswer(String answer) {
		// PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
		// true);

		try {
			PrintWriter out = new PrintWriter(
					new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
			out.println(answer);
		} catch (IOException e) {
			System.out.println("Message couldn't be sent");
			return;
		}

		System.out.println("sent Answer:\n" + answer);
	}

	public void quitConnection() {
		// TODO: Hier proxyServer Funktion , delete this handler?
		proxyServer.handlerIsDone(this);
		//
		try {
			clientSocket.close();

		} catch (IOException e) {
			System.out.println("RequestHandler could not close connection!");
		}
		Thread.currentThread().interrupt();
	}

	/*
	 * ////////////////// GETTER & SETTER
	 *//////////////////
	public boolean isRunning() {
		return this.running;
	}

	public Socket getClientSocket() {
		return this.clientSocket;
	}

	public int getID() {
		return this.id;
	}

	public boolean equals(RequestHandler oRH) {
		boolean res = false;
		if (oRH.getID() == this.getID()) {
			res = true;
		}

		return res;
	}

	public boolean equals(Object o) {
		boolean res = false;
		RequestHandler oRH = (RequestHandler) o;

		if (this.getClass().equals(o.getClass())) {
			res = this.equals(oRH);
		}

		return res;
	}
}
