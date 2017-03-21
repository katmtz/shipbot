package shipbot.staticlib;

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
	public static String IR_SENSOR_ID = "IR_0";
	public static String DRIVE_MOTOR_ID = "DRIVE_0";
	public static String Y_STEPPER_ID = "STEP_0";
	public static String Z_STEPPER_ID = "STEP_1";
	public static String FIXED_HEBI_ID = "HEBI_0";
	public static String EFFECTOR_HEBI_ID = "HEBI_1";
}
