package uni.app.dondeestaciono.alerta.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import uni.app.dondeestaciono.alerta.model.Alerta;

public interface AlertaRepository extends ReactiveCrudRepository<Alerta, String> {

}
