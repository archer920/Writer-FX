package com.stonesoupprogramming.writerfx.service

import com.stonesoupprogramming.writerfx.doa.LocalBuyingGuideFileDao
import com.stonesoupprogramming.writerfx.export.BuyingGuideFileExporter
import com.stonesoupprogramming.writerfx.models.BuyingGuide
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

private fun BuyingGuide.toText() : String {
    return BuyingGuideFileExporter(this).process()
}

interface LocalBuyingGuideFileService {

    fun save(buyingGuide: BuyingGuide, dest : File)

    fun save(buyingGuide: BuyingGuide) {
        val name = if(buyingGuide.title.entryText.isBlank()) { "untitled" } else {buyingGuide.title.entryText }
        val parts = arrayOf("articles", "fx", "backup", "$name.${System.currentTimeMillis()}.json")
        val folderPath = Paths.get(System.getProperty("user.home"), *parts.copyOfRange(0, parts.size - 1))
        val filePath = Paths.get(System.getProperty("user.home"), *parts)

        if(Files.notExists(folderPath)){
            Files.createDirectories(folderPath)
        }
        save(buyingGuide, filePath.toFile())
    }

    fun load(source : File) : BuyingGuide

    fun export(buyingGuide: BuyingGuide, dest : File)
}

@Service
class LocalBuyingGuideFileServiceImpl (@field: Autowired val localBuyingGuideFileDao: LocalBuyingGuideFileDao) : LocalBuyingGuideFileService {
    override fun export(buyingGuide: BuyingGuide, dest: File) {
        localBuyingGuideFileDao.writeFile(buyingGuide.toText(), dest)
    }

    override fun save(buyingGuide: BuyingGuide, dest: File) {
        localBuyingGuideFileDao.save(buyingGuide, dest)
    }

    override fun load(source: File): BuyingGuide =
            localBuyingGuideFileDao.load(source)
}