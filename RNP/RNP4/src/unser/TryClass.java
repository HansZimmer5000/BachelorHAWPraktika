package unser;

import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Zu Versuchszwecken!
 * @author Michael
 *
 */
public class TryClass {

	public static void main(String[] args) {
		RoutingTable table = new RoutingTable(Paths.get("RoutingTable1.txt"));
		
		Inet6Address ip1, ip3;
		
		try {
			ip1 = (Inet6Address) Inet6Address.getByName("2001:db8:0001:ffff:ffff:ffff:ffff:ffff");
			ip3 = (Inet6Address) Inet6Address.getByName("2001:db8:0005:ffff:ffff:ffff:ffff:ffff");
			int suffix1 = 48, suffix2 = suffix1;
			
			System.out.println("=" + printByte(ip1));
			System.out.println(">" + printByte(ip3));
			
			System.out.println(isWithin(ip1,ip3));
		} catch (UnknownHostException e) {}
	}
	
	static String printByte(Inet6Address ip){
		String s="";
		for(int i = 0; i < ip.getAddress().length; i++){
			if(Math.floorMod(i, 2) == 0 && i != 0){
				s +=" : ";
			}
			s += "."+ip.getAddress()[i];
		}
		return s;
	}
	
	static boolean isWithin(Inet6Address ip1, Inet6Address ip2){

		
		byte[] b1 = ip1.getAddress(), b2 = ip2.getAddress();
		int firstDiff = firstDifferent(b1, b2);
		
		byte[] nb1 = Arrays.copyOfRange(b1, firstDiff - 1, b1.length);
		byte[] nb2 = Arrays.copyOfRange(b2, firstDiff - 1, b2.length);
		
		System.out.println(""+ b1.length + " " + nb1.length + " " + nb1[0]);
		System.out.println(""+ b2.length + " " + nb2.length + " " + nb2[0]);
		
		long res1 = byteArrSum(nb1);
		long res2 = byteArrSum(nb2);
		
		System.out.println(res1);
		System.out.println(res2);
		
		return res1 > res2;
	}
	
	static long byteArrSum(byte[] arr){
		String hexChar;
		long sum = 1, tmpRes;
		
		for (byte b : arr) {
			hexChar = String.format("%02X", b);
			tmpRes = Integer.parseInt(hexChar, 16);
			tmpRes = Long.parseLong(hexChar, 16);
			
			if(tmpRes > 0){
				sum *= tmpRes;
			}
		}
		if(sum < 0){
			//sum = Long.MAX_VALUE;
		}
		return sum;
	}
	
	static int firstDifferent(byte[] b1, byte[] b2){
		
		int maxLength = Math.min(b1.length, b2.length);
		
		for(int i = 0; i < maxLength ; i++){
			if(b1[i]!=b2[i]){
				return i;
			}
		}
		
		return Integer.MAX_VALUE;
	}
}
