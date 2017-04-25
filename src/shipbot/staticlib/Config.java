package shipbot.staticlib;

import java.util.ArrayList;
import java.util.List;

/**
 * System settings and ID specifications.
 * 
 * @author kat
 *
 */
public class Config {
	
	// FUNCTIONAL FLAGS
	public static boolean DEBUG = true;
	
	// DEVICE IDs
	public static String DRIVE_MOTOR_ID = "DRIVE_0";
	public static String Y_STEPPER_ID = "STEP_0";
	public static String Z_STEPPER_ID = "STEP_1";
	public static String HEBI_ID = "HEBI";
	
	// OWNER HEADER CODES
	public static final Integer OWNER_ARDUINO = 0;
	public static final Integer OWNER_PI = 1;
	
	// ROBOT ORIENTATION
	public static final int FRONT_FACING = 1;
	public static final int SIDE_FACING = 0;

	public static final int DEVICE_DEPTH = 150;
	public static final int DEVICE_HEIGHT = 150;
	
	// TIMEOUT LIMITS (arbitrary!!)
	public static int MAX_TIMEOUT = 2000;
	public static int SLEEPTIME = 10;
	
	// BASE OFFSET THRESHOLD (the min distance we adjust with base motion)
	public static int OFFSET_THRESHOLD = 20;
	
	/* DEVICE & TESTBED CONSTANTS */
	
	/**
	 * Needed clearance to access testbed devices.
	 * Should be length of "fingers" + extra space
	 */
	public static int CLEARANCE = 51;
	
	/* State that motors should be in before moving on */
	public static int Y_TRAVELLING = 270;
	public static int Z_TRAVELLING = 10;
	
	
	
	/**
	 * Returns an array of all the motor ids
	 * @return
	 */
	public static List<String> getAllMotorIds() {
		List<String> ids = new ArrayList<String>();
		ids.add(DRIVE_MOTOR_ID);
		ids.add(Y_STEPPER_ID);
		ids.add(Z_STEPPER_ID);
		return ids;
	}
}
