package shipbot.hardware;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import shipbot.staticlib.DeviceData;

public class SensorIR extends Sensor {
	
	public static String VALUE = "distance";
	
	private String id;
	private Map<String, Double> data;

	public SensorIR(String id) {
		this.id = id;
		data = new HashMap<String, Double>();
		data.put(SensorIR.VALUE, 0.0);
	}
	
	public void update() {
		Map<String, Double> new_data = DeviceData.getSensorData(id);
		for (String key : new_data.keySet()) {
			if (data.containsKey(key)) {
				data.put(key, new_data.get(key));
			}
		}
	}
	
	@Override
	public Boolean isActive() {
		return data.size() > 0;
	}

	@Override
	public Double getValue() {
		return data.get(SensorIR.VALUE);
	}

	@Override
	public Set<String> getFields() {
		return data.keySet();
	}
}
