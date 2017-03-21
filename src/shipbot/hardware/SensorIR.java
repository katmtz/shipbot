package shipbot.hardware;

import java.util.Map;

import shipbot.staticlib.DeviceData;

public class SensorIR extends Sensor {
	
	private String id;
	private Double distance;

	public SensorIR(String id) {
		this.id = id;
		distance = -1.0;
	}
	
	public void update() {
		Map<String, Double> data = DeviceData.getSensorData(id);
		distance = data.get("distance");
	}
	
	@Override
	public Boolean isActive() {
		return distance > 0;
	}

	@Override
	public Double getValue() {
		return distance;
	}
}
