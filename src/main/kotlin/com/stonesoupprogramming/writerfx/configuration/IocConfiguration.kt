package com.stonesoupprogramming.writerfx.configuration

import com.stonesoupprogramming.writerfx.ui.*
import javafx.scene.Node
import javafx.scene.control.Accordion
import javafx.scene.control.Tab
import javafx.scene.control.TitledPane
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

object Constants {
    const val DOCUMENT_TITLE = "Article Title"

    const val INTRODUCTION_WORDS = 200
    const val PRODUCT_LONG_REVIEW_WORDS = 60
    const val PRODUCT_LONG_REVIEW_TITLE = "Product"

    const val NUM_ASPECTS = 2
    const val ASPECT_TITLE = "Important Aspect "
    const val ASPECT_WORDS = 30

    const val COST_VALUE_TITLE = "Cost and Value"
    const val COST_VALUE_WORDS = 30

    const val NUM_PROS = 5
    const val PRO_TITLE = "Pro"
    const val NUM_CONS = 2
    const val CON_TITLE = "Con"
    const val PRO_CON_WORDS = 10

    const val PRODUCT_TITLE = "Product"
    const val NUM_PRODUCTS = 10

    const val CONCLUSION_WORDS = 200

    const val CRITERIA_TITLE = "Criteria"
    const val CRITERIA_WORDS = 275
    const val NUM_CRITERIA = 5

    const val FAQ_TITLE = "FAQ"
    const val FAQ_WORDS = 75
    const val NUM_FAQ = 5

    const val SOURCE_TITLE = "Source"
    const val NUM_SOURCES = 5
}

object BeanNames {
    const val INTRODUCTION = "Introduction"
    const val PRODUCTS = "Products"
    const val CONCLUSION = "Conclusion"
    const val CRITERIA = "Criteria"
    const val FAQ = "Frequently Asked Questions"
    const val SOURCES = "Sources"
    const val TITLE = "Article Title"
}

private fun buildTab(title : String, content : Node) : Tab {
    val tab = Tab()
    tab.text = title
    tab.content = content
    return tab
}

private fun buildAccordianTab(title : String, content: List<TitledPane>) : Tab {
    val accordion = Accordion()
    accordion.panes.addAll(content)
    return buildTab(title, accordion)
}

private fun longReviewWidget() = TitledWidget(Constants.PRODUCT_LONG_REVIEW_WORDS, Constants.PRODUCT_LONG_REVIEW_TITLE)

private fun aspectWidgets() : List<TitledWidget> {
    val aspects = mutableListOf<TitledWidget>()
    for (i in 0 until Constants.NUM_ASPECTS){
        aspects.add(TitledWidget(Constants.ASPECT_WORDS, Constants.ASPECT_TITLE + " ${i + 1}"))
    }
    return aspects.toList()
}

private fun costValueWidget() =
        ReadOnlyTitledWidget(Constants.COST_VALUE_WORDS, Constants.COST_VALUE_TITLE)


private fun proWidgets() : List<ReadOnlyTitledWidget> {
    val pros = mutableListOf<ReadOnlyTitledWidget>()
    for(i in 0 until Constants.NUM_PROS){
        pros.add(ReadOnlyTitledWidget(Constants.PRO_CON_WORDS, Constants.PRO_TITLE + " ${i + 1}"))
    }
    return pros.toList()
}

private fun conWidgets() : List<ReadOnlyTitledWidget> {
    val cons = mutableListOf<ReadOnlyTitledWidget>()
    for(i in 0 until Constants.NUM_CONS){
        cons.add(ReadOnlyTitledWidget(Constants.PRO_CON_WORDS, Constants.CON_TITLE + " ${i + 1}"))
    }
    return cons.toList()
}

private fun introductionFrame() = MeasuredEntryWidget(Constants.INTRODUCTION_WORDS)

private fun conclusionFrame() = MeasuredEntryWidget(Constants.CONCLUSION_WORDS)

private fun productFrame(title : String) =
        ProductReviewFrame(title,
                longReviewWidget(),
                aspectWidgets(),
                costValueWidget(),
                proWidgets(),
                conWidgets())

private fun productWidgets() : List<ProductReviewFrame> {
    val products = mutableListOf<ProductReviewFrame>()
    for(i in 0 until Constants.NUM_PRODUCTS){
        products.add(productFrame(Constants.PRODUCT_TITLE + " ${i + 1}"))
    }
    return products.toList()
}

private fun criteriaFrame(title : String) =
        TitledAccordionPane(title,
                TitledWidget(Constants.CRITERIA_WORDS, title))

private fun criteriaWidgets() : List<TitledAccordionPane> {
    val criteria = mutableListOf<TitledAccordionPane>()
    for(i in 0 until Constants.NUM_CRITERIA){
        criteria.add(criteriaFrame(Constants.CRITERIA_TITLE + " ${i + 1}"))
    }
    return criteria.toList()
}

private fun faqFrame(title: String) =
        TitledAccordionPane(title, TitledWidget(Constants.FAQ_WORDS, title))

private fun faqWidgets() : List<TitledAccordionPane> {
    val faq = mutableListOf<TitledAccordionPane>()
    for(i in 0 until Constants.NUM_FAQ){
        faq.add(faqFrame(Constants.FAQ_TITLE + " ${i + 1}"))
    }
    return faq.toList()
}

@Configuration
@ComponentScan(basePackages = ["com.stonesoupprogramming.writerfx"])
class IocConfiguration {

    @Bean(name = [BeanNames.TITLE])
    fun titleFrame() = TitledLineEntryWidget(Constants.DOCUMENT_TITLE)

    @Bean(name = [BeanNames.INTRODUCTION])
    fun introductionTab() =
            MeasuredEntryTab(BeanNames.INTRODUCTION, introductionFrame())

    @Bean(name = [BeanNames.PRODUCTS])
    fun productsTab() =
            ReviewedProductsTab(productWidgets())

    @Bean(name = [BeanNames.CONCLUSION])
    fun conclusionTab() =
            MeasuredEntryTab(BeanNames.CONCLUSION, conclusionFrame())

    @Bean(name = [BeanNames.CRITERIA])
    fun criteriaTab() =
            AccordionTab(BeanNames.CRITERIA, criteriaWidgets())

    @Bean(name = [BeanNames.FAQ])
    fun faqTab() =
            AccordionTab(BeanNames.FAQ, faqWidgets())
}


