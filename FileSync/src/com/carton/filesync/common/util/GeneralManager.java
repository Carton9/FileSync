package com.carton.filesync.common.util;

import java.io.File;
import java.io.Serializable;

public abstract class GeneralManager implements Serializable{
	public abstract <T extends GeneralManager> T loadManager(byte data[]);
	public abstract <T extends GeneralManager> byte[] saveManager(T Manager);
	public static <T extends GeneralManager> void saveManagerToFile(File file,T manager) {
		
	}
	public static <T extends GeneralManager> T loadManagerFromFile(File file) {
		return null;
	}
}
