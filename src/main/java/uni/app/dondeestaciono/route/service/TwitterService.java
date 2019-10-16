package uni.app.dondeestaciono.route.service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import uni.app.dondeestaciono.config.property.TwitterProperties;
import uni.app.dondeestaciono.route.model.BlockRoute;
import uni.app.dondeestaciono.route.model.Point;
import uni.app.dondeestaciono.route.model.TweetData;
import uni.app.dondeestaciono.route.repository.BlockRouteRepository;

@Service
public class TwitterService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TwitterService.class);
  private static final String SEPARADOR_GUION_MEDIO = "-";
  private static final String SEPARADOR_COMA = ",";
  private static final String WORD_CORTE_TOTAL = "total";
  private static final String WORD_CORTE_PARCIAL = "parcial";
  private static final String WORD_EMPTY = "";
  private static final String WORD_CORTE = "corte";

  private final BlockRouteRepository blockRouteRepository;
  private final ApiNormalizadorService apiNormalizadorService;
  private final TwitterProperties twitterProperties;
  private final String WORD_FIN_DE = "fin de";
  private final String WORD_HABILITADO = "habilitado";
  private Twitter twitter;

  public TwitterService(
      BlockRouteRepository blockRouteRepository,
      ApiNormalizadorService apiNormalizadorService,
      TwitterProperties twitterProperties) {
    this.blockRouteRepository = blockRouteRepository;
    this.apiNormalizadorService = apiNormalizadorService;
    this.twitterProperties = twitterProperties;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void configuration() {
    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setDebugEnabled(twitterProperties.getDebug())
        .setOAuthConsumerKey(twitterProperties.getConsumerKey())
        .setOAuthConsumerSecret(twitterProperties.getConsumerSecret())
        .setOAuthAccessToken(twitterProperties.getAccessToken())
        .setOAuthAccessTokenSecret(twitterProperties.getAccessTokenSecret());
    TwitterFactory tf = new TwitterFactory(cb.build());
    twitter = tf.getInstance();
  }

  public Flux getTweetBlockRoute() {
    try {
      Query query = new Query(createQueryZones());
      QueryResult search = twitter.search(query);
      return Flux.fromIterable(search.getTweets()).flatMap(this::generateBlockRoute);
    } catch (TwitterException e) {
      LOGGER.info(e.getMessage(), e);
    }
    return Flux.empty();
  }

  private Publisher<?> generateBlockRoute(Status status) {
    return Mono.just(status)
        .flatMap(
            tweet -> {
              if (isStarted(status)) {
                return generateStartedBlockRoute(status);
              } else {
                return Mono.empty();
              }
            });
  }

  private Mono<?> generateStartedBlockRoute(Status status) {
    return Mono.just(status)
        .flatMap(tweet -> blockRouteRepository.findByTweetDataId(String.valueOf(tweet.getId())))
        .switchIfEmpty(
            Mono.just(status)
                .map(this::createBlockRoute)
                .flatMap(this::normalizarDireccion)
                .flatMap(blockRouteRepository::save));
  }

  private Mono<BlockRoute> normalizarDireccion(BlockRoute data) {
    return Mono.just(data)
        .flatMapMany(
            blockRoute -> apiNormalizadorService.getDireccionNormalizada(blockRoute.getDirection()))
        .next()
        .flatMap(
            direccionNormalizada -> {
              if (direccionNormalizada.getCoordenadas() != null) {
                Point point = new Point();
                point.setLatitude(
                    Double.valueOf(direccionNormalizada.getCoordenadas().getLatitude()));
                point.setLongitude(
                    Double.valueOf(direccionNormalizada.getCoordenadas().getLongitude()));
                data.setPoint(point);
              }
              return Mono.just(data);
            })
        .switchIfEmpty(Mono.just(data));
  }

  private boolean isStarted(Status tweet) {
    return !isFinished(tweet);
  }

  private boolean isFinished(Status tweet) {
    String message = tweet.getText().toLowerCase();
    return message.contains(WORD_FIN_DE) || message.contains(WORD_HABILITADO);
  }

  private BlockRoute createBlockRoute(Status tweet) {
    String message = tweet.getText();
    OffsetDateTime createdDate =
        OffsetDateTime.ofInstant(tweet.getCreatedAt().toInstant(), ZoneId.systemDefault());

    BlockRoute blockRoute = new BlockRoute();

    TweetData tweetData = new TweetData();
    tweetData.setId(String.valueOf(tweet.getId()));
    tweetData.setMessage(message);
    tweetData.setCreateAt(createdDate);

    blockRoute.setTweetData(tweetData);
    blockRoute.setStarted(createdDate);
    blockRoute.setDirection(getDirectionByMessage(message));
    blockRoute.setType(getTypeBlockByMessage(message.toLowerCase()));
    return blockRoute;
  }

  private String getTypeBlockByMessage(String message) {
    if (message.contains(WORD_CORTE_TOTAL)) {
      return WORD_CORTE_TOTAL;
    }
    if (message.contains(WORD_CORTE_PARCIAL)) {
      return WORD_CORTE_PARCIAL;
    }
    return null;
  }

  private String getDirectionByMessage(String message) {
    String[] split = message.split(SEPARADOR_GUION_MEDIO);
    if (split.length > 1) {
      return split[0];
    }
    split = message.split(SEPARADOR_COMA);
    if (split.length > 1) {
      return split[0];
    }
    split = message.split(WORD_CORTE);
    if (split.length > 1) {
      return split[0];
    }
    return WORD_EMPTY;
  }

  private String createQueryZones() {
    return "from:batransito corte";
  }
}
