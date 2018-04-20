import com.carton.filesync.common.util.*;
import com.carton.filesync.net.*;

public class TestMain {
	public static void main(String[] args) {
		SHALog serverLog=new SHALog();
		SHALog clientLog=new SHALog(serverLog);
		//System.out.println(serverLog.veriftyID(clientLog.generateSign()));
		ServiceDiscover discover1=new ServiceDiscover(false,serverLog,new NetworkManager());
		ServiceDiscover discover2=new ServiceDiscover(true,clientLog,new NetworkManager());
		discover1.initialize();
		discover2.initialize();
		GeneralServiceExecutePool pool=new GeneralServiceExecutePool();
		System.out.println("lunch");
		pool.lunchUnit(discover2);
		System.out.println("lunch2");
		pool.lunchUnit(discover1);
	}
}
