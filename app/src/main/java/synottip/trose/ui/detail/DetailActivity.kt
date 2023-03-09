package synottip.trose.ui.detail

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import synottip.trose.R
import synottip.trose.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailFragmentViewModel>()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        viewModel.data.observe(this) {item->
            binding.fifa.text = "FIFA World Cup ${item.year}"
            binding.hostCountryName.text = item.country
            binding.beginDate.text = item.dateStart
            binding.endDate.text = item.dateEnd
            binding.winnerName.text = item.winnerCountry
            binding.allCountriesCount.text = item.participatingCountries.toString()
            binding.finalStageCount.text = item.finalStageCountry.toString()
            binding.description.text = item.description
            binding.mascot.setImageResource(item.mascot)
            binding.flag.setImageResource(item.flagWinner)
        }
    }
}