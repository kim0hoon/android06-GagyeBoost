package com.example.gagyeboost.ui.home.selectPosition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gagyeboost.databinding.DialogBottomAddressResultBinding
import com.example.gagyeboost.model.data.PlaceDetail
import com.example.gagyeboost.ui.home.AddViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddressResultFragment(
    private val list: List<PlaceDetail>,
    private val viewModel: AddViewModel,
    private val moveCameraToPlace: (PlaceDetail) -> Unit
) :
    BottomSheetDialogFragment() {

    private var _binding: DialogBottomAddressResultBinding? = null
    private val binding get() = _binding!!
    private val adapter: AddressAdapter by lazy {
        AddressAdapter(viewModel) {
            dismiss()
            moveCameraToPlace(it)
        }.apply {
            submitList(list)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DialogBottomAddressResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvAddress.adapter = adapter
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}