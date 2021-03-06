package com.seirion.code.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import com.seirion.code.R
import com.seirion.code.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_input_code.*


class InputCodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_code)
        initUi()
    }

    private fun initUi() {
        addTextChangeListener(cardNumber0)
        addTextChangeListener(cardNumber1)
        addTextChangeListener(cardNumber2)
        addTextChangeListener(cardNumber3)

        cardName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable?) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!cardName.text.toString().trim().isEmpty()) {
                    checkCompletion()
                }
            }
        })

        ok.setOnClickListener { addCodeData() }
    }

    @SuppressLint("CheckResult")
    private fun addCodeData() {
        val name = cardName.text.toString().trim()
        val codeString = cardNumber0.text.toString() + cardNumber1.text.toString() +
                    cardNumber2.text.toString() + cardNumber3.text.toString()
        Log.d(TAG, "addCodeData: $name, $codeString")
        DataManager.addCodeData(this, name, codeString)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { finish() },
                { Log.e(TAG, "Failed to addCodeData: $name, $codeString") })
    }

    private fun addTextChangeListener(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable?) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editText.text.toString().length == 4) {
                    focusNext(editText)
                    checkCompletion()
                }
            }
        })
    }

    private fun focusNext(view: View) {
        when(view) {
            cardNumber0 -> cardNumber1.requestFocus()
            cardNumber1 -> cardNumber2.requestFocus()
            cardNumber2 -> cardNumber3.requestFocus()
        }
    }

    private fun checkCompletion() {
        ok.isEnabled = !cardName.text.toString().trim().isEmpty() &&
                !cardNumber0.text.toString().isEmpty() &&
                !cardNumber1.text.toString().isEmpty() &&
                !cardNumber2.text.toString().isEmpty() &&
                !cardNumber3.text.toString().isEmpty()
    }

    companion object {
        private val TAG = InputCodeActivity::class.java.simpleName

        fun start(activity: Activity) {
            Log.d(TAG, "$TAG.start()")
            val intent = Intent(activity, InputCodeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            activity.startActivity(intent)
        }
    }
}
