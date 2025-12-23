package app.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        AdminSeedConfig.class,
        UsersSeedConfig.class,
        MinioProperties.class
})
public class PropsConfig {
}
