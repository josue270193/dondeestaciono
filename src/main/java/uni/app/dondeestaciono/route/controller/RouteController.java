package uni.app.dondeestaciono.route.controller;

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
import uni.app.dondeestaciono.route.model.Route;
import uni.app.dondeestaciono.route.repository.RouteRepository;
import uni.app.dondeestaciono.route.service.ApiCabaService;

@RestController
@RequestMapping("/route")
public class RouteController {

  private static final Logger LOGGER = LoggerFactory.getLogger(RouteController.class);

  private final RouteRepository routeRepository;
  private final ApiCabaService apiCabaService;

  public RouteController(RouteRepository routeRepository, ApiCabaService apiCabaService) {
    this.routeRepository = routeRepository;
    this.apiCabaService = apiCabaService;
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
  public Flux<Route> getByPosition(
      @RequestParam(name = "latitude") Double latitude,
      @RequestParam(name = "longitude") Double longitude) {
    LOGGER.debug("getByPosition latitude: {} longitude: {}", latitude, longitude);
    return apiCabaService.getTransporteApi(latitude, longitude);
  }
}
