package shipbot.hardware;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import shipbot.staticlib.Config;
import shipbot.staticlib.DeviceData;

public class DriveMotor extends Motor {
	
	// Data Fields
	public static String X = "x";
	public static String Y = "y";
	public static String ORIENT = "r";
	
	private String id;
	private Map<String, Integer> data;

	public DriveMotor(String id) {
		this.id = id;
		data = new HashMap<String,Integer>();
		data.put(DriveMotor.X, 0);
		data.put(DriveMotor.Y, 0);
		data.put(DriveMotor.ORIENT, 0);
	}

	@Override
	public int get(String field) {
		return data.get(field);
	}

	@Override
	public void set(String field, int value) {
		data.put(field, value);
	}

	@Override
	public Set<String> getFields() {
		return data.keySet();
	}

}
