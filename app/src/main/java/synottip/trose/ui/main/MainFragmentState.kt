package synottip.trose.ui.main

import synottip.trose.data.ItemWorldCup
import synottip.trose.data.RemoteData
import synottip.trose.utils.NO_INTERNET

sealed class MainFragmentState {
    class SuccessConnect(
        val remoteData: RemoteData
    ) : MainFragmentState()
    class Offline(val data: List<ItemWorldCup>) : MainFragmentState()
    class Error(val message: String) : MainFragmentState()
    class NoInternet(val message: String = NO_INTERNET) : MainFragmentState()
    object Loading : MainFragmentState()
}
