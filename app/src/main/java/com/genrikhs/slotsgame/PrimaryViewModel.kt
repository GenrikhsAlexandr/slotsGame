package com.genrikhs.slotsgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.random.Random

class PrimaryViewModel : ViewModel() {

    companion object {
        const val SLOT_SIZE = 8
    }

    val toastMessage: MutableSharedFlow<String> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val checkWin: MutableSharedFlow<Unit> = MutableSharedFlow(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val balance: MutableStateFlow<Long> = MutableStateFlow(400)
    val bet: MutableStateFlow<Long> = MutableStateFlow(50)
    val isAutoSpinEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val isSlot1Spinning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val isSlot2Spinning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val isSlot3Spinning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val isSlot4Spinning: MutableStateFlow<Boolean> = MutableStateFlow(false)

    val isSpinning: StateFlow<Boolean> = combine(
        isSlot1Spinning,
        isSlot2Spinning,
        isSlot3Spinning,
        isSlot4Spinning
    ) { isSlot1Spinning, isSlot2Spinning, isSlot3Spinning, isSlot4Spinning ->
        isSlot1Spinning || isSlot2Spinning || isSlot3Spinning || isSlot4Spinning
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)


    fun spin(): SpinResult? {
        if (isSpinning.value) {
            error("already spinning")
        }

        if (balance.value != 0L && balance.value < bet.value) {
            toastMessage.tryEmit("Not enough balance")
        }

        if (balance.value >= bet.value) {
            isSlot1Spinning.value = true
            isSlot2Spinning.value = true
            isSlot3Spinning.value = true
            isSlot4Spinning.value = true

            val spinResult = SpinResult(
                slot1Position = Random.nextInt(0, SLOT_SIZE - 1),
                slot2Position = Random.nextInt(0, SLOT_SIZE - 1),
                slot3Position = Random.nextInt(0, SLOT_SIZE - 1),
                slot4Position = Random.nextInt(0, SLOT_SIZE - 1),
            )
            checkWin.tryEmit(Unit)
            return spinResult
        }
        return null
    }


    fun onIncreaseBetClicked() {
        if (bet.value < balance.value && !isSpinning.value) {
            bet.value = bet.value + 10
        }
    }

    fun onDecreaseBetClicked() {
        if (balance.value > 0 && !isSpinning.value && bet.value > 0)
            bet.value = bet.value - 10
    }

    fun onAutoSpinClicked() {
        if (balance.value > 0L)
            isAutoSpinEnabled.value = !isAutoSpinEnabled.value
    }

    fun onRestartClicked() {
        balance.value = 400
        bet.value = 50
        isAutoSpinEnabled.value = false
    }

    fun onSlot1Scrolled() {
        isSlot1Spinning.value = false
    }

    fun onSlot2Scrolled() {
        isSlot2Spinning.value = false
    }

    fun onSlot3Scrolled() {
        isSlot3Spinning.value = false
    }

    fun onSlot4Scrolled() {
        isSlot4Spinning.value = false
    }
}