package com.stonesoupprogramming.writerfx.ui

import com.stonesoupprogramming.writerfx.configuration.BeanNames
import javafx.scene.control.ScrollPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TitledPane
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
}

class TitledAccordionPane(
        title: String,
        titledWidget: TitledWidget) : TitledPane() {

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
class TabFrame(@Autowired @Qualifier(BeanNames.INTRODUCTION) val introduction: Tab,
               @Autowired @Qualifier(BeanNames.PRODUCTS) val products: Tab,
               @Autowired @Qualifier(BeanNames.CONCLUSION) val conclusion: Tab,
               @Autowired @Qualifier(BeanNames.CRITERIA) val criteria: Tab,
               @Autowired @Qualifier(BeanNames.FAQ) val faq: Tab,
               @Autowired @Qualifier(BeanNames.SOURCES) val sources: Tab) : TabPane() {


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
}