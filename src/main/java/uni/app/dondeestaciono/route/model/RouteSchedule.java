package uni.app.dondeestaciono.route.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
public class RouteSchedule {

  private RouteTypePermit permit;
  private List<RouteScheduleDetail> details;
}
