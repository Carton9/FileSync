import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.net.ControlSocket;
import com.net.ObejctFrame;
import com.net.TCPFrame;
import com.net.UniversalFileIO;

public class TestMain {
	static ControlSocket Ssocket=null;
	static ControlSocket Csocket=null;
	public static void main(String[] args) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		Thread a=new Thread() {
			public void run() {
				try {
					Csocket=new ControlSocket("127.0.0.1",3000);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.print("Connector error "+e);
				}
			}
		};
		
		Thread c=new Thread() {
			public void run() {
				try {
					Ssocket=new ControlSocket(3000);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.print("Listener error "+e);
				}
			}
		};
		a.start();
		c.start();
		a.join();
		c.join();
		Thread p=new Thread() {
			public void run() {
				while(true) {
					try {
						Ssocket.listenPort();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		Thread c2=new Thread() {
			public void run() {
				while(true) {
					try {
						Ssocket.listenControlPipe();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
			}
		};
		p.start();
		c2.start();
		List<Integer> b=new ArrayList<Integer>();
		b.add(1);
		//ObejctFrame<ArrayList> of=new ObejctFrame<ArrayList>(b);
		
		for(int i=0;i<1;i++) {
			UniversalFileIO io=new UniversalFileIO(new File("Test.txt"));
			//io.load();
			TCPFrame of=TCPFrame.createFrame(io);
			System.out.println(of.getClass());
			Csocket.submitFrame(of);
			//System.out.println("send");
		}
		
	}
	
	 public static void writeInt(int v) throws IOException {
	        System.out.println((char)((v >>> 24) & 0xFF));
	        System.out.println((char)((v >>> 16) & 0xFF));
	        System.out.println((char)((v >>> 8) & 0xFF));
	        System.out.println((char)((v >>> 0) & 0xFF));
	    }
	/*
	 * public final int readInt() throws IOException {
        int ch1 = in.read();
        int ch2 = in.read();
        int ch3 = in.read();
        int ch4 = in.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0)
            throw new EOFException();
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
    }
	 * Thread a=new Thread() {
			public void run() {
				try {
					Csocket=new ControlSocket("127.0.0.1",3000);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.print("Connector error "+e);
				}
			}
		};
		
		Thread c=new Thread() {
			public void run() {
				try {
					Ssocket=new ControlSocket(3000);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.print("Listener error "+e);
				}
			}
		};
		a.start();
		c.start();
		a.join();
		c.join();
		Thread p=new Thread() {
			public void run() {
				while(true) {
					try {
						Ssocket.listenPort();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		Thread c2=new Thread() {
			public void run() {
				while(true) {
					try {
						Ssocket.listenControlPipe();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
			}
		};
		p.start();
		c2.start();
		System.out.print("get");
		ObejctFrame<String> of=new ObejctFrame<String>("ccc");
		Csocket.submitFrame(of);
		System.out.print("get2");
	 */
}
