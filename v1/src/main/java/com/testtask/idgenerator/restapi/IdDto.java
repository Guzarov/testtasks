package com.testtask.idgenerator.restapi;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;

@Schema(
        name = "identifier",
        description = "Unique id"
)
public class IdDto {

    @NotNull
    public final BigInteger id;

    public IdDto(BigInteger id) {
        this.id = id;
    }

}
