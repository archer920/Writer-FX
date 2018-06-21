package com.stonesoupprogramming.writerfx

import com.google.gson.GsonBuilder
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.*
import java.util.*
import java.util.stream.Collectors

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

fun File.readJson() : Document {
    BufferedReader(FileReader(this)).use{
        val json = it.lines().collect(Collectors.joining())
        return GsonBuilder().setPrettyPrinting().create().fromJson(json, Document::class.java)
    }
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
        tabs.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
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

        val open = Button("Open")
        open.onAction = EventHandler {
            open(primaryStage)
        }

        val save = Button("Save")
        save.onAction = EventHandler {
            save()
        }
        val export = Button("Export")
        export.onAction = EventHandler {
            export()
        }
        hBox.children.addAll(open, save, export)
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
        val p = Runtime.getRuntime().exec("open -r ${file.path}")
        with(p){
            waitFor()
            println("Exit code is ${p.exitValue()}")
        }
        println("Done")
    }

    private fun open(primaryStage: Stage) {
        with(FileChooser()){
            title = "Open File"
            val file : File? = showOpenDialog(primaryStage)
            if(file != null){
                val document = file.readJson()
                title = document.title
                intro.fromDocument(document.introduction)
                for(i in 0 until products.size){
                    products[i].fromReviewedProduct(document.reviewedProducts[i])
                }
                conclusion.fromDocument(document.conclusion)
                for(i in 0 until criteria.size){
                    criteria[i].fromCriteria(document.criteria[i])
                }
                for(i in 0 until faq.size){
                    faq[i].fromFaq(document.faq[i])
                }
                sources.fromSources(document.sources)
            }
        }
    }

    private fun readFile(file: File) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}