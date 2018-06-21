package com.stonesoupprogramming.writerfx

import javafx.scene.control.ScrollPane
import javafx.scene.control.TitledPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import java.util.*

class SingleEntryFrame(private val widget: MeasuredArticleNode) : Pane(widget.asNode()) {

    fun toArticleString() = widget.articleText
    fun fromDocument(introduction: String) {
        widget.articleText = introduction
    }
}

class TitledEntryFrame(title: String, private val widget: TitledMeasuredArticleNode) : TitledPane() {
    init {
        text = title
        content = widget.asNode()
    }

    fun toCriteria() = Criteria(widget.articleTitle, widget.articleText)

    fun toFaq() = Faq(widget.articleTitle, widget.articleText)
    fun fromCriteria(criteria: Criteria) {
        widget.articleTitle = criteria.title
        widget.articleText = criteria.text
    }

    fun fromFaq(faq: Faq) {
        widget.articleTitle = faq.question
        widget.articleText = faq.answer
    }
}

class ProductFrame(
        title: String,
        private val intro: TitledMeasuredArticleNode,
        private val aspects: List<TitledMeasuredArticleNode>,
        private val costAndValue: MeasuredArticleNode,
        private val pros: List<MeasuredArticleNode>,
        private val cons: List<MeasuredArticleNode>) : TitledPane() {

    init {
        text = title

        val vBox = VBox()

        with(vBox.children) {
            add(intro.asNode())
            addAll(aspects.map { it.asNode() })
            add(costAndValue.asNode())
            addAll(pros.map { it.asNode() })
            addAll(cons.map { it.asNode() })
        }

        val scrollPane = ScrollPane()
        with(scrollPane) {
            vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            fitToHeightProperty().value = true
            fitToWidthProperty().value = true
            content = vBox
        }
        content = scrollPane
    }

    fun toReviewedProduct(): ReviewedProduct =
            ReviewedProduct(
                    intro.articleTitle,
                    intro.articleText,
                    aspects.map { Aspect(it.articleTitle, it.articleText) },
                    costAndValue.articleText,
                    pros.map { it.articleText },
                    cons.map { it.articleText })

    fun fromReviewedProduct(reviewedProduct: ReviewedProduct) {
        intro.articleTitle = reviewedProduct.productTitle
        intro.articleText = reviewedProduct.introduction
        for(i in 0 until reviewedProduct.aspects.size){
            aspects[i].articleTitle = reviewedProduct.aspects[i].title
            aspects[i].articleText = reviewedProduct.aspects[i].text
        }
        costAndValue.articleText = reviewedProduct.costAndValue
        for(i in 0 until reviewedProduct.pros.size){
            pros[i].articleText = reviewedProduct.pros[i]
        }
        for(i in 0 until reviewedProduct.cons.size){
            cons[i].articleText = reviewedProduct.cons[i]
        }
    }
}

class SourcesFrame(private val sources: List<ArticleNode>) : VBox() {
    init {
        children.addAll(sources.map { it.asNode() })
    }

    fun toSources() = sources.map { it.articleText }
    fun fromSources(sources: List<String>) {
        for(i in 0 until sources.size){
            this.sources[i].articleText = sources[i]
        }
    }
}

fun buildStandardSingleEntryFrame(numWords: Int, observer: Observer) =
        SingleEntryFrame(MeasuredArticleEntryWidget(ArticleEntryWidget(numWords, observer)))

fun buildStandardTitledEntryFrame(title: String, numWords: Int, observer: Observer) =
        TitledEntryFrame(title, TitledMeasuredArticleEntryWidget(ArticleEntryWidget(numWords, observer), title))


fun buildStandardProductFrame(
        observer: Observer,
        title: String,
                              introWords: Int = 60,
                              numAspects: Int = 2,
                              aspectWords: Int = 30,
                              costValueWords: Int = 30,
                              numPros: Int = 5,
                              numCons: Int = 2,
                              proConWords: Int = 10): ProductFrame {
    val intro = buildStandardTitledMeasuredEntryWidget(introWords, observer, title, lines = 10)
    val aspects = mutableListOf<TitledMeasuredArticleEntryWidget>()
    for (i in 1..numAspects) {
        aspects.add(buildStandardTitledMeasuredEntryWidget(aspectWords, observer,"Important Aspect $i"))
    }
    val costValue = buildStandardTitledMeasuredEntryWidget(costValueWords, observer, "Cost and Value", true)

    val pros = mutableListOf<TitleArticleEntryWidget>()
    for (i in 1..numPros) {
        pros.add(buildStandardTitledWidget(proConWords, "Pro $i", observer,true, 1))
    }

    val cons = mutableListOf<TitleArticleEntryWidget>()
    for (i in 1..numCons) {
        cons.add(buildStandardTitledWidget(proConWords, "Con $i", observer,true, 1))
    }
    return ProductFrame(title, intro, aspects, costValue, pros, cons)
}

fun buildStandardSourcesFrame(numSources: Int = 5): SourcesFrame {
    val frames = mutableListOf<ArticleNode>()
    for (i in 1..numSources) {
        frames.add(SourceWidget("Source $i"))
    }
    return SourcesFrame(frames)
}