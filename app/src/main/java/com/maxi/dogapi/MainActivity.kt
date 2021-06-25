package com.maxi.dogapi

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.maxi.dogapi.databinding.ActivityMainBinding
import com.maxi.dogapi.utils.NetworkResult
import com.maxi.dogapi.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        fetchData()
        _binding.imgRefresh.setOnClickListener {
            fetchResponse()
        }
    }

    private fun fetchData() {
        fetchResponse()
        mainViewModel.response.observe(this) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let {
                        _binding.imgDog.load(
                            response.data.message
                        ) {
                            transformations(RoundedCornersTransformation(16f))
                        }
                        downloadImage(response.data.message)
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

    private fun downloadImage(url: String) {
        mainViewModel.downloadImage(url)
        val imageLoader = ImageLoader(this)
        val request = ImageRequest.Builder(this)
            .data(url)
            .target { drawable ->
                Log.d("TAG", "downloadImage: ")
            }
            .build()
        val disposable = imageLoader.enqueue(request)
    }

    private fun fetchResponse() {
        mainViewModel.fetchDogResponse()
        _binding.pbDog.visibility = View.VISIBLE
    }
}
