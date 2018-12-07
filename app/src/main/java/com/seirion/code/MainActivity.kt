package com.seirion.code

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.seirion.code.data.allCodeData
import com.seirion.code.db.CodeData
import com.seirion.code.ui.InputCodeActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.Color
import com.google.zxing.MultiFormatReader
import com.google.zxing.MultiFormatWriter
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
        allCodeData(this)
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterSuccess {
                initUi()
            }
            .subscribe( { codeDataList = it }, { Log.e(TAG, "Failed to load data: $it") })
    }

    private fun initUi() {
        Log.d(TAG, "initUi()")
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = Adapter(this, codeDataList)

        for (codeData in codeDataList) {
            Log.e(TAG, "$codeData")
        }

        inputCode.setOnClickListener { InputCodeActivity.start(this) }
        scanning.setOnClickListener({})
    }


    private class Adapter(context: Context, dataList: List<CodeData>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        private val inflater = LayoutInflater.from(context)!!
        private val dataList = dataList

        override fun getItemCount() = dataList.size

        override fun getItemViewType(position: Int) = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.item_code, parent, false)
            return CodeViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(dataList[position])
        }

        private class CodeViewHolder(view: View) : ViewHolder(view) {
            private val nameTextView: TextView = view.findViewById(R.id.name)
            private val imageView: ImageView = view.findViewById(R.id.codeImage)

            @SuppressLint("CheckResult")
            override fun bind(codeData: CodeData) {
                nameTextView.text = codeData.name
                Single.fromCallable { generateBarCode(codeData.code) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { imageView.setImageBitmap(it) },
                        { Log.e(TAG, "Failed To generate barcode: ${codeData.code}") })
            }

            private fun generateBarCode(string: String): Bitmap {
                try {
                    val codeWriter = MultiFormatWriter()
                    return toBitmap(codeWriter.encode(string, BarcodeFormat.CODE_128, 1500, 400))
                } catch (e: WriterException) {
                    throw e
                }
            }

            private fun toBitmap(matrix: BitMatrix): Bitmap {
                val height = matrix.height
                val width = matrix.width
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

                for (x in 0 until width) {
                    for (y in 0 until height) {
                        val color = when (matrix.get(x, y)) {
                            true -> Color.BLACK
                            else -> Color.WHITE
                        }
                        bmp.setPixel(x, y, color)
                    }
                }
                return bmp
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
