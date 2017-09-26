package hiJack;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {

		String target = getTargetArg(args);
		String listPath = getListArg(args);
		
		System.out.println("Starting");
	
		HashSet<String> subdomainSet = SubdomainDork.run(target);
		int dorkSDCount = subdomainSet.size();
		if(listPath!=null){
			int lPC=0;
			Scanner s;
			try {
				s = new Scanner(new File(listPath));
				while (s.hasNext()){
					subdomainSet.add(s.next());
					lPC++;
				}
				s.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			System.out.println("Added "+lPC+" subdomains from "+listPath+", effectively added ("+(subdomainSet.size()-dorkSDCount)+")");
		}
		System.out.println("Looking for cnames in "+subdomainSet.size()+" subdomains");
		System.out.println(subdomainSet.toString());
		System.out.println("");
		
		HiJack.searchForCNamesHijacks(subdomainSet);
		
		System.out.println("Done");
	}

	private static String getTargetArg(String[] args) {
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
	
	private static String getListArg(String[] args) {
		if (args.length > 1) {
		    try {
		    	String val = String.valueOf(args[1]);
		    	return val;
		    } catch (Exception e) {
		    }
		}
		return null;
	}
}
