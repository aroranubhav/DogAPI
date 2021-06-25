package com.maxi.dogapi

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.load
import coil.request.Disposable
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.maxi.dogapi.databinding.ActivityMainBinding
import com.maxi.dogapi.utils.NetworkResult
import com.maxi.dogapi.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var _binding: ActivityMainBinding
    private lateinit var disposable: Disposable
    private var imageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        fetchData()
        _binding.imgRefresh.setOnClickListener {
            fetchResponse()
        }
        _binding.imgDownload.setOnClickListener {
            downloadImage(imageUrl)
        }
        observeDownloadResponse()
    }

    private fun fetchResponse() {
        mainViewModel.fetchDogResponse()
        _binding.pbDog.visibility = View.VISIBLE
    }


    private fun fetchData() {
        fetchResponse()
        mainViewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        imageUrl = response.data.message
                        _binding.imgDog.load(
                            response.data.message
                        ) {
                            transformations(RoundedCornersTransformation(16f))
                        }
                    }
                    _binding.pbDog.visibility = View.GONE
                }

                is NetworkResult.Error -> {
                    _binding.pbDog.visibility = View.GONE
                    Toast.makeText(
                        this,
                        response.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is NetworkResult.Loading -> {
                    _binding.pbDog.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun downloadImage(url: String?) {
        url?.let {
            /*val di = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + "/" +
                    resources.getString(R.string.dogs) + "/"*/

           /* val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)*/
            val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/" +
                    resources.getString(R.string.app_name) + "/"

            val dir = File(dirPath)

            val fileName: String = url.substring(url.lastIndexOf('/') + 1)

            val imageLoader = ImageLoader(this)
            val request = ImageRequest.Builder(this)
                .data(url)
                .target { drawable ->
                    mainViewModel.downloadImage(drawable.toBitmap(), dir, fileName)
                }
                .build()
            disposable = imageLoader.enqueue(request)
        }
    }

    private fun observeDownloadResponse() {
        mainViewModel.downloadResponse.observe(this) { response ->
            if (response) {
                Toast.makeText(this, "Saved !!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Unable to save image !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
