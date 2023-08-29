package alation.pentaho.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import alation.pentaho.api.version.PentahoAPIUtil;
import alation.pentaho.api.version.PentahoAPIV9Impl;

public class Metadata {

	private static final Logger LOGGER = Logger.getLogger(Metadata.class.getName());
	
	public List<String> getFolder(){
		List<String> filePathList = new ArrayList<String>();
		try {
		String filePath = null;
		PentahoAPIV9Impl impl = new PentahoAPIV9Impl();
		String filePathResponse = impl.retrieveFiletree(true);
		String xpathExpression = "//file[not(localePropertiesMapEntries)]/path[starts-with(., '/home')]";
		NodeList result = PentahoAPIUtil.parseXmlResponse(filePathResponse, xpathExpression);
		for (int i = 0; i < result.getLength(); i++) {
			filePath = result.item(i).getTextContent();
			filePathList.add(filePath);
		}
	
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			LOGGER.info(e);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			LOGGER.info(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.info(e);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			LOGGER.info(e);
		} catch(NullPointerException e)
		{
			LOGGER.error(e);
		}
		return filePathList;	
	}
	
	public static void main(String[] args) {
		
		Metadata data = new Metadata();
		
		System.out.println(data.getFolder());
	}
	
	

}