package com.seirion.code

import android.annotation.SuppressLint
import android.app.Activity
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
import com.seirion.code.util.DisplayUtils
import com.seirion.code.util.codeTextPretty
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import com.seirion.code.util.generateBarCode
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DisplayUtils.init(this)
        initUi()
        loadData()
    }

    override fun onStart() {
        super.onStart()
        menu.collapse()
    }

    override fun onBackPressed() {
        if (menu.isExpanded) {
            menu.collapse()
        } else {
            super.onBackPressed()
        }
    }

    @SuppressLint("CheckResult")
    private fun initUi() {
        Log.d(TAG, "initUi()")
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = Adapter(this)

        inputCode.setOnClickListener { InputCodeActivity.start(this) }
        scanning.setOnClickListener { ScanningActivity.start(this) }

        DataManager.allCodeData(this)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ updateUi(it) }, { Log.e(TAG, "Failed to load data: $it") })
    }

    @SuppressLint("CheckResult")
    private fun loadData() {
        val adapter = recyclerView.adapter as Adapter
        DataManager.observeDataChange()
            .map { DataManager.allCodeData(this).blockingGet() }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { adapter.dataList = it }
            .subscribe({ updateUi(it) }, { Log.e(TAG, "Failed to update ui: $it") })
    }

    private fun updateUi(dataList: List<CodeData>) {
        Log.d(TAG, "updateUi() with dataSize ${dataList.size}")
        recyclerView.adapter.let {
            if (it is Adapter) {
                it.dataList = dataList
                if (dataList.isEmpty()) {
                    emptyMessage.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyMessage.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
                it.notifyDataSetChanged()
            }
        }
    }

    private class Adapter(activity: Activity) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        private val activity = activity
        private val inflater = LayoutInflater.from(activity.applicationContext)!!
        var dataList: List<CodeData> = Collections.emptyList<CodeData>()!!

        override fun getItemCount() = dataList.size

        override fun getItemViewType(position: Int) = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.item_code, parent, false)
            return CodeViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(dataList[position], position)
        }

        private inner class CodeViewHolder(view: View) : ViewHolder(view) {
            private val root = view
            private val nameTextView: TextView = view.findViewById(R.id.name)
            private val imageView: ImageView = view.findViewById(R.id.codeImage)
            private val codeText: TextView = view.findViewById(R.id.code)
            private var index: Int = 0

            @SuppressLint("CheckResult")
            override fun bind(codeData: CodeData, position: Int) {
                index = position
                nameTextView.text = codeData.name
                codeText.text = codeTextPretty(codeData.code)
                imageView.setImageBitmap(generateBarCode(codeData.code, DisplayUtils.displayWidth))
                root.setOnLongClickListener { showDeletePopup(codeData) }
                root.setOnClickListener { SingleActivity.start(activity, index) }
            }

            private fun showDeletePopup(codeData: CodeData): Boolean {
                AlertDialog.Builder(activity)
                    .setTitle(R.string.item_remove_title)
                    .setMessage(R.string.item_remove_message)
                    .setPositiveButton(R.string.delete) { _, _ -> DataManager.deleteCodeData(root.context, codeData) }
                    .show()
                return true
            }
        }

        open class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            open fun bind(codeData: CodeData, position: Int) { }
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
