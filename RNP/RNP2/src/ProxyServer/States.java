package ProxyServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Models.*;
import ProxyMain.ProxyMainThread;

public class States {

	private final static String USER = "USER", PASS = "PASS", QUIT = "QUIT", STAT = "STAT", LIST = "LIST",
			RETR = "RETR", DELE = "DELE", NOOP = "NOOP", RSET = "RSET", UIDL = "UIDL";
	public final static ArrayList<String> SINGLECMD = new ArrayList<String>() {
		{
			add(QUIT);
			add(STAT);
			add(RSET);
			add(NOOP);
		}
	}, TUPELCMD = new ArrayList<String>() {
		{
			add(USER);
			add(PASS);
			add(RETR);
			add(DELE);
		}
	}, OPTIONALCMD = new ArrayList<String>() {
		{
			add(UIDL);
			add(LIST);
		}
	};

	private final static String ERR = "-ERR ", OK = "+OK ", // Genau ein
															// Leerzeichen nach
															// OK/ERR, laut RFC
			PASSOK = OK + "Password Accepted", CMDUNKN = ERR + "Unkown Command", BYE = OK + "BYE", END = ".",
			TRANSOK = OK + "Transaction Quit";

	public enum State {
		AUTH {
			@Override
			String dispatchMessage(String cmd, String input) {
				String answer;

				switch (cmd) {

				case USER:
					user = getUserByName(input, userList);
					if (user != null) {
						answer = OK + "Welcome " + input;
					} else {
						answer = ERR + "Unkown User";
					}
					break;

				case PASS:
					if (user != null) {
						if (input.equals(user.getPassword())) {
							answer = PASSOK;
						} else {
							answer = ERR + "Wrong Password";
						}
					} else {
						answer = ERR + "First use USER-Command";
					}
					break;

				case QUIT:
					answer = BYE;
					break;

				default:
					answer = CMDUNKN;
				}
				return answer;
			}
		},
		TRANSACTION {
			@Override
			public String dispatchMessage(String cmd, String input) {

				String answer = ERR;

				switch (cmd) {

				case QUIT:
					answer = BYE;
					break;

				case STAT:
					answer = OK + stat(mailList);
					break;

				case LIST:
					// Hier END anh�ngen notwendig!
					answer = listMessages(mailList) + END;
					break;

				case RETR:
					// if input can be used as a number, it gets parsed
					if (input.matches("^-?\\d+$")) {
						answer = sendMail(Integer.parseInt(input), mailList);
					}

					break;

				case DELE:
					if (input.matches("^-?\\d+$")) {
						answer = deleteMail(Integer.parseInt(input), mailList);
					}
					break;

				case NOOP:
					answer = OK;
					break;

				case RSET:
					resetAllDeleteFlags(mailList);
					answer = OK + stat(mailList);
					break;

				case UIDL:
					if (!input.equals("")) {
						int idx = Integer.valueOf(input);
						// Fuer Client ist erste Mail = 1, fuer Server = 0,
						// somit -1
						answer = uIDIndex(idx - 1, mailList);
					} else {
						answer = uIDAll(mailList) + END;
						break;
					}

					break;

				default:
					answer = CMDUNKN;
				}

				return answer;

			}
		},
		// TODO: Delete flagged messages
		UPDATE {
			@Override
			public String dispatchMessage(String cmd, String input) {
				if (cmd == "UPDATE" && input == "UPDATE") {
					ArrayList<Mail> removeBatch = new ArrayList<>();
					for (Mail actMail : mailList) {
						if (actMail.getDestroyFlag()) {
							removeBatch.add(actMail);
						}
					}
					mailList.removeAll(removeBatch);
					System.out.println("MailList updated: " + mailList.size() + " Emails left!");
				}
				return CMDUNKN; // Falls
								// es
								// ausversehen
								// ausgerufen
								// wird.
			}
		};

		// Abstrakte Methoden f�r alle States
		abstract String dispatchMessage(String cmd, String input);

		// State uebergreifende Variablen
		public static User user;
		public static ArrayList<User> userList;
		public static ArrayList<Mail> mailList;
	}

	// Instanzvariablen und Kotruktor
	private State state = State.AUTH;
	private RequestHandler handler;

	public States(RequestHandler handler, ArrayList<User> userList) {
		this.handler = handler;
		State.userList = userList;
	}

	void dispatchmessage(String message) {
		System.out.println(message + " will now be dispatched in 'States' with updated Maillist");
		State.mailList = ProxyMainThread.getActualMaillist(State.user);
		String[] splittedMsg = message.split(" ", 2);
		String cmd = splittedMsg[0], input = "", answer = ERR;

		System.out.println("Dispatched " + message + " has new Answer");
		if (OPTIONALCMD.contains(cmd)) {
			System.out.println("Optional command");
			// List und UIDL koennen mit und ohne Parameter sein.
			try {
				input = splittedMsg[1];
			} catch (ArrayIndexOutOfBoundsException e) {
				// ist ok wenn hier fehl schlaegt, da OPTIONALE auch kein Input
				// haben koennen
			}
			answer = this.state.dispatchMessage(cmd, input);
		} else if (TUPELCMD.contains(cmd)) {
			System.out.println("Tupel command");
			try {
				input = splittedMsg[1];
				answer = this.state.dispatchMessage(cmd, input);
			} catch (ArrayIndexOutOfBoundsException e) {
				// Wenn hier Exception -> Err als Answer, da ein Input gebraucht
				// wird!
			}
		} else if (SINGLECMD.contains(cmd)) {
			System.out.println("Single command");
			answer = this.state.dispatchMessage(cmd, input);
		}
		handler.sendAnswer(answer);

		// Arbeit die nach dem verschicken der Antwort erledigt werden muss.
		// z.b. State Wechsel, Connection beenden.
		switch (answer) {
		case (BYE):
			if (this.getState().equals(States.State.TRANSACTION)) {
				this.setState(States.State.UPDATE);
				this.state.dispatchMessage("UPDATE", "UPDATE");
			}
			handler.quitConnection();
			break;
		case (PASSOK):
			this.setState(State.TRANSACTION);
			break;
		}
	}

