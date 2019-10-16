package uni.app.dondeestaciono.route.model;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
public class RouteScheduleDetail {

  private String weekday;
  private OffsetDateTime startTime;
  private OffsetDateTime endTime;
  private List<String> exceptions;
  private Boolean isAllDay;
}
