package com.example.pybanker.ui.fragments


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.example.pybanker.R
import com.example.pybanker.model.DBHelper
import com.example.pybanker.model.SearchTransactionsAdapter
import kotlinx.android.synthetic.main.frg_search_general.*

/**
 * A simple [Fragment] subclass.
 *
 */
class FrgSearchGeneral : Fragment() {

    private var dbhelper: DBHelper? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        dbhelper = DBHelper(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frg_search_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        f_search_general_search_button.setOnClickListener {

            val searchKeywords = f_search_general_search_text.text.toString()
            if (searchKeywords.isEmpty()) {
                Toast.makeText(context, "Search keywords required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val res = dbhelper?.getSearchResults(searchKeywords)
            if (res!!.count == 0) {
                Toast.makeText(context, "No results returned", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val searchTransactions  = ArrayList<SearchTransaction>()

            try {
                while (res.moveToNext()) {
                    val credit = res.getString(3)
                    val debit = res.getString(4)

                    val amount = if (credit.isNullOrEmpty() || credit == "0.00") {
                        "£$debit"
                    } else {
                        "£$credit"
                    }

                    val type = if (credit.isNullOrEmpty() || credit == "0.00") {
                        Color.RED
                    } else {
                        Color.parseColor("#008000")
                    }

                    searchTransactions.add(
                        SearchTransaction(
                            res.getString(0),
                            res.getString(1),
                            res.getString(2),
                            amount,
                            type,
                            res.getString(5)
                        )
                    )
                }
                res.close()
            } catch (e: Exception) {
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }

            val layoutManager = LinearLayoutManager(activity)
            layoutManager.orientation = LinearLayoutManager.VERTICAL
            f_search_recycler_view.layoutManager = layoutManager
            f_search_recycler_view.adapter = SearchTransactionsAdapter(context, searchTransactions)
        }
    }


    data class SearchTransaction(var opdate: String,
                                  var description: String,
                                  var category: String,
                                  var amount: String,
                                  var type: Int,
                                  var account: String)

}
