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
	public static String FIXED_HEBI_ID = "HEBI_0";
	public static String EFFECTOR_HEBI_ID = "HEBI_1";
	
	// CODES
	public static final Integer OWNER_ARDUINO = 0;
	public static final Integer OWNER_PI = 1;
	
	// TIMEOUT (arbitrary!!)
	public static int MAX_TIMEOUT = 1000;
	public static int SLEEPTIME = 10;
	
	/**
	 * Returns an array of all the motor ids
	 * TODO: make this not gross?? 
	 * @return
	 */
	public static List<String> getAllMotorIds() {
		List<String> ids = new ArrayList<String>();
		ids.add(DRIVE_MOTOR_ID);
		ids.add(Y_STEPPER_ID);
		ids.add(Z_STEPPER_ID);
		ids.add(FIXED_HEBI_ID);
		ids.add(EFFECTOR_HEBI_ID);
		return ids;
	}
}
