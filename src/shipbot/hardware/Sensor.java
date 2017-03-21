package shipbot.hardware;

import java.io.IOException;

public abstract class Sensor {
	
	protected abstract void update() throws IOException;
	
	public abstract Boolean isActive();
	
	public abstract Double getValue();
	
}
