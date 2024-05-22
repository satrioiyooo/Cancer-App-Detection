package com.dicoding.asclepius.news

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.dicoding.asclepius.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NewsActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val newsWebView: WebView = findViewById(R.id.newsWeb)
        val loading : ProgressBar = findViewById(R.id.newsLoad)

        newsWebView.settings.javaScriptEnabled = true

        newsWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loading.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                loading.visibility = View.INVISIBLE
            }
        }
        val urlWeb = intent.getStringExtra("url")
        newsWebView.loadUrl(urlWeb.toString())



        val backBtn : FloatingActionButton = findViewById(R.id.back_btn)
        backBtn.setOnClickListener {
            finish()
        }
    }
}