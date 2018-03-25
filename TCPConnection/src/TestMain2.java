import java.util.ArrayList;

import com.net.PTFrame;
import com.net.ResultQueue;
import com.net.StreamFrame;

public class TestMain2 {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		ResultQueue a=new ResultQueue();
		Thread test=new Thread() {
			public void run() {
				ArrayList result=a.getGroupedFrame(new Class[] {StreamFrame.class,StreamFrame.class,StreamFrame.class,StreamFrame.class});
				System.out.print(result);
			}
		};
		test.start();
	
	}

}
