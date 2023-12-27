package com.github.mkorman9.quarkuskotlin

import io.smallrye.common.annotation.RunOnVirtualThread
import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jetbrains.annotations.NotNull
import java.util.UUID

@Path("/api/ducks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(value = [])
@RunOnVirtualThread
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
