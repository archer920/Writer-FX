package com.stonesoupprogramming.writerfx

import javafx.application.Application
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Accordion
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TitledPane
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

fun main(args: Array<String>){
    Application.launch(WriterFX::class.java, *args)
}

private object Constants {
    const val NUM_PRODUCTS = 10
    const val NUM_CRITERIA = 5
    const val NUM_FAQ = 5

    const val CRITERIA_WORDS = 275
    const val FAQ_WORDS = 75
}

private fun buildTab(title: String, node: Node) : Tab {
    val tab = Tab()
    tab.text = title
    tab.content = node
    return tab
}

private fun buildTab(title: String, nodes: List<TitledPane>) : Tab {
    val tab = Tab()
    tab.text = title

    val accordion = Accordion()
    accordion.panes.addAll(nodes)
    tab.content = accordion
    return tab
}

class WriterFX : Application() {

    private val intro = buildStandardSingleEntryFrame(200)
    private var products : List<ProductFrame>
    private val conclusion = buildStandardSingleEntryFrame( 200)
    private var criteria : List<TitledEntryFrame>
    private var faq : List<TitledEntryFrame>
    private val sources = buildStandardSourcesFrame(5)

    init {
        val productList = mutableListOf<ProductFrame>()
        for(i in 1..Constants.NUM_PRODUCTS){
            productList.add(buildStandardProductFrame("Product $i"))
        }
        products = productList.toList()

        val criteriaList = mutableListOf<TitledEntryFrame>()
        for(i in 1..Constants.NUM_CRITERIA){
            criteriaList.add(buildStandardTitledEntryFrame("Criteria $i", Constants.CRITERIA_WORDS))
        }
        criteria = criteriaList.toList()

        val faqList = mutableListOf<TitledEntryFrame>()
        for(i in 1..Constants.NUM_FAQ){
            faqList.add(buildStandardTitledEntryFrame("FAQ $i", Constants.FAQ_WORDS))
        }
        faq = faqList.toList()
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "Article Writer FX"

        val tabs = TabPane()
        tabs.tabs.add(buildTab("Introduction", intro))
        tabs.tabs.add(buildTab("Products", products))
        tabs.tabs.add(buildTab("Conclusion", conclusion))
        tabs.tabs.add(buildTab("Criteria", criteria))
        tabs.tabs.add(buildTab("FAQ", faq))
        tabs.tabs.add(buildTab("Sources", sources))


        val pane = BorderPane()
        pane.center = tabs

        val scene = Scene(pane, 600.toDouble(), 800.toDouble())
        primaryStage.scene = scene
        primaryStage.show()
    }
}