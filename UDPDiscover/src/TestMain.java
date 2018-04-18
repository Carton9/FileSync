import com.carton.filesync.common.util.*;
import com.carton.filesync.net.*;

public class TestMain {
	public static void main(String[] args) {
		ServiceDiscover discover1=new ServiceDiscover(false);
		ServiceDiscover discover2=new ServiceDiscover(true);
		GeneralServiceExecutePool pool=new GeneralServiceExecutePool();
		pool.lunchUnit(discover2);
		pool.lunchUnit(discover1);
	}
}
