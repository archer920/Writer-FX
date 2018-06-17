package com.stonesoupprogramming.writerfx

import com.google.gson.GsonBuilder
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.util.*

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

class WriterFX : Application(), Observer {


    private val title = TitleArticleEntryWidget(ArticleEntryWidget(20, this, lines = 1), "Article Title", true )
    private val intro = buildStandardSingleEntryFrame(200, this)
    private var products : List<ProductFrame>
    private val conclusion = buildStandardSingleEntryFrame( 200, this)
    private var criteria : List<TitledEntryFrame>
    private var faq : List<TitledEntryFrame>
    private val sources = buildStandardSourcesFrame(5)

    init {
        val productList = mutableListOf<ProductFrame>()
        for(i in 1..Constants.NUM_PRODUCTS){
            productList.add(buildStandardProductFrame(this,"Product $i"))
        }
        products = productList.toList()

        val criteriaList = mutableListOf<TitledEntryFrame>()
        for(i in 1..Constants.NUM_CRITERIA){
            criteriaList.add(buildStandardTitledEntryFrame("Criteria $i", Constants.CRITERIA_WORDS, this))
        }
        criteria = criteriaList.toList()

        val faqList = mutableListOf<TitledEntryFrame>()
        for(i in 1..Constants.NUM_FAQ){
            faqList.add(buildStandardTitledEntryFrame("FAQ $i", Constants.FAQ_WORDS, this))
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
        val hBox = HBox()

        pane.top = title
        pane.center = tabs

        val save = Button("Save")
        save.onAction = EventHandler {
            save()
        }
        val export = Button("Export")
        export.onAction = EventHandler {
            export()
        }
        hBox.children.addAll(save, export)
        pane.bottom = hBox

        val scene = Scene(pane, 600.toDouble(), 800.toDouble())
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun toDocument() : Document =
            Document(title.articleText,
                    intro.toArticleString(),
                    products.map { it.toReviewedProduct() },
                    conclusion.toArticleString(),
                    criteria.map { it.toCriteria() },
                    faq.map { it.toFaq() },
                    sources.toSources())

    private fun toJSON() : String =
            GsonBuilder().setPrettyPrinting().create().toJson(toDocument())


    private fun save() {
        val file = File(System.getProperty("user.home") + "/articles/fx/backup/${toDocument().title}.${System.currentTimeMillis()}.json")
        if(!file.exists()){
            file.createNewFile()
        }

        BufferedWriter(FileWriter(file)).use {
            it.write(toJSON())
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        save()
    }

    fun export(){
        val file = File(System.getProperty("user.home") + "/articles/${toDocument().title.replace(" ", "_")}.txt")
        if(!file.exists()){
            file.createNewFile()
        }
        BufferedWriter(FileWriter(file)).use {
            it.write(BuyingGuidExporter(toDocument()).process())
        }
        println(file.path)
        val p = Runtime.getRuntime().exec("open ${file.path}")
        with(p){
            waitFor()
            println("Exit code is ${p.exitValue()}")
        }
        println("Done")
    }
}