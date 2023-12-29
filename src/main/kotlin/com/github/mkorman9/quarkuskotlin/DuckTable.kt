package com.github.mkorman9.quarkuskotlin

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.timestamp
import org.ktorm.schema.uuid
import org.ktorm.schema.varchar

object DuckTable : Table<Nothing>("ducks") {
    val id = uuid("id").primaryKey()
    val name = varchar("name")
    val height = int("height")
    val createdAt = timestamp("created_at")
}
