package com.cupcake.chickenmasala.ui.fragment.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.cupcake.chickenmasala.R
import com.cupcake.chickenmasala.data.RepositoryImpl
import com.cupcake.chickenmasala.data.model.Recipe
import com.cupcake.chickenmasala.databinding.FragmentHomeBinding
import com.cupcake.chickenmasala.ui.base.BaseFragment
import com.cupcake.chickenmasala.ui.base.OnItemClickListener
import com.cupcake.chickenmasala.ui.fragment.details.DetailsFragment
import com.cupcake.chickenmasala.usecase.Repository
import com.cupcake.chickenmasala.utill.DataSourceProvider
import java.lang.Math.abs

class HomeFragment : BaseFragment<FragmentHomeBinding>(), OnItemClickListener<Recipe> {
    override val LOG_TAG: String = this::class.java.name

    override val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> FragmentHomeBinding
        get() = FragmentHomeBinding::inflate

    private lateinit var horizontalRecipeRecyclerAdapter: HorizontalRecipeRecyclerAdapter
    private lateinit var healthyViewPager: ViewPager2
    private lateinit var handler: Handler
    private lateinit var imageList: ArrayList<Int>
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var verticalRecipeRecyclerAdapter: VerticalRecipeRecyclerAdapter

    private val repository: Repository by lazy {
        RepositoryImpl(DataSourceProvider.getDataSource(requireActivity().application))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        horizontalRecipeRecycler()
        setupVerticalRecipeRecyclerView()
        setUpTransformer()
        addCallbacks()
    }

    private fun horizontalRecipeRecycler() {
        horizontalRecipeRecyclerAdapter = HorizontalRecipeRecyclerAdapter(this)
        horizontalRecipeRecyclerAdapter.submitList(repository.getRecipes())
        binding.horizontalRecyclerView.adapter = horizontalRecipeRecyclerAdapter
    }


    private fun setupVerticalRecipeRecyclerView() {
        verticalRecipeRecyclerAdapter = VerticalRecipeRecyclerAdapter(this)
        verticalRecipeRecyclerAdapter.submitList(repository.getRecipes())
        binding.verticalRecycler.adapter = verticalRecipeRecyclerAdapter
    }

    private fun addCallbacks() {
        healthyViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 4000)
            }
        })

        binding.viewAllIcon.setOnClickListener {
            // DishesFragment not implemented yet
            //  navigateToFragment(DishesFragment)
        }
    }



    private fun setupViewPager() {
        healthyViewPager = binding.healthyViewPager
        handler = Handler(Looper.myLooper()!!)
        imageList = ArrayList()
        imageList.add(R.drawable.view_pager_image_1)
        imageList.add(R.drawable.view_pager_image_2)
        imageList.add(R.drawable.view_pager_image_3)

        imageAdapter = ImageAdapter(imageList, healthyViewPager)
        healthyViewPager.adapter = imageAdapter

        healthyViewPager.offscreenPageLimit = 3
        healthyViewPager.clipToPadding = false
        healthyViewPager.clipChildren = false
        healthyViewPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
    }

    private fun setUpTransformer() {
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.14f
        }
        healthyViewPager.setPageTransformer(transformer)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 4000)
    }

    private val runnable = Runnable {
        healthyViewPager.currentItem = healthyViewPager.currentItem + 1
    }

    override fun onItemClicked(item: Recipe) {
        val destinationFragment = DetailsFragment.newInstance(item.id)
        navigateToFragment(destinationFragment)
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transition = requireActivity().supportFragmentManager.beginTransaction()
        transition.add(R.id.fragmentContainer, fragment)
        transition.commit()
    }

    companion object {
        const val RECENT_FOOD_LIMIT = 10
    }

}


