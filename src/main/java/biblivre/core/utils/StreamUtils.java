package biblivre.core.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;

public class StreamUtils {
	public static void cleanUp(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void cleanUp(BufferedWriter bw) {
		if (bw != null) {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
