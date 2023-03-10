package synottip.trose.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import synottip.trose.R
import synottip.trose.data.ItemWorldCup
import synottip.trose.databinding.ActivityMainBinding
import synottip.trose.ui.detail.DetailActivity
import synottip.trose.utils.SAVED_SETTINGS
import synottip.trose.utils.URL

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var webView: WebView
    private lateinit var errorTextView: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var card: CardView
    private lateinit var progress: ProgressBar
    private lateinit var currentState: MainFragmentState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        initElements()
        val sharedPref =
            this.getSharedPreferences(SAVED_SETTINGS, Context.MODE_PRIVATE)
        val sharedUrl = sharedPref.getString(URL, "")
        currentState = MainFragmentState.Loading
        viewModel.getFromLocal(
            pathUrl = sharedUrl ?: "",
            checkedInternetConnection = checkedInternetConnection()
        )
        viewModel.showData.observe(this) { state ->
            when (state) {
                is MainFragmentState.SuccessConnect -> {
                    val editor = sharedPref.edit()
                    editor.putString(URL, state.remoteData.urlPath)
                    editor.apply()
                    webView.isVisible = true
                    errorTextView.isVisible = false
                    card.isVisible = false
                    progress.isVisible = false
                    currentState = state
                    initWebView(savedInstanceState, state.remoteData.urlPath)
                }
                is MainFragmentState.NoInternet -> {
                    webView.isVisible = false
                    card.isVisible = false
                    errorTextView.isVisible = true
                    progress.isVisible = false
                    currentState = state
                    errorTextView.text = state.message
                }
                is MainFragmentState.Loading -> {
                    webView.isVisible = false
                    card.isVisible = false
                    progress.isVisible = true
                    errorTextView.isVisible = false
                    currentState = state
                }
                is MainFragmentState.Error -> {
                    webView.isVisible = false
                    card.isVisible = false
                    errorTextView.isVisible = true
                    errorTextView.text = state.message
                    progress.isVisible = false
                    currentState = state
                }
                is MainFragmentState.Offline -> {
                    webView.isVisible = false
                    errorTextView.isVisible = false
                    card.isVisible = true
                    progress.isVisible = false
                    currentState = state
                    setRecycledView(state.data)
                }
            }
        }
    }

    private fun checkedInternetConnection() : Boolean {
        var result = false
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

    private fun setRecycledView(items: List<ItemWorldCup>) {
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager
            .VERTICAL, false)
        val stateClickListener: CupAdapter.OnStateClickListener =
            object : CupAdapter.OnStateClickListener {
                override fun onStateClick(item: ItemWorldCup, position: Int) {
                    val intent = Intent(this@MainActivity, DetailActivity::class.java)
                    intent.putExtra("year", item.year)

                    startActivity(intent)
                }
            }
        recycler.adapter = CupAdapter(items, stateClickListener)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(savedInstanceState: Bundle?, url:String) {
        webView = findViewById(R.id.webView)
        webView.webViewClient = WebViewClient()
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState)
        }
        else {
            webView.loadUrl(url)
        }
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        webSettings.setSupportZoom(false)
        webSettings.allowFileAccess = true
        webSettings.allowContentAccess = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
    }

    private fun initElements() {
        errorTextView = findViewById(R.id.errorTv)
        webView = findViewById(R.id.webView)
        recycler = findViewById(R.id.historyRv)
        card = findViewById(R.id.offline)
        progress = findViewById(R.id.progress)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            when (currentState) {
                is MainFragmentState.SuccessConnect -> {

                }
                else -> {
                    super.onBackPressed()
                }
            }

        }
    }
}