package hiJack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {

		String target = getTargetArg(args);
		String listPath = getListArg(args);
		String dnsIP = getDNSIPArg(args);
		
		System.out.println("Dorking subdomains for "+target);
		HashSet<String> subdomainSet = SubdomainDork.runCRTSH(target);
		System.out.println(subdomainSet.size()+ " subdomains found via crt.sh dork");
		System.out.println("");
		
		int dorkSDCount = subdomainSet.size();
		subdomainSet = loadList(listPath, subdomainSet, dorkSDCount);
		System.out.println("");
		SubdomainDork.runAXFR(target,dnsIP);
		
		
		System.out.println(subdomainSet.size()+" total number of subdomains that will be checked");
		System.out.println(subdomainSet.toString());
		System.out.println("");

		HiJack.searchForCNamesHijacks(target,subdomainSet,dnsIP);

		System.out.println("Done");
	}

	private static HashSet<String> loadList(String listPath, HashSet<String> subdomainSet, int dorkSDCount) {
		if (listPath != null) {
			int lPC = 0;
			Scanner s;
			try {
				s = new Scanner(new File(listPath));
				while (s.hasNext()) {
					subdomainSet.add(s.next());
					lPC++;
				}
				s.close();
			} catch (FileNotFoundException e) {
				System.err.println("Could not load list file: "+e.getMessage());
				System.out.println("");
			}
			System.out.println(lPC+" subdomains provided via list " + listPath
					+ ", effectively added: "
					+ (subdomainSet.size() - dorkSDCount));
		}
		return subdomainSet;
	}

	private static String getTargetArg(String[] args) {
		if (args.length > 0) {
			try {
				String val = String.valueOf(args[0]);
				if (val.startsWith("www.")) {
					System.out
							.println("Removing www. of command line argument ...");
					val = val.substring(4);
				}
				if (val.toLowerCase().contains("http://")
						|| val.toLowerCase().contains("https://")) {
					throw new Exception();
				}
				return val;
			} catch (Exception e) {
			}
		}
		System.err
				.println("specify URL as first command line argument(example: oz-web.com)");
		System.err.println("second parameter is a url list file (/home/ozzi-/list)");
		System.err.println("third parameter sets a specific dns resolver (192.168.1.15)");
		System.exit(1);
		return null;
	}

	private static String getListArg(String[] args) {
		if (args.length > 1) {
			try {
				String val = String.valueOf(args[1]);
				return val;
			} catch (Exception e) {
				System.err.println("i don't understand your list arg");
				e.printStackTrace();
			}
		}
		return null;
	}

	private static String getDNSIPArg(String[] args) {
		if (args.length > 2) {
			try {
				String val = String.valueOf(args[2]);
				return val;
			} catch (Exception e) {
				System.err.println("i don't understand your DNS ip");
				e.printStackTrace();
			}
		}
		return null;
	}
}
