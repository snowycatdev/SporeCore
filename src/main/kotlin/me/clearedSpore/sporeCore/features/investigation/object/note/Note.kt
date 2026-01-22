package me.clearedSpore.sporeCore.features.investigation.`object`.note

import me.clearedSpore.sporeCore.util.doc.DocReader
import me.clearedSpore.sporeCore.util.doc.DocWriter
import org.dizitart.no2.collection.Document
import org.dizitart.no2.repository.annotations.Id

data class Note(
    @Id var id: String,
    var name: String,
    var timestamp: Long,
    var addedBy: String,
    var text: String
    ) {


    fun toDocument(): Document = DocWriter()
        .put("id", id)
        .put("name", name)
        .put("timestamp", timestamp)
        .put("addedBy", addedBy)
        .put("text", text)
        .build()

    companion object {
        fun fromDocument(doc: Document): Note? {
            val reader = DocReader(doc)

            return Note(
                id = reader.string("id") ?: return null,
                name = reader.string("name") ?: return null,
                timestamp = reader.long("timestamp") ?: return null,
                addedBy = reader.string("addedBy") ?: return null,
                text = reader.string("text") ?: return null,
            )
        }

    }
}