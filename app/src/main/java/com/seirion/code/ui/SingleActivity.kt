package com.seirion.code.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.seirion.code.R
import kotlinx.android.synthetic.main.activity_single.*
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.seirion.code.data.DataManager
import com.seirion.code.db.CodeData
import com.seirion.code.util.codeTextPretty
import com.seirion.code.util.generateBarCode
import io.reactivex.android.schedulers.AndroidSchedulers


class SingleActivity : AppCompatActivity() {

    private lateinit var codeDataList: List<CodeData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single)
        loadData()
    }

    @SuppressLint("CheckResult")
    private fun loadData() {
        DataManager.allCodeData(this)
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterSuccess { initUi() }
            .subscribe( { codeDataList = it }, { Log.e(TAG, "Failed to load data: $it") })
    }

    private fun initUi() {
        val adapter = Adapter(this, codeDataList)
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
            }
        })
        viewPager.pageMargin = 100
    }

    private class Adapter(context: Context, dataList: List<CodeData>): PagerAdapter() {
        private val context = context
        private var dataList = dataList

        override fun isViewFromObject(view: View, p: Any): Boolean {
            return view == p
        }

        override fun getCount() = dataList.size

        @SuppressLint("CheckResult")
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val linearLayout = LayoutInflater.from(context).inflate(R.layout.layout_single_item, null)
            container.addView(linearLayout)
            val codeData = dataList[position]
            linearLayout.findViewById<TextView>(R.id.name).text = codeData.name
            linearLayout.findViewById<TextView>(R.id.code).text = codeTextPretty(codeData.code)

            val codeImage = linearLayout.findViewById<ImageView>(R.id.codeImage)
            codeImage.setImageBitmap(generateBarCode(codeData.code))
            return linearLayout
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as LinearLayout
            container.removeView(view)
        }
    }

    companion object {
        private val TAG = SingleActivity::class.java.simpleName

        fun start(activity: Activity) {
            Log.d(TAG, "$TAG.start()")
            val intent = Intent(activity, SingleActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            activity.startActivity(intent)
        }
    }
}
