package shipbot.hardware;

public class ArmState {

	// State data
	private int fixed = 0;
	private int rotator = 0;
	
	public void setPosition(int fixed, int rotator) {
		this.fixed = fixed;
		this.rotator = rotator;
	}
	
	public int[] getPosition() {
		int[] retval = { fixed, rotator };
		return retval;
	}
	
}
