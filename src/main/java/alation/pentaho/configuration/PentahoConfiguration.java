package alation.pentaho.configuration;
import alation.pentaho.enums.GroupEnum;
import alation.pentaho.resources.Constants;
import alation.sdk.core.error.ValidationException;
import alation.sdk.core.manifest.FeatureEnum;
import alation.sdk.core.manifest.parameter.EncryptedTextParam;
import alation.sdk.core.manifest.parameter.Parameter;
import alation.sdk.core.manifest.parameter.SelectParam;
import alation.sdk.core.manifest.parameter.TextParam;
import alation.sdk.core.request.AbstractConfiguration;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
public class PentahoConfiguration extends AbstractConfiguration {
	private static final Logger LOGGER = Logger.getLogger(PentahoConfiguration.class.getName());
	public PentahoConfiguration() {
		super();
	}
	@SuppressWarnings("rawtypes")
	@Override
	public List<Parameter> buildParameters() {
		List<Parameter> parameterList = new ArrayList<>();
		// ---------------------------- Datasource connection - basic auth ----------------------------
		parameterList.add(
				new TextParam(
						Constants.PENTAHO_URL,
						"Base Url",
						"Base Url for Pentaho with name of Web App followed by /",
						FeatureEnum.RDBMS_GENERAL,
						GroupEnum.GROUP_SERVER.getGroupName()));
		parameterList.add(
				new TextParam(
						Constants.USERNAME,
						"Username",
						"Service account username.",
						FeatureEnum.RDBMS_GENERAL,
						GroupEnum.GROUP_SERVER.getGroupName()));
		parameterList.add(
				new EncryptedTextParam(
						Constants.PASSWORD,
						"Password",
						"Service account password.",
						FeatureEnum.RDBMS_GENERAL,
						GroupEnum.GROUP_SERVER.getGroupName()));
		parameterList.add(
				new SelectParam(
						Constants.LOG_LEVEL,
						"Log level",
						"Logging level to be used in the application.",
						FeatureEnum.RDBMS_GENERAL,
						"INFO",
						GroupEnum.GROUP_LOGGING.getGroupName(),
						Arrays.asList("INFO", "DEBUG", "WARN", "TRACE", "ERROR", "FATAL", "ALL")));
		return parameterList;
	}
	@Override
	public Level getLogLevel() {
		String logLevel = getLoggingLevel();
		if (StringUtils.isNotEmpty(logLevel)) {
			return Level.toLevel(logLevel, Level.INFO);
		}
		return super.getLogLevel();
	}
	public void validate() throws ValidationException {
		validateURI();
	}
	/*
	 * #TODO Need implement correctly
	 */
	private void validateURI() throws ValidationException {
		@SuppressWarnings("unused")
		URL url;
		try
		{
			url=new URL(getPentahoUrl()); 
		}
		catch(NullPointerException ne)
		{
			LOGGER.error("Pentaho Url cannot be null.");
			throw new ValidationException("Error connecting Pentaho Url. ");
		}
		catch (MalformedURLException mal) {
			LOGGER.error("Invalid url format. Accepted format: {protocol}://{host}:{port}");
		}
	}
	public String getPentahoUrl() {
		return this.getTextParam(Constants.PENTAHO_URL);
	}
	public String getPassword() {
		return this.getEncryptedTextParam(Constants.PASSWORD);
	}
	public String getUsername() {
		return this.getTextParam(Constants.USERNAME);
	}
	public String getLoggingLevel() {
		return this.getSelectParam(Constants.LOG_LEVEL);
	}
}