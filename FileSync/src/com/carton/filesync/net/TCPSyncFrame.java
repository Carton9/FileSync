package com.carton.filesync.net;

import java.io.IOException;

import com.carton.filesync.core.Core;
import com.carton.filesync.service.FileListSyncUnit;
import com.carton.filesync.service.FileSyncList;

public class TCPSyncFrame extends TCPFrame {
	FileSyncList list;
	protected TCPSyncFrame(FileSyncList list) {
		this.list=list;
	}
	protected TCPSyncFrame() {
		super();
		this.list=new FileSyncList(Core.getKey().getId());
	}
	@Override
	protected boolean send() {
		try {
			byte[] listInfo = list.Serialize();
			boolean check=sendCommend(TCPState.CheckAliveRequest);
			if(check)check=sendCommend(TCPState.FileListRequst);
			if(check)check=sendCommend(TCPState.TransmissionBegin);
			if(check) {
				cos.writeInt(listInfo.length);
				dos.write(listInfo, 0, listInfo.length);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	protected boolean revice() {
		try {
			TCPState state = this.filtedInfo();
			if(state==TCPState.TransmissionBegin)
				sendCommend(TCPState.TransmissionBeginConfirm);
			int dataLength=cis.readInt();
			byte[] dataBuff=new byte[dataLength];
			dis.read(dataBuff);
			list=FileSyncList.Deserialize(dataBuff);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
