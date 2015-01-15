package edu.cb.climbingclub.li.zhilin;

import edu.jenks.dist.cb.climbingclub.AbstractClimbInfo;

public class ClimbInfo extends AbstractClimbInfo {

	public ClimbInfo(String peakName, int climbTime) {
		super(peakName, climbTime);
	}

	@Override
	public int compareTo(AbstractClimbInfo climbObject) {
		String mountName = getPeakName();
		return this.getPeakName().compareTo(mountName);
	}

}