package alation.pentaho.api.version;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import alation.pentaho.api.version.entity.JDBCDataBaseDetails;
import alation.pentaho.api.version.entity.KettleStatus;
import alation.pentaho.exception.PentahoException;
import alation.pentaho.resources.Constants;
import okhttp3.ResponseBody;
public class PentahoAPIV9Impl implements PentahoAPIV9 {
	private static final Logger LOGGER = Logger.getLogger(PentahoAPIV9Impl.class.getName());
	private static String baseUrl = "http://localhost:8087/pentaho";
	@Override
	public Boolean isFileDownloadable(String path) {
		String responseString = null;
		try {
			ResponseBody Response = PentahoAPIUtil.executeXMLHttpRequestWithRetry(baseUrl,
					"/api" + Constants.FILE_RESOURCE + "canDownload?dirPath=" + path);
			responseString = Response.string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (NullPointerException ne) {
			LOGGER.error("Unable to retrive File Permissions dueto null Response.");
		}
		return Boolean.parseBoolean(responseString);
	}
	@Override
	public List<String> retrieveFilePath(String xml, Map<String, List<KettleStatus>> kettle) {
		List<String> filePathList = new ArrayList<>();
		try {
			String filePath = null;
			for (Map.Entry<String, List<KettleStatus>> entry : kettle.entrySet()) {
				List<KettleStatus> kettleStatusList = entry.getValue();
				for (KettleStatus kettleStatus : kettleStatusList) {
					String name = kettleStatus.getName();
					if (Constants.TRANSFORMATION.equals(kettleStatus.getType())) {
						name += ".ktr";
					} else if (Constants.JOB.equals(kettleStatus.getType())) {
						name += ".kjb";
					}				
					String xpathExpression = "//path[text()[contains(.,'"+name+"')]]";
					NodeList result = PentahoAPIUtil.parseXmlResponse(xml, xpathExpression);
					for (int i = 0; i < result.getLength(); i++) {
						filePath = result.item(i).getTextContent();
						filePathList.add(filePath);
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("Failed to retrieve XML response");
			e.printStackTrace();
		} catch (ParserConfigurationException | SAXException | XPathExpressionException e) {
			e.printStackTrace();
		}
		return filePathList;
	}
	@Override
	public Map<String, List<KettleStatus>> retrieveKettleStatus(String isXML) throws IOException {
		Map<String, List<KettleStatus>> statusMap = new HashMap<String, List<KettleStatus>>();
		try {
			ResponseBody xmlResponse = PentahoAPIUtil.executeXMLHttpRequestWithRetry(baseUrl,
					Constants.KETTLE_STATUS + "?xml=" + isXML);
			String response = xmlResponse.string().replaceAll("[\\r\\n]", "");
			Document doc = PentahoAPIUtil.parseXMLString(response);
			XPath xpath = PentahoAPIUtil.createXPath();
			XPathExpression exprTransstatusList = xpath.compile(Constants.TRANSSTATUSLISTPATH);
			XPathExpression exprJobstatusList = xpath.compile(Constants.JOBSSSTATUSLISTPATH);
			NodeList transstatusNodes = (NodeList) exprTransstatusList.evaluate(doc, XPathConstants.NODESET);
			NodeList jobsstatusNodes = (NodeList) exprJobstatusList.evaluate(doc, XPathConstants.NODESET);
			List<KettleStatus> transKettle = PentahoAPIUtil.extractKettleStatusList(transstatusNodes, xpath,
					"transname", Constants.TRANSFORMATION);
			List<KettleStatus> jobKettle = PentahoAPIUtil.extractKettleStatusList(jobsstatusNodes, xpath, "jobname",
					Constants.JOB);
			if (transKettle.isEmpty() && jobKettle.isEmpty()) {
				throw new PentahoException("No Transformation or Job Activity performed");
			}
			statusMap.put(Constants.TRANSFORMATION, transKettle);
			statusMap.put(Constants.JOB, jobKettle);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			LOGGER.error(e);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}  catch (NullPointerException ce) {
			LOGGER.error("Error Response from Api is Null. Please check url or retry later");
		}catch (PentahoException e) {
			LOGGER.error(e);
		}
		return statusMap;
	}
	@Override
	public List<String> downloadTransformationKTRFile(List<String> fileName, String locale) {
		File exportedFile = new File("src/main/resources/sample.zip");
		List<String> paths = new ArrayList<String>();
		for (String filePath : fileName) {
			LOGGER.info("Cheking for file Permission");
			if (isFileDownloadable(filePath)) {
				LOGGER.info("Exporting Transformation....");
				try {
					ResponseBody response = PentahoAPIUtil.executeXMLHttpRequestWithRetry(baseUrl, "/api"
							+ Constants.FILE_RESOURCE + filePath.replaceAll("/", ":") + "/download?locale=" + locale);
					byte[] responseBytes = response.bytes();
					response.close();
					OutputStream outputStream = new FileOutputStream(exportedFile);
					outputStream.write(responseBytes);
					outputStream.close();
					ZipFile zip = new ZipFile(exportedFile);
					Enumeration<? extends ZipEntry> fileEntries = zip.entries();
					String result = "";
					LOGGER.info("Fetching data from Exported Transformation.");
					while (fileEntries.hasMoreElements()) {
						ZipEntry entries = fileEntries.nextElement();
						if (entries.getName().endsWith("ktr")) {
							File extractedFile = new File("src/main/resources/" + entries.getName());
							paths.add(extractedFile.getAbsolutePath());
							InputStream fileContent = zip.getInputStream(entries);
							result = new BufferedReader(new InputStreamReader(fileContent)).lines()
									.collect(Collectors.joining("\n"));
							FileWriter writer = new FileWriter(extractedFile);
							writer.write(result);
							writer.close();
						}
					}
					LOGGER.info("Closing Zip File & Deleting....");
					zip.close();
					FileUtils.forceDelete(exportedFile);
					LOGGER.info("Succesfully Exported Transformation");
				} catch (NullPointerException ne) {
					LOGGER.error("Error creating Zip File. Invalid Content. Zip Content cannot be null");
				} catch (FileNotFoundException e) {
					LOGGER.error("File " + exportedFile + " not found.");
				} catch (IOException e) {
					LOGGER.error("Error creating zip file. " + e.getMessage());
				}
			} else {
				LOGGER.error("User don't have permission to download file from path: " + fileName);
			}
		}
		return paths;
	}
	@Override
	public List<String> downloadJobKJBile(List<String> fileName, String locale) throws IOException {
		File exportedFile = new File("src/main/resources/sample.zip");
		List<String> paths = new ArrayList<String>();
		for (String filePath : fileName) {
			LOGGER.info("Cheking for file Permission");
			if (isFileDownloadable(filePath)) {
				LOGGER.info("Exporting Job....");
				try {
					ResponseBody response = PentahoAPIUtil.executeXMLHttpRequestWithRetry(baseUrl, "/api"
							+ Constants.FILE_RESOURCE + filePath.replaceAll("/", ":") + "/download?locale=" + locale);
					byte[] responseBytes = response.bytes();
					response.close();
					OutputStream outputStream = new FileOutputStream(exportedFile);
					outputStream.write(responseBytes);
					outputStream.close();
					ZipFile zip = new ZipFile(exportedFile);
					Enumeration<? extends ZipEntry> fileEntries = zip.entries();
					String result = "";
					LOGGER.info("Fetching data from Exported Job.");
					while (fileEntries.hasMoreElements()) {
						ZipEntry entries = fileEntries.nextElement();
						if (entries.getName().endsWith("kjb")) {
							File extractedFile = new File("src/main/resources/" + entries.getName());
							paths.add(extractedFile.getAbsolutePath());
							InputStream fileContent = zip.getInputStream(entries);
							result = new BufferedReader(new InputStreamReader(fileContent)).lines()
									.collect(Collectors.joining("\n"));
							FileWriter writer = new FileWriter(extractedFile);
							writer.write(result);
							writer.close();
						}
					}
					LOGGER.info("Closing Zip File & Deleting....");
					zip.close();
					FileUtils.forceDelete(exportedFile);
					LOGGER.info("Succesfully Exported Job");
				} catch (NullPointerException ne) {
					LOGGER.error("Error creating Zip File. Invalid Content. Zip Content cannot be null");
				} catch (FileNotFoundException e) {
					LOGGER.error("File " + exportedFile + " not found.");
				} catch (IOException e) {
					LOGGER.error("Error creating zip file. " + e.getMessage());
				}
			} else {
				LOGGER.error("User don't have permission to download file from path: " + fileName);
			}
		}
		return paths;
	}
	@Override
	public List<String> retrieveJDBCDatabaseList() {
		List<String> listDatasourceNames = new ArrayList<String>();
		try {
			ResponseBody responseBody = PentahoAPIUtil.executeXMLHttpRequestWithRetry(baseUrl,"/plugin"+
					Constants.JDBC_DS_RESOURCE);
			String response = responseBody.string();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode itemArrayNode = rootNode.get("Item");
			if (itemArrayNode.isArray()) {
				for (JsonNode itemNode : itemArrayNode) {
					JsonNode valueNode = itemNode.get("$");
					if (valueNode != null && valueNode.isTextual()) {
						listDatasourceNames.add(valueNode.textValue());
					}
				}
			}
		} catch (IOException e) {
			LOGGER.error("Failed to retrieve JSON response" + e);
		} catch (NullPointerException ce) {
			LOGGER.error("Error Response from Api is Null. Please check url or retry later");
		}
		return listDatasourceNames;
	}
	@Override
	public String retrieveFiletree(Boolean showhidden) {
		String responseString = null;
		try {
			ResponseBody xmlResponse = PentahoAPIUtil.executeXMLHttpRequestWithRetry(baseUrl,
					"/api" + Constants.FILE_RESOURCE + "tree?showHidden=" + showhidden);
			responseString = xmlResponse.string().replaceAll("[\\r\\n]", "");
		} catch (IOException e) {
			LOGGER.info("Failed to retrieve XML response" + e);
		} catch (NullPointerException ce) {
			LOGGER.error("Error Response from Api is Null. Please check url or retry later" + baseUrl);
		}
		return responseString;
	}
	@Override
	public Map<String, JDBCDataBaseDetails> retrieveJDBCDatabaseByName(List<String> databaseName) {
		Map<String, JDBCDataBaseDetails> datasourceMap = new HashMap<>();
		for (String name : databaseName) {
			try {
				ResponseBody responseBody = PentahoAPIUtil.executeXMLHttpRequestWithRetry(baseUrl,"/plugin"+
						Constants.JDBC_DS_RESOURCE + "/" + name);
				String response = responseBody.string();
				ObjectMapper objectMapper = new ObjectMapper();
				JDBCDataBaseDetails details = new JDBCDataBaseDetails();
				details = objectMapper.readValue(response, JDBCDataBaseDetails.class);
				datasourceMap.put(name, details);
			} catch (IOException e) {
				LOGGER.error("Failed to retrieveb response for " + name);
			}catch (NullPointerException ce) {
				LOGGER.error("Error Response from Api is Null");
			}
		}
		return datasourceMap;
	}
}