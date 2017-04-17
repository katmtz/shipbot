package shipbot.hardware;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import shipbot.staticlib.MessageLog;

public class CVSensing {

	// CV OBJECT TYPES
	public static String NONE = "n";
	public static String VALVE = "valve";
	public static String BREAKER = "breaker";
	public static String SHUTTLECOCK = "shuttlecock";
	
	// Data keys
	private String DEVICE_TYPE = "device";
	private String DEVICE_SEEN = "seen";
	private String X_OFFSET = "x";
	private String Y_OFFSET = "y";
	private String ORIENTATION = "r";
	private String ANGLE = "theta";
	
	// Data storage map
	private Map<String, String> data;
	
	private String path = "devices/CV.txt";
	
	public CVSensing() {
		this.data = new HashMap<String,String>();
		this.data.put(this.DEVICE_TYPE, CVSensing.NONE);
		this.data.put(this.DEVICE_SEEN, "0");
		this.data.put(this.X_OFFSET, "0");
		this.data.put(this.Y_OFFSET, "0");
		this.data.put(this.ANGLE, "0");
		this.data.put(this.ORIENTATION, "0");
	}
	
	public void getNewCapture(String device_type) {
		String format_str = "@ 1\n%s %s\n";
		String msg = String.format(format_str, this.DEVICE_TYPE, device_type);
		try {
			// SEND COMMAND
			Writer writer = new FileWriter(this.path);
			writer.write(msg);
			writer.close();
			
			// AWAIT RESPONSE
			boolean responded = false;
			while (!responded) {
				Reader reader = new FileReader(this.path);
				StreamTokenizer tok = new StreamTokenizer(reader);
				tok.parseNumbers();
				responded = true;
			}
		} catch (IOException e) {
			MessageLog.printError("CV", "IO exception while communicating with CV file.");
		}
	}
	
	public int getHorizontalOffset() {
		String val = this.data.get(this.X_OFFSET);
		return Integer.parseInt(val);
	}
	
	public int getDeviceOrientation() {
		String val = this.data.get(this.ORIENTATION);
		return Integer.parseInt(val);
	}

	public boolean isUpward() {
		// TODO Auto-generated method stub
		return false;
	}
}
