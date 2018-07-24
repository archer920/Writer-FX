package com.stonesoupprogramming.writerfx.doa

import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.stonesoupprogramming.writerfx.models.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.io.*
import java.lang.reflect.Type
import java.util.stream.Collectors

interface BuyingGuideFileSaver {

    fun save(buyingGuide: BuyingGuide, dest : File)
}

interface BuyingGuideFileLoader {
    fun load(source : File) : BuyingGuide
}

interface LocalBuyingGuideFileDao : BuyingGuideFileSaver, BuyingGuideFileLoader

@Repository
class LocalBuyingGuideFileDaoImpl(@Autowired val gson: Gson) : LocalBuyingGuideFileDao {

    override fun save(buyingGuide: BuyingGuide, dest: File) {
        BufferedWriter(FileWriter(dest.createIfNonExists())). use {
            it.write(buyingGuide.toJSON(gson))
        }
    }

    override fun load(source: File): BuyingGuide {
        return source.readJSON(gson)
    }
}

private object Fields {
    const val ENTRY_TEXT = "entryText"
    const val REQUIRED_WORDS = "requiredWords"
    const val TITLE = "title"
}

@Component
class EntryTypeAdapter : TypeAdapter<Entry>() {

    override fun write(writer: JsonWriter, entry: Entry?) {
        when(entry){
            null -> writer.nullValue()
            else -> writer.value(entry.entryText)
        }
    }

    override fun read(reader: JsonReader): Entry? {
        return when(reader.peek()){
            JsonToken.NULL -> {
                reader.nextNull()
                return null
            }
            else -> {
                SimpleEntry(reader.nextString())
            }
        }
    }
}

@Component
class MeasuredEntryAdapter : TypeAdapter<MeasuredEntry>() {

    override fun write(writer: JsonWriter, measuredEntry: MeasuredEntry?) {
        when(measuredEntry){
            null -> writer.nullValue()
            else -> writer.value("${measuredEntry.entryText}, ${measuredEntry.requiredWords}")
        }
    }

    override fun read(reader: JsonReader): MeasuredEntry? {
        return when(reader.peek()){
            JsonToken.NULL -> {
                reader.nextNull()
                null
            }
            else -> {
                val parts = reader.nextString().split(", ")
                SimpleMeasuredEntry(parts[0], parts[1].toInt())
            }
        }
    }

}

@Component
class MeasuredTitledEntryAdapter : TypeAdapter<MeasuredTitledEntry>() {

    override fun write(writer: JsonWriter, measuredTitledEntry: MeasuredTitledEntry?) {
        when(measuredTitledEntry) {
            null -> writer.nullValue()
            else -> writer.value("${measuredTitledEntry.entryText}, ${measuredTitledEntry.title}, ${measuredTitledEntry.requiredWords}")
        }
    }

    override fun read(reader: JsonReader): MeasuredTitledEntry? {
        return when(reader.peek()){
            JsonToken.NULL -> {
                reader.nextNull()
                null
            }
            else -> {
                val parts = reader.nextString().split(", ")
                SimpleMeasuredTitledEntry(entryText = parts[0], title = parts[1], requiredWords = parts[2].toInt())
            }
        }
    }
}

@Component
class ReadOnlyMeasuredTitledEntryAdapter : TypeAdapter<ReadOnlyMeasuredTitleEntry>() {

    override fun write(writer: JsonWriter, readOnlyMeasuredTitleEntry: ReadOnlyMeasuredTitleEntry?) {
        when(readOnlyMeasuredTitleEntry){
            null -> writer.nullValue()
            else -> writer.value("${readOnlyMeasuredTitleEntry.entryText}, ${readOnlyMeasuredTitleEntry.title}, ${readOnlyMeasuredTitleEntry.requiredWords}")
        }
    }

    override fun read(reader: JsonReader): ReadOnlyMeasuredTitleEntry? {
        return when(reader.peek()){
            JsonToken.NULL -> {
                reader.nextNull()
                null
            } else -> {
                val parts = reader.nextString().split(", ")
                SimpleReadOnlyMeasuredTitleEntry(entryText = parts[0],
                        title = parts[1], requiredWords = parts[2].toInt())
            }
        }
    }

}

@Component
class TitledEntryAdapter : TypeAdapter<TitledEntry>(){

    override fun write(writer: JsonWriter, titledEntry: TitledEntry?) {
        when(titledEntry) {
            null -> writer.nullValue()
            else -> writer.value("${titledEntry.entryText}, ${titledEntry.title}")
        }
    }

    override fun read(reader: JsonReader): TitledEntry? {
        return when(reader.peek()){
            JsonToken.NULL -> {
                reader.nextNull()
                null
            }
            else -> {
                val parts = reader.nextString().split(", ")
                SimpleTitledEntry(parts[0], parts[1])
            }
        }
    }

}

