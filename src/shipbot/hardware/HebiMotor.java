package shipbot.hardware;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import shipbot.staticlib.MessageLog;

/**
 * Abstract interfacing with the HEBI motors.
 * 
 * @author kat
 *
 */
public class HebiMotor extends Motor {
	
	// Data Fields
	public static String FIXED = "s";
	public static String REACH = "e";
	public static String EFFECTOR = "h";
	
	private Map<String, Integer> data;
	
	public HebiMotor() {
		data = new HashMap<String,Integer>();
		data.put(HebiMotor.FIXED, 0);
		data.put(HebiMotor.REACH, 0);
		data.put(HebiMotor.EFFECTOR, 0);
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

	public boolean isFront() {
		if (data.get(HebiMotor.FIXED) == 0) {
			return true;
		} else if (data.get(HebiMotor.FIXED) == -90) {
			return false;
		} else {
			MessageLog.printError("[HEBISTATE]", "Unrecognized fixed hebi position!!!");
			return false;
		}
	}

	public boolean isReaching() {
		if (data.get(HebiMotor.REACH) == 180) {
			return true;
		} else if (data.get(HebiMotor.REACH) == 0){
			return false;
		} else {
			MessageLog.printError("[HEBISTATE]", "Unrecognized reach hebi position!!!");
			return false;
		}
	}
}
