import java.io.IOException;
import java.util.Arrays;

import com.net.ControlSocket;

public class TestMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Thread a=new Thread() {
			public void run() {
				try {
					ControlSocket socket=new ControlSocket("127.0.0.1",3000);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(Arrays.toString(socket.getMutilDataSocket(20)));;
					System.out.println(socket.dataSocketMap.size());;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.print("Connector error "+e);
				}
			}
		};
		Thread c=new Thread() {
			public void run() {
				try {
					ControlSocket socket=new ControlSocket(3000);
					while(true) {
						socket.listenPort();
						System.out.println(socket.dataSocketMap.size());;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.print("Listener error "+e);
				}
			}
		};
		a.start();
		c.start();
		
		
	}

}
