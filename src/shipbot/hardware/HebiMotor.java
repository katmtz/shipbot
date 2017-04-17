package shipbot.hardware;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
	
	// Command format:
	// @ 1
	// [STOP/GO]
	// s [VAL]
	// e [VAL]
	// h [VAl]
	public static String cmd_format = "@ 1\n%s\ns %d\ne %d\nh %d\n";
	
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
}
