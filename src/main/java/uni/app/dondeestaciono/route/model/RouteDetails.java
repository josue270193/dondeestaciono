package uni.app.dondeestaciono.route.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
public class RouteDetails {

  private String calle;
  private String altura;
  private String horario;
}
