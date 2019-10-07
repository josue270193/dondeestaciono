package uni.app.dondeestaciono.route.model.dto.caba;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import uni.app.dondeestaciono.config.deserializer.EstacionamientoInstanciaContenidoDeserializer;

@Data
public class EstacionamientoInstanciaDto {

  private String id;
  private String nombre;

  @JsonDeserialize(using = EstacionamientoInstanciaContenidoDeserializer.class)
  private EstacionamientoInstanciaContenidoDto contenido;
}
