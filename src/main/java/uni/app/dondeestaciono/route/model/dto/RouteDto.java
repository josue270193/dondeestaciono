package uni.app.dondeestaciono.route.model.dto;

import java.util.List;
import lombok.Data;

@Data
public class RouteDto {

  private String id;
  private String name;
  private List<PointDto> points;
  private LineStyleDto lineStyle;
}
