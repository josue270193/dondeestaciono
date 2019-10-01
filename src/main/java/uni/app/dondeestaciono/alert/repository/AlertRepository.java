package uni.app.dondeestaciono.alert.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import uni.app.dondeestaciono.alert.model.Alert;

public interface AlertRepository extends ReactiveCrudRepository<Alert, String> {}
