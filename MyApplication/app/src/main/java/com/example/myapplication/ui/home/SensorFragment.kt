package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentSensorBinding

class SensorFragment : Fragment() {
    private var _binding: FragmentSensorBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        _binding = FragmentSensorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 观察传感器信息
        homeViewModel.sensorInfo.observe(viewLifecycleOwner) { sensorInfo ->
            binding.textSensor.text = sensorInfo
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}