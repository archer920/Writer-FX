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

data class SimpleEntry(override var entryText: String) : Entry

data class SimpleTitledEntry(override var entryText: String, override var title: String) : TitledEntry

data class SimpleMeasuredEntry(override var entryText: String, override val requiredWords : Int) : MeasuredEntry

data class SimpleMeasuredTitledEntry(override var entryText: String, override val requiredWords: Int, override var title: String) : MeasuredTitledEntry

data class SimpleReadOnlyMeasuredTitleEntry(override var entryText: String, override var requiredWords: Int, override val title: String) : ReadOnlyMeasuredTitleEntry

data class SimpleReadOnlyTitledEntry(override var entryText: String, override val title: String) : ReadOnlyTitledEntry

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
        @Transient val conclusion: Entry,
        @Transient val criteria: List<TitledEntry>,
        @Transient val faq : List<TitledEntry>,
        @Transient val sources : List<Entry>
)