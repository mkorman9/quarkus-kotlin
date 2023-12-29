package com.github.mkorman9.quarkuskotlin

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Singleton
import jakarta.ws.rs.Produces
import org.ktorm.database.Database
import org.ktorm.support.postgresql.PostgreSqlDialect
import javax.sql.DataSource

@ApplicationScoped
class DatabaseProvider {
    @Singleton
    @Produces
    fun database(dataSource: DataSource): Database = Database.connect(
        dataSource,
        dialect = PostgreSqlDialect()
    )
}
