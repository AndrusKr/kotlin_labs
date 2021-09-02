// Приложение для чтения интернет-ресурсов.
// Разработать приложение для чтение интернет-ресурсов (RSS).
// При первом старте приложения после установки пользователь
// выбирает интересующие ресурсы (3 максимум) ,
// затем получает список новостей формата Дата, Картинка, Описание.
// При выборе интересующей новости, сайт открывается В ПРИЛОЖЕНИИ
// ( не браузером по умолчанию, не chrome и т.д.).
// Из открытой новости пользователь может выйти назад в приложение
// (отдельная кнопка, не системная кнопка(или жест) Назад),
// поделится новостью в соц сетях(из приложения, а не сайта).
// Предусмотреть возможность хранения кэша.
// Ориентир приложения Flipboard.
package by.andrus.rss

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import by.andrus.rss.adapter.FeedAdapter
import by.andrus.rss.common.HttpDataHandler
import by.andrus.rss.model.RootRss
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private val rssLink = "https://rss.nytimes.com/services/xml/rss/nyt/Business.xml"
    private val rssToJsonApi = "https://api.rss2json.com/v1/api.json?rss_url="
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.title = "NEWS"
        setSupportActionBar(toolbar)
        val linearLayoutManager =
            LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        loadRSS()
    }

    private fun loadRSS() {
        val loadRSSAsync = @SuppressLint("StaticFieldLeak")
        object : AsyncTask<String, String, String>() {
            var mDialog = ProgressDialog(this@MainActivity)
            override fun onPreExecute() {
                mDialog.setMessage("Please wait...")
                mDialog.show()
            }

            override fun onPostExecute(result: String?) {
                mDialog.dismiss()
                val rootRss = Gson().fromJson(result, RootRss::class.java)
                val adapter = FeedAdapter(rootRss, baseContext)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }

            override fun doInBackground(vararg params: String): String {
                val http = HttpDataHandler()
                return http.getHTTPDataHandler(params[0]).toString()
            }
        }
        val urlGetData = StringBuilder(rssToJsonApi)
        println(urlGetData)
        urlGetData.append(rssLink)
        loadRSSAsync.execute(urlGetData.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_refresh)
            loadRSS()
        return true
    }
}