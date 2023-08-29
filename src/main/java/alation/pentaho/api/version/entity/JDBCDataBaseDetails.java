package alation.pentaho.api.version.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Data
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class JDBCDataBaseDetails {
	private String databaseName;
	private String databasePort;
	private String databaseTypeName;
	private String name;
	private String hostname;
	private String id;
	private String username;
}