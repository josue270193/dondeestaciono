package uni.app.dondeestaciono.route.model.caba;

import java.util.List;
import lombok.Data;

@Data
public class EstacionamientoGeoFeatureGeometry {

  private String type;
  private List<List<List<Double>>> coordinates;
}
