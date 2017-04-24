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
	
	// TIMEOUT LIMITS (arbitrary!!)
	public static int MAX_TIMEOUT = 2000;
	public static int SLEEPTIME = 10;
	
	// BASE OFFSET THRESHOLD (the min distance we adjust with base motion)
	public static int OFFSET_THRESHOLD = 20;
	
	/* DEVICE & TESTBED CONSTANTS */
	
	/**
	 * Static height & depth of device centers on the testbed
	 * set at 1.5' in cm.
	 */
	public static int DEVICE_HEIGHT = 200;
	public static int DEVICE_DEPTH = 200;
	
	/**
	 * hebi arm depth offset for fixed hebi positions
	 * TEMPORARILY set at 3in in mm for downward &
	 * 4in in mm for outward
	 */
	public static int ARM_DEPTH_DOWN = 4;
	public static int ARM_DEPTH_FRONT = 7;
	
	/** 
	 * hebi arm height offset
	 * TEMPORARILY set at 0mm for downward &
	 * 25mm for horizontal
	 */
	public static int ARM_HEIGHT_DOWN = 0;
	public static int ARM_HEIGHT_FRONT = -25;
	
	/**
	 * length of hebi reach offset
	 * TEMPORARILY set at 6in in mm
	 */
	public static int REACH_OFFSET = 50;
	
	/**
	 * Needed clearance to access testbed devices.
	 * Should be length of "fingers" + extra space
	 * TEMPORARILY set at 2in in mm
	 */
	public static int CLEARANCE = 51;
	
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
