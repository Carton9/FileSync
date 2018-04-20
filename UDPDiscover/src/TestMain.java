import com.carton.filesync.common.util.*;
import com.carton.filesync.net.*;

public class TestMain {
	public static void main(String[] args) {
		SHALog serverLog=new SHALog((new SHALog()).generateSign());
		SHALog clientLog=new SHALog(serverLog.addNewConnecter());
		ServiceDiscover discover1=new ServiceDiscover(false,serverLog);
		ServiceDiscover discover2=new ServiceDiscover(true,clientLog);
		discover1.initialize();
		discover2.initialize();
		GeneralServiceExecutePool pool=new GeneralServiceExecutePool();
		System.out.println("lunch");
		pool.lunchUnit(discover2);
		System.out.println("lunch2");
		pool.lunchUnit(discover1);
	}
}
