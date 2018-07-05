package com.stonesoupprogramming.writerfx.models

interface Entry {
    var entryText : String
}

interface MeasuredEntry : Entry {
    val requiredWords : Int
    val wordCount : Int
        get() = entryText.split(" ").size
    val progress : Double
        get() = wordCount.toDouble() / requiredWords.toDouble()
}

interface TitledEntry : Entry {
    var title : String
}

interface ReadOnlyTitledEntry : Entry {
    val title : String
}

interface MeasuredTitledEntry : TitledEntry, MeasuredEntry

interface ReadOnlyMeasuredTitleEntry : MeasuredEntry, ReadOnlyTitledEntry

data class ReviewedProduct(
        val longReview : MeasuredTitledEntry,
        val aspects : List<MeasuredTitledEntry>,
        val costAndValue: ReadOnlyMeasuredTitleEntry,
        val pros : List<ReadOnlyMeasuredTitleEntry>,
        val cons: List<ReadOnlyMeasuredTitleEntry>
)

data class BuyingGuide(
        val title: Entry,
        val introduction: MeasuredEntry,
        val reviewedProducts: List<ReviewedProduct>,
        val conclusion: Entry,
        val criteria: List<TitledEntry>,
        val faq : List<TitledEntry>,
        val sources : List<Entry>
)