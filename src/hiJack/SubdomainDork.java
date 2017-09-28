package hiJack;
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
	public static HashSet<String> runAXFR(String target, String dnsIPP){
		HashSet<String> subdomainSet = new HashSet<String>();
		try {
			HashSet<String> NSSet = getNSOfTarget(target,dnsIPP);
			System.out.println("Testing for AXFR transfer with "+NSSet.toString());
			boolean allFailed=true;
			
			for (String NSIP : NSSet) {
				boolean failed=false;
				Scanner scanner = ProcessToScanner.run("dig AXFR " + target+" @"+NSIP);
				if (scanner.hasNext()) {
					String digResult = scanner.next();
					failed = axfrDigFailed(digResult);
				}
				if(!failed){
					// TODO implement logic for getting transfer data
					System.out.println("AXFR transfer success with "+NSIP+"! TODO implement intel gained here");
					allFailed=false;
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
			Scanner scanner = ProcessToScanner.run("dig " + target+dnsIP);
			if (scanner.hasNext()) {
				String digResult = scanner.next();
				String[] digLines = digResult.split("\n");
				for (String digLine : digLines) {
					if(isActuallyNSLine(digLine)) {
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
	
	private static boolean isActuallyNSLine(String digLine){
		return digLine.indexOf("NS")>5 && digLine.indexOf("NS")<digLine.length()-4 && Character.isWhitespace(digLine.substring(digLine.indexOf("NS")-1,digLine.indexOf("NS")).charAt(0));
	}
	
	private static boolean axfrDigFailed(String digResult){
		return (digResult.contains("Transfer failed.") || digResult.contains("connection refused") || digResult.contains("connection timed out") || digResult.contains("network unreachable"));
	}
}
