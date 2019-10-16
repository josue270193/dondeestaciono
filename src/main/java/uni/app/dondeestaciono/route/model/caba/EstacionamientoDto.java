package uni.app.dondeestaciono.route.model.caba;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class EstacionamientoDto {

  private Long totalFull;
  private Long total;
  private List<EstacionamientoInstanciaDto> instancias = new ArrayList<>();
}
