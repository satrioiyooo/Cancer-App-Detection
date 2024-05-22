package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.news.Adapter
import com.dicoding.asclepius.news.NewsService
import com.dicoding.asclepius.news.news
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.tensorflow.lite.task.vision.classifier.Classifications
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var allNewsRV : RecyclerView
    private lateinit var Adapter: Adapter
    private lateinit var allNewsLayout: LinearLayout
    val allNewsLayoutManager = LinearLayoutManager(this)
    var pageNum = 1
    var totalAllNews = -1



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri.let {
            binding.resultImage.setImageURI(it)

        }
        val imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    runOnUiThread {
                        Toast.makeText(this@ResultActivity, error, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        results?.let { it ->
                            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                println(it)
                                val sortedCategories =
                                    it[0].categories.sortedByDescending { it?.score }
                                val displayResult =
                                    sortedCategories.joinToString("\n") {
                                        "${it.label} " + NumberFormat.getPercentInstance()
                                            .format(it.score).trim()
                                    }
                                binding.resultText.text = displayResult
                            } else {
                                binding.resultText.text = ""
                            }
                        }
                    }
                }
            }
        )
        imageClassifierHelper.classifyStaticImage(imageUri)

        allNewsRV = findViewById(R.id.semuaBeritarv)
        allNewsLayout = findViewById(R.id.semuaBeritaLayout)

        hideAll()
        getAllNews()
        showAll()


        allNewsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(totalAllNews > allNewsLayoutManager.itemCount && allNewsLayoutManager.findFirstVisibleItemPosition() >= allNewsLayoutManager.itemCount -1) {
                    pageNum++
                    getAllNews()
                }
            }
        })
        val backBtn : FloatingActionButton = findViewById(R.id.back_btn)
        backBtn.setOnClickListener {
            finish()
        }
    }

    private fun showAll() {
        showLoading(true)
        allNewsLayout.visibility = View.VISIBLE
    }

    private fun getAllNews() {
        val news = NewsService.newsInstance.geteverything("health",pageNum)
        news.enqueue(object : Callback<news> {
            override fun onResponse(call: Call<news>, response: Response<news>) {
                val allNews = response.body()
                if (allNews != null) {
                    totalAllNews = allNews.totalResults
                    Adapter = Adapter(this@ResultActivity)
                    Adapter.clear()
                    Adapter.addAll(allNews.articles)
                    allNewsRV.adapter = Adapter
                    allNewsRV.layoutManager = allNewsLayoutManager
                    showLoading(false)
                }
            }

            override fun onFailure(call: Call<news>, t: Throwable) {
                Log.d(TAG, "Failed Fetching News", t)
            }
        })
    }

    private fun hideAll() {
        allNewsLayout.visibility = View.INVISIBLE
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_img_uri"
        const val TAG = "client"

    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarActivityMain.visibility = View.VISIBLE
        } else {
            binding.progressBarActivityMain.visibility = View.GONE
        }
    }


}