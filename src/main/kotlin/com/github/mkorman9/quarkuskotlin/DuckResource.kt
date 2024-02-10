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
import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestPath
import org.jetbrains.annotations.NotNull
import java.util.UUID

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
    ): DuckOperationSuccessResponse {
        if (!duckService.updateDuck(id, payload)) {
            throw WebApplicationException(
                Response.status(Response.Status.NOT_FOUND)
                    .entity(
                        DuckOperationErrorResponse(
                            status = "DuckNotFound",
                            cause = "Duck with given id was not found"
                        )
                    )
                    .build()
            )
        }

        return DuckOperationSuccessResponse()
    }

    @DELETE
    @Path("/{id}")
    fun deleteDuck(@RestPath id: UUID): DuckOperationSuccessResponse {
        if (!duckService.deleteDuck(id)) {
            throw WebApplicationException(
                Response.status(Response.Status.NOT_FOUND)
                    .entity(
                        DuckOperationErrorResponse(
                            status = "DuckNotFound",
                            cause = "Duck with given id was not found"
                        )
                    )
                    .build()
            )
        }

        return DuckOperationSuccessResponse()
    }
}

data class AddDuckResponse(
    val id: UUID
)

data class DuckOperationSuccessResponse(
    val status: String = "Ok"
)

data class DuckOperationErrorResponse(
    val status: String,
    val cause: String
)
