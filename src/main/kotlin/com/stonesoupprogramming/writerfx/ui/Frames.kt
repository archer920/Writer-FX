package com.stonesoupprogramming.writerfx.ui

import com.stonesoupprogramming.writerfx.configuration.BeanNames
import com.stonesoupprogramming.writerfx.configuration.Constants
import com.stonesoupprogramming.writerfx.models.BuyingGuide
import com.stonesoupprogramming.writerfx.models.Entry
import com.stonesoupprogramming.writerfx.models.ReviewedProduct
import com.stonesoupprogramming.writerfx.models.TitledEntry
import com.stonesoupprogramming.writerfx.service.LocalBuyingGuideFileService
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.io.File
import java.util.*
import javax.annotation.PostConstruct

class ProductReviewFrame(
        title : String,
        val longReview : TitledWidget,
        val aspects : List<TitledWidget>,
        val costAndValue : ReadOnlyTitledWidget,
        val pros : List<ReadOnlyTitledWidget>,
        val cons : List<ReadOnlyTitledWidget>) : TitledPane() {

    init {
        text = title

        val vBox = VBox()

        with (vBox.children){
            add(longReview)
            addAll(aspects)
            add(costAndValue)
            addAll(pros)
            addAll(cons)
        }

        val scrollPane = ScrollPane()
        with (scrollPane){
            //vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            fitToHeightProperty().value = true
            fitToWidthProperty().value = true
            content = vBox
        }
        content = scrollPane
    }

    fun toReviewedProduct() =
            ReviewedProduct(longReview, aspects, costAndValue, pros, cons)

    fun fromReviewedProduct(reviewedProduct: ReviewedProduct) {
        longReview.fromSimplified(reviewedProduct.longReview)
        aspects.forEachIndexed { index, aspect ->  aspect.fromSimplified(reviewedProduct.aspects[index])}
        costAndValue.fromSimplified(reviewedProduct.costAndValue)
        pros.forEachIndexed { index, pro -> pro.fromSimplified(reviewedProduct.pros[index]) }
        cons.forEachIndexed { index, con -> con.fromSimplified(reviewedProduct.cons[index])}
    }
}

class ReviewedProductsTab(private val productReviewFrames : List<ProductReviewFrame>) : Tab(BeanNames.PRODUCTS){

    init {
        val accordion = Accordion()
        accordion.panes.addAll(productReviewFrames)
        content = accordion
    }

    fun toReviewedProducts() =
            productReviewFrames.map { it.toReviewedProduct() }

    fun fromReviewedProducts(reviewedProducts: List<ReviewedProduct>) {
        productReviewFrames.forEachIndexed { index, productReviewFrame -> productReviewFrame.fromReviewedProduct(reviewedProducts[index]) }
    }
}

class MeasuredEntryTab(title: String, val measuredEntryWidget: MeasuredEntryWidget): Tab(title){
    init {
        content = measuredEntryWidget
    }
}

class AccordionTab(title: String, val panes : List<TitledAccordionPane>) : Tab(title) {
    init {
        val accordion = Accordion()
        accordion.panes.addAll(panes)
        content = accordion
    }

    fun toMeasuredEntry() =
            panes.map { it.titledWidget }.toList()

    fun fromMeasuredEntry(criteria: List<TitledEntry>) {
        panes.forEachIndexed { index, pane -> pane.titledWidget.fromSimplified(criteria[index]) }
    }
}

class TitledAccordionPane(
        title: String,
        val titledWidget: TitledWidget) : TitledPane() {

    init {
        text = title

        val scrollPane = ScrollPane()
        with(scrollPane){
            vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            fitToHeightProperty().value = true
            fitToWidthProperty().value = true
            content = titledWidget
        }
        content = scrollPane
    }
}

@Component
class SourcesTab(@Autowired private val entryObservable: EntryObservable) : Tab(BeanNames.SOURCES){


    val sources : List<Entry>
        get() = _sources.map { it }.toList()

    private val _sources = mutableListOf<TitledLineEntryWidget>()

    init {
        val vBox = VBox()

        for(i in 0 until Constants.NUM_SOURCES){
            val entry = TitledLineEntryWidget(entryObservable, Constants.SOURCE_TITLE + " ${i + 1}")
            _sources.add(entry)
            vBox.children.add(entry)
        }
        content = vBox
    }

    fun fromSources(sources: List<Entry>) {
        _sources.forEachIndexed { index, source -> source.fromSimplified(sources[index]) }
    }
}

