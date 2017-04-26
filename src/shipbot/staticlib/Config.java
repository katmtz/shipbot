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
	public static boolean USE_CV = false;
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

	/* Length of arm */
	public static final int ROTATOR_LENGTH = 152;

	public static final int ORIENT_UP = 0;
	public static final int ORIENT_SIDE = 1;
	
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
	public static int CLEARANCE = 30;
	
	/* State that motors should be in before moving on */
	public static int Y_TRAVELLING = 270;
	public static int Z_TRAVELLING = 20;
	
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
	
	// { hebi_rot, hebi_eff, H }
	public static int[] getAnglesAndOffset(int horiz_offset) {
		double theta = Math.asin((double) horiz_offset / Config.ROTATOR_LENGTH);
		double height = Config.ROTATOR_LENGTH * (1 - Math.cos(theta));
		//System.out.println(String.format("horiz was %d theta was %f height was %f", horiz_offset, theta, height));
		int[] retval = {  (int) theta, (int) (theta * -1), (int) height };
		return retval;
	}
}
