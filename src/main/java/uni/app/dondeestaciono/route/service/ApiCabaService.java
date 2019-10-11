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
import uni.app.dondeestaciono.route.model.Point;
import uni.app.dondeestaciono.route.model.Route;
import uni.app.dondeestaciono.route.model.caba.EstacionamientoDto;
import uni.app.dondeestaciono.route.model.caba.EstacionamientoGeoDto;
import uni.app.dondeestaciono.route.model.caba.EstacionamientoGeoFeatureDto;
import uni.app.dondeestaciono.route.repository.RouteRepository;

@Service
public class ApiCabaService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiCabaService.class);

  private final WebClient webClient;
  private final CabaProperties cabaProperties;
  private final RouteRepository routeRepository;
  private MultiValueMap<String, String> paramTransporte;

  public ApiCabaService(
      Builder webClient, CabaProperties cabaProperties, RouteRepository routeRepository) {

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
        webClient.baseUrl(cabaProperties.getUrlBase()).exchangeStrategies(strategies).build();
    this.routeRepository = routeRepository;

    paramTransporte = new LinkedMultiValueMap<>();
    paramTransporte.add("radio", "100");
    paramTransporte.add("client_id", cabaProperties.getClientId());
    paramTransporte.add("client_secret", cabaProperties.getClientSecret());
  }

  public Flux<Route> getTransporteApi(Double latitude, Double longitude) {
    return obtenerGeoData(latitude, longitude).thenMany(obtenerJsonData(latitude, longitude));
  }

  private Flux<Route> obtenerJsonData(Double latitude, Double longitude) {
    MultiValueMap<String, String> params = getMapTransporte(latitude, longitude, "json", true);

    return this.webClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(cabaProperties.getUrls().getEstacionamientos())
                    .queryParams(params)
                    .build())
        .retrieve()
        .bodyToMono(EstacionamientoDto.class)
        .flatMapIterable(EstacionamientoDto::getInstancias)
        .flatMap(
            instanciaDto -> {
              LOGGER.info("data: {}", instanciaDto);
              return routeRepository.findById(instanciaDto.getId());
            });
  }

  private Flux<?> obtenerGeoData(Double latitude, Double longitude) {
    MultiValueMap<String, String> params = getMapTransporte(latitude, longitude, "geojson");

    return this.webClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(cabaProperties.getUrls().getEstacionamientos())
                    .queryParams(params)
                    .build())
        .retrieve()
        .bodyToMono(EstacionamientoGeoDto.class)
        .flatMapIterable(EstacionamientoGeoDto::getFeatures)
        .filterWhen(
            featureDto -> routeRepository.existsById(featureDto.getId()).map(existed -> !existed))
        .map(this::createRoute)
        .flatMap(routeRepository::save);
  }

  private Route createRoute(EstacionamientoGeoFeatureDto featureDto) {
    Route route = new Route();
    route.setId(featureDto.getId());
    featureDto
        .getGeometry()
        .getCoordinates()
        .forEach(
            lista1 ->
                lista1.forEach(
                    lista2 -> {
                      Point point = new Point();
                      lista2.forEach(
                          valorDouble -> {
                            if (point.getLongitude() == null) {
                              point.setLongitude(valorDouble);
                            } else {
                              point.setLatitude(valorDouble);
                            }
                          });
                      route.getPoints().add(point);
                    }));
    return route;
  }

  private MultiValueMap<String, String> getMapTransporte(
      Double latitude, Double longitude, String formato, Boolean isFullInfo) {
    paramTransporte.set("x", longitude.toString());
    paramTransporte.set("y", latitude.toString());
    paramTransporte.set("formato", formato);
    paramTransporte.set("fullInfo", isFullInfo.toString());
    return paramTransporte;
  }

  private MultiValueMap<String, String> getMapTransporte(
      Double latitude, Double longitude, String formato) {
    return this.getMapTransporte(latitude, longitude, formato, false);
  }
}
