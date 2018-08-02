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
        articleWriterUI.stage = primaryStage

        val scene = Scene(articleWriterUI, 600.toDouble(), 800.toDouble())

        primaryStage.scene = scene
        primaryStage.show()
    }
}