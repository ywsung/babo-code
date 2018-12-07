package com.seirion.code.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import com.google.zxing.integration.android.IntentIntegrator
import com.seirion.code.R
import com.seirion.code.data.DataManager
import com.seirion.code.util.codeTextPretty
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_scanning.*


class ScanningActivity : AppCompatActivity() {

    private var code: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanning)
        initUi()
    }

    private fun initUi() {
        cardName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable?) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkCompletion()
            }
        })

        ok.setOnClickListener { addCodeData() }
        IntentIntegrator(this).initiateScan()
    }

    @SuppressLint("CheckResult")
    private fun addCodeData() {
        val name = cardName.text.toString().trim()
        Log.d(TAG, "addCodeData: $name, $code")
        DataManager.addCodeData(this, name, code!!)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { finish() },
                { Log.e(TAG, "Failed to addCodeData: $name, $code") })
    }

    private fun checkCompletion() {
        ok.isEnabled = !cardName.text.toString().trim().isEmpty()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result == null) {
                //Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Log.d(TAG, "result: ${result.contents}")
                code = result.contents
                if (TextUtils.isEmpty(code)) {
                    finish()
                } else {
                    codeText.text = codeTextPretty(code!!)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private val TAG = ScanningActivity::class.java.simpleName

        fun start(activity: Activity) {
            Log.d(TAG, "$TAG.start()")
            val intent = Intent(activity, ScanningActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            activity.startActivity(intent)
        }
    }
}
