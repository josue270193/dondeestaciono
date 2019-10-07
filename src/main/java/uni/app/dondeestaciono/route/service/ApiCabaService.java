package uni.app.dondeestaciono.route.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import reactor.core.publisher.Mono;
import uni.app.dondeestaciono.config.property.CabaProperties;
import uni.app.dondeestaciono.route.model.Route;
import uni.app.dondeestaciono.route.model.dto.RouteDto;
import uni.app.dondeestaciono.route.model.dto.caba.EstacionamientoDto;
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

  public Flux<RouteDto> getTransporteApi(Double latitude, Double longitude) {
    MultiValueMap<String, String> params = getMapTransporte(latitude, longitude, "json");

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
              LOGGER.info("instanciaDto: {}", instanciaDto);
              return routeRepository
                  .findById(instanciaDto.getId())
                  .defaultIfEmpty(new Route())
                  .flatMap(
                      route -> {
                        if (route.getId() == null) {
                          return obtenerTransporteData(latitude, longitude);
                        }
                        return Mono.just(route);
                      })
                  .flux();
            })
        .map(
            routeSaved -> {
              RouteDto routeDto = new RouteDto();
              BeanUtils.copyProperties(routeSaved, routeDto);
              return routeDto;
            })
    ;
  }

  private Mono<?> obtenerTransporteData(Double latitude, Double longitude) {
    MultiValueMap<String, String> params = getMapTransporte(latitude, longitude, "geojson", true);

    return this.webClient
        .get()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path(cabaProperties.getUrls().getEstacionamientos())
                    .queryParams(params)
                    .build())
        .retrieve()
        .bodyToMono(EstacionamientoDto.class);
  }

  private MultiValueMap<String, String> getMapTransporte(Double latitude, Double longitude,
      String formato, Boolean isFullInfo) {
    paramTransporte.set("x", longitude.toString());
    paramTransporte.set("y", latitude.toString());
    paramTransporte.set("formato", formato);
    paramTransporte.add("fullInfo", isFullInfo.toString());
    return paramTransporte;
  }

  private MultiValueMap<String, String> getMapTransporte(
      Double latitude, Double longitude, String formato) {
    return this.getMapTransporte(latitude, longitude, formato, false);
  }
}
