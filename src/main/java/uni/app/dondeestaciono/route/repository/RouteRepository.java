package uni.app.dondeestaciono.route.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import uni.app.dondeestaciono.route.model.Route;

public interface RouteRepository extends ReactiveCrudRepository<Route, String> {}
