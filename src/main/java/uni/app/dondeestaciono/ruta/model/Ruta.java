package uni.app.dondeestaciono.ruta.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Ruta {

	@Id
	private String id;
	private String nombre;
}
