package uni.app.dondeestaciono.alert.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uni.app.dondeestaciono.alert.model.Alert;
import uni.app.dondeestaciono.alert.model.dto.AlertDto;
import uni.app.dondeestaciono.alert.repository.AlertRepository;

@RestController
@RequestMapping("/alert")
public class AlertController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AlertController.class);

  private final AlertRepository alertRepository;

  public AlertController(AlertRepository alertRepository) {
    this.alertRepository = alertRepository;
  }

  @GetMapping("/")
  public Flux<AlertDto> getAll() {
    LOGGER.debug("getAll");
    return alertRepository
        .findAll()
        .map(
            alertSaved -> {
              AlertDto alertDto = new AlertDto();
              BeanUtils.copyProperties(alertSaved, alertDto);
              return alertDto;
            });
  }

  @GetMapping("/{alert_id}")
  public Mono<ResponseEntity<AlertDto>> getOne(@PathVariable(name = "alert_id") String alertId) {
    LOGGER.debug("getOne {}", alertId);
    return alertRepository
        .findById(alertId)
        .map(
            alertSaved -> {
              AlertDto alertDto = new AlertDto();
              BeanUtils.copyProperties(alertSaved, alertDto);
              return ResponseEntity.ok().body(alertDto);
            })
        .defaultIfEmpty(ResponseEntity.notFound().build());
  }

  @PostMapping("/")
  public Mono<ResponseEntity<AlertDto>> insertOne(@RequestBody AlertDto alertDto) {
    LOGGER.debug("insertOne {}", alertDto);
    Alert alert = new Alert();
    BeanUtils.copyProperties(alertDto, alert);
    return alertRepository
        .save(alert)
        .map(
            alertSaved -> {
              BeanUtils.copyProperties(alertSaved, alertDto);
              return ResponseEntity.ok().body(alertDto);
            })
        .defaultIfEmpty(ResponseEntity.badRequest().build());
  }
}
