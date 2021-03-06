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
	
	// HEBI comm strings
	private static String HEBI_GO = "0";
	private static String HEBI_STOP = "1";
	private static String HEBI_CMD = "@ 1\n%s\ns %d\ne %d\nh %d\n";
	
	// Arduino comm strings
	private static String UNINITIALIZED_MSG = "@ 1\nNO DATA\n";
	private static String KILL_MSG = "@ 1\nSTOP";

	/**
	 * Writes mapped data to specified motor's data file, setting the file owner to be the Pi. 
	 * @param motor_id - the motor whose data should be modified
	 * @param data - the field,value map data to write out
	 */
	public static void writeArduinoData(String motor_id, Map<String, Integer> data) throws IOException {
		String motor_path = String.format(motor_path_format, motor_id);
		StringBuilder sb = new StringBuilder(String.format("@ %d\n", Config.OWNER_PI));
		for (String key : data.keySet()) {
			sb.append(key).append(" ");
			sb.append(data.get(key).toString());
			sb.append('\n');
		}
		try {
			Writer writer = new FileWriter(motor_path);
			MessageLog.logDebugMessage("ARDUINO WRITE", String.format("[debug] wrote <%s> to path <%s>", sb.toString(), motor_path));
			writer.write(sb.toString());
			writer.close();
		} catch (IOException e) {
			MessageLog.printError("MOTOR_UPDATE", "IOException while writing motor data.");
			throw e;
		}
		return;
	}
	
	/**
	 * Checks if we're still reading the last command we wrote
	 * 
	 * @param motor_id the motor to check from
	 * @return True if there's been no new updates
	 * @throws IOException
	 */
	public static boolean waiting(String motor_id) throws IOException {
		String motor_path = String.format(motor_path_format, motor_id);
		MessageLog.logDebugMessage("DEVICE WAITING", String.format("waiting on %s", motor_path));
		BufferedReader reader = new BufferedReader(new FileReader(motor_path));
		if (reader.ready()) {
			String header = reader.readLine();
			reader.close();
			if ((header != null) && header.endsWith(String.valueOf(Config.OWNER_ARDUINO))) {
				return false;
			}
		}
		return true;
	}
	
	public static void writeToHebis(boolean active, int fixed, int reach, int effector) throws IOException {
		String hebi_path = String.format(motor_path_format, Config.HEBI_ID);
		String command;
		if (active) {
			command = String.format(DeviceData.HEBI_CMD, DeviceData.HEBI_GO, fixed, reach, effector);
		} else {
			command = String.format(DeviceData.HEBI_CMD, DeviceData.HEBI_STOP, fixed, reach, effector);
		}
		try {
			System.out.println(String.format("[debug] wrote <%s> to path <%s>", command, hebi_path));
			Writer writer = new FileWriter(hebi_path);
			writer.write(command);
			writer.close();
		} catch (IOException e) {
			MessageLog.printError("MOTOR_UPDATE", "IOException while writing hebi data.");
			throw e;
		}
		return;
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
