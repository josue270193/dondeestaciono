package uni.app.dondeestaciono.route.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import reactor.core.publisher.Flux;
import uni.app.dondeestaciono.config.property.CabaProperties;
import uni.app.dondeestaciono.route.model.normalizacion.DireccionNormalizada;
import uni.app.dondeestaciono.route.model.normalizacion.ObjectoNormalizado;

@Service
public class ApiNormalizadorService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiNormalizadorService.class);
  private static final String PARAMETRO_DIRECCION = "direccion";
  private static final String PARTIDO_CABA = ",caba";
  private static final String PARAMETRO_GEOCODIFICAR = "geocodificar";

  private final WebClient webClient;
  private final CabaProperties cabaProperties;

  public ApiNormalizadorService(Builder webClient, CabaProperties cabaProperties) {

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    ExchangeStrategies strategies =
        ExchangeStrategies.builder()
            .codecs(
                configurer -> {
                  configurer
                      .defaultCodecs()
                      .jackson2JsonEncoder(
                          new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                  configurer
                      .defaultCodecs()
                      .jackson2JsonDecoder(
                          new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                })
            .build();

    this.cabaProperties = cabaProperties;
    this.webClient =
        webClient
            .baseUrl(cabaProperties.getUrlBaseNormalizador())
            .exchangeStrategies(strategies)
            .build();
  }

  public Flux<DireccionNormalizada> getDireccionNormalizada(String direccion) {
    MultiValueMap<String, String> params = getParam(direccion);

    return this.webClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(cabaProperties.getUrls().getNormalizador())
                    .queryParams(params)
                    .build())
        .retrieve()
        .bodyToMono(ObjectoNormalizado.class)
        .flatMapIterable(ObjectoNormalizado::getDireccionesNormalizadas)
        .map(
            direccionNormalizada -> {
              LOGGER.info("normalizado: {}", direccionNormalizada);
              return direccionNormalizada;
            });
  }

  private MultiValueMap<String, String> getParam(String direccion) {
    MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
    param.set(PARAMETRO_DIRECCION, direccion.concat(PARTIDO_CABA));
    param.set(PARAMETRO_GEOCODIFICAR, "true");
    return param;
  }
}
