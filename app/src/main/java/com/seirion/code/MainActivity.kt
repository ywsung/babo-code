package com.seirion.code

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.seirion.code.data.DataManager
import com.seirion.code.db.CodeData
import com.seirion.code.ui.InputCodeActivity
import com.seirion.code.ui.ScanningActivity
import com.seirion.code.ui.SingleActivity
import com.seirion.code.util.codeTextPretty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import com.seirion.code.util.generateBarCode
import io.reactivex.Single


class MainActivity : AppCompatActivity() {

    private lateinit var codeDataList: List<CodeData>

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadData()
    }

    @SuppressLint("CheckResult")
    private fun loadData() {
        DataManager.allCodeData(this)
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterSuccess {
                initUi()
            }
            .subscribe( { codeDataList = it }, { Log.e(TAG, "Failed to load data: $it") })
    }

    @SuppressLint("CheckResult")
    private fun initUi() {
        Log.d(TAG, "initUi()")
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = Adapter(this, codeDataList)

        for (codeData in codeDataList) {
            Log.e(TAG, "$codeData")
        }

        inputCode.setOnClickListener { InputCodeActivity.start(this) }
        scanning.setOnClickListener { ScanningActivity.start(this) }

        DataManager.observeDataChange()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { updateUi() }
    }

    @SuppressLint("CheckResult")
    private fun updateUi() {
        val adapter = recyclerView.adapter
        if (adapter is Adapter) {
            DataManager.allCodeData(this)
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess { adapter.notifyDataSetChanged() }
                .subscribe( { adapter.dataList = it }, { Log.e(TAG, "Failed to update ui: $it") })
        }
    }

    private class Adapter(activity: Activity, dataList: List<CodeData>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        private val activity = activity
        private val inflater = LayoutInflater.from(activity.applicationContext)!!
        var dataList = dataList

        override fun getItemCount() = dataList.size

        override fun getItemViewType(position: Int) = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.item_code, parent, false)
            return CodeViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(dataList[position])
        }

        private inner class CodeViewHolder(view: View) : ViewHolder(view) {
            private val root = view
            private val nameTextView: TextView = view.findViewById(R.id.name)
            private val imageView: ImageView = view.findViewById(R.id.codeImage)
            private val codeText: TextView = view.findViewById(R.id.code)

            @SuppressLint("CheckResult")
            override fun bind(codeData: CodeData) {
                nameTextView.text = codeData.name
                codeText.text = codeTextPretty(codeData.code)
                Single.fromCallable { generateBarCode(codeData.code) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { imageView.setImageBitmap(it) },
                        { Log.e(TAG, "Failed To generate barcode: ${codeData.code}") })

                root.setOnLongClickListener { showDeletePopup(codeData) }
                root.setOnClickListener { SingleActivity.start(activity) }
            }

            private fun showDeletePopup(codeData: CodeData): Boolean {
                AlertDialog.Builder(root.context)
                    .setTitle(R.string.item_remove_title)
                    .setMessage(R.string.item_remove_message)
                    .setPositiveButton(R.string.delete) { _, _ -> DataManager.deleteCodeData(root.context, codeData) }
                    .show()
                return true
            }
        }

        open class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            open fun bind(codeData: CodeData) { }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
