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