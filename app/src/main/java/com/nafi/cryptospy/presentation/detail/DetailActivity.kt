package com.nafi.cryptospy.presentation.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import coil.load
import com.nafi.cryptospy.R
import com.nafi.cryptospy.data.model.Detail
import com.nafi.cryptospy.databinding.ActivityDetailBinding
import com.nafi.cryptospy.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailActivity : AppCompatActivity() {
    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }
    private val viewModel: DetailViewModel by viewModel {
        parametersOf(intent.extras)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setClickAction()
        viewModel.idExtras?.let { getDetailData(it) }
    }

    companion object {
        const val EXTRAS_ITEM = "EXTRAS_ITEM"

        fun startActivity(
            context: Context,
            id: String,
        ) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRAS_ITEM, id)
            context.startActivity(intent)
        }
    }

    private fun getDetailData(id: String) {
        viewModel.getDetailData(id).observe(this) {
            it.proceedWhen(
                doOnLoading = {
                    binding.layoutState.root.isVisible = true
                    binding.layoutState.pbLoading.isVisible = true
                    binding.layoutState.tvError.isVisible = false
                    binding.layoutDetailHeader.root.isVisible = false
                    binding.layoutDetailBottom.btnGoToWeb.isEnabled = false
                },
                doOnSuccess = {
                    binding.layoutState.root.isVisible = false
                    binding.layoutState.pbLoading.isVisible = false
                    binding.layoutState.tvError.isVisible = false
                    binding.layoutDetailHeader.root.isVisible = true
                    binding.layoutDetailBottom.btnGoToWeb.isEnabled = true
                    it.payload?.let { data ->
                        bindView(data)
                        setBtnWebClickAction(data.webSlug)
                        checkCoinIsFavorite(data)
                    }
                },
                doOnError = {
                    binding.layoutState.root.isVisible = true
                    binding.layoutState.pbLoading.isVisible = false
                    binding.layoutState.tvError.isVisible = true
                    binding.layoutState.tvError.text = it.exception?.message.orEmpty()
                    binding.layoutDetailHeader.root.isVisible = false
                    binding.layoutDetailBottom.btnGoToWeb.isEnabled = false
                },
                doOnEmpty = {
                    binding.layoutState.root.isVisible = true
                    binding.layoutState.pbLoading.isVisible = false
                    binding.layoutState.tvError.isVisible = true
                    binding.layoutState.tvError.text = getString(R.string.text_data_empty_or_not_available)
                    binding.layoutDetailHeader.root.isVisible = false
                    binding.layoutDetailBottom.btnGoToWeb.isEnabled = false
                },
            )
        }
    }

    private fun setClickAction() {
        binding.cardBackArrow.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setClickAddFavorite(detail: Detail) {
        binding.layoutDetailHeader.ivFavourite.setOnClickListener {
            addToFavorite(detail)
        }
    }

    private fun setClickRemoveFavorite(coinId: String) {
        binding.layoutDetailHeader.ivFavourite.setOnClickListener {
            removeFromFavorite(coinId)
        }
    }

    private fun checkCoinIsFavorite(data: Detail) {
        viewModel.checkCoinFavorite(data.id).observe(
            this,
        ) { isFavorite ->
            if (isFavorite.isEmpty()) {
                binding.layoutDetailHeader.ivFavourite.setImageResource(R.drawable.ic_favourite_off)
                setClickAddFavorite(data)
                checkCoinIsFavorite(data)
            } else {
                binding.layoutDetailHeader.ivFavourite.setImageResource(R.drawable.ic_favourite_on)
                setClickRemoveFavorite(data.id)
                checkCoinIsFavorite(data)
            }
        }
    }

    private fun removeFromFavorite(coinId: String) {
        viewModel.removeFromFavorite(coinId).observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    Toast.makeText(
                        this,
                        "Berhasil menghapus ke favorite",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
                doOnError = {
                    Toast.makeText(
                        this,
                        "Gagal menghapus ke favorite",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )
        }
    }

    private fun addToFavorite(detail: Detail) {
        viewModel.addToFavorite(detail).observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    Toast.makeText(
                        this,
                        "Berhasil menambakan ke favorite",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
                doOnError = {
                    Toast.makeText(
                        this,
                        "Gagal menambakan ke favorite",
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )
        }
    }

    private fun setBtnWebClickAction(webSlug: String) {
        binding.layoutDetailBottom.btnGoToWeb.setOnClickListener {
            goToWeb(webSlug)
        }
    }

    private fun bindView(detail: Detail) {
        detail.let {
            binding.layoutDetailHeader.ivIcon.load(it.image) {
                crossfade(true)
            }
            binding.layoutDetailHeader.tvName.text = it.name
            binding.layoutDetailHeader.tvPrice.text = it.price.toString()
            binding.tvDescription.text = it.description
        }
    }

    private fun goToWeb(webSlug: String) {
        viewModel.goToWeb(this, webSlug)
    }
}
