package synottip.trose.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import synottip.trose.R
import synottip.trose.utils.SAVED_SETTINGS
import synottip.trose.utils.URL

class MainFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref =
            requireActivity().getSharedPreferences(SAVED_SETTINGS, Context.MODE_PRIVATE)
        val sharedUrl = sharedPref.getString(URL, "")
        viewModel.getFromLocal(
            pathUrl = sharedUrl ?: "",
            checkedInternetConnection = checkedInternetConnection()
        )
        viewModel.showData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MainFragmentState.SuccessConnect -> {
                    val editor = sharedPref.edit()
                    editor.putString(URL, state.remoteData.urlPath)
                    editor.apply()
                    webView.isVisible = true
                    errorTextView.isVisible = false
                    recycler.isVisible = false
                    fab.isVisible = false
                    chronometer.isVisible = false
                    startButton.isVisible = false
                    stopButton.isVisible = false
                    resetButton.isVisible = false
                    progress.isVisible = false
                    currentState = state
                    initWebView(savedInstanceState, state.remoteData.urlPath)
                }
                is MainFragmentState.NoInternet -> {
                    webView.isVisible = false
                    recycler.isVisible = false
                    errorTextView.isVisible = true
                    fab.isVisible = false
                    chronometer.isVisible = false
                    startButton.isVisible = false
                    stopButton.isVisible = false
                    resetButton.isVisible = false
                    progress.isVisible = false
                    currentState = state
                    errorTextView.text = state.message
                }
                is MainFragmentState.Loading -> {
                    webView.isVisible = false
                    recycler.isVisible = false
                    errorTextView.isVisible = true
                    fab.isVisible = false
                    chronometer.isVisible = false
                    fab.isVisible = false
                    startButton.isVisible = false
                    stopButton.isVisible = false
                    resetButton.isVisible = false
                    progress.isVisible = true
                    currentState = state
                }
                is MainFragmentState.Error -> {
                    webView.isVisible = false
                    recycler.isVisible = false
                    errorTextView.isVisible = true
                    errorTextView.text = state.message
                    fab.isVisible = false
                    chronometer.isVisible = false
                    fab.isVisible = false
                    startButton.isVisible = false
                    stopButton.isVisible = false
                    resetButton.isVisible = false
                    progress.isVisible = false
                    currentState = state
                }
                is MainFragmentState.Offline -> {
                    recycler.isVisible = true
                    webView.isVisible = false
                    errorTextView.isVisible = false
                    fab.isVisible = true
                    chronometer.isVisible = true
                    startButton.isVisible = true
                    stopButton.isVisible = true
                    resetButton.isVisible = true
                    progress.isVisible = false
                    currentState = state
                    setRecycledView(state.notes)
                }
            }
        }
    }

    private fun checkedInternetConnection() : Boolean {
        var result = false
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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

    private fun setRecycledView(notes: List<Note>) {
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager
            .VERTICAL, false)
        val adapter = NoteAdapter()
        adapter.submitList(notes)
        recycler.adapter = adapter
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
        supportActionBar?.hide()
        errorTextView = findViewById(R.id.errorTv)
        webView = findViewById(R.id.webView)
        recycler = findViewById(R.id.noteRv)
        fab = findViewById(R.id.fab)
        chronometer = findViewById(R.id.chronometer)
        startButton = findViewById(R.id.start_bt)
        stopButton = findViewById(R.id.stop_bt)
        resetButton = findViewById(R.id.reset_bt)
        progress = findViewById(R.id.progress)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            when (currentState) {
                is MaintActivityState.SuccessConnect -> {

                }
                else -> {
                    super.onBackPressed()
                }
            }

        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }
}
