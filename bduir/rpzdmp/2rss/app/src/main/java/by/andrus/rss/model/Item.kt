package by.andrus.rss.model

data class Item(
    val title: String,
    val pubDate: String,
    val link: String,
    val guid: String,
    val author: String,
    val thumbnail: String,
    val description: String,
    val content: String,
    val enclosure: Enclosure,
    val categories: List<String>
)
