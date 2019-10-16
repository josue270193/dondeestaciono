package uni.app.dondeestaciono.route.service;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.geojson.Polygon;
import com.mongodb.client.model.geojson.Position;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import uni.app.dondeestaciono.route.model.Route;
import uni.app.dondeestaciono.route.repository.RouteRepository;
import uni.app.dondeestaciono.util.geo.CircularDrawingAlgorithm;
import uni.app.dondeestaciono.util.geo.PositionDto;

@Service
public class RouteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(RouteService.class);
  private static final String COLLECTION_ROUTE = "route";
  private static final String FIELD_GEOMETRY = "geometry";

  private final MongoClient mongoClient;
  private final MongoProperties mongoProperties;
  private final RouteRepository routeRepository;
  private MongoCollection<Route> collection;

  public RouteService(
      RouteRepository routeRepository, MongoClient mongoClient, MongoProperties mongoProperties) {
    this.routeRepository = routeRepository;
    this.mongoClient = mongoClient;
    this.mongoProperties = mongoProperties;

    CodecRegistry pojoCodecRegistry =
        org.bson.codecs.configuration.CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            org.bson.codecs.configuration.CodecRegistries.fromProviders(
                PojoCodecProvider.builder().automatic(true).build()));
    collection =
        this.mongoClient
            .getDatabase(this.mongoProperties.getDatabase())
            .withCodecRegistry(pojoCodecRegistry)
            .getCollection(COLLECTION_ROUTE, Route.class);
  }

  public Flux<Route> getByPositionAndRadius(Double latitude, Double longitude, Double radius) {
    List<PositionDto> circlePoints =
        CircularDrawingAlgorithm.getInstance()
            .getCirclePositions(new PositionDto(longitude, latitude), radius);

    List<Position> positions =
        circlePoints.stream()
            .map(positionDto -> new Position(positionDto.getLongitude(), positionDto.getLatitude()))
            .collect(Collectors.toList());

    return Flux.from(collection.find(Filters.geoIntersects(FIELD_GEOMETRY, new Polygon(positions))))
        .flatMap(route -> routeRepository.findById(route.getId()));
  }
}
