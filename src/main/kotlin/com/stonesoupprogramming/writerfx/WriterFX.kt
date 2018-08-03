package com.stonesoupprogramming.writerfx

import com.stonesoupprogramming.writerfx.configuration.IocConfiguration
import com.stonesoupprogramming.writerfx.configuration.UiConfigurationFactory
import javafx.application.Application
import javafx.stage.Stage
import org.springframework.context.annotation.AnnotationConfigApplicationContext

private lateinit var applicationArguments: Array<String>

fun main(args: Array<String>){
    applicationArguments = args
    Application.launch(WriterFX::class.java, *args)
}

class WriterFX : Application() {

    override fun start(primaryStage: Stage) {
        val applicationContext = AnnotationConfigApplicationContext(IocConfiguration::class.java)
        val uiConfigurationFactory = applicationContext.getBean(UiConfigurationFactory::class.java)
        uiConfigurationFactory.run(primaryStage, applicationArguments)
    }
}