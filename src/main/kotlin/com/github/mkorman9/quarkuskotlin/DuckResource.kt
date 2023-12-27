package com.github.mkorman9.quarkuskotlin

import jakarta.inject.Inject
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestResponse
import org.jetbrains.annotations.NotNull
import java.util.UUID

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
    fun addDuck(@NotNull @Valid payload: DuckPayload): AddDuckResponse {
        val id = duckService.addDuck(payload.name, payload.height)
        return AddDuckResponse(id)
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    fun updateDuck(
        @RestPath id: UUID,
        @NotNull @Valid payload: DuckPayload
    ): RestResponse<Void> {
        if (!duckService.updateDuck(id, payload.name, payload.height)) {
            return RestResponse.status(400)
        }

        return RestResponse.ok()
    }

    @DELETE
    @Path("/{id}")
    fun deleteDuck(@RestPath id: UUID): RestResponse<Void> {
        if (!duckService.deleteDuck(id)) {
            return RestResponse.status(400)
        }

        return RestResponse.ok()
    }
}

data class DuckPayload(
    @field:NotBlank @field:Size(max = 255) val name: String,
    @field:Min(value = 1) val height: Int
)

data class AddDuckResponse(
    val id: UUID
)
