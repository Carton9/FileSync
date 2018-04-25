package com.cartion.filesync.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.carton.filesync.common.util.GeneralManager;

public class SecurityManager extends GeneralManager {
	private KeyUnit TCPTransferKey;
	private SecurityLog UDPTransferKey;
	public SecurityManager(KeyUnit TCPTransferKey,SecurityLog UDPTransferKey) {
		this.TCPTransferKey=TCPTransferKey;
		this.UDPTransferKey=UDPTransferKey;
	}
	public KeyUnit getKey() {
		return TCPTransferKey;
	}
	public SecurityLog getLog() {
		return UDPTransferKey;
	}
	@Override
	public void loadConfiguration(byte[] data) {
		// TODO Auto-generated method stub
		try {
			DataInputStream dis=new DataInputStream(new ByteArrayInputStream(data));
			int TCPLength;
			TCPLength = dis.readInt();
			byte[] TCPKey=new byte[TCPLength];
			byte[] UDPKey=new byte[data.length-TCPLength];
			dis.read(TCPKey,0,TCPLength);
			dis.read(UDPKey,TCPLength,UDPKey.length);
			ObjectInputStream oos=new ObjectInputStream(new ByteArrayInputStream(TCPKey));
			this.TCPTransferKey=(KeyUnit)oos.readObject();
			oos=new ObjectInputStream(new ByteArrayInputStream(UDPKey));
			this.UDPTransferKey=(SecurityLog)oos.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public byte[] saveConfiguration() {
		// TODO Auto-generated method stub
		try {
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			ByteArrayOutputStream tbos=new ByteArrayOutputStream();
			ByteArrayOutputStream ubos=new ByteArrayOutputStream();
			DataOutputStream dis=new DataOutputStream(bos);
			ObjectOutputStream oos;
			oos = new ObjectOutputStream(tbos);
			oos.writeObject(TCPTransferKey);
			oos=new ObjectOutputStream(ubos);
			oos.writeObject(UDPTransferKey);
			byte[] TCPKey=tbos.toByteArray();
			byte[] UDPKey=ubos.toByteArray();
			dis.write(TCPKey.length);
			dis.write(TCPKey);
			dis.write(UDPKey);
			return bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
