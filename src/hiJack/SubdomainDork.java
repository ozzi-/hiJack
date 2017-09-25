package hiJack;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class SubdomainDork {
	/**
	 * @param target url such as test.ch
	 * @return set of subdomains that have at one point a https cert 
	 */
	public static HashSet<String> run(String target){
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
}
