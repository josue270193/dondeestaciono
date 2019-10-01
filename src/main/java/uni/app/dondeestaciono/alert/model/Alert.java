package uni.app.dondeestaciono.alert.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Alert {

  @Id private String id;
  private String name;
}
