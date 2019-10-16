package uni.app.dondeestaciono.route.model.normalizacion;

import lombok.Data;

@Data
public class DireccionNormalizada {

  private String direccion;
  private DireccionNormalizadaCoordenada coordenadas;
}
