package com.stonesoupprogramming.writerfx.ui

import com.stonesoupprogramming.writerfx.configuration.BeanNames
import com.stonesoupprogramming.writerfx.configuration.Constants
import com.stonesoupprogramming.writerfx.models.BuyingGuide
import com.stonesoupprogramming.writerfx.models.Entry
import com.stonesoupprogramming.writerfx.models.ReviewedProduct
import com.stonesoupprogramming.writerfx.service.LocalBuyingGuideFileService
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
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
            ReviewedProduct(longReview.toSimplified(), aspects.map { it.toSimplified() }, costAndValue.toSimplified(), pros.map { it.toSimplified() }, cons.map{ it.toSimplified() })
}

class ReviewedProductsTab(private val productReviewFrames : List<ProductReviewFrame>) : Tab(BeanNames.PRODUCTS){

    init {
        val accordion = Accordion()
        accordion.panes.addAll(productReviewFrames)
        content = accordion
    }

    fun toReviewedProducts() =
            productReviewFrames.map { it.toReviewedProduct() }
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
            panes.map { it.titledWidget.toSimplified() }.toList()
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
        get() = _sources.map { it.toSimplified() }.toList()

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
class ArticleWriterUI(@Autowired @Qualifier(BeanNames.TITLE) val title: TitledLineEntryWidget,
                      @Autowired val tabFrame: TabFrame,
                      @Autowired val localBuyingGuideFileService: LocalBuyingGuideFileService,
                      @Autowired val entryObservable: EntryObservable) : BorderPane(), Observer {

    @PostConstruct
    private fun init(){
        top = title
        center = tabFrame

        entryObservable.addObserver(this)
    }

    override fun update(o: Observable?, arg: Any?) {
        autoSave()
    }

    private fun toBuyingGuide() =
            BuyingGuide(title.toSimplified(),
                    tabFrame.introduction.measuredEntryWidget.toSimplified(),
                    tabFrame.products.toReviewedProducts(),
                    tabFrame.conclusion.measuredEntryWidget.toSimplified(),
                    tabFrame.criteria.toMeasuredEntry(),
                    tabFrame.faq.toMeasuredEntry(),
                    tabFrame.sources.sources)

    private fun autoSave(){
        localBuyingGuideFileService.save(toBuyingGuide())
    }
}