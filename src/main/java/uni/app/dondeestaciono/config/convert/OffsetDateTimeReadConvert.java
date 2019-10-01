package uni.app.dondeestaciono.config.convert;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class OffsetDateTimeReadConvert implements Converter<Date, OffsetDateTime> {

  @Override
  public OffsetDateTime convert(Date date) {
    return date.toInstant().atOffset(ZoneOffset.UTC);
  }
}
