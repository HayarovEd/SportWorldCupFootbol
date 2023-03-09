package synottip.trose.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import synottip.trose.data.DetailsWorldCup
import synottip.trose.data.details
import kotlinx.coroutines.launch

class DetailFragmentViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _data = MutableLiveData<DetailsWorldCup>()
    val data = _data

    init {
        getDetails ()
    }

    private fun getDetails () {
        viewModelScope.launch {
            val currentYear = savedStateHandle.get<Int>("year") ?: return@launch
            _data.value = details.first { it.year == currentYear }
        }
    }
}