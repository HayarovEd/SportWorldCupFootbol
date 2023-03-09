package synottip.trose.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import synottip.trose.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DetailFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.data.observe(viewLifecycleOwner) {item->
            binding.fifa.text = "FIFA World Cup ${item.year}"
            binding.hostCountryName.text = item.country
            binding.beginDate.text = item.dateStart
            binding.endDate.text = item.dateEnd
            binding.winnerName.text = item.winnerCountry
            binding.allCountriesCount.text = item.participatingCountries.toString()
            binding.finalStageCount.text = item.finalStageCountry.toString()
            binding.mascot.setImageResource(item.mascot)
            binding.flag.setImageResource(item.flagWinner)
        }
    }

}