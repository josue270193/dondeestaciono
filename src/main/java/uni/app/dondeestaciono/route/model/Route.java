package uni.app.dondeestaciono.route.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
public class Route {

  @Id private String id;
  private List<Point> points = new ArrayList<>();


}
