package filecopy;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class FCpacketUtil {

	private FCpacketUtil() {
	}

	public static final int MAX_BYTES_PER_DATAPACKET = 1000;

	public static ArrayList<FCpacket> fileToFCpackets(String sourcePath) {
		byte[] arr = readFile(sourcePath);
		ArrayList<FCpacket> res = new ArrayList<>();

		if (arr != null) {
			res = splitDataPacket(arr);
		}
		return res;
	}

	private static byte[] readFileTXT(String sourcePath) {
		byte[] res = null;
		try {
			res = Files.readAllBytes(Paths.get(sourcePath));
		} catch (IOException e) {
			System.out.println("ReadFile: Error beim Einlesen der Datei in " + sourcePath);
		}
		return res;
	}

	private static byte[] readFile(String sourcePath) {
		InputStream inStream = null;
		byte[] res = null, buffer = new byte[8192];
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		int readBytes;
		
		try {
			inStream = new FileInputStream(sourcePath);
		} catch (FileNotFoundException e) {
			System.out.println("readFile: InputStream konnte nicht erstellt werden!");
		}

		if (inStream != null) {
			readBytes = 0;
			
			try {
				readBytes = inStream.read(buffer);
			} catch (IOException e) {
				System.out.println("readFile: Vor While-Schleife konnte inStream nicht gelesen werden!");
			}

			while (readBytes > 0) {
				outStream.write(buffer, 0, readBytes);
				readBytes = 0;
				try {
					readBytes = inStream.read(buffer);
				} catch (IOException e) {
					System.out.println("readFile: In While-Schleife konnte inStream nicht gelesen werden!");
				}
			}

			try {
				inStream.close();
			} catch (IOException e) {
				System.out.println("readFile: inStream konnte nicht geschlossen werden");
			}
			try {
				outStream.close();
			} catch (IOException e) {
				System.out.println("readFile: outStream konnte nicht geschlossen werden");
			}

			res = outStream.toByteArray();
		}
		return res;
	}

	private static ArrayList<FCpacket> splitDataPacket(byte[] arr) {
		ArrayList<FCpacket> result = new ArrayList<>();
		FCpacket newPacket;
		byte[] newPart;
		int crtIndexEnd = MAX_BYTES_PER_DATAPACKET, crtIndexStart = 0;
		int arrLength = arr.length, newLength = 0, crtLength;
		long nextSeqNum = 1;

		while (crtIndexStart < arrLength) {
			if (crtIndexEnd > arrLength) {
				crtIndexEnd = arrLength;
			}
			crtLength = crtIndexEnd - crtIndexStart;

			newPart = Arrays.copyOfRange(arr, crtIndexStart, crtIndexEnd);
			newPacket = new FCpacket(nextSeqNum, newPart, crtLength);
			result.add(newPacket);

			newLength += crtLength;
			nextSeqNum++;
			crtIndexStart += MAX_BYTES_PER_DATAPACKET;
			crtIndexEnd += MAX_BYTES_PER_DATAPACKET;
		}
		if (arr.length == newLength) {
			FileCopyClient.testOut("DataPackets (" + newLength + " Byte) und altes Array (" + arr.length
					+ " Byte) haben gleiche länge!");
		} else {
			FileCopyClient.testOut("DataPackets (" + newLength + " Byte) und altes Array (" + arr.length
					+ " Byte) haben UNGLEICHE länge!");
		}
		return result;
	}

}
