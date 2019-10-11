package uni.app.dondeestaciono.config.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import uni.app.dondeestaciono.route.model.caba.EstacionamientoInstanciaContenidoDto;

public class EstacionamientoInstanciaContenidoDeserializer
    extends JsonDeserializer<EstacionamientoInstanciaContenidoDto> {

  @Override
  public EstacionamientoInstanciaContenidoDto deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {
    JsonToken jsonToken = p.getCurrentToken();
    if (jsonToken == JsonToken.VALUE_STRING) {
      return null;
    }
    return ctxt.readValue(p, EstacionamientoInstanciaContenidoDto.class);
  }
}
