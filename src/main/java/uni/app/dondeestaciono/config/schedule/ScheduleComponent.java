package uni.app.dondeestaciono.config.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uni.app.dondeestaciono.route.service.TwitterService;

@Component
@EnableAsync
public class ScheduleComponent {
  private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleComponent.class);
  private final TwitterService twitterService;

  public ScheduleComponent(TwitterService twitterService) {
    this.twitterService = twitterService;
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void scheduleTwitterDaily() {
    LOGGER.debug("Event scheduleTwitterDaily");
  }

  @Async
  @Scheduled(cron = "0 0 */1 * * ?")
  public void scheduleTwitterHourly() {
    LOGGER.debug("Event scheduleTwitterHourly");
    twitterService.getTweetBlockRoute().subscribe();
  }
}
