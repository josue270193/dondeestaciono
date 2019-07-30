package uni.app.dondeestaciono.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uni.app.dondeestaciono.model.Alerta;
import uni.app.dondeestaciono.repository.AlertaRepository;

@RestController
@RequestMapping("/alerta")
public class AlertaController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlertaController.class);

	private final AlertaRepository alertaRepository;

	public AlertaController(AlertaRepository alertaRepository) {
		this.alertaRepository = alertaRepository;
	}

	@GetMapping("/")
	public Flux<Alerta> getAll() {
		LOGGER.debug("getAll");
		return alertaRepository.findAll()
				.log();
	}

	@GetMapping("/{id_alerta}")
	public Mono<Alerta> getOne(
			@PathVariable(name = "id_alerta") String id
	) {
		LOGGER.debug("getOne - " + id);
		return alertaRepository
				.findById(id)
				.log();
	}

	@PostMapping("/")
	public Mono<Alerta> insertOne(@RequestBody Alerta alerta) {
		LOGGER.debug("insertOne - " + alerta);
		return alertaRepository
				.save(alerta)
				.log();
	}
}
