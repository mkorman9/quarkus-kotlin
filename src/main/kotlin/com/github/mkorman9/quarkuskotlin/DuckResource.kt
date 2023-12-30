package com.github.mkorman9.quarkuskotlin

import jakarta.validation.Valid
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.DefaultValue
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.PUT
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestResponse
import org.jetbrains.annotations.NotNull
import java.util.UUID

data class AddDuckResponse(
    val id: UUID
)

@Path("/api/ducks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(value = [])
class DuckResource(
    private val duckService: DuckService
) {
    @GET
    fun getDucksPage(
        @QueryParam("pageSize") @DefaultValue("10") pageSize: Long,
        @QueryParam("pageToken") pageToken: UUID?
    ): DucksPage {
        return duckService.findDucksPage(
            pageSize = Math.clamp(pageSize, 1, 100),
            pageToken = pageToken
        )
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun addDuck(@NotNull @Valid payload: AddDuckPayload): AddDuckResponse {
        val id = duckService.addDuck(payload)
        return AddDuckResponse(id)
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    fun updateDuck(
        @RestPath id: UUID,
        @NotNull @Valid payload: UpdateDuckPayload
    ): RestResponse<Void> {
        if (!duckService.updateDuck(id, payload)) {
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
