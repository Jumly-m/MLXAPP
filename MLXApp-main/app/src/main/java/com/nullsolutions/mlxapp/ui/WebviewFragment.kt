package com.nullsolutions.mlxapp.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.navArgs
import com.nullsolutions.mlxapp.R
import com.nullsolutions.mlxapp.databinding.FragmentWebviewBinding


class WebviewFragment : Fragment(R.layout.fragment_webview) {
    private var _binding: FragmentWebviewBinding? = null
    private val binding: FragmentWebviewBinding get() = _binding!!
    private val args: WebviewFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWebviewBinding.bind(view)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onResume() {
        super.onResume()
        openWebsite(args.websiteUrl)

        requireActivity().onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()
                    } else {
                        if (isEnabled) {
                            isEnabled = false
                            requireActivity().onBackPressed()
                        }
                    }
                }
            })
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun openWebsite(url: String) {
        try {
            val mWebSettings: WebSettings = binding.webView.settings
            binding.webView.webViewClient = WebViewClient()
            mWebSettings.javaScriptEnabled = true
            mWebSettings.setSupportZoom(false)
            mWebSettings.domStorageEnabled = true
            mWebSettings.allowFileAccess = true
            mWebSettings.allowFileAccess = true
            mWebSettings.allowContentAccess = true
            binding.webView.loadUrl(url)
            binding.swipeRefresh.setOnRefreshListener { binding.webView.loadUrl(binding.webView.url.toString()) }
            binding.webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, progress: Int) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.progressBar.progress = progress * 100
                    if (progress == 100) {
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
        } catch (ex: Exception) {
            Toast.makeText(context, "Unknown Error", Toast.LENGTH_LONG).show()
        }
    }

}