	// Weitere Methoden
	private static User getUserByName(String name, ArrayList<User> userList) {
		for (User user : userList) {
			if (user.getName().equals(name)) {
				return user;
			}
		}
		return null;
	}

	private static boolean userKnown(String name, ArrayList<User> userList) {
		return getUserByName(name, userList) != null;
	}

	private static String uIDIndex(int index, ArrayList<Mail> mailList) {
		String res = ERR + "No Mail with this Index";
		if (index < mailList.size()) {
			Mail actMail = mailList.get(index);
			actMail.setUID();
			if (!actMail.getDestroyFlag()) {
				if (!actMail.getUID().equals(" ")) {
					return OK + index + " " + actMail.getUID();
				} else {
					return ERR + "UID for " + index + " couldn't be set";
				}
			} else {
				return ERR + "Message going to be destroyed, no UID will be shown";
			}
		}
		return res;
	}

	private static String uIDAll(ArrayList<Mail> mailList) {
		String res = ERR + "no Mails", actIndexAnswer;
		String[] splittedMsg;
		int emailCount = getEmailCount(mailList);

		String answer = OK + "\n";
		for (int i = 0; i < emailCount; i++) {
			actIndexAnswer = uIDIndex(i, mailList);
			splittedMsg = actIndexAnswer.split(" ", 3);
			System.out.println(splittedMsg[0] + " " + splittedMsg[1] + " " + splittedMsg[2]);
			if (splittedMsg[0].equals("+OK")) {
				answer += "" + (i + 1) + " " + splittedMsg[2] + "\n";
			}
		}
		System.out.println("States: uIDAll(): " + answer);
		return answer;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	private static String stat(ArrayList<Mail> mailList) {
		int numberOfMessages = getEmailCount(mailList);
		long sizeOfMessages = sizeOfMessages(mailList);
		return numberOfMessages + " " + sizeOfMessages;
	}

	private static int getEmailCount(ArrayList<Mail> mailList) {
		int size = 0;
		for (Mail actMail : mailList) {
			if (!actMail.getDestroyFlag()) {
				size++;
			}
		}
		return size;
	}

	private static long sizeOfMessages(ArrayList<Mail> mailList) {
		long size = 0;
		Mail actMail;
		for (int i = 0; i < mailList.size(); i++) {
			actMail = mailList.get(i);
			if (!actMail.getDestroyFlag()) {
				size = size + actMail.getSize();
			}
		}
		return size;
	}

	private static String listMessages(ArrayList<Mail> mailList) {
		int emailCount = getEmailCount(mailList);
		Mail actMail;
		String answer = "+OK mailbox has " + emailCount + " messages (" + sizeOfMessages(mailList) + ")\n";
		for (int i = 0; i < emailCount; i++) {
			actMail = mailList.get(i);
			if (!actMail.getDestroyFlag()) {
				answer += (i + 1) + " " + mailList.get(i).getSize() + "\n";
			}
		}
		System.out.println("States: listMessages(): " + answer);
		return answer;
	}

	private static String sendMail(int index, ArrayList<Mail> mailList) {

		String answer;

		if (index > 0 && index <= mailList.size()) {
			int internalIndex = index - 1;
			answer = OK + "message " + index + "\n";
			// Add Headers
			Mail mail = mailList.get(internalIndex);
			Map<String, List<String>> headers = mail.getHeader();

			// iterate over Entryset and get Headers with Content
			for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
				String headerKey = entry.getKey();
				List<String> values = entry.getValue();
				answer += headerKey + ":";
				for (int i = 0; i < values.size(); i++) {
					answer += " " + values.get(i) + "\n";
				}
			}

			// Add Body
			answer += "\n" + mail.getBody() + "\n.";
		} else {
			answer = ERR + "message does not exist";
		}

		// Send Answer
		return answer;
	}

	private static String deleteMail(int index, ArrayList<Mail> mailList) {
		String answer;
		if (index > 0 && index <= mailList.size()) {
			int internalIndex = index - 1;
			Mail mail = mailList.get(internalIndex);
			mail.setDestroyFlag(true);

			System.out.println("STATES: deleteMail(): deleted = " + mail.getDestroyFlag());
			answer = OK + " email " + index + " will be deleted";
		} else {
			answer = ERR + "message does not exist";
		}
		return answer;
	}

	private static void resetAllDeleteFlags(ArrayList<Mail> mailList) {
		for (int i = 0; i < mailList.size(); i++) {
			mailList.get(i).setDestroyFlag(false);
		}
	}
}
