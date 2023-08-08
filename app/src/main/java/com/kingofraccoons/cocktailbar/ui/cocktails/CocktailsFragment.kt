package com.kingofraccoons.cocktailbar.ui.cocktails

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomappbar.BottomAppBarTopEdgeTreatment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.kingofraccoons.cocktailbar.R
import com.kingofraccoons.cocktailbar.databinding.FragmentCocktailsBinding
import com.kingofraccoons.cocktailbar.repository.viewmodel.CocktailViewModel
import org.koin.android.ext.android.inject


class CocktailsFragment : Fragment() {
    private val viewModel: CocktailViewModel by inject()

    private var _binding: FragmentCocktailsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val adapter = CocktailsAdapter {
        findNavController().navigate(
            CocktailsFragmentDirections.actionCocktailsFragmentToDetailCocktailFragment(it)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCocktailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerCocktails.adapter = adapter
        val spacing = (8 * requireContext().resources.displayMetrics.density).toInt()
        binding.recyclerCocktails.addItemDecoration(
            GridSpacingItemDecoration(2, spacing, false)
        )
        binding.buttonAddCocktail.setOnClickListener {
            findNavController().navigate(
                CocktailsFragmentDirections.actionCocktailsFragmentToAddCocktailFragment(
                    null
                )
            )
        }

        binding.fabShare.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "text/plain"
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Смотри какие коктейли я создал в приложении Cocktail Bar!\n" + viewModel.cocktailsLiveData.value.orEmpty()
                    .reversed().drop(4)
                    .joinToString { it.title } + "и другие.\nХочешь попробовать?")
            startActivity(Intent.createChooser(intent, "Share text"))
        }

        viewModel.cocktailsLiveData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                adapter.submitList(it)
                binding.recyclerCocktails.visibility = View.VISIBLE
                binding.imageTitle.visibility = View.GONE
                binding.textCreateCocktail.visibility = View.GONE
                binding.fabShare.visibility = View.VISIBLE
                binding.buttonAddCocktail.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    this.topToBottom = View.NO_ID
                    this.bottomToBottom = R.id.container
                }
            } else {
                binding.recyclerCocktails.visibility = View.GONE
                binding.imageTitle.visibility = View.VISIBLE
                binding.textCreateCocktail.visibility = View.VISIBLE
                binding.fabShare.visibility = View.GONE
                binding.buttonAddCocktail.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    this.topToBottom = R.id.text_create_cocktail
                    this.bottomToBottom = View.NO_ID
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}