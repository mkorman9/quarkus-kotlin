package com.github.mkorman9.quarkuskotlin

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Singleton
import jakarta.ws.rs.Produces
import org.jdbi.v3.core.Jdbi
import javax.sql.DataSource

@ApplicationScoped
class JdbiProvider {
    @Singleton
    @Produces
    fun jdbi(dataSource: DataSource): Jdbi = Jdbi.create(dataSource)
}
