package com.cartion.filesync.security;

public interface KeyUnit {
	public int decryptSize(int originSize);
	public int encryptSize(int encryptionSize);
	public byte[] decrypt(byte[] in);
	public byte[] encrypt(byte[] in);
}
