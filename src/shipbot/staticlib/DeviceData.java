package shipbot.staticlib;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface for interacting with device data files. 
 * 
 * @author kat
 *
 */
public class DeviceData {

	private static String motor_path_format = "devices/actuators/%s.txt";	
	private static String UNINITIALIZED_MSG = "@ 1\nNO DATA\n";
	private static String KILL_MSG = "STOP";
	
	/**
	 * Test sensor and motor data functions. 
	 * Does nothing if Config.DEBUG is false.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (!Config.DEBUG) {
			return;
		}
		
		// MOTOR READ TEST
		String id = "DRIVE_0";
		Map<String, Integer> motor_data = DeviceData.getMotorData(id);
		for (String key : motor_data.keySet()) {
			if (key != "owner") {
				System.out.print(key);
				System.out.print(": ");
				System.out.println(motor_data.get(key).toString());
			} else {
				System.out.print("Owner is ");
				System.out.println(motor_data.get(key).toString());
			}
		}
		
		// MOTOR WRITE TEST
		motor_data.put("modified", 490);
		motor_data.remove("owner");
		try {
			DeviceData.writeMotorData(id, motor_data);
		} catch (Exception e) {
			System.out.println("lol what");
		}
	}
	
	/**
	 * Tokenizes the motor data file, reads fields and stores data as a map
	 * of field -> (int) value. Be sure to check that the owner is Arduino
	 * (which is to say, the last writer was the Arduino) before storing values
	 * in system state. 
	 * 
	 * @param motor_id
	 * @return
	 */
	public static Map<String, Integer> getMotorData(String motor_id) {
		String motor_path = String.format(motor_path_format, motor_id);
		Map<String, Integer> data = new HashMap<String, Integer>();
		try {
			// TODO: lock motor file before reading!
			Reader reader = new FileReader(motor_path);
			StreamTokenizer tok = new StreamTokenizer(reader);
			tok.parseNumbers();
			
			boolean eof = false;
			String field = "";
			while (!eof) {
				int token = tok.nextToken();
				switch (token) {
					case StreamTokenizer.TT_EOF:
						eof = true;
						break;
					case StreamTokenizer.TT_WORD:
						field = tok.sval;
						break;
					case StreamTokenizer.TT_NUMBER:
						data.put(field, (int) tok.nval);
						break;
				}
			}
			reader.close();
			if (data.get("@") != Config.OWNER_ARDUINO) {
				MessageLog.printError("MOTOR_UPDATE", "Read old data! Dumping.");
			}
		} catch (IOException e) {
			MessageLog.printError("MOTOR_UPDATE", "IOException while updating motor data.");
		}
		return data;
	}
	
	/**
	 * Writes mapped data to specified motor's data file, setting the file owner to be the Pi. 
	 * @param motor_id - the motor whose data should be modified
	 * @param data - the field,value map data to write out
	 */
	public static void writeMotorData(String motor_id, Map<String, Integer> data) throws IOException {
		String motor_path = String.format(motor_path_format, motor_id);
		StringBuilder sb = new StringBuilder(String.format("@ %d\n", Config.OWNER_PI));
		for (String key : data.keySet()) {
			sb.append(key).append(" ");
			sb.append(data.get(key).toString());
			sb.append('\n');
		}
		try {
			Writer writer = new FileWriter(motor_path);
			// System.out.println(String.format("WROTE {%s} to device data", sb.toString()));
			writer.write(sb.toString());
			writer.close();
		} catch (IOException e) {
			MessageLog.printError("MOTOR_UPDATE", "IOException while writing motor data.");
			throw e;
		}
		return;
	}
	
	public static boolean waiting(String motor_id) throws IOException {
		String motor_path = String.format(motor_path_format, motor_id);
		BufferedReader reader = new BufferedReader(new FileReader(motor_path));
		if (reader.ready()) {
			String header = reader.readLine();
			reader.close();
			if ((header != null) && header.endsWith(String.valueOf(Config.OWNER_ARDUINO))) {
				System.out.println("FOUND NEW DATA");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Utility function to clear device data files before
	 * starting system. 
	 * 
	 * @throws IOException
	 */
	public static void clear_data() throws IOException {
		// Clear motor files.
		for (String motor_id : Config.getAllMotorIds()) {
			String motor_path = String.format(motor_path_format, motor_id);
			try {
				Writer writer = new FileWriter(motor_path);
				writer.write(UNINITIALIZED_MSG);
				writer.close();
			} catch (IOException e) {
				MessageLog.printError("DATA_INIT", "IOException while clearing motor data.");
				throw e;
			}
		}
		
		// Clear opencv file
		String cv_path = "devices/CV.txt";
		try {
			Writer writer = new FileWriter(cv_path);
			writer.write(UNINITIALIZED_MSG);
			writer.close();
		} catch (IOException e) {
			MessageLog.printError("DATA_INIT", "IOException while clearing CV data.");
			throw e;
		}
	}
	
	public static void send_kill() {
		for (String motor_id : Config.getAllMotorIds()) {
			String motor_path = String.format(motor_path_format, motor_id);
			try {
				Writer writer = new FileWriter(motor_path);
				writer.write(KILL_MSG);
				writer.close();
			} catch (IOException e) {
				MessageLog.printError("DATA_CLEANUP", "IOException while clearing motor data.");
			}
		}
		
		// Clear opencv file
		String cv_path = "devices/CV.txt";
		try {
			Writer writer = new FileWriter(cv_path);
			writer.write(KILL_MSG);
			writer.close();
		} catch (IOException e) {
			MessageLog.printError("DATA_CLEANUP", "IOException while clearing CV data.");
		}
	}
}
