package com.example.tramxeuth.ViewModel

import androidx.lifecycle.ViewModel
import com.example.tramxeuth.Model.*
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class BienSoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BienSoUiState())
    val uiState: StateFlow<BienSoUiState> = _uiState

    private val PAGE_SIZE = 20
    private val dbRef = FirebaseDatabase.getInstance()
        .getReference("biensotrongbai")

    // Lưu toàn bộ items đã load (trước khi filter)
    private val allItems = mutableListOf<BienSoItem>()

    // ==================== LOAD LẦN ĐẦU ====================

    fun loadFirst() {
        _uiState.update { it.copy(
            pagingState = it.pagingState.copy(isLoading = true, error = null)
        )}

        dbRef.orderByKey()
            .limitToFirst(PAGE_SIZE)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = snapshot.children.mapNotNull { child ->
                        val data = child.getValue(BienSoTrongBai::class.java) ?: return@mapNotNull null
                        // Key của Firebase chính là biển số xe
                        BienSoItem(firebaseKey = child.key ?: "", data = data.copy(biensoxe = child.key))
                    }

                    allItems.clear()
                    allItems.addAll(items)

                    val lastKey = items.lastOrNull()?.firebaseKey
                    val hasMore = items.size >= PAGE_SIZE.toInt()

                    _uiState.update { state ->
                        val filtered = applyFilter(allItems, state.pagingState.filter)
                        state.copy(
                            pagingState = state.pagingState.copy(
                                items = allItems,
                                isLoading = false,
                                lastKey = lastKey,
                                hasMore = hasMore,
                                totalCount = allItems.size
                            ),
                            filteredItems = filtered
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _uiState.update { it.copy(
                        pagingState = it.pagingState.copy(
                            isLoading = false,
                            error = error.message
                        )
                    )}
                }
            })
    }

    // ==================== LOAD THÊM (LAZY LOAD) ====================

    fun loadMore() {
        val state = _uiState.value.pagingState
        if (!state.hasMore || state.isLoadingMore || state.lastKey == null) return

        _uiState.update { it.copy(
            pagingState = it.pagingState.copy(isLoadingMore = true)
        )}

        dbRef.orderByKey()
            .startAfter(state.lastKey)
            .limitToFirst(PAGE_SIZE)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newItems = snapshot.children.mapNotNull { child ->
                        val data = child.getValue(BienSoTrongBai::class.java) ?: return@mapNotNull null
                        BienSoItem(firebaseKey = child.key ?: "", data = data.copy(biensoxe = child.key))
                    }

                    allItems.addAll(newItems)

                    val newLastKey = newItems.lastOrNull()?.firebaseKey
                    val hasMore = newItems.size >= PAGE_SIZE.toInt()

                    _uiState.update { state ->
                        val filtered = applyFilter(allItems, state.pagingState.filter)
                        state.copy(
                            pagingState = state.pagingState.copy(
                                items = allItems.toList(),
                                isLoadingMore = false,
                                lastKey = newLastKey ?: state.pagingState.lastKey,
                                hasMore = hasMore,
                                totalCount = allItems.size
                            ),
                            filteredItems = filtered
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _uiState.update { it.copy(
                        pagingState = it.pagingState.copy(
                            isLoadingMore = false,
                            error = error.message
                        )
                    )}
                }
            })
    }

    // ==================== REALTIME LISTENER (tùy chọn) ====================
    // Nếu muốn cập nhật live khi DB thay đổi, dùng hàm này thay loadFirst()

    private var realtimeListener: ValueEventListener? = null

    fun startRealtimeListen() {
        realtimeListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { child ->
                    val data = child.getValue(BienSoTrongBai::class.java) ?: return@mapNotNull null
                    BienSoItem(firebaseKey = child.key ?: "", data = data.copy(biensoxe = child.key))
                }
                allItems.clear()
                allItems.addAll(items)

                _uiState.update { state ->
                    val filtered = applyFilter(allItems, state.pagingState.filter)
                    state.copy(
                        pagingState = state.pagingState.copy(
                            items = allItems.toList(),
                            isLoading = false,
                            totalCount = allItems.size,
                            hasMore = false // Realtime load hết
                        ),
                        filteredItems = filtered
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _uiState.update { it.copy(
                    pagingState = it.pagingState.copy(error = error.message)
                )}
            }
        }
        dbRef.addValueEventListener(realtimeListener!!)
    }

    fun stopRealtimeListen() {
        realtimeListener?.let { dbRef.removeEventListener(it) }
    }

    // ==================== FILTER ====================

    fun updateFilter(newFilter: BienSoFilter) {
        _uiState.update { state ->
            state.copy(
                pagingState = state.pagingState.copy(filter = newFilter),
                filteredItems = applyFilter(allItems, newFilter)
            )
        }
    }

    fun updateSearch(query: String) {
        updateFilter(_uiState.value.pagingState.filter.copy(searchQuery = query))
    }

    private fun applyFilter(items: List<BienSoItem>, filter: BienSoFilter): List<BienSoItem> {
        return items.filter { it.matchFilter(filter) }
    }

    override fun onCleared() {
        super.onCleared()
        stopRealtimeListen()
    }
}