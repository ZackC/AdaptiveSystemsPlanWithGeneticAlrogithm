package main.java.omnet.tactics;

import main.java.main.OmnetStateData;
import main.java.omnet.components.ServerF;

public class DecreaseDimmerLevelF extends DecreaseDimmerLevel {

	public DecreaseDimmerLevelF(){
		latency=1;
		failureWeight=0.05;
	}
	
	@Override
	public String toString(){
	 	return "DecreaseDimmerLevelF";
	}

	@Override
	public void callPerformTactic(OmnetStateData sd) {
		sd.performTactic(this, ServerF.class);
		
	}
	


}
