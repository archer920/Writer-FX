package com.stonesoupprogramming.writerfx.doa

import com.google.gson.GsonBuilder
import com.stonesoupprogramming.writerfx.models.BuyingGuide
import org.springframework.stereotype.Repository
import java.io.*
import java.util.stream.Collectors

interface BuyingGuideFileSaver {

    fun save(buyingGuide: BuyingGuide, dest : File)
}

interface BuyingGuideFileLoader {
    fun load(source : File) : BuyingGuide
}

interface LocalBuyingGuideFileDao : BuyingGuideFileSaver, BuyingGuideFileLoader

@Repository
class LocalBuyingGuideFileDaoImpl : LocalBuyingGuideFileDao {

    override fun save(buyingGuide: BuyingGuide, dest: File) {
        BufferedWriter(FileWriter(dest.createIfNonExists())). use {
            it.write(buyingGuide.toJson())
        }
    }

    override fun load(source: File): BuyingGuide {
        return source.readJSON()
    }
}

private fun BuyingGuide.toJson() = GsonBuilder().setPrettyPrinting().create().toJson(this)

private fun File.createIfNonExists(): File {
    if(!this.exists()){
        this.createNewFile()
    }
    return this
}

private fun File.readJSON() : BuyingGuide {
    BufferedReader(FileReader(this)).use {
        val json = it.lines().collect(Collectors.joining())
        return GsonBuilder().setPrettyPrinting().create().fromJson(json, BuyingGuide::class.java)
    }
}