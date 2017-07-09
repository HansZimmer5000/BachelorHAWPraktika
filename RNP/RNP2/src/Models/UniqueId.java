package Models;

import java.util.ArrayList;
import java.util.Random;

public class UniqueId {
	// Server beliebig soll String 1-70Chars vergeben, Wertebereich:
	// Hex: 0x21 - 0x7E
	// Dec: 33 - 126
	// Dauerhafte Speicherung!
	// UNIQUE! ID
	private UniqueId() {
	}

	private static ArrayList<String> keys = new ArrayList<>();
	private final static int MIN_VALUE = 33, MAX_VALUE = 126, MAX_LENGTH = 70;

	public static synchronized String generateId() {
		Random rand = new Random();
		String res;
		int dec, length = rand.nextInt(MAX_LENGTH);

		do {
			res = "";
			for (int i = 0; i < length; i++) {
				dec = rand.nextInt(MAX_VALUE);
				if (dec >= MIN_VALUE) {
					res += Character.toString((char) dec);
				}
			}
		} while (keys.contains(res));
		keys.add(res);
		return res;		
	}
}
