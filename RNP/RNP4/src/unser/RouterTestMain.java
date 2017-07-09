package unser;

import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.nio.file.Paths;

public class RouterTestMain {

	public static void main(String[] args) {
		
		Inet6Address localHostIp6Address;
		try {
			localHostIp6Address = (Inet6Address) Inet6Address.getByName("fe80:0:0:0:c233:5eff:feee:567f");
			System.out.println("Router gestartet mit IpAdresse: "+ localHostIp6Address);
			Router router = new Router(8000, Paths.get("RoutingTableMacToWindows.txt"), localHostIp6Address);
			router.run();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}
