package com.nafi.cryptospy.presentation.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.nafi.cryptospy.R
import com.nafi.cryptospy.data.model.Favorite
import com.nafi.cryptospy.databinding.FragmentFavoriteBinding
import com.nafi.cryptospy.presentation.common.adapter.FavoriteListAdapter
import com.nafi.cryptospy.presentation.common.adapter.FavoriteListener
import com.nafi.cryptospy.presentation.detail.DetailActivity
import com.nafi.cryptospy.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding

    private val viewModel: FavoriteViewModel by viewModel()

    private val adapter: FavoriteListAdapter by lazy {
        FavoriteListAdapter(
            object : FavoriteListener {
                override fun onDeleteFavoriteClicked(favorite: Favorite) {
                    viewModel.deleteFavorite(favorite)
                }

                override fun onItemClicked(coinId: String?) {
                    if (coinId != null) {
                        navigateToDetail(coinId)
                    }
                }
            },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setList()
    }

    private fun setList() {
        binding.rvFavorite.adapter = this@FavoriteFragment.adapter
    }

    private fun observeData() {
        viewModel.getAllFavorite().observe(viewLifecycleOwner) { result ->
            result.proceedWhen(
                doOnLoading = {
                    binding.layoutStateFavorite.root.isVisible = true
                    binding.layoutStateFavorite.pbLoading.isVisible = true
                    binding.layoutStateFavorite.tvError.isVisible = false
                    binding.rvFavorite.isVisible = false
                },
                doOnSuccess = {
                    binding.layoutStateFavorite.root.isVisible = false
                    binding.layoutStateFavorite.pbLoading.isVisible = false
                    binding.layoutStateFavorite.tvError.isVisible = false
                    binding.rvFavorite.isVisible = true
                    result.payload?.let {
                        adapter.submitData(it)
                    }
                },
                doOnEmpty = {
                    binding.layoutStateFavorite.root.isVisible = true
                    binding.layoutStateFavorite.pbLoading.isVisible = false
                    binding.layoutStateFavorite.tvError.isVisible = true
                    binding.layoutStateFavorite.tvError.text =
                        getString(R.string.text_empty_coin_favorite)
                    binding.rvFavorite.isVisible = false
                    result.payload?.let {
                        adapter.submitData(it)
                    }
                },
                doOnError = {
                    binding.layoutStateFavorite.root.isVisible = true
                    binding.layoutStateFavorite.pbLoading.isVisible = false
                    binding.layoutStateFavorite.tvError.isVisible = true
                    binding.rvFavorite.isVisible = false
                },
            )
        }
    }

    private fun navigateToDetail(coinId: String) {
        DetailActivity.startActivity(requireContext(), coinId)
    }
}
