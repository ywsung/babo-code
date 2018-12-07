package com.seirion.code

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUi()
    }

    private fun initUi() {
        val layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = Adapter(this)
    }


    private class Adapter(context: Context) : RecyclerView.Adapter<Adapter.ViewHolder>() {

        private val inflater = LayoutInflater.from(context)!!
        private lateinit var data: ArrayList<String> // FIXME

        //override fun getItemCount() = data.size
        override fun getItemCount() = 0

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
