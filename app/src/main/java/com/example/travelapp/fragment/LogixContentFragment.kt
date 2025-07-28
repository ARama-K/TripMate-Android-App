package com.example.travelapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.travelapp.api.ApiClient
import com.example.travelapp.api.UnsplashResponse
import com.example.travelapp.databinding.LogixContentFragmentBinding
import com.example.travelapp.viewmodel.DiaryViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Handler
import android.os.Looper
import androidx.viewpager2.widget.ViewPager2


class LogixContentFragment : Fragment() {
    private var imageUrls: List<String> = emptyList()
    private var currentSlide = 0
    private val handler = Handler(Looper.getMainLooper())
    private val slideRunnable = object : Runnable {
        override fun run() {
            if (imageUrls.isNotEmpty()) {
                currentSlide = (currentSlide + 1) % imageUrls.size
                binding?.imageSlider?.setCurrentItem(currentSlide, true)
                handler.postDelayed(this, 3000) // change slide every 3 seconds
            }
        }
    }
    private var binding: LogixContentFragmentBinding? = null
    private lateinit var diaryViewModel: DiaryViewModel
    private var diaryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            diaryId = it.getString("content")?.toIntOrNull() ?: -1
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LogixContentFragmentBinding.inflate(inflater, container, false)
        val view = binding!!.root

        diaryViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[DiaryViewModel::class.java]

        val diary = diaryViewModel.findDiarybyId(diaryId)

        diary?.let {
            binding?.apply {
                title.text = it.title
                date.text = it.date
                fee.text = it.fee.toString()
                score.text = it.rating.toString()
                content.text = it.description
            }

            val query = it.location ?: "travel"

            ApiClient.unsplashApi.searchPhotos(query, perPage = 5)
                .enqueue(object : Callback<UnsplashResponse> {
                    override fun onResponse(
                        call: Call<UnsplashResponse>,
                        response: Response<UnsplashResponse>
                    ) {
                        if (response.isSuccessful) {
                            val urls =
                                response.body()?.results?.map { result -> result.urls.regular }
                                    ?: emptyList()
                            imageUrls = urls
                            binding?.imageSlider?.adapter = SlideshowAdapter(imageUrls)
                            handler.postDelayed(slideRunnable, 3000)
                        }
                    }

                    override fun onFailure(call: Call<UnsplashResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "Failed to load images", Toast.LENGTH_SHORT).show()
                    }
                })

        } ?: run {
            Toast.makeText(requireContext(), "Diary entry not found", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(slideRunnable)
        binding = null
    }

}
