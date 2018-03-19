package com.carton.filesync.net;

public enum TCPState {
	/*FileRequst(100),*/FilePacketRequst(101),FilePacketReady(102),FileStreamRequst(103),FileStreamReady(104),PTStreamRequst(105),PTStreamReady(106),FileListRequst(107),FileListReady(108)/*,
	CommandRequst(200)*/,DisconnectRequst(201),DisconnectReady(202)/*,
	StateRequest(300)*/,CheckAliveRequest(301),CheckAliveReply(302)/*,
	TransmissionRequest(400)*/,TransmissionBegin(401),TransmissionBeginConfirm(402),TransmissionStandBy(403),TransmissionWait(404),TransmissionEnd(405),TransmissionEndConfirm(406)/*
	*/,ControlPipeConnect(501),ControlPipedConnected(502),DataPipeConnect(503),DataPipedConnected(504),
	CommunicationError(999);
	
	private int type;
	TCPState(int type) {
		this.type=type;
	}
	public int getType() {
		return type;
	}
	public static TCPState getTCPStateByInt(int type) {
		switch(type) {
		//case 100:return TCPState.FileRequst;
		case 101:return TCPState.FilePacketRequst;
		case 102:return TCPState.FilePacketReady;
		case 103:return TCPState.FileStreamRequst;
		case 104:return TCPState.FileStreamReady;
		case 105:return TCPState.PTStreamRequst;
		case 106:return TCPState.PTStreamReady;
		case 107:return TCPState.FileListRequst;
		case 108:return TCPState.FileListReady;
		//case 200:return TCPState.CommandRequst  ;
		case 201:return TCPState.DisconnectRequst;
		case 202:return TCPState.DisconnectReady;
		//case 300:return TCPState.StateRequest;
		case 301:return TCPState.CheckAliveRequest;
		case 302:return TCPState.CheckAliveReply;
		//case 400:return TCPState.TransmissionRequest;
		case 401:return TCPState.TransmissionBegin;
		case 402:return TCPState.TransmissionBeginConfirm;
		case 403:return TCPState.TransmissionStandBy;
		case 404:return TCPState.TransmissionWait;
		case 405:return TCPState.TransmissionEnd;
		case 406:return TCPState.TransmissionEndConfirm;
		case 999:return TCPState.CommunicationError;
		}
		return null;
	}
	public String toString() {
		return type+"";
	}
}
