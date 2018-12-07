package com.seirion.code

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import com.seirion.code.ui.InputCodeActivity
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadData()
    }

    @SuppressLint("CheckResult")
    private fun loadData() {
        Completable.fromAction { } // FIXME
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { initUi() }, { Log.e(TAG, "Failed to load data: $it") })
    }

    private fun initUi() {
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = Adapter(this)

        inputCode.setOnClickListener { InputCodeActivity.start(this) }
        scanning.setOnClickListener({})
    }


    private class Adapter(context: Context) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        private val inflater = LayoutInflater.from(context)!!
        private var data = ArrayList<String>()

        override fun getItemCount() = data.size

        override fun getItemViewType(position: Int) = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = inflater.inflate(R.layout.item_code, parent, false)
            return CodeViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(position)
        }

        private class CodeViewHolder(view: View) : ViewHolder(view) {
            override fun bind(position: Int) {

            }
        }

        open class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            open fun bind(position: Int) {

            }
        }
    }

    companion object {
        private val TAG = MainActivity.javaClass.simpleName
    }
}
