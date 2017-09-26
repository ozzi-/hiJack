package hiJack;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;


public class HiJack {
	
	public static void searchForCNamesHijacks(HashSet<String> subdomainSet, String dnsIP) {
		boolean found=false;
		for (String string : subdomainSet) {
			try {
				dnsIP = (dnsIP==null)?"":" @"+dnsIP;
				Process extProc = Runtime.getRuntime().exec("dig " + string+dnsIP);
				extProc.waitFor();

				InputStream theInputStream = extProc.getInputStream();
				Scanner scannerNoDelimiter = new java.util.Scanner(theInputStream);
				Scanner scanner = scannerNoDelimiter.useDelimiter("\\A");
				
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
								if(isURLRegistered(to)){
									System.out.println("Found potential hijack: "+from + " CNAME " + to);
									found=true;
								}
							}
						}
					}
				}
				scannerNoDelimiter.close();
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
			Process extProc = Runtime.getRuntime().exec("nslookup " + to);
			extProc.waitFor();
			InputStream theInputStream = extProc.getInputStream();
			Scanner scanner = new java.util.Scanner(theInputStream);
			
			java.util.Scanner theScanner = scanner.useDelimiter("\\A");
			if (theScanner.hasNext()) {
				String theReadBuffer = theScanner.next();
				// jackpot
				if(theReadBuffer.contains("** server can't find")){
					scanner.close();
					return true;
				}
			}
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
