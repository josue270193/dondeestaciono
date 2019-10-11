package uni.app.dondeestaciono.route.model.caba;

import lombok.Data;

@Data
public class EstacionamientoGeoFeatureDto {

  private String id;
  private EstacionamientoGeoFeatureGeometry geometry;
}
