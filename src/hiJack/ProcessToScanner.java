package hiJack;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ProcessToScanner {
	public static Scanner run(String cmd) throws IOException, InterruptedException {
		java.lang.Process extProc = Runtime.getRuntime().exec(cmd);
		extProc.waitFor();
		InputStream theInputStream = extProc.getInputStream();
		@SuppressWarnings("resource")
		Scanner scanner = new java.util.Scanner(theInputStream);
		Scanner theScanner = scanner.useDelimiter("\\A");
		return theScanner;
	}
}
