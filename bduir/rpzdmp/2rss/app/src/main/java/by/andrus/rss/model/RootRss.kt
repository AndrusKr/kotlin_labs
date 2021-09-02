package by.andrus.rss.model

data class RootRss(
    val status: String,
    val feed: Feed,
    val items: List<Item>
)
