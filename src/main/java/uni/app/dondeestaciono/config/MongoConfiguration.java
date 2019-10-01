package uni.app.dondeestaciono.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import uni.app.dondeestaciono.config.convert.OffsetDateTimeReadConvert;
import uni.app.dondeestaciono.config.convert.OffsetDateTimeWriteConvert;

@Configuration
public class MongoConfiguration {

  @Bean
  public MongoCustomConversions customConversions() {
    List<Converter<?, ?>> converters = new ArrayList<>();
    converters.add(new OffsetDateTimeWriteConvert());
    converters.add(new OffsetDateTimeReadConvert());
    return new MongoCustomConversions(converters);
  }
}
