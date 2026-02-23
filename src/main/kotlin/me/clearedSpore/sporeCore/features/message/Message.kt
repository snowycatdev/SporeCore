package me.clearedSpore.sporeCore.features.message

import me.clearedSpore.sporeCore.util.doc.DocReader
import me.clearedSpore.sporeCore.util.doc.DocWriter
import org.dizitart.no2.collection.Document
import org.dizitart.no2.repository.annotations.Id
import java.util.UUID

data class Message(
    @Id val id: String,
    val timestamp: Long,
    val type: MessageType,
    val message: String,
    val caller: UUID,
    val raw: Boolean
) {

    fun toDocument(): Document  = DocWriter()
        .put("id", id)
        .putLong("timestamp", timestamp)
        .put("type", type.name)
        .put("message", message)
        .putId("caller", caller)
        .putBoolean("raw", raw)
        .build()

    companion object {
        fun fromDocument(doc: Document): Message? {
            val reader = DocReader(doc)

            return Message(
                id = reader.string("id") ?: return null,
                timestamp = reader.long("timestamp"),
                type = reader.enum<MessageType>("type") ?: return null,
                message = reader.string("message") ?: return null,
                caller = reader.id("caller") ?: return null,
                raw = reader.boolean("raw")
            )

        }
    }

}
