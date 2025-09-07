package com.example.tramxeuth.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tramxeuth.Data.BaoRungRepository
import com.example.tramxeuth.Model.BaoRung
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BaoRungViewModel(
    private val repository: BaoRungRepository = BaoRungRepository()
): ViewModel() {
    private val _canhBaoRung = MutableStateFlow(BaoRung())
    val canhBaoRung: StateFlow<BaoRung> = _canhBaoRung
    init {
        repository.listenCanhBaoRung { baoRung ->
            _canhBaoRung.value = baoRung
        }
    }
    //hàm cập nhật giá trị cảnh báo
    fun updateCanhBaoRung(value: Boolean) {
        repository.setCanhBaoRung(value)
    }
}