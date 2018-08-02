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
import org.springframework.stereotype.Component
import java.util.*

private object Colors {
    const val BLACK = "-fx-text-inner-color: black"
    const val GREEN = "-fx-text-inner-color: green"
}

private object WordCount {
    const val INTERVAL = 5
}

@Component
class EntryObservable : Observable(){

    fun update() {
        setChanged()
        notifyObservers()
    }
}

class TitledLineEntryWidget(val entryObservable: EntryObservable, title: String, placeHolderText: String = "") : VBox(), ReadOnlyTitledEntry {

    private val text = Text(title)
    private val textField = TextField(placeHolderText)

    init {
        children.addAll(text, textField)
        textField.focusedProperty().addListener { _, _, newValue -> if(!newValue){ notifyObservers() } }
    }

    override var entryText: String
        get() = textField.text
        set(value) {
            textField.text = value
        }
    override val title: String
        get() = text.text

    fun toSimplified(): ReadOnlyTitledEntry {
        return SimpleReadOnlyTitledEntry(entryText, title)
    }

    private fun notifyObservers(){
        entryObservable.update()
    }

    fun fromSimplified(title: Entry) {
        entryText = title.entryText
    }
}

open class MeasuredEntryWidget(val entryObservable: EntryObservable, override val requiredWords : Int, placeHolderText: String = "") : BorderPane(), MeasuredEntry {

    private val text = TextArea(placeHolderText)
    private val progressLabel = Text("0%")
    private val progressBar = ProgressBar()
    protected var currentStyle : String = Colors.BLACK


    init {

        center = text
        bottom = HBox(progressLabel, progressBar)

        text.wrapTextProperty().value = true
        text.onKeyTyped = EventHandler { onKeyTyped() }
        text.focusedProperty().addListener { _, _, newValue -> if(!newValue){ notifyObservers() } }
    }

    override var entryText: String
        get() = text.textProperty().value
        set(value) {
            text.textProperty().value = value
        }

    protected open fun onKeyTyped(){
        updateLabel()
        notifyObservers()
    }

    private fun updateLabel(){
        currentStyle = if(wordCount >= requiredWords) {
            Colors.GREEN
        } else {
            Colors.BLACK
        }
        text.style = currentStyle
        progressBar.progress = progress
        progressLabel.text = "${(progress * 100).toInt()}%"
    }

    protected open fun notifyObservers(){
        if(wordCount % WordCount.INTERVAL == 0){
            entryObservable.update()
        }
    }

    open fun toSimplified(): MeasuredEntry {
        return SimpleMeasuredEntry(entryText, requiredWords)
    }

    open fun fromSimplified(entry: Entry) {
        entryText = entry.entryText
    }
}

class TitledWidget(entryObservable: EntryObservable,
                   override val requiredWords : Int, titlePlaceHolder: String = "",
                   placeHolderText: String = "") : MeasuredEntryWidget(entryObservable, requiredWords, placeHolderText), MeasuredTitledEntry {

    private val titleText = TextField(titlePlaceHolder)

    init {
        top = titleText
        titleText.focusedProperty().addListener { _, _, newValue -> if(!newValue){ notifyObservers() } }
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

    override fun toSimplified(): MeasuredTitledEntry {
        return SimpleMeasuredTitledEntry(entryText, requiredWords, title)
    }

    override fun fromSimplified(entry: Entry) {
        super.fromSimplified(entry)
        //FIXME: This is really hacky and should be refactored
        when (entry){
            is TitledEntry -> title = entry.title
            else -> throw IllegalArgumentException("Missing required title field")
        }
    }
}

class ReadOnlyTitledWidget(entryObservable: EntryObservable,
                           override val requiredWords: Int,
                           override val title: String,
                           placeHolderText: String = "") : MeasuredEntryWidget(entryObservable, requiredWords, placeHolderText), ReadOnlyMeasuredTitleEntry {

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

    override fun toSimplified(): ReadOnlyMeasuredTitleEntry {
        return SimpleReadOnlyMeasuredTitleEntry(entryText, requiredWords, title)
    }
}
