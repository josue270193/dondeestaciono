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
import uni.app.dondeestaciono.route.model.EnumBlockType;
import uni.app.dondeestaciono.route.model.Point;
import uni.app.dondeestaciono.route.model.TweetData;
import uni.app.dondeestaciono.route.repository.BlockRouteRepository;

@Service
public class TwitterService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TwitterService.class);
  private static final String QUERY_CORTE_BA = "from:batransito corte";
  private static final String SEPARADOR_GUION_MEDIO = "-";
  private static final String SEPARADOR_COMA = ",";
  private static final String WORD_CORTE_TOTAL = "total";
  private static final String WORD_EMPTY = "";
  private static final String WORD_CORTE = "corte";
  private static final String WORD_SENTIDO = "sentido";
  private static final String WORD_CONTINUA = "continua";
  private static final String WORD_FIN_DE = "fin de";
  private static final String WORD_HABILITADO = "habilitado";
  private static final String WORD_MANIFESTANTE = "manifestante";
  private static final String WORD_MANIFESTACION = "manifestacion";
  private static final String WORD_INCIDENTE = "incidente";
  private static final String WORD_OPERATIVO = "operativo";
  private static final String WORD_ALTURA = "altura";
  private static final String WORD_Y = "y";
  private static final String WORD_O_ACENTO = "ó";
  private static final String WORD_I_ACENTO = "í";
  private static final String WORD_A_ACENTO = "á";
  private static final String WORD_E_ACENTO = "é";
  private static final String WORD_O = "o";
  private static final String WORD_I = "i";
  private static final String WORD_A = "a";
  private static final String WORD_E = "e";
  private static final String WORD_PUNTO = "\\.";
  private static final String WORD_ESPACIO = " ";

  private final BlockRouteRepository blockRouteRepository;
  private final ApiNormalizadorService apiNormalizadorService;
  private final TwitterProperties twitterProperties;
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
              if (isStarted(status) && isNew(status)) {
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
            blockRoute -> {
              String direction = blockRoute.getDirection();
              direction = transformarDireccion(direction);
              return apiNormalizadorService.getDireccionNormalizada(direction);
            })
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

  private boolean isNew(Status tweet) {
    return !isContinuous(tweet);
  }

  private boolean isContinuous(Status tweet) {
    String message = tweet.getText().toLowerCase();
    return message.contains(WORD_CONTINUA);
  }

  private String transformarDireccion(String direction) {
    direction = direction.replace(WORD_ALTURA, WORD_Y);
    direction = direction.replaceAll(WORD_PUNTO, WORD_ESPACIO);
    return direction;
  }

  private BlockRoute createBlockRoute(Status tweet) {
    String message = tweet.getText();
    message = normalizarMensaje(message);
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
    blockRoute.setType(getTypeBlockByMessage(message));
    blockRoute.setIsTotal(isTotalBlock(message));

    return blockRoute;
  }

  private String normalizarMensaje(String message) {
    message = message.toLowerCase();
    message = message.replace(WORD_O_ACENTO, WORD_O);
    message = message.replace(WORD_I_ACENTO, WORD_I);
    message = message.replace(WORD_A_ACENTO, WORD_A);
    message = message.replace(WORD_E_ACENTO, WORD_E);
    return message;
  }

  private Boolean isTotalBlock(String message) {
    return WORD_CORTE_TOTAL.contains(message);
  }

  private EnumBlockType getTypeBlockByMessage(String message) {
    if (message.contains(WORD_MANIFESTANTE) || message.contains(WORD_MANIFESTACION)) {
      return EnumBlockType.MANIFESTACION;
    }
    if (message.contains(WORD_INCIDENTE)) {
      return EnumBlockType.INCIDENTE;
    }
    if (message.contains(WORD_OPERATIVO)) {
      return EnumBlockType.OPERATIVO;
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
    split = message.split(WORD_SENTIDO);
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
    return QUERY_CORTE_BA;
  }
}
