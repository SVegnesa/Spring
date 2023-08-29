package alation.pentaho.resources;
public class Constants {
	// Parameters
	// ---------------------------- Configuration Constants ----------------------------
	public static final String PENTAHO_URL = "pentaho_url";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String LOG_LEVEL = "log_level";  
	//----------------------------------------------- API Endpoints --------------------------------
	public static final String KETTLE_STATUS = "/kettle/status/";
	public static final String RETRIEVE_FILEPATH = "/pentaho/api/repo/files/tree?showHidden=false";
	public static final String IS_PATHDOWNLOADBLE = "/pentaho/api/repo/files/canDownload?dirPath=";
	public static final String JDBCDATASOURCE = "/pentaho/plugin/data-access/api/datasource/jdbc/connection";
	/*Use below constants before api's with dynamic endpoints
	 * Example: /pentaho/api/repo/files/{file path}/download?locale={locale}*/
	public static final String FILE_RESOURCE="/repo/files/";
	public static final String JDBC_DS_RESOURCE="/data-access/api/datasource/jdbc/connection";
	//--------------------------------------------- XPath Expressions -------------------------------
	public static final String TRANSSTATUSLISTPATH =  "//transstatuslist/transstatus";
	public static final String JOBSSSTATUSLISTPATH = "//jobstatuslist/jobstatus";
	//--------------------------------------------- other -------------------------------------------
	public static final String TRANSFORMATION =  "transformation";
	public static final String JOB =  "job";
	public static final Integer MAX_RETRIES = 3;
	public static final String IS_PATH_DOWNLOADBLE = "/api/repo/files";
	public static final String FAILED_TO_CLOSE_STATEMENT = "Failed to close OracleStatement.";
}