package alation.pentaho;
import java.util.ArrayList;
import java.util.List;

import alation.pentaho.api.version.PentahoAPIV9Impl;
public class Main {
	public static void main(String[] args) throws Exception {
		PentahoAPIV9Impl impl = new PentahoAPIV9Impl();		
		
		
		impl.retrieveJDBCDatabaseList();
		
		List<String> test = new ArrayList<String>();
		
		
	//	System.out.println(impl.downloadTransformationKTRFile(test, "de"));
		
//		List<String> jdbcDSNames=impl.retrieveFilePath(impl.retrieveFiletree(true), impl.retrieveKettleStatus("Y"));
//		for(String path:jdbcDSNames)
//		{
//		System.out.println(path+"\t"+impl.isFileDownloadable(path));
//		}
//		
//		System.out.println(impl.downloadTransformationKTRFile(jdbcDSNames, "de"));
//		System.out.println(impl.downloadJobKJBile(jdbcDSNames, "de"));
	}
}