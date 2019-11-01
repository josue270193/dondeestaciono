package uni.app.dondeestaciono.route.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uni.app.dondeestaciono.route.model.BlockRoute;
import uni.app.dondeestaciono.route.model.Route;
import uni.app.dondeestaciono.route.repository.BlockRouteRepository;
import uni.app.dondeestaciono.route.repository.RouteRepository;
import uni.app.dondeestaciono.route.service.ApiCabaService;
import uni.app.dondeestaciono.route.service.RouteService;
import uni.app.dondeestaciono.route.service.TwitterService;

@RestController
@RequestMapping("/route")
public class RouteController {

  private static final Logger LOGGER = LoggerFactory.getLogger(RouteController.class);

  private final BlockRouteRepository blockRouteRepository;
  private final RouteRepository routeRepository;
  private final RouteService routeService;
  private final ApiCabaService apiCabaService;
  private final TwitterService twitterService;

  public RouteController(
      BlockRouteRepository blockRouteRepository,
      RouteRepository routeRepository,
      RouteService routeService,
      ApiCabaService apiCabaService,
      TwitterService twitterService) {
    this.blockRouteRepository = blockRouteRepository;
    this.routeRepository = routeRepository;
    this.routeService = routeService;
    this.apiCabaService = apiCabaService;
    this.twitterService = twitterService;
  }

  @GetMapping("/")
  public Flux<Route> getAll() {
    LOGGER.debug("getAll");
    return routeRepository.findAll();
  }

  @GetMapping("/{route_id}")
  public Mono<ResponseEntity<Route>> getOne(@PathVariable(name = "route_id") String routeId) {
    LOGGER.debug("getOne {}", routeId);
    return routeRepository
        .findById(routeId)
        .map(routeSaved -> ResponseEntity.ok().body(routeSaved))
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PostMapping("/")
  public Mono<ResponseEntity<Route>> insertOne(@RequestBody Route route) {
    LOGGER.debug("insertOne {}", route);
    return routeRepository
        .save(route)
        .map(routeSaved -> ResponseEntity.ok().body(routeSaved))
        .defaultIfEmpty(ResponseEntity.badRequest().build());
  }

  @GetMapping("/filter")
  public Flux<Route> getByPosition(@RequestParam Double latitude, @RequestParam Double longitude) {
    LOGGER.debug("getByPosition latitude: {} longitude: {}", latitude, longitude);
    return apiCabaService.getEstacionamientoApi(latitude, longitude);
  }

  @GetMapping("/filterByRadius")
  public Flux<Route> getByPositionRadius(
      @RequestParam Double latitude, @RequestParam Double longitude, @RequestParam Double radius) {
    LOGGER.debug(
        "getByPositionRadius latitude: {} longitude: {} radius: {}", latitude, longitude, radius);

    return routeService.getByPositionAndRadius(latitude, longitude, radius);
  }

  @GetMapping("/block")
  public Flux<BlockRoute> getBlocksByDay() {
    return blockRouteRepository.findByStartedGreaterThanEqualAndPointIsNotNull(
        LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC));
  }

  @GetMapping("/blockByMonth")
  public Flux<BlockRoute> getBlocksByMonth() {
    return blockRouteRepository.findByStartedGreaterThanEqualAndPointIsNotNull(
        YearMonth.now().atDay(1).atStartOfDay().atOffset(ZoneOffset.UTC));
  }

  @GetMapping("/tweetBlock")
  public Flux retrieveBlock() {
    return twitterService.getTweetBlockRoute();
  }
}
