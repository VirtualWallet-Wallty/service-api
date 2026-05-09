package com.krushkov.virtualwallet.swagger;

import com.krushkov.virtualwallet.helpers.ConstantMessages;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    Contact myContact = new Contact()
            .name(ConstantMessages.CONTACT_NAME)
            .url(ConstantMessages.CONTACT_URL)
            .email(ConstantMessages.CONTACT_EMAIL);

    @Bean
    public OpenAPI walltyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(ConstantMessages.OPEN_API_TITLE)
                        .description(ConstantMessages.OPEN_API_DESCRIPTION)
                        .version(ConstantMessages.OPEN_API_VERSION)
                        .contact(myContact)
                );
    }
}
