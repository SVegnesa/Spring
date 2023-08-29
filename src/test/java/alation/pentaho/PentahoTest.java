package alation.pentaho;
import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import alation.pentaho.api.version.PentahoAPIV9Impl;
public class PentahoTest {
	public static void main(String[] args) throws IOException,ConnectException {
            PentahoAPIV9Impl impl = new PentahoAPIV9Impl();
            List<String> databasename = impl.retrieveJDBCDatabaseList();
    		System.out.println("retrieveJDBCDatabaseList : " + databasename);
    		System.out.println("retrieveJDBCDatabaseByName : " + impl.retrieveJDBCDatabaseByName(databasename));
    }
}