package com.stonesoupprogramming.writerfx.export

import com.stonesoupprogramming.writerfx.models.*

fun StringBuilder.appendLine(string: String){
    this.append("$string\n")
}

fun StringBuilder.appendLine(){
    this.append("\n")
}

class BuyingGuideFileExporter(private val buyingGuide: BuyingGuide) {

    private val stringBuilder = StringBuilder()

    fun process() : String {
        return with(stringBuilder){
            appendLine("Best ${buyingGuide.title.entryText} Reviewed & Rated for Quality")
            appendLine()
            appendLine(buyingGuide.introduction.entryText)
            appendLine()

            appendLine("""<h2 style="text-align: center;">10 Best ${buyingGuide.title.entryText}</h2>""")
            appendLine()
            buyingGuide.reviewedProducts.forEachIndexed { index, reviewedProduct ->  processProduct(index, reviewedProduct)}

            appendLine(buyingGuide.conclusion.entryText)
            appendLine()

            appendLine("""<h2 style="text-align: center;">Criteria Used to Evaluate ${buyingGuide.title.entryText}</h2>""")
            appendLine()
            buyingGuide.criteria.forEach { processTitledEntry(it) }

            appendLine()
            appendLine("""<h2 styles="text-align: center;">FAQ</h2>""")
            appendLine("""<p style=text-align: center;"><strong>Frequently Asked Questions</strong></p>""")
            appendLine()

            buyingGuide.faq.forEach { processFaq(it) }
            appendLine()

            appendLine("<h3>Sources</h3>")
            buyingGuide.sources.forEachIndexed { index, source -> processSource(index, source) }
            toString()
        }
    }

    private fun processFaq(faq: TitledEntry) {
        with(stringBuilder){
            appendLine("<b>Q. ${faq.title}</b>")
            appendLine()
            appendLine("<strong>A:</strong> ${faq.entryText}")
            appendLine()
        }
    }

    private fun processSource(index: Int, source: ReadOnlyTitledEntry) {
        stringBuilder.appendLine("${index + 1}. ${source.entryText}")
    }

    private fun processTitledEntry(te: TitledEntry) {
        with(stringBuilder){
            appendLine("<strong>${te.title}</strong>")
            appendLine()
            appendLine(te.entryText)
            appendLine()
        }
    }

    private fun processProduct(index: Int, reviewedProduct: ReviewedProduct) {
        with(stringBuilder){
            appendLine("${index + 1}. ${reviewedProduct.longReview.title}")
            appendLine()

            appendLine(reviewedProduct.longReview.entryText)
            appendLine()

            reviewedProduct.aspects.forEach { processTitledEntry(it) }
            processReadOnlyTitledEntry(reviewedProduct.costAndValue)

            appendLine()
            reviewedProduct.pros.forEach { stringBuilder.appendLine("${it.entryText}\n") }
            reviewedProduct.cons.forEach { stringBuilder.appendLine("${it.entryText}\n") }
        }
    }

    private fun processReadOnlyTitledEntry(rte: ReadOnlyMeasuredTitleEntry) {
        with(stringBuilder){
            appendLine("<strong>${rte.title}</strong>")
            appendLine()
            appendLine(rte.entryText)
        }
    }
}