package uni.app.dondeestaciono.route.model;

import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Route {

  @Id private String id;
  private String name;
  private List<LatLng> points;
  private LineStyle lineStyle;
}
