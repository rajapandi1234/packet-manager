package io.mosip.registration.processor.biodedupe.config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePropertySource;

import io.mosip.registration.processor.bio.dedupe.service.impl.BioDedupeServiceImpl;
import io.mosip.registration.processor.biodedupe.stage.BioDedupeStage;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;

@PropertySource("classpath:bootstrap.properties")
@Configuration
public class BioDedupeBeanConfig {
	@Bean
	public PropertySourcesPlaceholderConfigurer getPropertySourcesPlaceholderConfigurer(Environment env) throws IOException {

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
		List<String> applicationNames = getAppNames(env);
		Resource[] appResources = new Resource[applicationNames.size()];

		for (int i = 0; i < applicationNames.size(); i++) {
			String loc = env.getProperty("spring.cloud.config.uri") + "/registration-processor/"
					+ env.getProperty("spring.profiles.active") + "/" + env.getProperty("spring.cloud.config.label")
					+ "/" + applicationNames.get(i) + "-" + env.getProperty("spring.profiles.active") + ".properties";
			appResources[i] = resolver.getResources(loc)[0];
			((AbstractEnvironment) env).getPropertySources()
            .addLast(new ResourcePropertySource(applicationNames.get(i), loc));
		}
		pspc.setLocations(appResources);
		return pspc;

	}

	public List<String> getAppNames(Environment env) {
		String names = env.getProperty("spring.application.name");
		return Stream.of(names.split(",")).collect(Collectors.toList());
	}
	
	@Bean
	public BioDedupeService getBioDedupeService() {
		return new BioDedupeServiceImpl();
	}
	
	@Bean 
	public BioDedupeStage getBioDedupeStage() {
		return new BioDedupeStage();
	}

}
