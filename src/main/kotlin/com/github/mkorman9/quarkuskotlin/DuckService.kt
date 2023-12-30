package com.github.mkorman9.quarkuskotlin

import com.fasterxml.uuid.Generators
import jakarta.enterprise.context.ApplicationScoped
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.ktorm.database.Database
import org.ktorm.dsl.asc
import org.ktorm.dsl.delete
import org.ktorm.dsl.eq
import org.ktorm.dsl.from
import org.ktorm.dsl.greater
import org.ktorm.dsl.insert
import org.ktorm.dsl.limit
import org.ktorm.dsl.map
import org.ktorm.dsl.orderBy
import org.ktorm.dsl.select
import org.ktorm.dsl.update
import org.ktorm.dsl.whereWithConditions
import java.time.Instant
import java.util.UUID

data class Duck(
    val id: UUID,
    val name: String,
    val height: Int,
    val createdAt: Instant
)

data class DucksPage(
    val data: List<Duck>,
    val pageSize: Int,
    val nextPageToken: UUID?
)

data class AddDuckPayload(
    @field:NotBlank @field:Size(max = 255) val name: String,
    @field:Min(value = 1) val height: Int
)

data class UpdateDuckPayload(
    @field:Size(min = 1, max = 255) val name: String?,
    @field:Min(value = 1) val height: Int?
)

@ApplicationScoped
class DuckService(
    private val db: Database
) {
    fun findDucksPage(pageSize: Int, pageToken: UUID? = null): DucksPage {
        val data = db
            .from(DuckTable)
            .select()
            .whereWithConditions {
                if (pageToken != null) {
                    it += DuckTable.id greater pageToken
                }
            }
            .orderBy(DuckTable.id.asc())
            .limit(pageSize)
            .map { row ->
                Duck(
                    id = row[DuckTable.id]!!,
                    name = row[DuckTable.name]!!,
                    height = row[DuckTable.height]!!,
                    createdAt = row[DuckTable.createdAt]!!
                )
            }

        return DucksPage(
            data = data,
            pageSize = pageSize,
            nextPageToken = data.lastOrNull()?.id
        )
    }

    fun addDuck(payload: AddDuckPayload): UUID {
        val id = ID_GENERATOR.generate()

        db.insert(DuckTable) {
            set(it.id, id)
            set(it.name, payload.name)
            set(it.height, payload.height)
            set(it.createdAt, Instant.now())
        }

        return id
    }

    fun updateDuck(id: UUID, payload: UpdateDuckPayload): Boolean {
        val affectedRows = db.update(DuckTable) {
            set(it.id, it.id)  // prevent empty update
            if (payload.name != null) {
                set(it.name, payload.name)
            }
            if (payload.height != null) {
                set(it.height, payload.height)
            }

            where {
                it.id eq id
            }
        }

        return affectedRows > 0
    }

    fun deleteDuck(id: UUID): Boolean {
        val affectedRows = db.delete(DuckTable) {
            it.id eq id
        }

        return affectedRows > 0
    }

    companion object {
        private val ID_GENERATOR = Generators.timeBasedEpochGenerator()
    }
}
