package uni.app.dondeestaciono.route.model.caba;

import java.util.List;
import lombok.Data;

@Data
public class EstacionamientoGeoDto {

  private List<EstacionamientoGeoFeatureDto> features;
}
