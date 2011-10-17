package wei.mark.pingpongboss.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.UUID;

import android.content.Context;
import android.os.Environment;

public class FileUtils {
	private static String sID = null;
	private static final String INSTALLATION = "INSTALLATION";
	public static final String HISTORY = "pingpongboss.history";

	public synchronized static String id(Context context) {
		if (sID == null) {
			File installation = new File(context.getFilesDir(), INSTALLATION);
			try {
				if (!installation.exists())
					writeInstallationFile(installation);
				sID = readInstallationFile(installation);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return sID;
	}

	private static String readInstallationFile(File installation)
			throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}

	private static void writeInstallationFile(File installation)
			throws IOException {
		FileOutputStream out = new FileOutputStream(installation);
		String id = UUID.randomUUID().toString();
		out.write(id.getBytes());
		out.close();
	}

	public synchronized static boolean exportHistory(ArrayList<String> history) {
		if (history == null) return false;
		try {
			File sdcard = Environment.getExternalStorageDirectory();
			File file = new File(sdcard, FileUtils.HISTORY);
			
			if (file.exists()) file.delete();
			file.createNewFile();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			for (String query : history) {
				writer.write(query);
				writer.newLine();
			}
			
			writer.close();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public synchronized static ArrayList<String> importHistory() {
		try {
			File sdcard = Environment.getExternalStorageDirectory();
			File file = new File(sdcard, FileUtils.HISTORY);
			
			if (!file.exists()) return null;
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			ArrayList<String> history = new ArrayList<String>();
			String query;
			while ((query = reader.readLine()) != null) {
				history.add(query);
			}
			
			reader.close();
			
			return history;
		} catch (IOException e) {
			e.printStackTrace();
			
			return null;
		}
	}
}