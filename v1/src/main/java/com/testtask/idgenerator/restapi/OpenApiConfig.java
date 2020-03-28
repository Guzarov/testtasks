package com.testtask.idgenerator.restapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.stereotype.Component;

@OpenAPIDefinition(
        info = @Info(
                title = "Id generator",
                version = "v3",
                description = "Test task for maxilect.ru",
                contact = @Contact(
                        name = "Guzarov Konstantin",
                        email = "guzarov@gmail.com"
                )
        ),
        servers = {
                @Server(
                        description = "Default server"
                )
        }
)
@Component
public class OpenApiConfig {
}
