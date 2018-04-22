package com.carton.filesync.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.cartion.filesync.security.KeyUnit;

public class SecureSocket extends Socket {
	Socket socket;KeyUnit unit;
	public SecureSocket(Socket socket,KeyUnit unit) {
		this.socket=socket;
		this.unit=unit;
	}
	public OutputStream getOutputStream() throws IOException {
		return new SecureNetOutputStream(socket.getOutputStream(),unit);
	}
	public InputStream getInputStream() throws IOException {
		return new SecureNetInputStream(socket.getInputStream(),unit);
	}
	public void close() throws IOException {
		this.socket.close();
	}
}
