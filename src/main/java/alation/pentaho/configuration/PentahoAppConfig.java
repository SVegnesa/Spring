package alation.pentaho.configuration;
import alation.sdk.rdbms.configuration.RdbmsApplicationConfiguration;
/** Class to determine Application configuration for Oracle datasource. */
public class PentahoAppConfig extends RdbmsApplicationConfiguration {
  @Override
  public ParserGrammarType getParserGrammarType() {
    return ParserGrammarType.ORACLE;
  }
  @Override
  public boolean supportsDefaultDbLogging() {
    return true;
  }
  /**
   * Can be determined by metadata.getIdentifierQuoteString(). For Oracle double quote is returned.
   */
  @Override
  public String getIdentifierQuoteString() {
    return "\"";
  }
  @Override
  public boolean supportsRollback() {
    return true;
  }
  @Override
  public boolean supportsMultipleResultSets() {
    return false;
  }
  @Override
  public boolean supportsPreparedStatement() {
    return true;
  }
  @Override
  public String explainQueryPrefix() {
    return "EXPLAIN PLAN FOR";
  }
  @Override
  public String currentSchemaQuery() {
    return "SELECT sys_context( 'userenv', 'current_schema' ) FROM dual";
  }
  @Override
  public String checkConnectionQuery() {
    return "SELECT 1 FROM DUAL";
  }
  @Override
  public boolean supportsConcurrentQueries() {
    return true;
  }
  @Override
  public boolean supportsQueryBatchExecution() {
    return true;
  }
  @Override
  public boolean supportsImpersonation() {
    return true;
  }
  @Override
  public boolean supportsIncrementalMetaDataExtraction() {
    return true;
  }
}