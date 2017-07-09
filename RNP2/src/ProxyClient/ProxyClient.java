package ProxyClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

import Models.*;

public class ProxyClient {

	private Account account;

	private Socket serverSocket;
	BufferedReader in;
	PrintWriter out;

	public ProxyClient(Account account) {
		this.account = account;
	}

	public boolean connect() {
		try {
//			 SocketFactory sf = SSLSocketFactory.getDefault();
//			 serverSocket = sf.createSocket(account.getHost(),
//			 account.getPort());
			serverSocket = new Socket(account.getHost(), account.getPort());
			serverSocket.setReuseAddress(true); // To reuse the port
			in = new BufferedReader(new InputStreamReader(this.serverSocket.getInputStream(), StandardCharsets.UTF_8));
			out = new PrintWriter(new OutputStreamWriter(serverSocket.getOutputStream(), StandardCharsets.UTF_8), true);
			getServerAnswer();
			return true;
		} catch (IOException e) {
			System.out.println("Error: IP or Port is wrong!");
			return false;
		}
	}

	public void disconnect() {
		try {
			serverSocket.close();
			System.out.println("ClientSocket for :"+account.getMail()+"closed! - "+serverSocket.isClosed());
		} catch (IOException e) {
			System.out
					.println("Disconnected Socket; IOException; socket = null? " + Objects.equals(serverSocket, null));
		}
	}

	public boolean login() {
		String userAnswer, passAnswer;

		try {
			userAnswer = sendCommand("USER " + account.getMail());

			if (userAnswer.startsWith("+OK")) {
				passAnswer = sendCommand("PASS " + account.getPassword());
				if (passAnswer.startsWith("+OK")) {
					System.out.println("Login erfolgreich");
					return true;
				}
			}
		} catch (IOException e) {
			System.out.println("Login IOException");
		}
		return false;
	}

	public void logout() {
		try {
			sendCommand("QUIT");
		} catch (IOException e) {
			System.out.println("Logout IOException");
		}
	}

	public int getNumberOfNewMails() {
		int res = -1;
		try {
			String response = sendCommand("STAT");
			if (response.startsWith("+OK")) {
				String[] values = response.split(" ");

				res = Integer.parseInt(values[1]);
				System.out.println("getNumberofNewMails erfolgreich");
			} else {
				System.out.println("getNumberofNewMails -Err; res = -1!");
			}
		} catch (IOException e) {
			System.out.println("getNumberofNewMails IOException; res = -1!");
		}
		return res;
	}

	/**
	 * GMX trennt anscheinend den Head nicht mit ":", obacht!
	 * @param i
	 * @return
	 * @throws IOException
	 */
	private Mail getMail(int i) throws IOException {
		String response = sendCommand("RETR " + i);
		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		String headerName = null, headerValue;
		int size = 0;

		if (response.startsWith("+OK")) {
			// Header, endet nach einer leeren response
			while ((response = getServerAnswer()).length() != 0) {
				System.out.println("Response.length > 0");
				// if (response.startsWith("\t")) {
				// continue; // : multiline n�tig oder nich?
				// } // no process of multiline headers
				System.out.println(response);

				int colonPosition = response.indexOf(":");
				if (colonPosition >= 0) {
					headerName = response.substring(0, colonPosition);
					// String headerValue;

					if (response.length() > colonPosition) {
						headerValue = response.substring(colonPosition + 2);
					} else {
						headerValue = "";
					}
					
					List<String> headerValues = headers.get(headerName);
					if (headerValues == null) {
						headerValues = new ArrayList<String>();
						headers.put(headerName, headerValues);
					}
					headerValues.add(headerValue);
					System.out.println("New Value: "+ headerName + " Value: " + headerValues);

					size += response.getBytes().length;
				}
			}
			// Body beginnt nach der oben erwahnten leeren Response
			StringBuilder bodyBuilder = new StringBuilder();
			while (!(response = getServerAnswer()).equals(".")) {
				bodyBuilder.append(response + "\n");
				size += response.getBytes().length;
			}

			// Nun Mail zum loeschen freigeben
			response = sendCommand("DELE " + i);

			System.out.println("Mail mit Index " + i + " konnte abgerufen werden");
			return new Mail(headers, bodyBuilder.toString(), size);
		} else {
			// Anfang der Antwort ist icht +OK, somit -ERR.
			System.out.println("Mail mit Index " + i + " konnte NICHT abgerufen werden");
			return null;
		}
	}

	public ArrayList<Mail> getNewMails() {
		// Zieht die neuen Messages und loescht diese beim Provider.
		int numOfMessages = getNumberOfNewMails();
		Mail actMail;
		ArrayList<Mail> messageList = new ArrayList<>();

		for (int i = 1; i <= numOfMessages; i++) {
			try {
				actMail = getMail(i);
			} catch (IOException e) {
				actMail = null;
			}
			if (actMail != null) {
				messageList.add(actMail);
			}
		}
		return messageList;
	}

	public String sendCommand(String cmd) throws IOException {
		// get user-prompt and send it to Server
		String message = cmd;
		// PrintWriter out = new PrintWriter(serverSocket.getOutputStream(),
		// true);
		// PrintWriter out = new PrintWriter(
		// new OutputStreamWriter(serverSocket.getOutputStream(),
		// StandardCharsets.UTF_8), true);
		out.println(message);
		System.out.println("Server: Message sent: " + message);
		return getServerAnswer();
	}

	private String getServerAnswer() throws IOException {
		// Scanner in = new Scanner(serverSocket.getInputStream(),
		// StandardCharsets.UTF_8.name());
		// BufferedReader in = new BufferedReader(
		// new InputStreamReader(this.serverSocket.getInputStream(),
		// StandardCharsets.UTF_8));
		String message = in.readLine();
		System.out.println("Received message: " + message);

		return message;
	}
}
