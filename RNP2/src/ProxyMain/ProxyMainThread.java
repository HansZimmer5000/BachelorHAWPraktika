package ProxyMain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import Models.*;
import ProxyClient.ProxyClient;
import ProxyServer.ProxyServerThread;
import main.Main;

public class ProxyMainThread implements Runnable {

	private final boolean DEBUGGING = true;

	private int port;
	private ArrayList<User> userList;
	private static HashMap<User, ArrayList<Mail>> userMailMap = new HashMap<>();
	final long SLEEPTIME_MS; // 30 Sekunden.

	private Thread serverThread;

	private boolean interrupted;

	public ProxyMainThread(ArrayList<User> userList, int port, long maildropFrequenzyInMS) {
		this.port = port;
		this.userList = userList;
		this.SLEEPTIME_MS = maildropFrequenzyInMS;
	}

	@Override
	public void run() {

		ProxyServerThread proxyServerThread = new ProxyServerThread(userList, port);
		serverThread = new Thread(proxyServerThread);
		serverThread.start();

		System.out.println("VOR CLIENT-Schleife: ");

		///////////////////// TIMER////////////////////////////

		TimerTask action = new TimerTask() {
			public void run() {
				updateAccMailMap();
			}
		};

		Timer caretaker = new Timer();
		caretaker.schedule(action, 0, SLEEPTIME_MS);

		/////////////////////////////////////////////

		// System.out.println("NACH CLIENT-Schleife");
		// serverThread.interrupt();
		// TODO: Cleanup
	}

	private void updateAccMailMap() {
		System.out.println("UpdateAccMailMap gestartet");
		ArrayList<Mail> allAccMailList = new ArrayList<>();

		for (User user : userList) {
			// alte Mails hinzufügen, damit sie nicht überschrieben werden
			ArrayList<Mail> oldMails = userMailMap.get(user);
			if (!(oldMails == null)) {
				allAccMailList.addAll(oldMails);
			}

			for (Account acc : user.getAccounts()) {
				ArrayList<Mail> mailList = new ArrayList<>();
				if (!DEBUGGING) {
					mailList = getAccMaillist(acc);
				}
				if (!Objects.equals(null, mailList)) {
					allAccMailList.addAll(mailList);
				}
			}
			System.out.println("allAccMaillIst hat: " + allAccMailList.size() + " E-Mails");
			this.userMailMap.put(user, allAccMailList);
		}
	}

	private ArrayList<Mail> getAccMaillist(Account acc) {
		boolean connectionOK = false, loginOK = false;
		ArrayList<Mail> mailList = null;

		ProxyClient proxyClient = new ProxyClient(acc);

		connectionOK = proxyClient.connect();
		if (connectionOK) {
			loginOK = proxyClient.login();
			if (loginOK) {
				ArrayList<Mail> newMails = proxyClient.getNewMails();
				// HIer wird QUIT gesendet, um MAils von Server zu löschen
				proxyClient.logout();
				mailList = newMails;
			} else {
				System.out.println("In den Accountg " + acc.getHost() + " konnte nicht eingelogt werden.");
			}
		} else {
			System.out.println("Es konnte keine Verbindung zu " + acc.getHost() + " hergestellt werden.");
		}

		proxyClient.disconnect();

		return mailList;
	}

	public static ArrayList<Mail> getActualMaillist(User user) {
		return userMailMap.get(user);
	}
}
