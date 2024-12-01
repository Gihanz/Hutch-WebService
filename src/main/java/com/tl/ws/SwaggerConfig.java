/*
 *  ***************************************
 *  * @author Gihan Liyange
 *  * @date Jul 13, 2020 - 11:11:05 PM
 *  ***************************************
 */

package com.tl.ws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/*
 * @Description	:This class contains swagger configurations.
 */
@Configuration
@EnableWebMvc
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.tl.ws")).build()
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("N-able BAW Web Service Documentation")
				.description("N-able BAW REST API Documentation")
				.termsOfServiceUrl("http://www-03.ibm.com/software/sla/sladb.nsf/sla/bm?Open")
				.contact(new Contact("Agility Team", "http://www.n-able.biz", "appnova@n-able.biz"))
				.license("Apache License Version 2.0")
				.licenseUrl("https://github.com/IBM-Bluemix/news-aggregator/blob/master/LICENSE").version("1.1")
				.build();
	}
}
