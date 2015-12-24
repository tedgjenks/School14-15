package edu.cb.climbingclub.busbee.hunter;

import java.util.ArrayList;

import edu.jenks.dist.cb.climbingclub.AbstractClimbInfo;
import edu.jenks.dist.cb.climbingclub.AbstractClimbingClub;

public class ClimbingClubChronological extends AbstractClimbingClub {

	public ClimbingClubChronological() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addClimb(String peakName, int climbTime) {
		ClimbInfo climbInfo = new ClimbInfo(peakName, climbTime);
		getClimbList().add(climbInfo);
		
	}

	@Override
	public int distinctPeakNames() {
		ArrayList<String> distinctPeakNames = new ArrayList<String>();
		for(AbstractClimbInfo climb : getClimbList()){
			String peakName = climb.getPeakName();
			if(distinctPeakNames.indexOf(peakName) < 0){
				distinctPeakNames.add(peakName);
			}	
		}
		return distinctPeakNames.size();
	}

}
