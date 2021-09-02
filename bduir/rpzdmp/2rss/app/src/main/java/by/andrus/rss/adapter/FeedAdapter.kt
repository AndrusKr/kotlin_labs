package by.andrus.rss.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import by.andrus.rss.R
import by.andrus.rss.WebViewActivity
import by.andrus.rss.interfaces.ItemClickListener
import by.andrus.rss.model.RootRss
import java.io.IOException


class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    View.OnClickListener,
    View.OnLongClickListener {
    var txtTitle: TextView = itemView.findViewById(R.id.txtTitle) as TextView
    var txtPubDate: TextView = itemView.findViewById(R.id.txtPubDate) as TextView
    var txtContent: TextView = itemView.findViewById(R.id.txtContent) as TextView
    var imageView: ImageView = itemView.findViewById(R.id.imgView) as ImageView

    private var itemClickListener: ItemClickListener? = null

    init {
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onClick(v: View?) {
        itemClickListener!!.onClick(v, adapterPosition, false)
    }

    override fun onLongClick(v: View?): Boolean {
        itemClickListener!!.onClick(v, adapterPosition, true)
        return true
    }
}

class FeedAdapter(private val rootRssObj: RootRss, private val mContext: Context) :
    RecyclerView.Adapter<FeedViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(mContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val itemView = inflater.inflate(R.layout.row, parent, false)
        return FeedViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.txtTitle.text = rootRssObj.items[position].title
        holder.txtPubDate.text = rootRssObj.items[position].pubDate
        holder.txtContent.text = rootRssObj.items[position].content
        val imgUrl = rootRssObj.items[position].enclosure.link
        println(imgUrl)
        val loadImage = LoadImage(holder.imageView)
        loadImage.execute(imgUrl)
        holder.setItemClickListener { view, position, isLongClick ->
            if (!isLongClick) {
                val browserIntent =
                    Intent(mContext, WebViewActivity::class.java)
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                browserIntent.putExtra("url", rootRssObj.items[position].link)
                mContext.startActivity(browserIntent)
            }
        }
    }

    override fun getItemCount(): Int {
        return rootRssObj.items.size
    }

    private open class LoadImage(ivResult: ImageView) :
        AsyncTask<String, Void, Bitmap>() {
        val imageView: ImageView = ivResult

        override fun doInBackground(vararg strings: String): Bitmap? {
            var bitmap: Bitmap? = null
            try {
                val inputStream = java.net.URL(strings[0]).openStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bitmap
        }

        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }
}