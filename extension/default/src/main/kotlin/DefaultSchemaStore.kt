package io.holixon.avro.adapter.common

import io.holixon.avro.adapter.api.SchemaResolver
import org.apache.avro.Schema
import org.apache.avro.message.SchemaStore

class DefaultSchemaStore(private val schemaResolver: SchemaResolver) : SchemaStore {

  override fun findByFingerprint(fingerprint: Long): Schema? = schemaResolver
    .apply(fingerprint.toString())
    .map { it.schema }
    .orElse(null)
}
