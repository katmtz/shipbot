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
	
	// ORIENTATION
	public static final int FRONT_FACING = 1;
	public static final int SIDE_FACING = 0;
	
	// TIMEOUT LIMITS (arbitrary!!)
	public static int MAX_TIMEOUT = 2000;
	public static int SLEEPTIME = 10;
	
	/**
	 * Static height of device centers on the testbed
	 * set at 1.5' in cm.
	 */
	public static int DEVICE_HEIGHT = 458;
	
	/**
	 * depth of hebi arm when fixed hebi @ 0º 
	 * TEMPORARILY set at 3in in mm
	 */
	public static int ARM_DEPTH_VERTICAL = 76;
	
	/**
	 * depth of hebi arm when fixed hebi @ -90º
	 * TEMPORARILY set at 4in in mm
	 */
	public static int ARM_DEPTH_HORIZONTAL = 101;
	
	/**
	 * length of hebi reach offset
	 * TEMPORARILY set at 6in in mm
	 */
	public static int REACH_OFFSET = 152;
	
	/**
	 * Returns an array of all the motor ids
	 * @return
	 */
	public static List<String> getAllMotorIds() {
		List<String> ids = new ArrayList<String>();
		ids.add(DRIVE_MOTOR_ID);
		ids.add(Y_STEPPER_ID);
		ids.add(Z_STEPPER_ID);
		ids.add(HEBI_ID);
		return ids;
	}
}
