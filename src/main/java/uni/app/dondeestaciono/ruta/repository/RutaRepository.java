package uni.app.dondeestaciono.ruta.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import uni.app.dondeestaciono.ruta.model.Ruta;

public interface RutaRepository extends ReactiveCrudRepository<Ruta, String> {}
