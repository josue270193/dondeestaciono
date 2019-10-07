package uni.app.dondeestaciono.route.model.dto.caba;

import lombok.Data;

@Data
public class EstacionamientoInstanciaDto {

  private String id;
  private String nombre;
  private EstacionamientoInstanciaContenidoDto contenido;
}
