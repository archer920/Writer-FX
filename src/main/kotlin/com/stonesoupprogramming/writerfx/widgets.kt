package com.stonesoupprogramming.writerfx

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.text.Text
import java.util.*

interface Nodeable {
    fun asNode() : Node
}

interface ArticleNode : ArticleEntry, Nodeable

interface MeasuredArticleNode : MeasuredArticleEntry, Nodeable

interface TitledMeasuredArticleNode : TitledEntryMeasured, MeasuredArticleNode

private object Colors {
    const val BLACK = "-fx-entryText-inner-color: black"
    const val GREEN = "-fx-entryText-inner-color: green"
}

class ArticleEntryWidget(override val requiredWords : Int, observer: Observer, startText : String = "", lines : Int = 5) : Observable(), MeasuredArticleNode {
    val textArea = TextArea(startText)
    private var lastWordCount : Int = 0

    init {
        textArea.wrapTextProperty().value = true
        textArea.onKeyTyped = EventHandler<KeyEvent> {
            checkProgress()
        }
        textArea.prefRowCount = lines
        addObserver(observer)
    }

    override var articleText: String
        get() = textArea.textProperty().value
        set(value) {
            textArea.textProperty().value = value
        }

    override fun asNode(): Node = textArea

    fun checkProgress() {
        if(wordCount - 5 > lastWordCount){
            lastWordCount = wordCount
            setChanged()
            notifyObservers()
        }
        textArea.style = when {
            wordCount >= requiredWords -> {
                setChanged()
                notifyObservers()
                Colors.GREEN
            }
            else -> Colors.BLACK
        }
    }
}

open class MeasuredArticleEntryWidget(private val articleEntryWidget: ArticleEntryWidget) : BorderPane(), MeasuredArticleNode by articleEntryWidget {

    private val progressBar = ProgressBar()
    private val progressLabel = Text("0%")

    init {
        articleEntryWidget.textArea.onKeyTyped = EventHandler<KeyEvent> {
            update()
        }

        val hBox = HBox()
        hBox.children.addAll(progressLabel, progressBar)

        center = articleEntryWidget.asNode()
        bottom = hBox
    }

    protected open fun update() {
        articleEntryWidget.checkProgress()
        progressBar.progress = articleEntryWidget.progress
        progressLabel.text = "${(articleEntryWidget.progress * 100).toInt()}%"
    }

    override fun asNode(): Node = this
}


class SourceWidget(title: String) : BorderPane(), ArticleNode {
    override fun asNode(): Node = this

    override var articleText: String
        get() = textField.text
        set(value) {
            textField.text = value
        }

    private val label = Text(title)
    private val textField = TextField()

    init {
        left = label
        center = textField
    }
}

class TitleArticleEntryWidget(private val articleEntryWidget: ArticleEntryWidget,
                              title : String = "", readOnlyTitle: Boolean = false) : BorderPane(), TitledMeasuredArticleNode, MeasuredArticleNode by articleEntryWidget {

    private val titleEntry = TextField(title)
    private val text  = Text(title)

    init {
        articleEntryWidget.textArea.onKeyTyped = EventHandler<KeyEvent> {
            articleEntryWidget.checkProgress()
            checkProgress()
        }
        top = if(readOnlyTitle){
            text
        } else {
            titleEntry
        }
        center = articleEntryWidget.asNode()
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

    override fun asNode(): Node = this
}


class TitledMeasuredArticleEntryWidget(articleEntryWidget: ArticleEntryWidget, title : String = "", readOnlyTitle : Boolean = false) : MeasuredArticleEntryWidget(articleEntryWidget), TitledMeasuredArticleNode {

    private val titleEntry : TextField = TextField(title)
    private val text  = Text(title)

    init {
        titleEntry.editableProperty().value = !readOnlyTitle
        articleEntryWidget.textArea.onKeyTyped = EventHandler<KeyEvent> {
            update()
        }
        top = if(readOnlyTitle){
            text
        } else {
            titleEntry
        }
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

    override fun asNode(): Node = this
}

fun buildStandardTitledMeasuredEntryWidget(requiredWords: Int, observer: Observer, title : String = "", readOnlyTitle: Boolean = false, lines: Int = 5): TitledMeasuredArticleEntryWidget
        = TitledMeasuredArticleEntryWidget(ArticleEntryWidget(requiredWords, observer, lines = lines), title, readOnlyTitle)

fun buildStandardTitledWidget(requiredWords: Int, title : String, observer: Observer, readOnlyTitle: Boolean = false, lines: Int = 5) =
        TitleArticleEntryWidget(ArticleEntryWidget(requiredWords, observer, lines = lines), title, readOnlyTitle)