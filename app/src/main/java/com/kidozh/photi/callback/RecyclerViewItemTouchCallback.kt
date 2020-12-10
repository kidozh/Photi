package com.kidozh.photi.callback


import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.kidozh.photi.R


class RecyclerViewItemTouchCallback(private val context: Context) :
    ItemTouchHelper.Callback() {
    private val TAG = RecyclerViewItemTouchCallback::class.java.simpleName
    private val icon: Drawable?
    private val background: ColorDrawable
    private var listener: onInteraction? = null
    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (listener != null) {
            listener!!.onRecyclerViewMoved(viewHolder.adapterPosition, target.adapterPosition)
        } else {
            //return false;
        }
        return true
    }

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        // get forum first
        if (listener != null) {
            listener!!.onRecyclerViewSwiped(position, direction)
        }
    }

    interface onInteraction {
        fun onRecyclerViewSwiped(position: Int, direction: Int)
        fun onRecyclerViewMoved(fromPosition: Int, toPosition: Int)
    }

    init {
        icon = ContextCompat.getDrawable(context, R.drawable.ic_outline_delete_24)
        background = ColorDrawable(context.getColor(R.color.MaterialColorAmber))
        if (context is onInteraction) {
            listener = context
        } else {
            Log.e(
                TAG,
                "Context " + context + " doesn't implement " + onInteraction::class.java.simpleName
            )
        }
    }
}