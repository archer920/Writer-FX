package com.stonesoupprogramming.writerfx.ui

import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Spinner
import javafx.scene.layout.BorderPane
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.text.Text
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

data class Configuration(val includeIntroduction : Boolean = true,
                         val introductionWords : Int = 200,
                         val numProducts : Int = 10,
                         val productIntroductionWords : Int = 60,
                         val numAspects : Int = 2,
                         val aspectWords : Int = 30,
                         val includeCostValue : Boolean = true,
                         val costValueWords : Int = 30,
                         val numPros : Int = 5,
                         val prosWords : Int = 10,
                         val numCons : Int = 2,
                         val consWords : Int = 10,
                         val includeConclusion : Boolean = true,
                         val conclusionWords : Int = 200,
                         val numCriteria : Int = 5,
                         val criteriaWords : Int = 275,
                         val numFaq : Int = 5,
                         val faqWords : Int = 75,
                         val numSources : Int = 5)

open class AlignedGridPane(columns : Int = 2) : GridPane(){


    init{
        val constraints = (0 until columns).map { ColumnConstraints() }.toList()
        constraints.forEach { it.percentWidth = 100.toDouble() / columns.toDouble() }
        columnConstraints.addAll(constraints)

        hgap = 10.toDouble()
        vgap = 10.toDouble()
        padding = Insets(10.toDouble(), 10.toDouble(), 10.toDouble(), 10.toDouble())
    }

    constructor(vararg children : Node) : this(children.size) {
        addRow(0, *children)
    }
}

private class SizedSpinner(initialValue : Int = 0) : Spinner<Int>(0, Int.MAX_VALUE, initialValue){

    init {
        prefWidth = 100.toDouble()
    }
}

@Component
class ConfigurationDialog : BorderPane() {

    private object Labels {
        const val WORD_COUNT = "Required Words"
        const val INCLUDE_INTRODUCTION = "Include Introduction"
        const val INCLUDE_CONCLUSION = "Include Conclusion"
        const val NUM_PRODUCTS = "Number of Products"
        const val NUM_CRITERIA = "Number of Criteria"
        const val NUM_FAQ = "Number of FAQs"
        const val NUM_SOURCES = "Number of Sources"
        const val INCLUDE_PRODUCT_LONG_REVIEW = "Include Product Long Review"
        const val NUM_ASPECTS = "Number of Aspects"
        const val INCLUDE_COST_VALUE = "Include Cost and Value"
        const val NUM_PROS = "Number of Pros"
        const val NUM_CONST = "Number of Cons"
    }

    private val includeIntroductionBox = CheckBox(Labels.INCLUDE_INTRODUCTION)
    private val introductionCountLabel = Text(Labels.WORD_COUNT)
    private val introductionSpinner = SizedSpinner(200)

    private val numProductsLabel = Text(Labels.NUM_PRODUCTS)
    private val numProductsSpinner = SizedSpinner(10)

    private val includeConclusionBox = CheckBox(Labels.INCLUDE_CONCLUSION)
    private val includeConclusionLabel = Text(Labels.WORD_COUNT)
    private val conclusionSpinner = SizedSpinner(200)

    private val includeProductLongReviewLabel = CheckBox(Labels.INCLUDE_PRODUCT_LONG_REVIEW)
    private val longReviewCountLabel = Text(Labels.WORD_COUNT)
    private val longReviewSpinner = SizedSpinner(60)

    private val numAspectsLabel = Text(Labels.NUM_ASPECTS)
    private val numAspectsSpinner = SizedSpinner(2)

    private val aspectsWordCount = Text(Labels.WORD_COUNT)
    private val aspectsWordCountSpinner = SizedSpinner(30)

    private val includeCostValueBox = CheckBox(Labels.INCLUDE_COST_VALUE)
    private val includeCostValueLabel = Text(Labels.WORD_COUNT)
    private val includeCostValueSpinner = SizedSpinner(30)

    private val numProsLabel = Text(Labels.NUM_PROS)
    private val numProsSpinner = SizedSpinner(5)
    private val proWordLabel = Text(Labels.WORD_COUNT)
    private val proWordSpinner = SizedSpinner(10)

    private val numConsLabel = Text(Labels.NUM_CONST)
    private val numConsSpinner = SizedSpinner(2)
    private val conWordLabel = Text(Labels.WORD_COUNT)
    private val conWordSpinner = SizedSpinner(10)

    private val numCriteriaLabel = Text(Labels.NUM_CRITERIA)
    private val numCriteriaSpinner = SizedSpinner(5)
    private val criteriaWordLabel = Text(Labels.WORD_COUNT)
    private val criteriaWordSpinner = SizedSpinner(275)

    private val numFaqLabel = Text(Labels.NUM_FAQ)
    private val numFaqSpinner = SizedSpinner(5)
    private val faqWordLabel = Text(Labels.WORD_COUNT)
    private val faqWordSpinner = SizedSpinner(75)

