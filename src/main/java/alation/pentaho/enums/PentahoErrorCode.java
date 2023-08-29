package alation.pentaho.enums;
import alation.sdk.core.error.ErrorCode;
public enum PentahoErrorCode implements ErrorCode {
  //////// Using codes from 4000 - 4999 for Connection error codes
  
	KETTLE_XML_EXCEPTION("Kettle XML Exception","Kettle XML Exception occurred. Unable to load the job from XML file"),
	KETTLE_DATABASE_EXCEPTION("Database Error Occured","Error occurred while trying to connect to the database"),
	KETTLE_SQL_EXCEPTION("Kettle SQL Exception occurred. There was an issue with a database query.","Check your SQL query and database connectivity."),
	KETTLE_TRANS_EXCEPTION("Kettle Transformation Exception occurred. There was an issue with a transformation.", "Check the transformation configuration and input data."),
	KETTLE_JOB_EXCEPTION("Kettle Job Exception occurred. There was an issue with a job.", "Check the job configuration and input data.");
	
  // Describe the nature of the problem.
  private String description;
  // If possible provide a suggestion on how to resolve the issue.
  private String hint;
  
  public String getDescription() {
    return description;
  }
  public String getHint() {
    return hint;
  }
  PentahoErrorCode(String description) {
    
    this.description = description;
  }
  
  PentahoErrorCode(String description, String hint) {
    
    this.description = description;
    this.hint = hint;
  }
  @Override
  public String toString() {
    return this.getDescription();
  }
@Override
public int getCode() {
	// TODO Auto-generated method stub
	return 0;
}
}