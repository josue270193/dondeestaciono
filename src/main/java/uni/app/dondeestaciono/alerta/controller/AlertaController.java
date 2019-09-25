package uni.app.dondeestaciono.alerta.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uni.app.dondeestaciono.alerta.model.AlertaDto;
import uni.app.dondeestaciono.alerta.repository.AlertaRepository;

@RestController
@RequestMapping("/alerta")
public class AlertaController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlertaController.class);

  private final AlertaRepository alertaRepository;

  public AlertaController(AlertaRepository alertaRepository) {
    this.alertaRepository = alertaRepository;
  }

  @GetMapping("/")
  public Flux<AlertaDto> getAll() {
    LOGGER.debug("getAll");
    return alertaRepository.findAll().log();
  }

  @GetMapping("/{id_alerta}")
  public Mono<AlertaDto> getOne(@PathVariable(name = "id_alerta") String id) {
    LOGGER.debug("getOne {}", id);
    return alertaRepository.findById(id).log();
  }

  @PostMapping("/")
  public Mono<AlertaDto> insertOne(@RequestBody AlertaDto alerta) {
    LOGGER.debug("insertOne {}", alerta);
    return alertaRepository.save(alerta).log();
  }
}
