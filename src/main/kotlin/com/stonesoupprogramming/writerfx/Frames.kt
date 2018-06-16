package com.stonesoupprogramming.writerfx

import javafx.scene.control.ScrollPane
import javafx.scene.control.TitledPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox

class SingleEntryFrame (articleEntryWidget : MeasuredArticleNode) : Pane(articleEntryWidget.asNode())

class TitledEntryFrame(title : String, widget : TitledMeasuredArticleNode) : TitledPane() {
    init {
        text = title
        content = widget.asNode()
    }
}

class ProductFrame (
        title: String,
        private val intro : TitledMeasuredArticleNode,
        private val aspects : List<TitledMeasuredArticleNode>,
        private val costAndValue : MeasuredArticleNode,
        private val pros : List<MeasuredArticleNode>,
        private val cons : List<MeasuredArticleNode>) : TitledPane(){

    init {
        text = title

        val vBox = VBox()

        with (vBox.children){
            add(intro.asNode())
            addAll(aspects.map { it.asNode() })
            add(costAndValue.asNode())
            addAll(pros.map { it.asNode() })
            addAll(cons.map { it.asNode() })
        }

        val scrollPane = ScrollPane()
        with(scrollPane){
            vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            fitToHeightProperty().value = true
            fitToWidthProperty().value = true
            content = vBox
        }
        content = scrollPane
    }
}

class SourcesFrame(private val sources : List<ArticleNode>, title : String = "Sources") : VBox(){
    init {
        children.addAll(sources.map { it.asNode() })
    }
}

fun buildStandardSingleEntryFrame(numWords : Int) =
    SingleEntryFrame(MeasuredArticleEntryWidget(ArticleEntryWidget(numWords)))

fun buildStandardTitledEntryFrame(title : String, numWords: Int) =
        TitledEntryFrame(title, TitledMeasuredArticleEntryWidget(ArticleEntryWidget(numWords), title))


fun buildStandardProductFrame(title : String,
                              introWords : Int = 60,
                              numAspects : Int = 2,
                              aspectWords : Int = 30,
                              costValueWords: Int = 30,
                              numPros : Int = 5,
                              numCons : Int = 2,
                              proConWords : Int =  10) : ProductFrame {
    val intro = buildStandardTitledMeasuredEntryWidget(introWords, title)
    val aspects = mutableListOf<TitledMeasuredArticleEntryWidget>()
    for (i in 0..numAspects){
        aspects.add(buildStandardTitledMeasuredEntryWidget(aspectWords, "Important Aspect ${i + 1}"))
    }
    val costValue = buildStandardTitledMeasuredEntryWidget(costValueWords, "Cost and Value", true)

    val pros = mutableListOf<TitleArticleEntryWidget>()
    for (i in 0..numPros){
        pros.add(buildStandardTitledWidget(proConWords, "Pro ${i + 1}", true, 1))
    }

    val cons = mutableListOf<TitleArticleEntryWidget>()
    for (i in 0..numCons){
        cons.add(buildStandardTitledWidget(proConWords, "Con ${i + 1}", true, 1))
    }
    return ProductFrame(title, intro, aspects, costValue, pros, cons)
}

fun buildStandardSourcesFrame(numSources : Int = 5) : SourcesFrame {
    val frames = mutableListOf<ArticleNode>()
    for(i in 1..numSources){
        frames.add(SourceWidget("Source ${i + 1}"))
    }
    return SourcesFrame(frames)
}