package com.stonesoupprogramming.writerfx

fun StringBuilder.appendLine(string: String){
    this.append("$string\n")
}

fun StringBuilder.appendLine(){
    this.append("\n")
}

class BuyingGuidExporter(private val document: Document){

    private val stringBuilder = StringBuilder()
    private var productCount = 1

    fun process() : String {
        with(stringBuilder){
            appendLine("Best ${document.title} Reviewed & Rated for Quality")
            appendLine()
            appendLine("${document.introduction}")
            appendLine()

            appendLine("10 Best ${document.title}")
            appendLine()
            document.reviewedProducts.forEachIndexed { index, reviewedProduct ->  processProduct(index, reviewedProduct)}

            appendLine(document.conclusion)
            appendLine()

            appendLine("Criteria Used to Evaluate ${document.title} ")
            appendLine()
            document.criteria.forEach { processCriteria(it) }

            appendLine()
            appendLine("FAQ")
            appendLine("Frequently Asked Questions")
            appendLine()
            document.faq.forEach { processFaq(it) }
            appendLine("Sources")
            document.sources.forEachIndexed{ index, source -> processSource(index, source)}
        }
        return stringBuilder.toString()
    }

    private fun processSource(index: Int, source: String) {
        with(stringBuilder){
            appendLine("${index + 1}. $source")
        }
    }

    private fun processFaq(faq: Faq) {
        with(stringBuilder){
            appendLine()
            appendLine("Q: ${faq.question}")
            appendLine()
            appendLine("A: ${faq.answer}")
        }
    }

    private fun processCriteria(criteria: Criteria) {
        with(stringBuilder){
            appendLine(criteria.title)
            appendLine()
            appendLine(criteria.text)
            appendLine()
        }
    }

    private fun processProduct(index : Int, reviewedProduct: ReviewedProduct) {
        with(stringBuilder){
            appendLine("${index + 1}. ${reviewedProduct.productTitle}")
            appendLine()
            appendLine(reviewedProduct.introduction)
            appendLine()
            reviewedProduct.aspects.forEach { aspect -> processAspect(aspect)}
            processCostValue(reviewedProduct.costAndValue)
            reviewedProduct.pros.forEach {pro -> processProsCons(pro)}
            reviewedProduct.cons.forEach { con -> processProsCons(con) }
        }
    }

    private fun processProsCons(value: String) {
        with(stringBuilder){
            appendLine(value)
            appendLine()
        }
    }

    private fun processCostValue(costAndValue: String) {
        with(stringBuilder){
            appendLine("<strong>Cost and Value</strong>")
            appendLine()
            appendLine(costAndValue)
            appendLine()
        }
    }

    private fun processAspect(aspect: Aspect) {
        with(stringBuilder){
            appendLine("<strong>${aspect.title}</strong>")
            appendLine()
            appendLine(aspect.text)
            appendLine()
        }
    }
}