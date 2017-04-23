package shipbot.staticlib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Manage error messaging and status logging for the system.
 * 
 * @author kat
 *
 */
public class MessageLog {

	// Log file paths
	private static String debug_log = "logs/debug.log";
	private static String status_log = "logs/status.log";
	private static String error_log = "logs/error.log";
	
	// Format Strings
	private static String error_format = "[ %s ERROR ] (%s) %s\n";
	private static String mission_log_format = "[ MISSION LOG ] (%s) %s\n";
	private static String task_log_format = "    [ TASK LOG ] (%s) %s\n";
	private static String debug_log_format = "[ %s DEBUG ] (%s) %s\n";
	
	private static final DateFormat date_format = new SimpleDateFormat("HH:mm:ss yyyy/MM/dd");
	
	/**
	 * Print an error message to System.err.
	 * 
	 * @param component - the name of the object 
	 * @param message
	 */
	public static void printError(String component, String message) {
		String time = date_format.format(new Date());
		String error_msg = String.format(error_format, component, time, message);
		try {
			Writer log = new FileWriter(new File(error_log), true);
			log.write(error_msg);
			log.close();
		} catch (IOException e) {
			System.err.print("EXCEPTION WHILE LOGGING ERROR :: ");
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Log a mission-level status report.
	 * 
	 * @param message - the message to log
	 */
	public static void logMissionStatus(String message) {
		String time = date_format.format(new Date());
		String log_msg = String.format(mission_log_format, time, message);
		// Open mission log file, append log message
		try {
			Writer log = new FileWriter(new File(status_log), true);
			log.write(log_msg);
			log.close();
		} catch (IOException e) {
			System.err.print("ERROR LOGGING STATUS :: ");
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Log a task-level status report.
	 * 
	 * @param message - the message to log
	 */
	public static void logTaskStatus(String message) {
		// write task info to mission log
		String time = date_format.format(new Date());
		String log_msg = String.format(task_log_format, time, message);
		try {
			Writer log = new FileWriter(new File(status_log), true);
			log.write(log_msg);
			log.close();
		} catch (IOException e) {
			System.err.print("ERROR LOGGING STATUS :: ");
			System.err.println(e.getMessage());
		}
	}
	
	public static void logDebugMessage(String component, String message) {
		String time = date_format.format(new Date());
		String debug_msg = String.format(debug_log_format, component, time, message);
		try {
			Writer log = new FileWriter(new File(debug_log), true);
			log.write(debug_msg);
			log.close();
		} catch (IOException e) {
			System.err.print("ERROR LOGGING DEBUG MESSAGE :: ");
			System.err.println(e.getMessage());
		}
	}
	
	public static void clearLogs() {
		try {
			Writer status = new FileWriter(new File(status_log), false);
			status.write("");
			status.close();
			
			Writer error = new FileWriter(new File(error_log), false);
			error.write("");
			error.close();
			
			Writer debug = new FileWriter(new File(debug_log), false);
			debug.write("");
			debug.close();
		} catch (IOException e) {
			System.err.println("IO Exception while clearing logs, proceeding.");
		}
	}
}
