package uni.app.dondeestaciono.ruta.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uni.app.dondeestaciono.ruta.model.Ruta;
import uni.app.dondeestaciono.ruta.repository.RutaRepository;

@RestController
@RequestMapping("/ruta")
public class RutaController {

	private static final Logger LOGGER = LoggerFactory.getLogger(
			RutaController.class);

	private final RutaRepository rutaRepository;

	public RutaController(RutaRepository rutaRepository) {
		this.rutaRepository = rutaRepository;
	}

	@GetMapping("/")
	public Flux<Ruta> getAll() {
		LOGGER.debug("getAll");
		return rutaRepository.findAll()
				.log();
	}

	@GetMapping("/{id_ruta}")
	public Mono<Ruta> getOne(
			@PathVariable(name = "id_ruta") String id
	) {
		LOGGER.debug("getOne - " + id);
		return rutaRepository
				.findById(id)
				.log();
	}

	@PostMapping("/")
	public Mono<Ruta> insertOne(@RequestBody Ruta ruta) {
		LOGGER.debug("insertOne - " + ruta);
		return rutaRepository
				.save(ruta)
				.log();
	}
}
