package uni.app.dondeestaciono.route.model;

import java.time.OffsetDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
public class TweetData {

  @Id private String id;
  private String message;
  private OffsetDateTime createAt;
}
