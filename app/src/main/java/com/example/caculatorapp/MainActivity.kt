package com.example.caculatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.caculatorapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var canAddOperation = false
    private var canAddDemical = false
    private var alreadyAddDemical = false
    private var alreadyAddDemicalPrevious = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun numberAction(view: View) {
        if (view is Button) {
            if (view.text == ".") {
                if (binding.tvWorkings.length() > 0 && canAddDemical && isNumber(binding.tvWorkings.text.last())) {
                    binding.tvWorkings.append(view.text)
                    canAddDemical = false
                    canAddOperation = false
                    alreadyAddDemical = true
                }
            } else {
                binding.tvWorkings.append(view.text)
                canAddOperation = true
                if (!alreadyAddDemical) canAddDemical = true
            }
        }
    }

    fun operatorAction(view: View) {
        if (binding.tvWorkings.length() > 0 && view is Button && canAddOperation && isNumber(binding.tvWorkings.text.last())) {
            binding.tvWorkings.append(view.text)
            if (alreadyAddDemical) alreadyAddDemicalPrevious = true
            else alreadyAddDemicalPrevious = false
            canAddOperation = false
            alreadyAddDemical = false
            canAddDemical = false
        }
    }

    fun allClearAction(view: View) {
        binding.tvWorkings.text = ""
        binding.tvResults.text = ""
        canAddOperation = false
        canAddDemical = false
        alreadyAddDemical = false
        alreadyAddDemicalPrevious = false
    }

    fun backspaceAction(view: View) {
        val length = binding.tvWorkings.length()
        if (length > 0) {
            var lastToRemove: Char = binding.tvWorkings.text.last()
            when (lastToRemove) {
                '+', '-', 'x', '/' -> {
                    binding.tvWorkings.text = binding.tvWorkings.text.subSequence(0, length - 1)
                    canAddOperation = true
                    if (!alreadyAddDemicalPrevious) canAddDemical = true
                }
                '.' -> {
                    binding.tvWorkings.text = binding.tvWorkings.text.subSequence(0, length - 1)
                    canAddOperation = true
                    canAddDemical = true
                    alreadyAddDemical = false
                }
                else -> binding.tvWorkings.text = binding.tvWorkings.text.subSequence(0, length - 1)
            }
        }

    }

    fun equalsAction(view: View) {
        binding.tvResults.text = caculateResults()
    }

    private fun isNumber(char: Char): Boolean {
        when (char) {
            numberChar.ZERO.number, numberChar.ONE.number, numberChar.TWO.number,
            numberChar.THREE.number, numberChar.FOUR.number, numberChar.FIVE.number,
            numberChar.SIX.number, numberChar.SEVEN.number, numberChar.EIGHT.number,
            numberChar.NINE.number -> return true
            else -> return false
        }
    }

    private fun caculateResults(): String {
        val digitOperators = digitsOperator()
        if (digitOperators.isEmpty()) return ""

        val multiplyDivide = multiplyDivideCaculate(digitOperators)
        if (multiplyDivide.isEmpty()) return ""

        val result = addSubtractCaculate(multiplyDivide)
        return result.toString()
    }

    private fun addSubtractCaculate(passedList: MutableList<Any>): Float {
        var result = passedList[0].toString().toFloat()
        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1].toString().toFloat()
                if (operator == '+')
                    result += nextDigit
                if (operator == '-')
                    result -= nextDigit
            }
        }

        return result
    }

    private fun multiplyDivideCaculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('/')) {
            list = caculateTimeDivide(list)
        }
        return list
    }

    private fun caculateTimeDivide(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices) {
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1].toString().toFloat()
                val nextDigit = passedList[i + 1].toString().toFloat()
                when (operator) {
                    'x' -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1
                    }
                    '/' -> {
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                    }
                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            if (i > restartIndex) {
                newList.add(passedList[i])
            }
        }

        return newList
    }

    private fun digitsOperator(): MutableList<Any> {
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in binding.tvWorkings.text) {
            if (character.isDigit() || character == '.')
                currentDigit += character
            else {
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(character)
            }
        }

        if (currentDigit != "")
            list.add(currentDigit)

        return list
    }

    enum class numberChar(val number: Char) {
        ZERO('0'),
        ONE('1'),
        TWO('2'),
        THREE('3'),
        FOUR('4'),
        FIVE('5'),
        SIX('6'),
        SEVEN('7'),
        EIGHT('8'),
        NINE('9'),
    }
}