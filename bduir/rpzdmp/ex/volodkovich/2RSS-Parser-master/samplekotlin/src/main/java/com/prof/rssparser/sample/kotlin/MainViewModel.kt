

package com.prof.rssparser.sample.kotlin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prof.rssparser.Channel
import com.prof.rssparser.Parser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.io.File

class MainViewModel : ViewModel() {

    private var refreshChannel =  emptyArray<String>()

    fun getRefreshChannel(): Array<String> {
        return refreshChannel
    }
    private lateinit var articleListLive: MutableLiveData<Channel>

    private val _snackbar = MutableLiveData<String>()
    val snackbar: LiveData<String>
        get() = _snackbar

    private val _rssChannel = MutableLiveData<Channel>()
    val rssChannel: LiveData<Channel>
        get() = _rssChannel

    private val okHttpClient by lazy {
        OkHttpClient()
    }

    fun onSnackbarShowed() {
       _snackbar.value = null
    }

    fun fetchFeed(parser: Parser,isOnline: Boolean) {
        viewModelScope.launch {
            try {
                if (refreshChannel.isNullOrEmpty()) {
                    nul()
                } else{
                    if (isOnline) {
                        val channel = parser.getChannel(refreshChannel[0])
                       fetchForUrlAndParseRawRata(refreshChannel);
                    } else if (!isOnline) {

                        val channel = parser.getChannel(refreshChannel[0])
                        _rssChannel.postValue(channel)
                        //val channel = parser.getChannel(refreshChannel[0]) почему это не работает а с верху код работает
                       // _rssChannel.postValue(channel)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _snackbar.value = "An error has occurred. Please retry"
                _rssChannel.postValue(Channel(null, null, null, null, null, null, mutableListOf()))
            }
        }
    }

    fun fetchForUrlAndParseRawRata(url: Array<String>) {
        val parser = Parser.Builder().build()

        viewModelScope.launch(Dispatchers.IO) {
            refreshChannel = emptyArray()
            var bool = true
            var channel = Channel(null, null, null, null, null, null, mutableListOf())
            for (i in 0..url.size-1)
            {
                if (bool) {
                    channel = parser.getChannel(url[i])
                    bool = false
                } else {
                    channel.articles.addAll(parser.getChannel(url[i]).articles)
                }
                refreshChannel+=url[i]
            }
            _rssChannel.postValue(channel)
        }
    }

    fun nul() {
        viewModelScope.launch(Dispatchers.IO) {
            refreshChannel = emptyArray()
            _rssChannel.postValue(Channel(null, null, null, null, null, null, mutableListOf()))
        }
    }
    fun refreshChannelFill(url: Array<String>)
    {
        refreshChannel = emptyArray()
        url.forEach { refreshChannel+= it }
    }
}
