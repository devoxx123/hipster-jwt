/*
 * Copyright 2016-2017 the original author or authors from the JHipster project.
 *
 * This file is part of the JHipster project, see https://www.jhipster.tech/
 * for more information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.status.main.config;

import static springfox.documentation.builders.PathSelectors.regex;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;

import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import io.github.jhipster.config.JHipsterProperties;
import io.github.jhipster.config.apidoc.customizer.SwaggerCustomizer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * A swagger customizer to setup {@link Docket} with TCX settings.
 * 
 */
public class AppSwaggerCustomizer implements SwaggerCustomizer, Ordered {

    /**
     * The default order for the customizer.
     */
    public static final int DEFAULT_ORDER = 0;

    private int order = DEFAULT_ORDER;

    private final JHipsterProperties jHipsterProperties;

    public AppSwaggerCustomizer(JHipsterProperties jHipsterProperties) {
        this.jHipsterProperties = jHipsterProperties;
    }

    public void customize(Docket docket) {
        
        ApiInfo apiInfo = new ApiInfoBuilder()
        		.title(jHipsterProperties.getSwagger().getTitle())
        		.description(jHipsterProperties.getSwagger().getDescription())
        		.version(jHipsterProperties.getSwagger().getVersion())
        		.build();
        
        docket.host(jHipsterProperties.getSwagger().getHost())
            .protocols(new HashSet<String>(Arrays.asList(jHipsterProperties.getSwagger().getProtocols())))
            .apiInfo(apiInfo)
            .useDefaultResponseMessages(jHipsterProperties.getSwagger().isUseDefaultResponseMessages())
            .forCodeGeneration(true)
            .directModelSubstitute(ByteBuffer.class, String.class)
            .genericModelSubstitutes(ResponseEntity.class)
            .ignoredParameterTypes(Pageable.class)
            .select()
            .paths(regex(jHipsterProperties.getSwagger().getDefaultIncludePattern()))
            .build();
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
