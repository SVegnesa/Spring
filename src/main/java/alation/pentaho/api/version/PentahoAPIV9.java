package alation.pentaho.api.version;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.xml.sax.SAXException;
import alation.pentaho.api.version.entity.JDBCDataBaseDetails;
import alation.pentaho.api.version.entity.KettleStatus;
import alation.sdk.core.error.ConnectorException;
/*
 * API Methods Rest & Kettle for Pentaho PDI & PUC version 9.4
 */
public interface PentahoAPIV9{
	public Boolean isFileDownloadable(String showhidden);
	//Xpath path=path(//path[text()[contains(.,'JOB_OB_Lottery_DB_LOAD.kjb')]]) & filter based on fileNme 
	public List<String> retrieveFilePath(String xml, Map<String, List<KettleStatus>> kettle);
	//retrieve 
	public String retrieveFiletree(Boolean showhidden);
	//xpath Expression for tranformation=transstatus job=jobstatus
	public  Map<String, List<KettleStatus>> retrieveKettleStatus(String isXML) throws IOException, SAXException, ConnectorException;
	public List<String> downloadTransformationKTRFile(List<String> fileName,String locale) throws ConnectorException;
	public List<String> downloadJobKJBile(List<String> fileName,String locale) throws IOException;
	public List<String> retrieveJDBCDatabaseList();
	public Map<String, JDBCDataBaseDetails> retrieveJDBCDatabaseByName(List<String>databaseName);
}