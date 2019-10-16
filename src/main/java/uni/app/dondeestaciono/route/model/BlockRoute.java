package uni.app.dondeestaciono.route.model;

import java.time.OffsetDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
public class BlockRoute {

  @Id private String id;
  private TweetData tweetData;
  private OffsetDateTime started;
  private OffsetDateTime finished;
  private String direction;
  private String type;
  private Point point;
}
