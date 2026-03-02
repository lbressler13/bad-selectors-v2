package xyz.lbres.badselectorsv2.phone.shufflecircle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import xyz.lbres.badselectorsv2.databinding.FragmentPhoneShuffleCircleBinding

class ShuffleCircleFragment : Fragment() {
    private lateinit var binding: FragmentPhoneShuffleCircleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPhoneShuffleCircleBinding.inflate(layoutInflater)
        return binding.root
    }
}
