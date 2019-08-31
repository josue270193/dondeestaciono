package uni.app.dondeestaciono.ruta.model;

import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Ruta {

  @Id private String id;
  private String nombre;
  private List<LatLng> puntos;
  private LineaEstilo linea;
}
