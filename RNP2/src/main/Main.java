package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.*;
import ProxyMain.ProxyMainThread;

public class Main {

	//public static User TESTUSER = new User("bai4rnpN", "lab30.cpt.haw-hamburg.de", "Y3ZdGYmh", new ArrayList<Account>());
	
	
	//Erledigt: Mailliste wir daktuell immer Ã¼berschrieben (komplett)
	//TODO: Verbindung korrekt quitten (antwort = null wenn socket disconnected?).
	//TODO: Möglich, dass Mailserver.jar bei jeder neuen Verbindung neuen Thread erstellt. Vielleicht schließen wir Socket aber auch nicht ordentlich!
	public static void main(String[] args) {

		User TESTUSER = new User("jerom@mail.com", "jerom@mail.com", "password", new ArrayList<Account>());
//		Account acc = new Account("bai4rnpN", "Y3ZdGYmh", "lab30.cpt.haw-hamburg.de", 11000);
//		Account acc2 = new Account("bai4rnpN", "Y3ZdGYmh", "lab31.cpt.haw-hamburg.de", 11000);
		
		Account acc = new Account("bai4rnpN", "Y3ZdGYmh", "localhost", 11000);
		
		TESTUSER.addAccount(acc);
		//TESTUSER.addAccount(acc2);
		
		ArrayList<User> userList = new ArrayList<>();
		userList.add(TESTUSER);
		
		ProxyMainThread pst1 = new ProxyMainThread(userList, 4000, 3000);
		Thread t1 = new Thread(pst1);
		t1.start();
		t1.interrupt();
	}

}

//String from = "From:";
//String whom = Main.TESTUSER.getMail();
//List<String> whomList = new ArrayList<>();
//whomList.add(whom);
//Map<String, List<String>> header = new HashMap<>();
//header.put(from, whomList);
//String mailBody = "\nThis is the Body \n.";
//long mailSize = 345;
//Mail testMail = new Mail(header, mailBody, mailSize);
//Mail testMail2 = new Mail(header, mailBody, mailSize);

