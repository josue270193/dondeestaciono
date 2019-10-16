package uni.app.dondeestaciono.route.model.normalizacion;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ObjectoNormalizado {

  private List<DireccionNormalizada> direccionesNormalizadas = new ArrayList<>();
}
