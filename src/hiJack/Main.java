package hiJack;
import java.util.HashSet;

public class Main {
	public static void main(String[] args) {

		String target = getArgs(args);
		System.out.println("Starting");
	
		HashSet<String> subdomainSet = SubdomainDork.run(target);
		System.out.println("Looking for cnames in "+subdomainSet.size()+" subdomains");
		System.out.println(subdomainSet.toString());
		System.out.println("");
		
		HiJack.searchForCNamesHijacks(subdomainSet);
		
		System.out.println("DONE");
	}

	private static String getArgs(String[] args) {
		if (args.length > 0) {
		    try {
		    	String val = String.valueOf(args[0]);
		    	if(val.startsWith("www.")){
		    		System.out.println("Removing www. of command line argument ...");
		    		val = val.substring(4);
		    	}
		    	if(val.toLowerCase().contains("http://") || val.toLowerCase().contains("https://")){
		    		throw new Exception();
		    	}
		    	return val;
		    } catch (Exception e) {
		    }
		}
		System.err.println("specify URL as first command line argument(example: oz-web.com)");
        System.exit(1);
		return null;
	}

	
}
