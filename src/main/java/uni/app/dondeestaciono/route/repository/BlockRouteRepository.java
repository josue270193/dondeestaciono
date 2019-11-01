package uni.app.dondeestaciono.route.repository;

import java.time.OffsetDateTime;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uni.app.dondeestaciono.route.model.BlockRoute;

public interface BlockRouteRepository extends ReactiveCrudRepository<BlockRoute, String> {

  Mono<BlockRoute> findByTweetDataId(String id);

  Flux<BlockRoute> findByStartedGreaterThanEqualAndPointIsNotNull(OffsetDateTime dateTime);
}
