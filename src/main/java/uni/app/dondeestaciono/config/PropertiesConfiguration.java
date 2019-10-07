package uni.app.dondeestaciono.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import uni.app.dondeestaciono.config.property.CabaProperties;
import uni.app.dondeestaciono.config.property.CabaUrlsProperties;
import uni.app.dondeestaciono.config.property.FirebaseProperties;

@Configuration
@EnableConfigurationProperties(
    value = {CabaProperties.class, CabaUrlsProperties.class, FirebaseProperties.class})
public class PropertiesConfiguration {}