@Component
class ReadOnlyTitledEntryAdapter : TypeAdapter<ReadOnlyTitledEntry>() {

    override fun write(writer: JsonWriter, readOnlyTitledEntry: ReadOnlyTitledEntry?) {
        when(readOnlyTitledEntry) {
            null -> writer.nullValue()
            else -> writer.value("${readOnlyTitledEntry.entryText}, ${readOnlyTitledEntry.title}")
        }
    }

    override fun read(reader: JsonReader): ReadOnlyTitledEntry? {
        return when(reader.peek()){
            JsonToken.NULL -> {
                reader.nextNull()
                null
            }
            else -> {
                val parts = reader.nextString().split(", ")
                return SimpleReadOnlyTitledEntry(parts[0], parts[1])
            }
        }
    }

}

@Component
class EntryInstanceCreator : JsonSerializer<Entry>, JsonDeserializer<Entry> {

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): Entry {
        return SimpleEntry(json.asString)
    }

    override fun serialize(src: Entry, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.entryText)
    }
}

@Component
class MeasuredEntryCreator : JsonSerializer<MeasuredEntry>, JsonDeserializer<MeasuredEntry> {

    override fun serialize(src: MeasuredEntry, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return src.toJsonObject()
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): MeasuredEntry {
        return SimpleMeasuredEntry(json.asJsonObject.get(Fields.ENTRY_TEXT).asString,
                json.asJsonObject.get(Fields.REQUIRED_WORDS).asInt)
    }
}

@Component
class MeasuredTitleEntryCreator : JsonSerializer<MeasuredTitledEntry> , JsonDeserializer<MeasuredTitledEntry> {

    override fun serialize(src: MeasuredTitledEntry, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return src.toTitledJsonObject()
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): MeasuredTitledEntry {
        with(json.asJsonObject){
            return SimpleMeasuredTitledEntry(
                    get(Fields.ENTRY_TEXT).asString,
                    get(Fields.REQUIRED_WORDS).asInt,
                    get(Fields.TITLE).asString)
        }
    }
}

@Component
class ReadOnlyMeasuredTitledEntryCreator :  JsonSerializer<ReadOnlyMeasuredTitleEntry>, JsonDeserializer<ReadOnlyMeasuredTitleEntry> {

    override fun serialize(src: ReadOnlyMeasuredTitleEntry, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return src.toTitledJsonObject()
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): ReadOnlyMeasuredTitleEntry {
        with(json.asJsonObject){
            return SimpleReadOnlyMeasuredTitleEntry(
                    get(Fields.ENTRY_TEXT).asString,
                    get(Fields.REQUIRED_WORDS).asInt,
                    get(Fields.TITLE).asString)
        }
    }
}

@Component
class TitledEntryCreator : JsonSerializer<TitledEntry>, JsonDeserializer<TitledEntry> {

    override fun serialize(src: TitledEntry, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return src.toTitledJsonObject()
    }

    override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): TitledEntry {
        with(json.asJsonObject){
            return SimpleTitledEntry(
                    get(Fields.ENTRY_TEXT).asString,
                    get(Fields.TITLE).asString)
        }
    }
}

private fun BuyingGuide.toJSON(gson: Gson) = gson.toJson(this)

private fun File.createIfNonExists(): File {
    if(!this.exists()){
        this.createNewFile()
    }
    return this
}

private fun File.readJSON(gson: Gson) : BuyingGuide {
    BufferedReader(FileReader(this)).use {
        val json = it.lines().collect(Collectors.joining())
        return gson.fromJson(json, BuyingGuide::class.java)
    }
}

private fun MeasuredEntry.toJsonObject() : JsonObject {
    val json = JsonObject()
    with (json){
        add(Fields.ENTRY_TEXT, JsonPrimitive(this@toJsonObject.entryText))
        add(Fields.REQUIRED_WORDS, JsonPrimitive(this@toJsonObject.requiredWords))
    }
    return json
}

private fun MeasuredTitledEntry.toTitledJsonObject() : JsonObject {
    val json = this.toJsonObject()
    json.add(Fields.TITLE, JsonPrimitive(this.title))
    return json
}

private fun ReadOnlyMeasuredTitleEntry.toTitledJsonObject() : JsonObject {
    val json = this.toJsonObject()
    json.add(Fields.TITLE, JsonPrimitive(this.title))
    return json
}

private fun TitledEntry.toTitledJsonObject() : JsonObject {
    val json = JsonObject()
    with(json){
        add(Fields.ENTRY_TEXT, JsonPrimitive(this@toTitledJsonObject.entryText))
        add(Fields.TITLE, JsonPrimitive(this@toTitledJsonObject.title))
    }
    return json
}