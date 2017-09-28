package hiJack;
import java.util.HashSet;
import java.util.Scanner;


public class HiJack {
	
	public static void searchForCNamesHijacks(String target, HashSet<String> subdomainSet, String dnsIPP) {
		boolean found=false;
		for (String subdomain : subdomainSet) {
			try {
				String dnsIP = (dnsIPP==null)?"":" @"+dnsIPP;
				Scanner scanner = ProcessToScanner.run("dig " + subdomain+dnsIP);
				
				if (scanner.hasNext()) {
					String digResult = scanner.next();
					if (digResult.contains("CNAME")) {
						String[] digLines = digResult.split("\n");
						for (String digLine : digLines) {
							if (digLine.contains("CNAME")) {
								int fromEnd = digLine.indexOf(" ") != -1 ? digLine
										.indexOf(" ") : digLine.indexOf("	");
								String from = digLine.substring(0, fromEnd);
								int toStart = digLine.indexOf("CNAME") + "CNAME".length()+1;
								String to = digLine.substring(toStart);
								if(!isURLRegistered(to)){
									String potential = to.endsWith(target+".")?"potential":"actual";
									System.out.println("Found "+potential+" hijack: "+from + " CNAME " + to);
									found=true;
								}
							}
						}
					}
				}
				scanner.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(!found){
			System.out.println("Found no hijack possibilities ...");
		}
	}

	/**
	 * @param to url to be checked if available to register
	 * @return 
	 */
	public static boolean isURLRegistered(String to) {
		try {
			Scanner scanner = ProcessToScanner.run("nslookup " + to);
			if (scanner.hasNext()) {
				String theReadBuffer = scanner.next();
				// jackpot
				if(theReadBuffer.contains("** server can't find")){
					scanner.close();
					return false;
				}
			}
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
