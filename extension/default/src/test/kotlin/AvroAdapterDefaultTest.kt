package io.toolisticon.avro.adapter.common

import io.toolisticon.avro.adapter.api.AvroAdapterApi.toByteArray
import io.toolisticon.avro.adapter.api.ext.ByteArrayExt.toHexString
import io.toolisticon.avro.adapter.common.AvroAdapterDefault.isAvroSingleObjectEncoded
import io.toolisticon.avro.adapter.common.AvroAdapterDefault.readPayloadAndSchemaId
import io.toolisticon.avro.adapter.common.ext.SchemaExt.fingerprint
import io.toolisticon.avro.lib.test.AvroAdapterTestLib
import mu.KLogging
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class AvroAdapterDefaultTest {
  companion object : KLogging()

  private val bytes = AvroAdapterTestLib.sampleFoo.toByteArray()


  @Test
  internal fun `read payload and schemaId from encoded bytes`() {
    logger.info { bytes.toHexString() }

    // too short
    assertThatThrownBy { "foo".encodeToByteArray().readPayloadAndSchemaId() }.isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("single object encoded bytes must have at least length > 10, was: 3")

    // long enough, but does not start with V1_Header
    assertThatThrownBy {
      "hello my precious world".encodeToByteArray().readPayloadAndSchemaId()
    }.isInstanceOf(IllegalArgumentException::class.java)
      .hasMessage("single object encoded bytes need to start with [C3 01]")

    val payloadAndSchemaId = bytes.readPayloadAndSchemaId()
    assertThat(payloadAndSchemaId.schemaId).isEqualTo(AvroAdapterTestLib.sampleFoo.schema.fingerprint.toString())
    assertThat(payloadAndSchemaId.payload.toHexString()).isEqualTo("[06 66 6F 6F]")
  }

  @Test
  internal fun `extract schemaId and payload`() {
    val (schemaId, payload) = bytes.readPayloadAndSchemaId()

    assertThat(schemaId).isEqualTo(AvroAdapterTestLib.sampleEventFingerprint.toString())
    assertThat(payload.toHexString()).isEqualTo("[06 66 6F 6F]")
  }

  @Test
  internal fun `is avro single object encoded`() {
    assertThat(bytes.isAvroSingleObjectEncoded()).isTrue

    assertThat("foo".encodeToByteArray().isAvroSingleObjectEncoded()).isFalse
  }

}
