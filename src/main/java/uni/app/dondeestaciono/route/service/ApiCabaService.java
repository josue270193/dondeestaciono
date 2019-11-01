package uni.app.dondeestaciono.route.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
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
import reactor.core.publisher.Mono;
import uni.app.dondeestaciono.config.property.CabaProperties;
import uni.app.dondeestaciono.route.model.Point;
import uni.app.dondeestaciono.route.model.Route;
import uni.app.dondeestaciono.route.model.RouteDetails;
import uni.app.dondeestaciono.route.model.RouteSchedule;
import uni.app.dondeestaciono.route.model.RouteScheduleDetail;
import uni.app.dondeestaciono.route.model.EnumRoutePermit;
import uni.app.dondeestaciono.route.model.caba.EstacionamientoDto;
import uni.app.dondeestaciono.route.model.caba.EstacionamientoGeoDto;
import uni.app.dondeestaciono.route.model.caba.EstacionamientoGeoFeatureDto;
import uni.app.dondeestaciono.route.model.caba.EstacionamientoInstanciaContenidoDetalleDto;
import uni.app.dondeestaciono.route.model.caba.EstacionamientoInstanciaDto;
import uni.app.dondeestaciono.route.repository.RouteRepository;

@Service
public class ApiCabaService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ApiCabaService.class);
  private static final String PARAM_PERMISO = "permiso";
  private static final String PARAM_HORARIO = "horario";
  private static final String PARAM_CALLE = "calle";
  private static final String PARAM_ALTURA = "altura";
  private static final String VALOR_24_HORA = "24 HORAS";
  private static final String VALOR_HABILES_7_21 = "DÍAS HÁBILES DE 7 A 21 HORAS";
  private static final String SEPARADOR_WEEKDAY = ",";

  private final WebClient webClient;
  private final CabaProperties cabaProperties;
  private final RouteRepository routeRepository;

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
  }

  public Flux<Route> getEstacionamientoApi(Double latitude, Double longitude) {
    return getGeoData(latitude, longitude).thenMany(getJsonData(latitude, longitude));
  }

  private Flux<?> getGeoData(Double latitude, Double longitude) {
    MultiValueMap<String, String> params = getParamEstacionamiento(latitude, longitude, "geojson");

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

  private Flux<Route> getJsonData(Double latitude, Double longitude) {
    MultiValueMap<String, String> params =
        getParamEstacionamiento(latitude, longitude, "json", true);

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
        .flatMap(this::generateRoute);
  }

  private Publisher<? extends Route> generateRoute(EstacionamientoInstanciaDto instanciaDto) {
    return Mono.just(instanciaDto)
        .flatMap(dto -> routeRepository.findById(dto.getId()))
        .flatMap(
            route ->
                Mono.just(route)
                    .filter(routeFilter -> routeFilter.getSchedule() == null)
                    .map(
                        routeFilter -> {
                          routeFilter.setSchedule(createRouteSchedule(instanciaDto));
                          routeFilter.setDetails(createRouteDetails(instanciaDto));
                          return routeFilter;
                        })
                    .flatMap(routeRepository::save)
                    .switchIfEmpty(Mono.just(route)));
  }

  private RouteDetails createRouteDetails(EstacionamientoInstanciaDto instanciaDto) {
    RouteDetails routeDetails = new RouteDetails();
    Map<String, String> mapa =
        instanciaDto.getContenido().getContenido().stream()
            .collect(
                Collectors.toMap(
                    EstacionamientoInstanciaContenidoDetalleDto::getNombreId,
                    EstacionamientoInstanciaContenidoDetalleDto::getValor));

    String valorAltura = mapa.get(PARAM_ALTURA);
    String valorCalle = mapa.get(PARAM_CALLE);
    String valorHorario = mapa.get(PARAM_HORARIO);

    routeDetails.setAltura(valorAltura);
    routeDetails.setCalle(valorCalle);
    routeDetails.setHorario(valorHorario);

    return routeDetails;
  }

  private RouteSchedule createRouteSchedule(EstacionamientoInstanciaDto instanciaDto) {
    RouteSchedule routeSchedule = new RouteSchedule();
    Map<String, String> mapa =
        instanciaDto.getContenido().getContenido().stream()
            .collect(
                Collectors.toMap(
                    EstacionamientoInstanciaContenidoDetalleDto::getNombreId,
                    EstacionamientoInstanciaContenidoDetalleDto::getValor));

    String valorPermiso = mapa.get(PARAM_PERMISO);
    String valorHorario = mapa.get(PARAM_HORARIO);

    if (valorPermiso != null) {
      try {
        routeSchedule.setPermit(EnumRoutePermit.getByValue(valorPermiso));
      } catch (IllegalArgumentException ex) {
        LOGGER.error(ex.getMessage());
      }
    }

    if (valorHorario != null) {
      List<RouteScheduleDetail> details = null;

      if (VALOR_24_HORA.compareToIgnoreCase(valorHorario) == 0) {
        details = new ArrayList<>();
        details.add(createRouteScheduleDetail(getAllWeekday(), getStartTime(), getEndTime(), true));
      } else if (VALOR_HABILES_7_21.compareToIgnoreCase(valorHorario) == 0) {
        details = new ArrayList<>();
        details.add(
            createRouteScheduleDetail(getWeekdayWorkday(), getStartTime(), getEndTime(), false));
      }
      routeSchedule.setDetails(details);
    }

    return routeSchedule;
  }

  private RouteScheduleDetail createRouteScheduleDetail(
      String weekday, OffsetDateTime startTime, OffsetDateTime endTime, boolean isAllDay) {
    RouteScheduleDetail routeScheduleDetail = new RouteScheduleDetail();
    routeScheduleDetail.setIsAllDay(isAllDay);
    routeScheduleDetail.setWeekday(weekday);
    routeScheduleDetail.setStartTime(startTime);
    routeScheduleDetail.setEndTime(endTime);
    return routeScheduleDetail;
  }

  private String getAllWeekday() {
    return getWeekday(
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY,
        Calendar.SUNDAY);
  }

  private String getWeekdayWorkday() {
    return getWeekday(
        Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY);
  }

  private OffsetDateTime getStartTime() {
    return OffsetDateTime.parse("2019-01-01T07:00:00-03:00");
  }

  private OffsetDateTime getEndTime() {
    return OffsetDateTime.parse("2019-01-01T21:00:00-03:00");
  }

  private String getWeekday(Integer... days) {
    StringBuilder builder = new StringBuilder();
    Map<Integer, String> map = new HashMap<>();
    for (Integer value : days) {
      switch (value) {
        case Calendar.MONDAY:
          map.put(value, "MO");
          break;
        case Calendar.TUESDAY:
          map.put(value, "TU");
          break;
        case Calendar.WEDNESDAY:
          map.put(value, "WE");
          break;
        case Calendar.THURSDAY:
          map.put(value, "TH");
          break;
        case Calendar.FRIDAY:
          map.put(value, "FR");
          break;
        case Calendar.SATURDAY:
          map.put(value, "SA");
          break;
        case Calendar.SUNDAY:
          map.put(value, "SU");
          break;
        default:
          break;
      }
    }
    for (Integer key : new ArrayList<>(map.keySet())) {
      builder.append(map.get(key)).append(SEPARADOR_WEEKDAY);
    }
    if (builder.length() > 2) {
      return builder.subSequence(0, builder.length() - 1).toString();
    }
    return builder.toString();
  }

  private Route createRoute(EstacionamientoGeoFeatureDto featureDto) {
    Route route = new Route();
    route.setId(featureDto.getId());
    route.setGeometry(featureDto.getGeometry());
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

  private MultiValueMap<String, String> getParamEstacionamiento(
      Double latitude, Double longitude, String formato, Boolean isFullInfo) {
    MultiValueMap<String, String> paramEstacionamiento = new LinkedMultiValueMap<>();
    paramEstacionamiento.add("radio", "100");
    paramEstacionamiento.add("client_id", cabaProperties.getClientId());
    paramEstacionamiento.add("client_secret", cabaProperties.getClientSecret());
    paramEstacionamiento.set("x", longitude.toString());
    paramEstacionamiento.set("y", latitude.toString());
    paramEstacionamiento.set("formato", formato);
    paramEstacionamiento.set("fullInfo", isFullInfo.toString());
    return paramEstacionamiento;
  }

  private MultiValueMap<String, String> getParamEstacionamiento(
      Double latitude, Double longitude, String formato) {
    return this.getParamEstacionamiento(latitude, longitude, formato, false);
  }
}
