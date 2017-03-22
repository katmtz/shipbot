package shipbot.hardware;

import java.io.IOException;
import java.util.Set;

public abstract class Sensor {
	
	protected abstract void update() throws IOException;
	
	public abstract Boolean isActive();
	
	public abstract Double getValue();
	
	public abstract Set<String> getFields();
	
}
