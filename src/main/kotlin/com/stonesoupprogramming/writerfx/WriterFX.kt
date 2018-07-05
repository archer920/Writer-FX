package com.stonesoupprogramming.writerfx

import com.stonesoupprogramming.writerfx.configuration.IocConfiguration
import com.stonesoupprogramming.writerfx.ui.ArticleWriterUI
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import org.springframework.context.annotation.AnnotationConfigApplicationContext

fun main(args: Array<String>){
    Application.launch(WriterFX::class.java, *args)
}

class WriterFX : Application() {

    override fun start(primaryStage: Stage) {
        val applicationContext = AnnotationConfigApplicationContext(IocConfiguration::class.java)
        primaryStage.title = "Article Writer FX"

        val articleWriterUI = applicationContext.getBean(ArticleWriterUI::class.java)
        val scene = Scene(articleWriterUI, 600.toDouble(), 800.toDouble())
        primaryStage.scene = scene
        primaryStage.show()
    }

//    private fun toDocument() : Document =
//            Document(title.articleText,
//                    intro.toArticleString(),
//                    products.map { it.toReviewedProduct() },
//                    conclusion.toArticleString(),
//                    criteria.map { it.toCriteria() },
//                    faq.map { it.toFaq() },
//                    sources.toSources())
//
//    private fun toJSON() : String =
//            GsonBuilder().setPrettyPrinting().create().toJson(toDocument())
//
//
//    private fun save() {
//        val file = File(System.getProperty("user.home") + "/articles/fx/backup/${toDocument().title}.${System.currentTimeMillis()}.json")
//        if(!file.exists()){
//            file.createNewFile()
//        }
//
//        BufferedWriter(FileWriter(file)).use {
//            it.write(toJSON())
//        }
//    }
//
//    override fun update(o: Observable?, arg: Any?) {
//        save()
//    }
//
//    fun export(){
//        val file = File(System.getProperty("user.home") + "/articles/${toDocument().title.replace(" ", "_")}.txt")
//        if(!file.exists()){
//            file.createNewFile()
//        }
//        BufferedWriter(FileWriter(file)).use {
//            it.write(BuyingGuidExporter(toDocument()).process())
//        }
//        println(file.path)
//        val p = Runtime.getRuntime().exec("open -R ${file.path}")
//        with(p){
//            waitFor()
//            println("Exit code is ${p.exitValue()}")
//        }
//        println("Done")
//    }
//
//    private fun open(primaryStage: Stage) {
//        with(FileChooser()){
//            title = "Open File"
//            val file : File? = showOpenDialog(primaryStage)
//            if(file != null){
//                val document = file.readJson()
//                title = document.title
//                intro.fromDocument(document.introduction)
//                for(i in 0 until products.size){
//                    products[i].fromReviewedProduct(document.reviewedProducts[i])
//                }
//                conclusion.fromDocument(document.conclusion)
//                for(i in 0 until criteria.size){
//                    criteria[i].fromCriteria(document.criteria[i])
//                }
//                for(i in 0 until faq.size){
//                    faq[i].fromFaq(document.faq[i])
//                }
//                sources.fromSources(document.sources)
//            }
//        }
//    }
}