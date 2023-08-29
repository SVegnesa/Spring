package alation.pentaho.datasource;
import alation.pentaho.configuration.PentahoConfiguration;
import alation.sdk.core.error.ConnectorException;
import alation.sdk.core.request.auth.Auth;
import alation.sdk.core.stream.Stream;
import alation.sdk.dl.mde.datasource.rdbms.DirectLineageExtraction;
import alation.sdk.dl.mde.requests.rdbms.ExtractionRequest;
import alation.sdk.dl.mde.streams.DirectLineageExtractionMessage;
import alation.sdk.rdbms.configuration.RdbmsApplicationConfiguration;
import alation.sdk.rdbms.datasource.RdbmsDatasource;
import alation.sdk.rdbms.mde.datasource.MetadataExtraction;
import alation.sdk.rdbms.mde.models.SchemaId;
import alation.sdk.rdbms.mde.streams.FilterExtractionMessage;
import alation.sdk.rdbms.mde.streams.MetadataExtractionMessage;
import java.util.List;
import org.apache.log4j.Logger;
public class PentahoDatasource
    implements RdbmsDatasource<PentahoConfiguration>, MetadataExtraction<PentahoConfiguration>,
	DirectLineageExtraction<PentahoConfiguration>{
  private static final Logger LOGGER = Logger.getLogger(PentahoDatasource.class.getName());
  
@Override
public PentahoConfiguration createConfiguration() {
	// TODO Auto-generated method stub
	return null;
}
@Override
public void validate(PentahoConfiguration config) throws ConnectorException {
	// TODO Auto-generated method stub
	config.validate();
}
@Override
public void validate(PentahoConfiguration config, Auth auth) throws ConnectorException {
	// TODO Auto-generated method stub
	config.validate();
}
@Override
public RdbmsApplicationConfiguration getApplicationConfiguration() {
	// TODO Auto-generated method stub
	return null;
}
@Override
public String getConnectorName() {
	// TODO Auto-generated method stub
	return null;
}
@Override
public String getConnectorVersion() {
	// TODO Auto-generated method stub
	return null;
}
@Override
public String getDescription() {
	// TODO Auto-generated method stub
	return null;
}
@Override
public String getSourceName() {
	// TODO Auto-generated method stub
	return null;
}
@Override
public PentahoConfiguration getUserConfiguration() {
	// TODO Auto-generated method stub
	return null;
}
@Override
public void directLineageExtraction(ExtractionRequest<PentahoConfiguration> arg0,
		Stream<DirectLineageExtractionMessage> arg1) throws ConnectorException {
	// TODO Auto-generated method stub
	
}
@Override
public void filterExtraction(PentahoConfiguration arg0, Stream<FilterExtractionMessage> arg1)
		throws ConnectorException {
	// TODO Auto-generated method stub
	
}
@Override
public void metadataExtraction(PentahoConfiguration arg0, List<SchemaId> arg1, boolean arg2,
		Stream<MetadataExtractionMessage> arg3) throws ConnectorException {
	// TODO Auto-generated method stub
	
}

  
}
