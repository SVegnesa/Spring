package alation.pentaho.api.version.entity;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Data
@Getter @Setter @AllArgsConstructor @NoArgsConstructor @ToString
@SuppressWarnings("unused")
public class FilePath {
    private Map<String, String> map = new HashMap<>(); 
    public void put(String name, String filePath) {
        map.put(name, filePath);
    }
}