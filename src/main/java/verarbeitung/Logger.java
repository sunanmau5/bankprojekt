package verarbeitung;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

public class Logger implements Serializable {

	private static final long serialVersionUID = 1001L;

	private Logger() {
	}

	private static Logger log = null;

	public static Logger getInstance() {
		if (log == null) {
			log = new Logger();
		}
		return log;
	}

	public void logException(String message) {
		File fileName = new File("logDatei.txt");

		try {
			// If file already exists will do nothing
			fileName.createNewFile();

			StringBuilder sb = new StringBuilder();
			sb.append("- - - - - - - - - - - - - - - -");
			sb.append(System.lineSeparator());
			sb.append(new Date());
			sb.append(System.lineSeparator());
			sb.append(message);
			sb.append(System.lineSeparator());

			// Open given file in append mode.
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
			out.write(sb.toString());
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
