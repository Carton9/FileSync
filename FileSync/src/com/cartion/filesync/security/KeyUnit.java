package com.cartion.filesync.security;

import java.io.Serializable;

public abstract class KeyUnit implements Serializable{
	public abstract int decryptSize(int originSize);
	public abstract int encryptSize(int encryptionSize);
	public abstract byte[] decrypt(byte[] in);
	public abstract byte[] encrypt(byte[] in);
	public abstract String getCypherType();
}
