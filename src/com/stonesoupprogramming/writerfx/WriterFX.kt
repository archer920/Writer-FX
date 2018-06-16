package com.stonesoupprogramming.writerfx

import javafx.application.Application
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.scene.text.Text
import javafx.stage.Stage

interface ArticleEntry {
    val requiredWords : Int
    val wordCount : Int
        get() = articleText.split(" ").size
    var articleText : String
    val progress : Double
         get() = wordCount.toDouble() / requiredWords.toDouble()
}

interface TitledEntry : ArticleEntry {
    var articleTitle : String
}

private object Colors {
    const val BLACK = "-fx-text-inner-color: black"
    const val GREEN = "-fx-text-inner-color: green"
}

class ArticleEntryField(override val requiredWords : Int, startText : String = "") : TextArea(startText), ArticleEntry{

    init {
       this.onKeyTyped = EventHandler<KeyEvent> {
           checkProgress()
       }
    }

    override var articleText: String
        get() = textProperty().value
        set(value) {
            textProperty().value = value
        }

    fun checkProgress() {
        style = when {
            wordCount >= requiredWords -> Colors.GREEN
            else -> Colors.BLACK
        }
    }
}

class TitledEntryField(private val articleEntryField: ArticleEntryField,
                            title : String = "") : BorderPane(), TitledEntry, ArticleEntry by articleEntryField {

    private val titleEntry : TextField = TextField(title)

    init {
        articleEntryField.onKeyTyped = EventHandler<KeyEvent> {
            articleEntryField.checkProgress()
            checkProgress()
        }
        top = titleEntry
        center = articleEntryField
    }

    override var articleTitle: String
        get() = titleEntry.text
        set(value) {
            titleEntry.text = value
        }

    private fun checkProgress(){
        titleEntry.style = when {
            wordCount >= requiredWords -> Colors.GREEN
            else -> Colors.BLACK
        }
    }
}

open class ProgressEntryFrame(protected val articleEntryField: ArticleEntryField) : BorderPane(), ArticleEntry by articleEntryField {

    private val progressBar = ProgressBar()
    private val progressLabel = Text("0%")

    init {
        articleEntryField.onKeyTyped = EventHandler<KeyEvent> {
            update()
        }

        val hBox = HBox()
        hBox.children.addAll(progressLabel, progressBar)

        center = articleEntryField
        bottom = hBox
    }

    open protected fun update() {
        articleEntryField.checkProgress()
        progressBar.progress = articleEntryField.progress
        progressLabel.text = "${(articleEntryField.progress * 100).toInt()}%"
    }
}

class TitledProgressEntryFrame(articleEntryField: ArticleEntryField, title : String = "") : ProgressEntryFrame(articleEntryField), TitledEntry {

    private val titleEntry : TextField = TextField(title)

    init {
        articleEntryField.onKeyTyped = EventHandler<KeyEvent> {
            update()
        }
        top = titleEntry
    }

    override var articleTitle: String
        get() = titleEntry.text
        set(value) {
            titleEntry.text = value
        }

    override fun update() {
        super.update()
        titleEntry.style = when {
            wordCount >= requiredWords -> Colors.GREEN
            else -> Colors.BLACK
        }
    }
}

fun main(args: Array<String>){
    Application.launch(WriterFX::class.java, *args)
}

class WriterFX : Application() {

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Article Writer FX"

        val grid = GridPane()
        with(grid){
            alignment = Pos.CENTER
            hgap = 10.toDouble()
            vgap = 10.toDouble()
            padding = Insets(25.toDouble(), 25.toDouble(), 25.toDouble(), 25.toDouble())

            add(TitledProgressEntryFrame(ArticleEntryField(5)), 0, 0)
        }

        val scene = Scene(grid, 300.toDouble(), 200.toDouble())
        primaryStage.scene = scene
        primaryStage.show()
    }
}