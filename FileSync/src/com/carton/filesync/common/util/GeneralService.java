package com.carton.filesync.common.util;

public interface GeneralService {
	boolean stateFlags[]=new boolean[3];
	public String getName();
	public void initialize();
	public void execute();
	public void finish();
}
