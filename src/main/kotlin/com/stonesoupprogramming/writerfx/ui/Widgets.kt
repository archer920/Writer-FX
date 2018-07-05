package com.stonesoupprogramming.writerfx.ui

import com.stonesoupprogramming.writerfx.models.*
import javafx.event.EventHandler
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text

private object Colors {
    const val BLACK = "-fx-text-inner-color: black"
    const val GREEN = "-fx-text-inner-color: green"
}

private object WordCount {
    const val INTERVAL = 5
}

interface EntryUpdateListener {
    fun onEntryUpdate(oldCount : Int, newCount : Int, text : String)
}

open class EntryWidget(placeHolderText : String = "") : BorderPane(), Entry {

    private val text = TextArea(placeHolderText)

    init {
        text.wrapTextProperty().value = true
        center = text
    }

    override var entryText: String
        get() = text.textProperty().value
        set(value) {
           text.textProperty().value = value
        }
}

class TitledLineEntryWidget(title: String, placeHolderText: String = "") : VBox(), ReadOnlyTitledEntry {

    private val text = Text(title)
    private val textField = TextField(placeHolderText)

    init {
        children.addAll(text, textField)
    }

    override var entryText: String
        get() = textField.text
        set(value) {
            textField.text = value
        }
    override val title: String
        get() = text.text
}

class TitledEntryWidget(title: String, titlePlaceHolder: String = ""): EntryWidget(titlePlaceHolder){

    private val titleText = Text(title)

    init {
        top = titleText
    }
}

open class MeasuredEntryWidget(override val requiredWords : Int, placeHolderText: String = "") : BorderPane(), MeasuredEntry {

    private val text = TextArea(placeHolderText)
    private val progressLabel = Text("0%")
    private val progressBar = ProgressBar()
    protected var currentStyle : String = Colors.BLACK

    val updateListeners = mutableListOf<EntryUpdateListener>()

    init {

        center = text
        bottom = HBox(progressLabel, progressBar)

        text.wrapTextProperty().value = true
        text.onKeyTyped = EventHandler { onKeyTyped() }
    }

    override var entryText: String
        get() = text.textProperty().value
        set(value) {
            val oldCount = wordCount
            text.textProperty().value = value
            if(wordCount - WordCount.INTERVAL > oldCount){
                updateListeners.forEach { it.onEntryUpdate(oldCount, wordCount, value) }
            }
        }

    protected open fun onKeyTyped(){
        currentStyle = if(wordCount >= requiredWords) {
            Colors.GREEN
        } else {
            Colors.BLACK
        }
        text.style = currentStyle
        progressBar.progress = progress
        progressLabel.text = "${(progress * 100).toInt()}%"
    }
}

class TitledWidget(override val requiredWords : Int, titlePlaceHolder: String = "", placeHolderText: String = "") : MeasuredEntryWidget(requiredWords, placeHolderText), MeasuredTitledEntry {

    private val titleText = TextField(titlePlaceHolder)

    init {
        top = titleText
    }

    override var title: String
        get() = titleText.textProperty().value
        set(value) {
            titleText.textProperty().value = value
        }

    override fun onKeyTyped() {
        super.onKeyTyped()
        titleText.style = currentStyle
    }
}

class ReadOnlyTitledWidget(override val requiredWords: Int,
                           override val title: String,
                           placeHolderText: String = "") : MeasuredEntryWidget(requiredWords, placeHolderText), ReadOnlyMeasuredTitleEntry {

    private val titleText = Text(title)

    init {
        top = titleText
    }

    override fun onKeyTyped() {
        super.onKeyTyped()
        val fill = if(wordCount >= requiredWords) {
            Color.GREEN
        } else {
            Color.BLACK
        }
        titleText.fill = fill
    }
}
