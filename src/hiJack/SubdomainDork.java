package hiJack;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class SubdomainDork {
	/**
	 * @param target url such as test.ch
	 * @return set of subdomains that have at one point a https cert 
	 */
	public static HashSet<String> runCRTSH(String target){
		HashSet<String> subdomainSet = new HashSet<String>();

		try {
			String html = HTTP.get("https://crt.sh/?q=%25."+target);
			Document doc = Jsoup.parse(html);
			Elements elements = doc.select("td");
			boolean first = true;
			for (org.jsoup.nodes.Element element : elements) {
				if(!element.toString().contains("style=") && !first){
					subdomainSet.add(element.html());
				}
				first=false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return subdomainSet;
	}
	
	/**
	 * @param target url such as test.ch
	 * @param dnsIP 
	 * @return set of subdomains received through zone transfer
	 */
	public static HashSet<String> runAXFR(String target, String dnsIP){
		HashSet<String> subdomainSet = new HashSet<String>();
		try {
			HashSet<String> NSSet = getNSOfTarget(target,dnsIP);
			System.out.println("Testing for AXFR transfer with "+NSSet.toString());
			dnsIP = (dnsIP==null)?"":" @"+dnsIP;
			boolean allFailed=true;
			for (String NSIP : NSSet) {
				boolean failed=false;
				Process extProc = Runtime.getRuntime().exec("dig AXFR " + target+" @"+NSIP);
				extProc.waitFor();
				InputStream theInputStream = extProc.getInputStream();
				Scanner scanner = new java.util.Scanner(theInputStream);
				
				java.util.Scanner theScanner = scanner.useDelimiter("\\A");
				if (theScanner.hasNext()) {
					String digResult = scanner.next();
					if(digResult.contains("Transfer failed.") || digResult.contains("connection refused") || digResult.contains("connection timed out") || digResult.contains("network unreachable")){
						failed=true;
					}
				}
				if(!failed){
					System.out.println("AXFR transfer success with "+NSIP+"! TODO implement intel gained here");
					allFailed=false;
					// TODO implement logic for getting transfer data
				}
				scanner.close();
			}
			if(allFailed){
				System.out.println("All AXFR transfers failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return subdomainSet;
	}

	private static HashSet<String> getNSOfTarget(String target, String dnsIP){
		HashSet<String> nsSet = new HashSet<String>();
		try {
			dnsIP = (dnsIP==null)?"":" @"+dnsIP;
			Process extProc = Runtime.getRuntime().exec("dig " + target+dnsIP);
			extProc.waitFor();
			InputStream theInputStream = extProc.getInputStream();
			Scanner scanner = new java.util.Scanner(theInputStream);
			
			java.util.Scanner theScanner = scanner.useDelimiter("\\A");
			if (theScanner.hasNext()) {
				String digResult = scanner.next();
				String[] digLines = digResult.split("\n");
				for (String digLine : digLines) {
					if (digLine.contains("	NS")) {
						int toStart = digLine.indexOf("NS") + "NS".length()+1;
						String to = digLine.substring(toStart);
						to=to.substring(0, to.length()-1);
						nsSet.add(to);
					}
				}
			}
			scanner.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return nsSet;
	}

	
}
