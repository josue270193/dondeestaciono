package uni.app.dondeestaciono.route.model.normalizacion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DireccionNormalizadaCoordenada {

  @JsonProperty(value = "x")
  private String longitude;

  @JsonProperty(value = "y")
  private String latitude;
}
