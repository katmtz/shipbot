package shipbot.staticlib;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;

/**
 * Interface for interacting with device data files. 
 * 
 * @author kat
 *
 */
public class DeviceData {

	private static int buffer_size = 1024;
	private static String sensor_path_format = "devices/sensors/%s.txt";
	private static String motor_path_format = "devices/actuators/%s.txt";
	
	public static Map<String, Double> getSensorData(String sensor_id) {
		String sensor_path = String.format(sensor_path_format, sensor_id);
		try {
			// check data file for new info!
			Reader reader = new FileReader(sensor_path);
			char[] buffer = new char[buffer_size];
			reader.read(buffer, 0, buffer_size);
			String data = String.valueOf(buffer);
			reader.close();
		} catch (IOException e) {
			MessageLog.printError("SENSOR_UPDATE", "IOException while updating sensor data.");
		}
		return null;
	}
	
	public static Map<String, Integer> getMotorData(String motor_id, String[] keys) {
		String motor_path = String.format(motor_path_format, motor_id);
		try {
			// check data file for new info!
			Reader reader = new FileReader(motor_path);
			char[] buffer = new char[buffer_size];
			reader.read(buffer, 0, buffer_size);
			String data = String.valueOf(buffer);
			reader.close();
		} catch (IOException e) {
			MessageLog.printError("MOTOR_UPDATE", "IOException while updating motor data.");
		}
		return null;
	}
	
	public static void writeMotorData(String motor_id, String[] keys) {
		return;
	}
}
