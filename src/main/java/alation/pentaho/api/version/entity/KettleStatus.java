package alation.pentaho.api.version.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Data
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
@SuppressWarnings("unused")
public class KettleStatus {
	private String type;
	private String id;
	private String name;
	private String statusDescription;
	private String logDate;
}