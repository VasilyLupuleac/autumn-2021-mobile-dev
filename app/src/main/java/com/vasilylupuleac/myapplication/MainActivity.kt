package com.vasilylupuleac.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.squareup.moshi.Moshi
import com.vasilylupuleac.myapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import by.kirich1409.viewbindingdelegate.viewBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    val viewModel: MainViewModel by viewModels()
    private val viewBinding by viewBinding(ActivityMainBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = setupRecyclerView()

        //viewBinding.userRecyclerView().isVisible = false
        //findViewById<View>(R.id.progressBar).isVisible = true

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewState.collect { viewState ->
                    renderViewState(viewState)
                }
            }
        }

    }

    private fun renderViewState(viewState: MainViewModel.ViewState) {
        when (viewState) {
            is MainViewModel.ViewState.Loading -> {
                viewBinding.usersRecyclerView.isVisible = false
                viewBinding.progressBar.isVisible = true
            }
            is MainViewModel.ViewState.Data -> {
                viewBinding.usersRecyclerView.isVisible =true
                (viewBinding.usersRecyclerView.adapter as UserAdapter).apply {
                    this.userList = viewState.userList
                    this.notifyDataSetChanged()
                }
                viewBinding.progressBar.isVisible = false
            }
        }
    }
    private fun setupRecyclerView() : UserAdapter {
        val recyclerView = findViewById<RecyclerView>(R.id.usersRecyclerView)
        val adapter = UserAdapter()
        recyclerView.adapter = adapter

        return adapter
    }

}