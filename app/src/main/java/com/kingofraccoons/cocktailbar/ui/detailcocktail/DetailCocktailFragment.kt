package com.kingofraccoons.cocktailbar.ui.detailcocktail

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kingofraccoons.cocktailbar.R
import com.kingofraccoons.cocktailbar.databinding.FragmentDetailCocktailBinding
import com.kingofraccoons.cocktailbar.databinding.LayoutAskDeleteBinding
import com.kingofraccoons.cocktailbar.repository.viewmodel.CocktailViewModel
import org.bson.types.ObjectId
import org.koin.android.ext.android.inject

class DetailCocktailFragment : Fragment() {
    private val viewModel: CocktailViewModel by inject()

    private var _binding: FragmentDetailCocktailBinding? = null
    private val args: DetailCocktailFragmentArgs by navArgs()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailCocktailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ResourcesCompat.getDrawable(resources, R.drawable.ic_line, requireContext().theme)?.let {
            binding.recyclerIngredients.addItemDecoration(LineSpacingItemDecoration(it))
        }

        binding.buttonDeleteCocktail.setOnClickListener {
            createAskDeleteAlertDialog(args.idCocktail)
        }

        binding.buttonEdit.setOnClickListener {
            findNavController().navigate(DetailCocktailFragmentDirections.actionDetailCocktailFragmentToAddCocktailFragment(args.idCocktail))
        }

        viewModel.cocktailsLiveData.observe(viewLifecycleOwner) {
            it.find { it.id == args.idCocktail }?.let {
                if (it.image.isNotEmpty())
                    binding.imageCocktail.setImageURI(Uri.parse(it.image))

                binding.textTitleCocktail.text = it.title
                if (it.desc.isNotEmpty())
                    binding.textDescCocktail.text = it.desc
                else
                    binding.textDescCocktail.visibility = View.GONE
                binding.recyclerIngredients.adapter = IngredientsAdapter(it.ingredients)
                if (it.recipe.isNotEmpty())
                    binding.textRecipeCocktail.text = getRecipe(it.recipe)
                else
                    binding.textRecipeCocktail.visibility = View.GONE
            }
        }
    }

    private fun getRecipe(recipe: String) = "Recipe:\n$recipe"

    private fun getTitleAskDelete() =
        "Вы уверены, что хотите удалить коктейль" + viewModel.cocktailsLiveData.value.orEmpty()
            .find { it.id == args.idCocktail }?.title + "?"

    private fun createAskDeleteAlertDialog(cocktailId: ObjectId) {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        alertDialog.setView(
            LayoutAskDeleteBinding.bind(
                layoutInflater.inflate(
                    R.layout.layout_ask_delete,
                    null
                )
            ).let { binding ->
                binding.textTitleRemove.text = getTitleAskDelete()
                binding.buttonRemove.setOnClickListener {
                    viewModel.deleteCocktail(cocktailId)
                    alertDialog.dismiss()
                    findNavController().popBackStack()
                }
                binding.buttonCancel.setOnClickListener {
                    alertDialog.dismiss()
                }
                binding.root
            })
        alertDialog.let {
            it.window?.setBackgroundDrawable(ColorDrawable(0))
            it.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}