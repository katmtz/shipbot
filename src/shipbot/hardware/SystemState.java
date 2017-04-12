package shipbot.hardware;

import java.util.List;

import shipbot.staticlib.Config;

/** 
 * Represents the full system's current status at a high level, including
 * - location (position on the testbed)
 * - depth (y-axis stepper motion)
 * - height (z-axis stepper motion)
 * - rotation (fixed HEBI motor angular position)
 * - spin (end effector HEBI motor angular position)
 * 
 * Initiates reads and writes to device data files.
 * 
 * @author Kat Martinez
 *
 */
public class SystemState {
	
	// Devices
	private Motor drive;
	private Motor stepper_y;
	private Motor stepper_z;
	private Motor hebi_fixed;
	private Motor hebi_effector;
	
	public SystemState() {
		// Initialize sensors and actuators
		stepper_z = new StepperMotor(Config.Z_STEPPER_ID);
		stepper_y = new StepperMotor(Config.Y_STEPPER_ID);
		drive = new DriveMotor(Config.DRIVE_MOTOR_ID);
		hebi_fixed = new HebiMotor(Config.FIXED_HEBI_ID);
		hebi_effector = new HebiMotor(Config.EFFECTOR_HEBI_ID);
	}
	
	/**
	 * Gets X position relative to testbed.
	 * 
	 * @return X position
	 */
	public int getXPosition() {
		return drive.get(DriveMotor.X);
	}
	
	/**
	 * Gets Y position relative to testbed.
	 * 
	 * @return Y position
	 */
	public int getYPosition() {
		return drive.get(DriveMotor.Y);
	}
	
	/**
	 * Get Y-axis depth of upper assembly.
	 * 
	 * @return
	 */
	public int getDepth() {
		return stepper_y.get(StepperMotor.POS);
	}
	
	/**
	 * Get Z-axis height of upper assembly.
	 * 
	 * @return
	 */
	public int getHeight() {
		return stepper_z.get(StepperMotor.POS);
	}
	
	public int getRotation() {
		return hebi_fixed.get(HebiMotor.POS);
	}
	
	public int getSpin() {
		return hebi_effector.get(HebiMotor.POS);
	}
	
	/* UPDATE METHODS */
	
	public void updateLocation(int x, int y) {
		this.drive.set(DriveMotor.X, x);
		this.drive.set(DriveMotor.Y, y);
	}
	
	public void updateDepth(int depth) {
		this.stepper_y.set(StepperMotor.POS, depth);
	}
	
	public void updateHeight(int height) {
		this.stepper_z.set(StepperMotor.POS, height);
	}
}