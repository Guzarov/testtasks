package com.testtask.idgenerator.restapi;

import com.testtask.idgenerator.generator.Generator;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@OpenAPIDefinition(
        info = @Info(
                title = "Id generator",
                version = "v3",
                description = "Test task",
                contact = @Contact(
                        name = "Guzarov Konstantin",
                        email = "guzarov@gmail.com"
                )
        )
)
@Tag(
        name = "IdGenerator",
        description = "Operations for generate unique id"
)
@RestController
public class MainController {

    private final Generator producer;

    public MainController(Generator producer) {
        this.producer = producer;
    }

    @Operation(
            description = "Generate new unique integer",
            summary = "Return next unique integer value",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unique ID")
            }
    )
    @GetMapping(value = "/id", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<IdDto> getNextId() {
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noStore())
                .body(new IdDto(producer.generate()));
    }

}
