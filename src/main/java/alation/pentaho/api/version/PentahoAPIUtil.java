package alation.pentaho.api.version;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import alation.pentaho.api.version.entity.KettleStatus;
import alation.pentaho.enums.PentahoErrorCode;
import alation.pentaho.resources.Constants;
import okhttp3.ConnectionSpec;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
public class PentahoAPIUtil {
	private static final Logger LOGGER = Logger.getLogger(PentahoAPIUtil.class.getName());
	/*SSL Trust all certificate*/
	TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
		}
		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
		}
		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return new java.security.cert.X509Certificate[] {};
		}
	} };
	public OkHttpClient getHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
		return new OkHttpClient.Builder().connectTimeout(6000, TimeUnit.SECONDS).writeTimeout(6000, TimeUnit.SECONDS)
				.readTimeout(6000, TimeUnit.SECONDS)
				.connectionSpecs(Collections.singletonList(ConnectionSpec.MODERN_TLS))
				.hostnameVerifier((hostname, session) -> true).build();
	}
	public static String authorization() {
		// Credentials.basic(config.getUsername(),config.getPassword());
		String authorization = Credentials.basic("admin", "passor");
		return authorization;
	}
	/*
	 * Returns Response Body after executing HTTP Request. It can be used further
	 * for other API Calls
	 */
	public static ResponseBody executeXMLHttpRequestWithRetry(String baseUrl, String apiEndpoint) throws IOException {
		OkHttpClient client = new OkHttpClient();
		String authorizationHeader = authorization();
		int maxRetries = Constants.MAX_RETRIES;
		ResponseBody body=null;
		try {
			Request request = new Request.Builder().url(baseUrl+apiEndpoint)
					.addHeader("Authorization", authorizationHeader).build();
			Response response;
			for (int retryCount = 0; retryCount < maxRetries; retryCount++) {
				response = client.newCall(request).execute();
				if (response.isSuccessful()) {
					body=response.body();		
					return body;
				} else {
					LOGGER.info("Retrying... Attempt " + (retryCount + 1));	
					getMessage(response.code(), response.message());
				}
				response.close();
				if (retryCount < maxRetries - 1) {
					System.out.println("Retrying... Attempt " + (retryCount + 1));
				} else {
					System.out.println("Max retry attempts reached.");
				}
			}	
		} catch (IllegalArgumentException e) {
			LOGGER.error("Invalid url format: "+baseUrl+" Accepted format {protocol}://{host}:{port}");
			System.exit(1);
		} 	
		catch(ConnectException ce)
		{
			LOGGER.error("Error Connecting url: "+baseUrl);	
		}
		catch(SSLException ce)
		{
			LOGGER.error("Error Connecting. Site is secured. Please check if valid certificate is installed. "+baseUrl);	
		}
		return body;
	}
	public static NodeList parseXmlResponse(String xmlResponse, String xpathExpression)
			throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xmlResponse));
		Document doc = builder.parse(is);
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		XPathExpression xpathExpr = xpath.compile(xpathExpression);
		return (NodeList) xpathExpr.evaluate(doc, XPathConstants.NODESET);
	}
	public static List<KettleStatus> extractKettleStatusList(NodeList nodeList, XPath xpath, String obj_name,
			String type) throws XPathExpressionException {
		List<KettleStatus> kettleStatusList = new ArrayList<>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			String name = xpath.evaluate(obj_name, nodeList.item(i));
			String id = xpath.evaluate("id", nodeList.item(i));
			String statusDesc = xpath.evaluate("status_desc", nodeList.item(i));
			String logDate = xpath.evaluate("log_date", nodeList.item(i));
			boolean nameExists = false;
			for (KettleStatus existingStatus : kettleStatusList) {
				if (existingStatus.getName().equals(name) && existingStatus.getStatusDescription().equals(statusDesc)) {
					nameExists = true;
					if (logDate.compareTo(existingStatus.getLogDate()) > 0) {
						existingStatus.setLogDate(logDate);
						existingStatus.setId(id);
					} 
					break;
				}
			}
			if (!nameExists) {
				KettleStatus kettleStatus = new KettleStatus();
				kettleStatus.setType(type);
				kettleStatus.setId(id);
				kettleStatus.setName(name);
				kettleStatus.setStatusDescription(statusDesc);
				kettleStatus.setLogDate(logDate);
				kettleStatusList.add(kettleStatus);
			}
		}
		return kettleStatusList;
	}
	public static Document parseXMLString(String xmlString) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xmlString));
		return builder.parse(is);
	}
	public static XPath createXPath() {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		return xpathFactory.newXPath();
	}
	public static void getMessage(int statusCode, String message) {
		if (statusCode == 200) {
			LOGGER.info("Request was processed Successfully.");
		}
		if (statusCode == 201) {
			LOGGER.info("Successfully created");
			LOGGER.info(message);
		}
		if (statusCode == 400) {
			LOGGER.info("Bad Request. please check url or endpoint you are using.");
		}
		if (statusCode == 401) {
			// LOGGER.info(statusCode+": Unauthorized. Please check your credentials.");
			LOGGER.error(statusCode + ": Unauthorized. Please check your credentials.");
		}
		if (statusCode == 403) {
			LOGGER.info("User don't have permissions for performing this action.");
			LOGGER.info(message);
		}
		if (statusCode == 404) {
			LOGGER.info("Resource Not Found. Please check API Endpoint.");
			LOGGER.info(message);
		}
		if (statusCode == 405) {
			LOGGER.info("Method Not Allowed. Please check API Endpoint.");
			LOGGER.info(message);
		}
		if (statusCode == 409) {
			LOGGER.info(
					"Conflict. Please check your data. There should not be redundant uniqueId for data to be posted by this API.");
			LOGGER.info(message);
		}
	}

}