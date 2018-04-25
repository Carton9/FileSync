package com.carton.filesync.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

public abstract class GeneralManager{
	public GeneralManager(byte[] config) {
		this.loadConfiguration(config);
	}
	public GeneralManager(File file) {
		loadManagerFromFile(file);
	}
	public GeneralManager() {
	}
	
	public abstract void loadConfiguration(byte data[]);
	public abstract byte[] saveConfiguration();
	public void saveManagerToFile(File file) throws IOException {
		FileOutputStream fos=new FileOutputStream(file);
		fos.write(this.saveConfiguration());
		fos.close();
	}
	public void loadManagerFromFile(File file) {
	}
}
