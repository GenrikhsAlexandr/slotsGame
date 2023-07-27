package com.genrikhs.slotsgame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.genrikhs.slotsgame.PrimaryViewModel.Companion.SLOT_SIZE
import com.genrikhs.slotsgame.databinding.FragmentPrimaryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PrimaryFragment : Fragment() {

    private var _binding: FragmentPrimaryBinding? = null
    private val binding: FragmentPrimaryBinding get() = _binding!!

    private val viewModel: PrimaryViewModel by viewModels()

    private val scrollInterpolator = PathInterpolator(0.90f, 0.10f, 0.10f, 0.90f)

    private var positionSlot1 = 0
    private var positionSlot2 = 1
    private var positionSlot3 = 2
    private var positionSlot4 = 3

    companion object {
        fun newInstance() = PrimaryFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrimaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.slot1.adapter = SlotAdapter()
        binding.slot2.adapter = SlotAdapter()
        binding.slot3.adapter = SlotAdapter()
        binding.slot4.adapter = SlotAdapter()

        setClickListeners()
        subscribe()
    }

    private fun setClickListeners() {

        binding.buttonAutoSpin.setOnClickListener {
            viewModel.onAutoSpinClicked()
        }

        binding.buttonAutoStop.setOnClickListener {
            viewModel.onAutoSpinClicked()
        }

        binding.buttonSpin.setOnClickListener {
            spin()
        }

        binding.buttonMinus.setOnClickListener {
            viewModel.onDecreaseBetClicked()
        }

        binding.buttonPlus.setOnClickListener {
            viewModel.onIncreaseBetClicked()
        }
        binding.buttonBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.slot1.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModel.onSlot1Scrolled()
                        positionSlot1 =
                            (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() % 8
                        println("positionSlot1 $positionSlot1")
                    }
                }
            }
        )
        binding.slot2.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModel.onSlot2Scrolled()
                        positionSlot2 =
                            (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() % 8
                        println("positionSlot2 $positionSlot2")

                    }
                }
            }
        )
        binding.slot3.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModel.onSlot3Scrolled()
                        positionSlot3 =
                            (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() % 8
                        println("positionSlot3 $positionSlot3")

                    }
                }
            }
        )
        binding.slot4.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModel.onSlot4Scrolled()
                        positionSlot4 =
                            (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() % 8
                        println("positionSlot4 $positionSlot4")

                    }
                }
            }
        )
    }

    private fun spin() {

        val result = viewModel.spin() ?: return
        val viewHeight = binding.slot1.getChildAt(0).height
        val baseTime = 3000
        val timeShift = 250
        val baseOffset = (SLOT_SIZE * 4) * viewHeight

        binding.slot1.smoothScrollBy(
            0,
            baseOffset + (viewHeight * result.slot1Position),
            scrollInterpolator,
            baseTime
        )
        binding.slot2.smoothScrollBy(
            0,
            baseOffset + (viewHeight * result.slot2Position),
            scrollInterpolator,
            baseTime + timeShift
        )
        binding.slot3.smoothScrollBy(
            0,
            baseOffset + (viewHeight * result.slot3Position),
            scrollInterpolator,
            baseTime + timeShift * 2
        )
        binding.slot4.smoothScrollBy(
            0,
            baseOffset + (viewHeight * result.slot4Position),
            scrollInterpolator,
            baseTime + timeShift * 3
        )
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isAutoSpinEnabled.collect {
                binding.buttonAutoSpin.isVisible = !it
                binding.buttonAutoStop.isVisible = it
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.bet.collect {
                binding.textBet.text = it.toString()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.balance.collect {
                binding.textCountMoney.text = it.toString()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.toastMessage.collect { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSpinning.collect { isSpinning ->
                binding.buttonSpin.isEnabled = !isSpinning
                if (!isSpinning && viewModel.isAutoSpinEnabled.value) {
                    spinWithDelay()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.checkWin.collect {
                checkWin()
            }
        }
    }

    private fun gameOver() {
        val dialogEnd = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_end, null)
        val dialogBackground = dialogEnd.findViewById<ImageView>(R.id.end)
        val restartButton = dialogEnd.findViewById<ImageButton>(R.id.restart)
        val restartDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogEnd)
            .create()
        restartButton.setOnClickListener {
            restartGame()
            restartDialog.dismiss()
        }
        dialogBackground.setImageResource(R.drawable.end)
        restartButton.setBackgroundResource(R.drawable.bott_res)
        restartDialog.show()
    }

    private suspend fun checkWin() {
        viewModel.isSpinning.collect {
            if (!it) {
                if (positionSlot1 == positionSlot2 ||
                    positionSlot2 == positionSlot3 ||
                    positionSlot3 == positionSlot4
                ) {
                    viewModel.balance.value = viewModel.balance.value + viewModel.bet.value * 2
                    Toast.makeText(requireContext(), "You win", Toast.LENGTH_SHORT).show()
                }
                viewModel.balance.value = viewModel.balance.value - viewModel.bet.value
                if (viewModel.balance.value == 0L) {
                    gameOver()
                    viewModel.isAutoSpinEnabled.value = false
                }
            }
        }
    }

    private fun spinWithDelay() {
        lifecycleScope.launch {
            if (viewModel.isAutoSpinEnabled.value) {
                delay(5000)
                spin()
            }
        }
    }

    private fun restartGame() {
        viewModel.onRestartClicked()
    }
}
