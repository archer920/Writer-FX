package com.stonesoupprogramming.writerfx

interface ArticleEntry {
    var articleText : String
}

interface MeasuredArticleEntry : ArticleEntry {
    val requiredWords : Int
    val wordCount : Int
        get() = articleText.split(" ").size
    val progress : Double
        get() = wordCount.toDouble() / requiredWords.toDouble()
}

interface TitledEntryMeasured : MeasuredArticleEntry {
    var articleTitle : String
}

data class Document(val title : String,
                    val introduction : String,
                    val reviewedProducts : List<ReviewedProduct>,
                    val conclusion : String,
                    val criteria : List<Criteria>,
                    val faq : List<Faq>,
                    val sources : List<String>)

data class ReviewedProduct(
        val productTitle : String,
        val introduction: String,
        val aspects : List<Aspect>,
        val costAndValue : String,
        val pros : List<String>,
        val cons: List<String>)

data class Criteria(val title: String, val text: String)

data class Faq(val question : String, val answer : String)

data class Aspect(val title : String, val text : String)