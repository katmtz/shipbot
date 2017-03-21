package shipbot.hardware;

import java.util.Map;

public abstract class Motor {
	
	protected abstract void recieveUpdate();
	
	protected abstract void sendUpdate();
	
	public abstract int get(String field);
	
	public abstract void set(String field, int value);
}
