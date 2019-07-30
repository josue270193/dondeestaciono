package uni.app.dondeestaciono.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import uni.app.dondeestaciono.model.Alerta;

public interface AlertaRepository extends ReactiveCrudRepository<Alerta, String> {

}
