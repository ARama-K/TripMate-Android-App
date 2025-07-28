package com.example.travelapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.R
import com.example.travelapp.databinding.HomeCardLayoutBinding
import com.example.travelapp.entity.DiaryEntry
import com.example.travelapp.viewmodel.DiaryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class HomeRecyclerViewAdapter(
    private val diaryViewModel: DiaryViewModel,
    private val diaryListId: List<Int?>?
) : RecyclerView.Adapter<HomeRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HomeCardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val idDiary = diaryListId?.get(position)
        if (idDiary != null) {
            val diary = diaryViewModel.findDiarybyId(idDiary)
            if (diary != null) {
                // Set up slideshow with Unsplash images
                val context = holder.itemView.context
                val searchTerm = diary.title ?: "travel"

                // Generate random Unsplash URLs
                val imageUrls = List(5) {
                    "https://source.unsplash.com/800x600/?$searchTerm&sig=${diary.id}${it}"
                }

                // Set up ViewPager2 slideshow
                val slideshowAdapter = SlideshowAdapter(imageUrls)
                holder.binding.viewPager.adapter = slideshowAdapter

                holder.binding.logixTiltle.text = diary.title
                holder.binding.logixIntroduce.text = diary.description

                holder.binding.cardView.setOnClickListener { v ->
                    val arg = Bundle().apply {
                        putString("content", diary.id.toString())
                    }
                    replaceFragment(arg, v)
                }

                // Auto-scroll ViewPager2 every 3 seconds
                (context as? FragmentActivity)?.lifecycleScope?.launch {
                    while (true) {
                        val viewPager = holder.binding.viewPager
                        val nextItem = (viewPager.currentItem + 1) % imageUrls.size
                        withContext(Dispatchers.Main) {
                            viewPager.setCurrentItem(nextItem, true)
                        }
                        kotlinx.coroutines.delay(3000)
                    }
                }
            } else {
                holder.binding.logixTiltle.text = "Diary not found"
                holder.binding.logixIntroduce.text = ""
            }
        } else {
            holder.binding.logixTiltle.text = "ID not available"
            holder.binding.logixIntroduce.text = ""
        }
    }

    private fun replaceFragment(arg: Bundle, view: View) {
        val fragmentActivity = view.context as FragmentActivity
        val navHostFragment =
            fragmentActivity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.logixContentFragment, arg)
    }

    override fun getItemCount(): Int = diaryListId?.size ?: 0

    class ViewHolder(val binding: HomeCardLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
