package com.stonesoupprogramming.writerfx.configuration

import com.stonesoupprogramming.writerfx.ui.ConfigurationDialog
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*


@Component
class PropertiesManager {

    object Keys {
        const val LAST_FILE = "LAST_FILE"
    }

    private val propertiesFileLocation = "${System.getProperty("user.home")}/.writerfx.properties"

    val properties: Properties = Properties()

    init {
        if(!Files.exists(Paths.get(propertiesFileLocation))){
            Files.createFile(Paths.get(propertiesFileLocation))
        }
        properties.load(FileInputStream(Paths.get(propertiesFileLocation).toFile()))
    }

    fun saveProperties() {
        properties.store(FileOutputStream(Paths.get(propertiesFileLocation).toFile()), null)
    }
}

@Component
class UiConfigurationFactory(@Autowired private val propertiesManager: PropertiesManager,
                             @Autowired private val configurationDialog: ConfigurationDialog) {

    fun run(stage: Stage, commandLineArgs : Array<String>){
        stage.title = "Article Writer FX"
        if(openConfiguration(commandLineArgs)){
            stage.scene = Scene(configurationDialog)
            stage.sizeToScene()
            stage.show()
        } else {
            Platform.exit()
        }
    }

    private fun openConfiguration(commandLineArgs: Array<String>) =
            !propertiesManager.properties.containsKey(PropertiesManager.Keys.LAST_FILE) ||
            commandLineArgs.contains("--new-document)")
}