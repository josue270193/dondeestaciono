package uni.app.dondeestaciono.config.convert;

import java.time.OffsetDateTime;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class OffsetDateTimeWriteConvert implements Converter<OffsetDateTime, Date> {

  @Override
  public Date convert(OffsetDateTime offsetDateTime) {
    return Date.from(offsetDateTime.toInstant());
  }
}
