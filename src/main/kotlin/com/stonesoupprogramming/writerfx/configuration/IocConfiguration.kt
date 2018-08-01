package com.stonesoupprogramming.writerfx.configuration

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.stonesoupprogramming.writerfx.doa.*
import com.stonesoupprogramming.writerfx.models.*
import com.stonesoupprogramming.writerfx.ui.*
import javafx.scene.Node
import javafx.scene.control.Accordion
import javafx.scene.control.Tab
import javafx.scene.control.TitledPane
import org.springframework.beans.factory.annotation.Autowired
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

private fun longReviewWidget(entryObservable: EntryObservable) = TitledWidget(entryObservable, Constants.PRODUCT_LONG_REVIEW_WORDS, Constants.PRODUCT_LONG_REVIEW_TITLE)

private fun aspectWidgets(entryObservable: EntryObservable) : List<TitledWidget> {
    val aspects = mutableListOf<TitledWidget>()
    for (i in 0 until Constants.NUM_ASPECTS){
        aspects.add(TitledWidget(entryObservable, Constants.ASPECT_WORDS, Constants.ASPECT_TITLE + " ${i + 1}"))
    }
    return aspects.toList()
}

private fun costValueWidget(entryObservable: EntryObservable) =
        ReadOnlyTitledWidget(entryObservable, Constants.COST_VALUE_WORDS, Constants.COST_VALUE_TITLE)


private fun proWidgets(entryObservable: EntryObservable) : List<ReadOnlyTitledWidget> {
    val pros = mutableListOf<ReadOnlyTitledWidget>()
    for(i in 0 until Constants.NUM_PROS){
        pros.add(ReadOnlyTitledWidget(entryObservable, Constants.PRO_CON_WORDS, Constants.PRO_TITLE + " ${i + 1}"))
    }
    return pros.toList()
}

private fun conWidgets(entryObservable: EntryObservable) : List<ReadOnlyTitledWidget> {
    val cons = mutableListOf<ReadOnlyTitledWidget>()
    for(i in 0 until Constants.NUM_CONS){
        cons.add(ReadOnlyTitledWidget(entryObservable, Constants.PRO_CON_WORDS, Constants.CON_TITLE + " ${i + 1}"))
    }
    return cons.toList()
}

private fun introductionFrame(entryObservable: EntryObservable) = MeasuredEntryWidget(entryObservable, Constants.INTRODUCTION_WORDS)

private fun conclusionFrame(entryObservable: EntryObservable) = MeasuredEntryWidget(entryObservable, Constants.CONCLUSION_WORDS)

private fun productFrame(entryObservable: EntryObservable, title : String) =
        ProductReviewFrame(title,
                longReviewWidget(entryObservable),
                aspectWidgets(entryObservable),
                costValueWidget(entryObservable),
                proWidgets(entryObservable),
                conWidgets(entryObservable))

private fun productWidgets(entryObservable: EntryObservable) : List<ProductReviewFrame> {
    val products = mutableListOf<ProductReviewFrame>()
    for(i in 0 until Constants.NUM_PRODUCTS){
        products.add(productFrame(entryObservable, Constants.PRODUCT_TITLE + " ${i + 1}"))
    }
    return products.toList()
}

private fun criteriaFrame(entryObservable: EntryObservable, title : String) =
        TitledAccordionPane(title,
                TitledWidget(entryObservable, Constants.CRITERIA_WORDS, title))

private fun criteriaWidgets(entryObservable: EntryObservable) : List<TitledAccordionPane> {
    val criteria = mutableListOf<TitledAccordionPane>()
    for(i in 0 until Constants.NUM_CRITERIA){
        criteria.add(criteriaFrame(entryObservable, Constants.CRITERIA_TITLE + " ${i + 1}"))
    }
    return criteria.toList()
}

private fun faqFrame(entryObservable: EntryObservable, title: String) =
        TitledAccordionPane(title, TitledWidget(entryObservable, Constants.FAQ_WORDS, title))

private fun faqWidgets(entryObservable: EntryObservable) : List<TitledAccordionPane> {
    val faq = mutableListOf<TitledAccordionPane>()
    for(i in 0 until Constants.NUM_FAQ){
        faq.add(faqFrame(entryObservable, Constants.FAQ_TITLE + " ${i + 1}"))
    }
    return faq.toList()
}

@Configuration
@ComponentScan(basePackages = ["com.stonesoupprogramming.writerfx"])
class IocConfiguration {

    @Bean(name = [BeanNames.TITLE])
    fun titleFrame(@Autowired entryObservable: EntryObservable) = TitledLineEntryWidget(entryObservable, Constants.DOCUMENT_TITLE)

    @Bean(name = [BeanNames.INTRODUCTION])
    fun introductionTab(@Autowired entryObservable: EntryObservable) =
            MeasuredEntryTab(BeanNames.INTRODUCTION, introductionFrame(entryObservable))

    @Bean(name = [BeanNames.PRODUCTS])
    fun productsTab(@Autowired entryObservable: EntryObservable) =
            ReviewedProductsTab(productWidgets(entryObservable))

    @Bean(name = [BeanNames.CONCLUSION])
    fun conclusionTab(@Autowired entryObservable: EntryObservable) =
            MeasuredEntryTab(BeanNames.CONCLUSION, conclusionFrame(entryObservable))

    @Bean(name = [BeanNames.CRITERIA])
    fun criteriaTab(@Autowired entryObservable: EntryObservable) =
            AccordionTab(BeanNames.CRITERIA, criteriaWidgets(entryObservable))

    @Bean(name = [BeanNames.FAQ])
    fun faqTab(@Autowired entryObservable: EntryObservable) =
            AccordionTab(BeanNames.FAQ, faqWidgets(entryObservable))

    @Bean
    fun gson(@Autowired entryTypeAdapter: EntryTypeAdapter,
             @Autowired titledEntryAdapter: TitledEntryAdapter,
             @Autowired measuredEntryAdapter: MeasuredEntryAdapter,
             @Autowired measuredTitledEntryAdapter: MeasuredTitledEntryAdapter,
             @Autowired readOnlyMeasuredTitledEntryAdapter: ReadOnlyMeasuredTitledEntryAdapter,
             @Autowired readOnlyTitledEntryAdapter: ReadOnlyTitledEntryAdapter): Gson {

        return GsonBuilder()
                .registerTypeAdapter(Entry::class.java, entryTypeAdapter)
                .registerTypeAdapter(TitledLineEntryWidget::class.java, readOnlyTitledEntryAdapter)
                .registerTypeAdapter(ReadOnlyTitledEntry::class.java, readOnlyTitledEntryAdapter)
                .registerTypeAdapter(MeasuredEntry::class.java, measuredEntryAdapter)
                .registerTypeAdapter(MeasuredEntryWidget::class.java, measuredEntryAdapter)
                .registerTypeAdapter(MeasuredTitledEntry::class.java, measuredTitledEntryAdapter)
                .registerTypeAdapter(TitledWidget::class.java, measuredTitledEntryAdapter)
                .registerTypeAdapter(ReadOnlyMeasuredTitleEntry::class.java, readOnlyMeasuredTitledEntryAdapter)
                .registerTypeAdapter(ReadOnlyTitledWidget::class.java, readOnlyMeasuredTitledEntryAdapter)
                .registerTypeAdapter(TitledEntry::class.java, titledEntryAdapter)
                .setPrettyPrinting().create()
    }
}