    private val numSourcesLabel = Text(Labels.NUM_FAQ)
    private val numSourcesSpinner = SizedSpinner(5)

    private val completeBtn = Button("Finished")

    @PostConstruct
    private fun initialize(){
        configureUi()
        configureState()
        configureListeners()
    }

    private fun configureState() {
        includeIntroductionBox.selectedProperty().value = true
        includeProductLongReviewLabel.selectedProperty().value = true
        includeCostValueBox.selectedProperty().value = true
        includeConclusionBox.selectedProperty().value = true
    }

    private fun configureListeners() {
        includeIntroductionBox.selectedProperty().addListener { _, _, newValue -> introductionSpinner.disableProperty().value = !newValue }
        numProductsSpinner.valueProperty().addListener { _, _, newValue ->
            val affectedControls = listOf(includeProductLongReviewLabel, longReviewSpinner, numAspectsSpinner, aspectsWordCountSpinner,
                    includeCostValueBox, includeCostValueSpinner, numProsSpinner, proWordSpinner, numConsSpinner, conWordSpinner)
            when(newValue){
                0 -> {
                    affectedControls.forEach { it.disableProperty().value = true }
                }
                else -> {
                    affectedControls.forEach { it.disableProperty().value = false }
                }
            }
        }
        includeProductLongReviewLabel.selectedProperty().addListener { _, _, newValue -> longReviewSpinner.disableProperty().value = !newValue }
        numAspectsSpinner.valueProperty().addListener{ _, _, newValue ->
            when(newValue){
                0 -> aspectsWordCountSpinner.disableProperty().value = true
                else -> aspectsWordCountSpinner.disableProperty().value = false
            }
        }
        includeCostValueBox.selectedProperty().addListener{ _, _, newValue -> includeCostValueSpinner.disableProperty().value = !newValue }
        numProsSpinner.valueProperty().addListener{ _, _, newValue ->
            when(newValue){
                0 -> proWordSpinner.disableProperty().value = true
                else -> proWordSpinner.disableProperty().value = false
            }
        }
        numConsSpinner.valueProperty().addListener{ _, _, newValue ->
            when(newValue){
                0 -> conWordSpinner.disableProperty().value = true
                else -> conWordSpinner.disableProperty().value = false
            }
        }
        includeConclusionBox.selectedProperty().addListener{ _, _, newValue -> conclusionSpinner.disableProperty().value = !newValue}

        numCriteriaSpinner.valueProperty().addListener { _, _, newValue ->
            when(newValue) {
                0 -> criteriaWordSpinner.disableProperty().value = true
                else -> criteriaWordSpinner.disableProperty().value = false
            }
        }
        numFaqSpinner.valueProperty().addListener(SpinnerChangeListener(faqWordSpinner))
    }

    private fun configureUi(){
        val grid = AlignedGridPane()
        with(grid){
            addRow(0, includeIntroductionBox, AlignedGridPane(introductionCountLabel, introductionSpinner))
            addRow(1, AlignedGridPane(numProductsLabel, numProductsSpinner))
            addRow(2, includeProductLongReviewLabel, AlignedGridPane(longReviewCountLabel, longReviewSpinner))
            addRow(3, AlignedGridPane(numAspectsLabel, numAspectsSpinner), AlignedGridPane(aspectsWordCount, aspectsWordCountSpinner))
            addRow(4, includeCostValueBox, AlignedGridPane(includeCostValueLabel, includeCostValueSpinner))
            addRow(5, AlignedGridPane(numProsLabel, numProsSpinner), AlignedGridPane(proWordLabel, proWordSpinner))
            addRow(6, AlignedGridPane(numConsLabel, numConsSpinner), AlignedGridPane(conWordLabel, conWordSpinner))
            addRow(7, includeConclusionBox, AlignedGridPane(includeConclusionLabel, conclusionSpinner))
            addRow(8, AlignedGridPane(numCriteriaLabel, numCriteriaSpinner), AlignedGridPane(criteriaWordLabel, criteriaWordSpinner))
            addRow(9, AlignedGridPane(numFaqLabel, numFaqSpinner), AlignedGridPane(faqWordLabel, faqWordSpinner))
            addRow(10, AlignedGridPane(numSourcesLabel, numSourcesSpinner))
        }
        center = grid

        val hBox = HBox(completeBtn)
        hBox.alignment = Pos.CENTER_RIGHT
        hBox.padding = Insets(10.toDouble(), 10.toDouble(), 10.toDouble(), 10.toDouble())
        bottom = hBox
    }
}

private class SpinnerChangeListener(vararg targets : Node) : ChangeListener<Int>{

    private val targetNodes = listOf(*targets)

    override fun changed(observable: ObservableValue<Int>?, oldValue: Int, newValue: Int) {
        when(newValue){
            0 -> targetNodes.forEach { it.disableProperty().value = true }
            else -> targetNodes.forEach{ it.disableProperty().value = false }
        }
    }
}