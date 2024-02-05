package com.github.mkorman9.quarkuskotlin

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
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
    fun addDuck(@NotNull @Valid request: AddDuckRequest): AddDuckResponse {
        val id = duckService.addDuck(request.toPayload())
        return AddDuckResponse(id)
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    fun updateDuck(
        @RestPath id: UUID,
        @NotNull @Valid request: UpdateDuckRequest
    ): RestResponse<DuckOperationStatusResponse> {
        if (!duckService.updateDuck(id, request.toPayload())) {
            return RestResponse.status(
                RestResponse.Status.NOT_FOUND,
                DuckOperationStatusResponse(
                    status = "error",
                    cause = "Duck with given id was not found"
                )
            )
        }

        return RestResponse.ok(
            DuckOperationStatusResponse(
                status = "ok"
            )
        )
    }

    @DELETE
    @Path("/{id}")
    fun deleteDuck(@RestPath id: UUID): RestResponse<DuckOperationStatusResponse> {
        if (!duckService.deleteDuck(id)) {
            return RestResponse.status(
                RestResponse.Status.NOT_FOUND,
                DuckOperationStatusResponse(
                    status = "error",
                    cause = "Duck with given id was not found"
                )
            )
        }

        return RestResponse.ok(
            DuckOperationStatusResponse(
                status = "ok"
            )
        )
    }
}

data class AddDuckRequest(
    @field:NotBlank @field:Size(max = 255) val name: String,
    @field:Min(value = 1) val height: Int
) {
    fun toPayload() = AddDuckPayload(
        name = name,
        height = height
    )
}

data class AddDuckResponse(
    val id: UUID
)

data class UpdateDuckRequest(
    @field:Size(min = 1, max = 255) val name: String?,
    @field:Min(value = 1) val height: Int?
) {
    fun toPayload() = UpdateDuckPayload(
        name = name,
        height = height
    )
}

data class DuckOperationStatusResponse(
    val status: String,
    @field:JsonInclude(JsonInclude.Include.NON_NULL) val cause: String? = null
)
