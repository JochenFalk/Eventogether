package com.company.eventogether.adapters

import android.animation.ObjectAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.company.eventogether.databinding.ListGroupBinding
import com.company.eventogether.databinding.ListItemBinding
import com.company.eventogether.model.ListGroup
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class ExpandableListAdapter : ExpandableAdapter<ExpandableAdapter.ViewHolder>() {

    var listGroups = ArrayList<ListGroup>()

    private class GroupViewHolder(val listGroupBinding: ListGroupBinding) :
        ExpandableAdapter.ViewHolder(listGroupBinding.root)

    private class ItemViewHolder(val listItemBinding: ListItemBinding) :
        ExpandableAdapter.ViewHolder(listItemBinding.root)

    override fun getChildCount(groupPosition: Int): Int {
        return listGroups[groupPosition].items?.size ?: 0
    }

    override fun getGroupCount(): Int {
        return listGroups.size
    }

    override fun onBindChildViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        val item = listGroups[groupPosition].items?.get(childPosition)
        if (payloads.isEmpty()) {
            (holder as? ItemViewHolder)?.apply {
                listItemBinding.textViewItem.text = item
            }
        }
    }

    override fun onBindGroupViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        val group = listGroups[groupPosition]
        if (payloads.isEmpty()) {
            (holder as? GroupViewHolder)?.apply {
                listGroupBinding.textViewGroupTitle.text = group.title
            }
        }
    }

    override fun onCreateChildViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return ItemViewHolder(ListItemBinding.inflate(inflater, viewGroup, false))
    }

    override fun onCreateGroupViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return GroupViewHolder(ListGroupBinding.inflate(inflater, viewGroup, false))
    }

    override fun onGroupViewHolderExpandChange(
        holder: ViewHolder,
        groupPosition: Int,
        animDuration: Long,
        expand: Boolean
    ) {
        val arrowImage = when {
            holder as? GroupViewHolder != null -> holder.listGroupBinding.arrowImage
            else -> return
        }
        if (expand) {
            ObjectAnimator.ofFloat(arrowImage, View.ROTATION, 90f)
                .setDuration(animDuration)
                .start()
            arrowImage.animate()
                .setDuration(animDuration)
                .rotation(90f)
                .start()
        } else {
            ObjectAnimator.ofFloat(arrowImage, View.ROTATION, 0f)
                .setDuration(animDuration)
                .start()
        }
    }

    override fun getGroupItemViewType(groupPosition: Int): Int {
        return 1
    }

    override fun getChildItemViewType(groupPosition: Int, childPosition: Int): Int {
        return -1
    }

    override fun isGroup(viewType: Int): Boolean {
        return viewType > 0
    }
}