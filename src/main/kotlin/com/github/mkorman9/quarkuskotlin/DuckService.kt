package com.github.mkorman9.quarkuskotlin

import com.fasterxml.uuid.Generators
import jakarta.enterprise.context.ApplicationScoped
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
import org.ktorm.dsl.where
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

@ApplicationScoped
class DuckService(
    private val db: Database
) {
    fun findDucksPage(pageSize: Int, pageToken: UUID? = null): DucksPage {
        var query = db
            .from(DuckTable)
            .select()
            .orderBy(DuckTable.id.asc())
            .limit(pageSize)

        if (pageToken != null) {
            query = query.where(DuckTable.id greater pageToken)
        }

        val data = query.map { row ->
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

    fun addDuck(name: String, height: Int): UUID {
        val id = ID_GENERATOR.generate()

        db.insert(DuckTable) {
            set(it.id, id)
            set(it.name, name)
            set(it.height, height)
            set(it.createdAt, Instant.now())
        }

        return id
    }

    fun updateDuck(id: UUID, name: String?, height: Int?): Boolean {
        if (name == null && height == null) {
            return true
        }

        val affectedRows = db.update(DuckTable) {
            if (name != null) {
                set(it.name, name)
            }
            if (height != null) {
                set(it.height, height)
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
