package shipbot.hardware;

import java.util.HashMap;
import java.util.Map;

public class CVSensing {

	// CV OBJECT TYPES
	public static String NONE = "n";
	public static String VALVE = "v";
	public static String BREAKER = "b";
	public static String SHUTTLECOCK = "s";
	
	// Data keys
	private String DEVICE_TYPE = "device";
	private String DEVICE_SEEN = "seen";
	private String X_OFFSET = "x";
	private String Y_OFFSET = "y";
	private String ORIENTATION = "r";
	private String ANGLE = "theta";
	
	// Data storage map
	private Map<String, String> data;
	
	public CVSensing() {
		this.data = new HashMap<String,String>();
		this.data.put(this.DEVICE_TYPE, CVSensing.NONE);
		this.data.put(this.DEVICE_SEEN, "0");
		this.data.put(this.X_OFFSET, "0");
		this.data.put(this.Y_OFFSET, "0");
		this.data.put(this.ANGLE, "0");
		this.data.put(this.ORIENTATION, "0");
	}
	
}
