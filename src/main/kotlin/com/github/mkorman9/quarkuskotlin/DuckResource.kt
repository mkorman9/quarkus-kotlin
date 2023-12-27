package com.github.mkorman9.quarkuskotlin

import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.jetbrains.annotations.NotNull
import java.util.*

@Path("/api/ducks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(value = [])
class DuckResource {
    @Inject
    lateinit var duckService: DuckService

    @GET
    fun getDucks(): List<Duck> {
        return duckService.findDucks()
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun addDuck(@NotNull @Valid payload: AddDuckPayload): AddDuckResponse {
        val id = duckService.addDuck(payload.name, payload.height)
        return AddDuckResponse(id)
    }
}

data class AddDuckPayload(
    @field:NotBlank @field:Size(max = 255) val name: String,
    @field:Min(value = 1) val height: Int
)

data class AddDuckResponse(
    val id: UUID
)
