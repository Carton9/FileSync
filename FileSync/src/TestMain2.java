import java.io.File;
import java.io.IOException;
import com.cartion.filesync.security.*;
import com.cartion.filesync.security.SecurityManager;

public class TestMain2 {
	public static void main(String[] args) throws IOException{
		SHALog serverLog=new SHALog();
		DECKey key=DECKey.getdefultKey();
		SecurityManager manager=new SecurityManager(key,serverLog);
		manager.saveManagerToFile(new File("/home/mike/c.config"));
		manager.loadManagerFromFile(new File("/home/mike/c.config"));
	}
}
