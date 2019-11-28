
package com.status.main.config;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import javax.servlet.Servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.DispatcherServlet;

import com.google.common.collect.Lists;

import io.github.jhipster.config.JHipsterProperties;
import io.github.jhipster.config.apidoc.customizer.SwaggerCustomizer;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ ApiInfo.class, BeanValidatorPluginsConfiguration.class, Servlet.class, DispatcherServlet.class })
@AutoConfigureAfter(JHipsterProperties.class)
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerAutoConfiguration {

	static final String STARTING_MESSAGE = "Starting Swagger";
	static final String STARTED_MESSAGE = "Started Swagger in {} ms";
	static final String MANAGEMENT_TITLE_SUFFIX = "Management API";
	static final String MANAGEMENT_GROUP_NAME = "management";
	static final String MANAGEMENT_DESCRIPTION = "Management endpoints documentation";

	private final Logger log = LoggerFactory.getLogger(SwaggerAutoConfiguration.class);

	private final JHipsterProperties jHipsterProperties;

	public SwaggerAutoConfiguration(JHipsterProperties jHipsterProperties) {
		this.jHipsterProperties = jHipsterProperties;
	}
	
	@Bean
	@ConditionalOnMissingBean(name = "swaggerSpringfoxApiDocket")
	public Docket swaggerSpringfoxApiDocket(List<SwaggerCustomizer> swaggerCustomizers,
			ObjectProvider<AlternateTypeRule[]> alternateTypeRules) {
		log.debug(STARTING_MESSAGE);
		StopWatch watch = new StopWatch();
		watch.start();

		Docket docket = createDocket().securitySchemes(Lists.newArrayList(apiKey()))
				.securityContexts(Collections.singletonList(securityContext()));
		swaggerCustomizers.forEach(customizer -> customizer.customize(docket));

		Optional.ofNullable(alternateTypeRules.getIfAvailable()).ifPresent(docket::alternateTypeRules);

		watch.stop();
		log.debug(STARTED_MESSAGE, watch.getTotalTimeMillis());
		return docket;
	}

	@Bean
	public AppSwaggerCustomizer tcxSwaggerCustomizer() {
		return new AppSwaggerCustomizer(jHipsterProperties);
	}

	/**
	 * Springfox configuration for the management endpoints (actuator) Swagger
	 * docs.
	 *
	 * @param appName
	 *            the application name
	 * @param managementContextPath
	 *            the path to access management endpoints
	 * @return the Swagger Springfox configuration
	 */
	@Bean
	@ConditionalOnProperty("management.endpoints.web.base-path")
	@ConditionalOnExpression("'${management.endpoints.web.base-path}'.length() > 0")
	@ConditionalOnMissingBean(name = "swaggerSpringfoxManagementDocket")
	public Docket swaggerSpringfoxManagementDocket(@Value("${spring.application.name:application}") String appName,
			@Value("${management.endpoints.web.base-path}") String managementContextPath) {

		ApiInfo apiInfo = new ApiInfo(StringUtils.capitalize(appName) + " " + MANAGEMENT_TITLE_SUFFIX,
				MANAGEMENT_DESCRIPTION, jHipsterProperties.getSwagger().getVersion(), "", ApiInfo.DEFAULT_CONTACT, "",
				"", new ArrayList<>());

		return createDocket().securitySchemes(Lists.newArrayList(apiKey()))
				.securityContexts(Collections.singletonList(securityContext())).apiInfo(apiInfo)
				.useDefaultResponseMessages(jHipsterProperties.getSwagger().isUseDefaultResponseMessages())
				.groupName(MANAGEMENT_GROUP_NAME).host(jHipsterProperties.getSwagger().getHost())
				.protocols(new HashSet<>(Arrays.asList(jHipsterProperties.getSwagger().getProtocols())))
				.forCodeGeneration(true).directModelSubstitute(ByteBuffer.class, String.class)
				.genericModelSubstitutes(ResponseEntity.class).select()
				.apis(RequestHandlerSelectors.basePackage("com.status.main.controller")).paths(PathSelectors.any())
				.build();
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("/.*")).build();
	}

	private List<SecurityReference> defaultAuth() {
		final AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		final AuthorizationScope[] authorizationScopes = new AuthorizationScope[] { authorizationScope };
		return Collections.singletonList(new SecurityReference("Bearer", authorizationScopes));
	}

	private ApiKey apiKey() {
		return new ApiKey("Bearer", "Authorization", "header");
	}

	protected Docket createDocket() {
		return new Docket(DocumentationType.SWAGGER_2);
	}

}
