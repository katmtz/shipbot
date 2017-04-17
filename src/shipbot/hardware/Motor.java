package shipbot.hardware;

import java.util.Set;

public abstract class Motor {
	
	public abstract int get(String field);
	
	public abstract void set(String field, int value);
	
	public abstract Set<String> getFields();
	
}
