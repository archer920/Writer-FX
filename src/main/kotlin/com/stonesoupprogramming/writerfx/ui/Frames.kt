package com.stonesoupprogramming.writerfx.ui

import com.stonesoupprogramming.writerfx.configuration.BeanNames
import com.stonesoupprogramming.writerfx.configuration.Constants
import com.stonesoupprogramming.writerfx.models.ReviewedProduct
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
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
class SourcesTab : Tab(BeanNames.SOURCES){

    val sources : List<TitledLineEntryWidget>
        get() = _sources.toList()

    private lateinit var _sources : MutableList<TitledLineEntryWidget>

    init {
        val vBox = VBox()

        for(i in 0 until Constants.NUM_SOURCES){
            val entry = TitledLineEntryWidget(Constants.SOURCE_TITLE + " ${i + 1}")
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
               @Autowired val sources: SourcesTab) : SourcesTab() {


    @PostConstruct
    private fun init(){
        tabClosingPolicy = TabClosingPolicy.UNAVAILABLE
        tabs.addAll(introduction, products, conclusion, criteria, faq, sources)
    }
}

@Component
class ArticleWriterUI(@Autowired @Qualifier(BeanNames.TITLE) val title: TitledLineEntryWidget,
                      @Autowired val tabFrame: TabFrame) : BorderPane() {

    @PostConstruct
    private fun init(){
        top = title
        center = tabFrame
    }

    fun toBuyingGuide(){

    }
}