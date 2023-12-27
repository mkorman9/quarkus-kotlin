package com.github.mkorman9.quarkuskotlin

import com.fasterxml.uuid.Generators
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import org.jdbi.v3.core.Jdbi
import java.time.Instant
import java.util.UUID

data class Duck(
    val id: UUID,
    val name: String,
    val height: Int,
    val createdAt: Instant
)

@ApplicationScoped
class DuckService {
    @Inject
    lateinit var jdbi: Jdbi

    fun findDucks(): List<Duck> {
        return jdbi.withHandle<List<Duck>, Exception> { handle ->
            handle.createQuery("select id, name, height, created_at from ducks")
                .map { rs, _ ->
                    Duck(
                        id = rs.getObject("id") as UUID,
                        name = rs.getString("name"),
                        height = rs.getInt("height"),
                        createdAt = rs.getTimestamp("created_at").toInstant()
                    )
                }
                .list()
        }
    }

    fun addDuck(name: String, height: Int): UUID {
        val id = ID_GENERATOR.generate()
        val now = Instant.now()

        jdbi.withHandle<Int, Exception> { handle ->
            handle.createUpdate("""
                insert into ducks (id, name, height, created_at)
                values (:id, :name, :height, :createdAt)
            """)
                .bind("id", id)
                .bind("name", name)
                .bind("height", height)
                .bind("createdAt", now)
                .execute()
        }

        return id
    }

    companion object {
        private val ID_GENERATOR = Generators.timeBasedEpochGenerator()
    }
}