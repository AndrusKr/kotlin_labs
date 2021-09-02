package by.andrus.rss.common

import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class HttpDataHandler {
    fun getHTTPDataHandler(urlStr: String?): String? {
        try {
            val url = URL(urlStr)
            val urlConnection = url.openConnection() as HttpURLConnection
            if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream: InputStream = BufferedInputStream(urlConnection.inputStream)
                val r = BufferedReader(InputStreamReader(inputStream))
                val sb = StringBuilder()
                var line: String?
                while (r.readLine().also { line = it } != null) {
                    sb.append(line)
                    stream = sb.toString()
                    urlConnection.disconnect()
                }
            }
        } catch (e: IOException) {
            return null
        }
        return stream
    }

    companion object {
        var stream = ""
    }
}