@Component
class TabFrame(@Autowired @Qualifier(BeanNames.INTRODUCTION) val introduction: MeasuredEntryTab,
               @Autowired @Qualifier(BeanNames.PRODUCTS) val products: ReviewedProductsTab,
               @Autowired @Qualifier(BeanNames.CONCLUSION) val conclusion: MeasuredEntryTab,
               @Autowired @Qualifier(BeanNames.CRITERIA) val criteria: AccordionTab,
               @Autowired @Qualifier(BeanNames.FAQ) val faq: AccordionTab,
               @Autowired val sources: SourcesTab) : TabPane() {


    @PostConstruct
    private fun init(){
        tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
        tabs.addAll(introduction, products, conclusion, criteria, faq, sources)
    }
}

@Component
class UiMenu {

    val menuBar = MenuBar()
    val fileMenu = Menu("File")
    val openItem = MenuItem("Open")
    val saveItem = MenuItem("Save")
    val exportItem = MenuItem("Export")
    val exitItem = MenuItem("Exit")

    @PostConstruct
    fun init(){
        fileMenu.items.addAll(openItem, saveItem, exportItem, exitItem)
        menuBar.useSystemMenuBarProperty().value = true
        menuBar.menus.addAll(fileMenu)
    }
}

@Component
class FilePicker {

    fun openFile(primaryStage: Stage) : File? {
        with(FileChooser()){
            title = "Open File"
            return showOpenDialog(primaryStage)
        }
    }

    fun saveFile(primaryStage: Stage) : File? {
        with(FileChooser()){
            title = "Save File"
            return showSaveDialog(primaryStage)
        }
    }

    fun exportFile(primaryStage: Stage) : File? {
        with(FileChooser()){
            title = "Export File"
            return showSaveDialog(primaryStage)
        }
    }
}

@Component
class ArticleWriterUI(@Autowired @Qualifier(BeanNames.TITLE) val title: TitledLineEntryWidget,
                      @Autowired val tabFrame: TabFrame,
                      @Autowired val uiMenu: UiMenu,
                      @Autowired val filePicker: FilePicker,
                      @Autowired val localBuyingGuideFileService: LocalBuyingGuideFileService,
                      @Autowired val entryObservable: EntryObservable) : BorderPane(), Observer {

    lateinit var stage : Stage

    @PostConstruct
    private fun init(){
        top = title
        center = tabFrame

        entryObservable.addObserver(this)
        children.addAll(uiMenu.menuBar)

        uiMenu.saveItem.onAction = EventHandler<ActionEvent> { save() }
        uiMenu.openItem.onAction = EventHandler<ActionEvent> { open() }
    }

    override fun update(o: Observable?, arg: Any?) {
        autoSave()
    }

    private fun toBuyingGuide() =
            BuyingGuide(title.toSimplified(),
                    tabFrame.introduction.measuredEntryWidget,
                    tabFrame.products.toReviewedProducts(),
                    tabFrame.conclusion.measuredEntryWidget,
                    tabFrame.criteria.toMeasuredEntry(),
                    tabFrame.faq.toMeasuredEntry(),
                    tabFrame.sources.sources)


    private fun autoSave(){
        localBuyingGuideFileService.save(toBuyingGuide())
    }

    private fun save(){
        if(this::stage.isInitialized){
            with(filePicker.saveFile(stage)){
                if(this != null){
                    localBuyingGuideFileService.save(toBuyingGuide(), this)
                }
            }
        }
    }

    private fun open(){
        if(this::stage.isInitialized){
            with(filePicker.openFile(stage)){
                if(this != null){
                    val buyingGuide = localBuyingGuideFileService.load(this)
                    fromBuyingGuide(buyingGuide)
                }
            }
        }
    }

    private fun fromBuyingGuide(buyingGuide: BuyingGuide){
        title.fromSimplified(buyingGuide.title)
        tabFrame.introduction.measuredEntryWidget.fromSimplified(buyingGuide.introduction)
        tabFrame.products.fromReviewedProducts(buyingGuide.reviewedProducts)
        tabFrame.conclusion.measuredEntryWidget.fromSimplified(buyingGuide.conclusion)
        tabFrame.criteria.fromMeasuredEntry(buyingGuide.criteria)
        tabFrame.faq.fromMeasuredEntry(buyingGuide.faq)
        tabFrame.sources.fromSources(buyingGuide.sources)
    }
}