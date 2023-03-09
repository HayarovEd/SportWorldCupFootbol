package synottip.trose.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import synottip.trose.R
import synottip.trose.data.ItemWorldCup
import synottip.trose.utils.SAVED_SETTINGS
import synottip.trose.utils.URL
import java.security.Key

class MainFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var webView: WebView
    private lateinit var errorTextView: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var card: CardView
    private lateinit var progress: ProgressBar
    private lateinit var currentState: MainFragmentState

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initElements()
        val sharedPref =
            requireActivity().getSharedPreferences(SAVED_SETTINGS, Context.MODE_PRIVATE)
        val sharedUrl = sharedPref.getString(URL, "")
        currentState = MainFragmentState.Loading
        viewModel.getFromLocal(
            pathUrl = sharedUrl ?: "",
            checkedInternetConnection = checkedInternetConnection()
        )
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            when (currentState) {
                is MainFragmentState.SuccessConnect -> {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
                else -> {

                }
            }

        }
        viewModel.showData.observe(viewLifecycleOwner) { state ->
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

    private fun setRecycledView(items: List<ItemWorldCup>) {
        recycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager
            .VERTICAL, false)
        val stateClickListener: CupAdapter.OnStateClickListener =
            object : CupAdapter.OnStateClickListener {
                override fun onStateClick(item: ItemWorldCup, position: Int) {
                    val bundle = bundleOf("year" to item.year)
                    view?.findNavController()?.navigate(R.id.action_mainFragment_to_detailFragment, bundle)
                }
            }
        recycler.adapter = CupAdapter(items, stateClickListener)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(savedInstanceState: Bundle?, url:String) {
        webView = requireActivity().findViewById(R.id.webView)
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
        webView.setOnKeyListener { view, code, keyEvent ->
            code == KeyEvent.KEYCODE_BACK
        }
    }

    private fun initElements() {
        errorTextView = requireActivity().findViewById(R.id.errorTv)
        webView = requireActivity().findViewById(R.id.webView)
        recycler = requireActivity().findViewById(R.id.historyRv)
        card = requireActivity().findViewById(R.id.offline)
        progress = requireActivity().findViewById(R.id.progress)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }
}
