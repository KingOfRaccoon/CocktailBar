package com.kingofraccoons.cocktailbar.ui.addcocktail

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kingofraccoons.cocktailbar.R
import com.kingofraccoons.cocktailbar.databinding.FragmentAddCocktailBinding
import com.kingofraccoons.cocktailbar.databinding.ItemChipBinding
import com.kingofraccoons.cocktailbar.databinding.LayoutAddIngredientBinding
import com.kingofraccoons.cocktailbar.model.Cocktail
import com.kingofraccoons.cocktailbar.repository.viewmodel.CocktailViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import io.realm.RealmList
import org.bson.types.ObjectId
import org.koin.android.ext.android.inject

class AddCocktailFragment : Fragment() {
    private val viewModel: CocktailViewModel by inject()
    private val args: AddCocktailFragmentArgs by navArgs()

    private var _binding: FragmentAddCocktailBinding? = null

    private val binding get() = _binding!!
    private var ingredientsLiveData = MutableLiveData(listOf<String>())
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            binding.containerEdittextTitle.error = null
        }
    }
    private var imageUri = ""

    private var getImages = registerForActivityResult<String, Uri>(
        ActivityResultContracts.GetContent()
    ) { uri ->
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(requireContext(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCocktailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.chipAddChip.setOnClickListener {
            createAddIngredientAlertDialog()
        }

        ingredientsLiveData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                addChip(it.last())
            }
        }

        binding.imageLoadImage.setOnClickListener {
            getImages.launch("image/*")
        }

        binding.buttonSave.setOnClickListener {
            binding.edittextTitle.addTextChangedListener(textWatcher)

            if (binding.edittextTitle.text.isNullOrEmpty()) {
                binding.containerEdittextTitle.error = "Add title cocktail's"
            } else {
                viewModel.addCocktail(
                    Cocktail(
                        viewModel.cocktailsLiveData.value?.find { it.id == args.cocktailId }?.id ?:ObjectId(),
                        binding.edittextTitle.text.toString().trim(),
                        binding.edittextDesc.text.toString().trim(),
                        RealmList(*ingredientsLiveData.value.orEmpty().toTypedArray()),
                        binding.edittextRecipe.text.toString().trim(),
                        imageUri
                    )
                )
                findNavController().popBackStack()
            }
        }

        binding.buttonCancelSave.setOnClickListener {
            findNavController().popBackStack()
        }

        if (args.cocktailId != null)
            viewModel.cocktailsLiveData.observe(viewLifecycleOwner) {
                it.find { it.id == args.cocktailId }?.let {
                    binding.edittextTitle.text = Editable.Factory.getInstance().newEditable(it.title)
                    binding.edittextDesc.text = Editable.Factory.getInstance().newEditable(it.desc)
                    binding.edittextRecipe.text = Editable.Factory.getInstance().newEditable(it.recipe)
                    it.ingredients.forEachIndexed { index, s ->
                        if (index != it.ingredients.lastIndex)
                            addChip(s)
                    }
                    ingredientsLiveData.value = it.ingredients
                    if (it.image.isNotEmpty()) {
                        imageUri = it.image
                        binding.imageLoadImage.setImageURI(Uri.parse(it.image))
                        binding.imageLoadImage.updatePadding(0, 0, 0, 0)
                    }
                }
            }
    }

    private fun createAddIngredientAlertDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        alertDialog.setView(
            LayoutAddIngredientBinding.bind(
                layoutInflater.inflate(
                    R.layout.layout_add_ingredient,
                    null
                )
            ).let { binding ->
                binding.buttonAddIngredient.setOnClickListener {
                    val textListener = object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            binding.containerEdittextName.error = null
                        }
                    }
                    binding.edittextName.addTextChangedListener(textListener)

                    if (binding.edittextName.text.isNullOrEmpty()) {
                        binding.containerEdittextName.error = "Add name ingredient's"
                    } else {
                        ingredientsLiveData.postValue(ingredientsLiveData.value?.plus(binding.edittextName.text.toString()))
                        binding.edittextName.removeTextChangedListener(textListener)
                        alertDialog.dismiss()
                    }
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

    private fun addChip(nameIngredients: String) {
        val chip = layoutInflater.inflate(R.layout.item_chip, null, false)
        ItemChipBinding.bind(chip).let { bindingChip ->
            bindingChip.root.text = nameIngredients
            bindingChip.root.setOnCloseIconClickListener {
                binding.chipGroupIngredients.removeView(bindingChip.root)
            }
            binding.chipGroupIngredients.addView(
                bindingChip.root,
                binding.chipGroupIngredients.childCount - 1
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.edittextTitle.removeTextChangedListener(textWatcher)
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        println(requestCode)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            println(resultCode)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri.toString()
                binding.imageLoadImage.updatePadding(0, 0, 0, 0)
                binding.imageLoadImage.setImageURI(result.uri)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                println(result.error.message)
                Toast.makeText(requireContext(), "Retry load image", Toast.LENGTH_SHORT).show()
            }
        }
    }
}