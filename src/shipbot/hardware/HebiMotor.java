package shipbot.hardware;

import java.util.Map;

/**
 * Abstract interfacing with the HEBI motors.
 * 
 * @author kat
 *
 */
public class HebiMotor extends Motor {
	
	// Data Fields
	public static String POS = "angle";
	
	private String id;
	private Map<String, Integer> data;
	
	public HebiMotor(String id) {
		this.id = id;
		data.put(HebiMotor.POS, 0);
		// TODO properly initialize motor data w/ file IO
	}

	@Override
	protected void recieveUpdate() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void sendUpdate() {
		// TODO Auto-generated method stub
	}

	@Override
	public int get(String field) {
		this.recieveUpdate();
		return data.get(field);
	}

	@Override
	public void set(String field, int value) {
		data.put(field, value);
		this.sendUpdate();
	}
}
