package com.kidozh.photi.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kidozh.photi.R
import com.kidozh.photi.adapter.ObservationPositionAdapter
import com.kidozh.photi.callback.RecyclerViewItemTouchCallback
import com.kidozh.photi.database.ObservationPositionDatabase
import com.kidozh.photi.databinding.ActivityManagePositionBinding
import com.kidozh.photi.databinding.ActivityNewPositionBinding
import com.kidozh.photi.entity.ObservationPosition
import com.kidozh.photi.vmodel.ManageViewModel

class ManagePositionActivity : BaseAmbientActivity(),ObservationPositionAdapter.OnCardClicked {
    private lateinit var model: ManageViewModel
    private lateinit var binding: ActivityManagePositionBinding
    private var adapter: ObservationPositionAdapter = ObservationPositionAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagePositionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        model = ViewModelProvider(this).get(ManageViewModel::class.java)
        configureRecyclerview()
        bindViewModel()
        configureBtn()
    }

    fun configureRecyclerview(){
        binding.recyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL,false)
        binding.recyclerview.adapter = adapter

    }

    fun bindViewModel(){
        model.observationPositions.observe(this, Observer { list->
            adapter.setObservationPositions(list)
        })
    }

    fun configureBtn(){
        binding.addLocationBtn.setOnClickListener{l->
            var intent = Intent(this, NewPositionActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onClicked(pos: Int, observationPosition: ObservationPosition) {
        TODO("Not yet implemented")
    }

    override fun onLongClicked(pos: Int, observationPosition: ObservationPosition) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.delete_location_confirmation,observationPosition.name))
            .setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
                val thread = Thread {
                    model.dao.delete(observationPosition)
                }
                thread.start()
            })
            .setNegativeButton(android.R.string.cancel,DialogInterface.OnClickListener {dialog,which ->
                dialog.cancel()
            })
            .show()




    }
}