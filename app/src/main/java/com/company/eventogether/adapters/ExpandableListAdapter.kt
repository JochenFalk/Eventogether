package com.company.eventogether.adapters

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.company.eventogether.AppSettings.TICKET_PROVIDERS
import com.company.eventogether.R
import com.company.eventogether.databinding.ListGroupBinding
import com.company.eventogether.databinding.ListItemBinding
import com.company.eventogether.helpclasses.StringResourcesProvider
import com.company.eventogether.helpclasses.Tools
import com.company.eventogether.model.EventExpandableGroup
import org.koin.java.KoinJavaComponent
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class ExpandableListAdapter(private val callback: (String) -> Unit) :
    ExpandableAdapter<ExpandableAdapter.ViewHolder>() {

    var expandableList = ArrayList<EventExpandableGroup>()

    private class GroupViewHolder(val listGroupBinding: ListGroupBinding) :
        ViewHolder(listGroupBinding.root) {
        fun bind(group: EventExpandableGroup) {
            listGroupBinding.textViewGroupTitle.text = group.groupTitle
        }
    }

    private class ItemViewHolder(
        private val listItemBinding: ListItemBinding,
        private val callback: (String) -> Unit
    ) :
        ViewHolder(listItemBinding.root) {

        val stringResourcesProvider: StringResourcesProvider by KoinJavaComponent.inject(
            StringResourcesProvider::class.java
        )
        val group1CustomText =
            stringResourcesProvider.getStringByName("event_expandable_list_group1_custom_text")
        val group2CustomText =
            stringResourcesProvider.getStringByName("event_expandable_list_group2_custom_text")
        val group3CustomText =
            stringResourcesProvider.getStringByName("event_expandable_list_group3_custom_text")

        fun bind(groupItems: ArrayList<Any>?, groupPosition: Int, childPosition: Int) {

            if (groupItems != null) {
                with(listItemBinding.textViewItem) {

                    when (groupPosition) {

                        0 -> {
                            when (childPosition) {

                                0 -> {
                                    text = groupItems[childPosition].toString()
                                    setOnClickListener(null)
                                }

                                1 -> {
                                    text = group1CustomText
                                    setTextColor(stringResourcesProvider.getColor(R.color.link_color))
                                    setOnClickListener {
                                        Tools.extractUrlFromString(groupItems[childPosition].toString())
                                            ?.let { url -> callback(url) }
                                    }
                                }
                            }
                        }

                        1 -> {

                            when (childPosition) {

                                0 -> {
                                    text = groupItems[childPosition].toString()
                                }

                                1 -> {
                                    val parts =
                                        groupItems[childPosition].toString().split("\\s+".toRegex())

                                    if (parts.size == 2) {
                                        val firstThreeElements =
                                            parts.subList(0, 1).joinToString(separator = " ")
                                        val remainingElements =
                                            parts.subList(1, parts.size)
                                                .joinToString(separator = " ")
                                        val textStringParsed =
                                            "$group2CustomText $firstThreeElements ($remainingElements)"

                                        text = textStringParsed
                                    }
                                }

                                2 -> {
                                    text = groupItems[childPosition].toString()
                                }
                            }

                            setTextColor(stringResourcesProvider.getColor(R.color.dark_blue_faded))
                            setOnClickListener(null)
                        }

                        2 -> {

                            when (childPosition) {

                                0 -> {
                                    text = group3CustomText
                                    setTextColor(stringResourcesProvider.getColor(R.color.dark_blue_faded))
                                    setOnClickListener(null)
                                }

                                else -> {

                                    TICKET_PROVIDERS.forEach { provider ->

                                        if (groupItems[childPosition - 1].toString()
                                                .startsWith(provider)
                                        ) {

                                            text = provider
                                            setTextColor(stringResourcesProvider.getColor(R.color.link_color))
                                            setOnClickListener {
                                                Tools.extractUrlFromString(groupItems[childPosition - 1].toString())
                                                    ?.let { url -> callback(url) }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getChildCount(groupPosition: Int): Int {

        return if (groupPosition == 2) {
            (expandableList[groupPosition].groupItems?.size ?: 0) + 1
        } else {
            expandableList[groupPosition].groupItems?.size ?: 0
        }
    }

    override fun getGroupCount(): Int {
        return expandableList.size
    }

    override fun onBindChildViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        childPosition: Int,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            val groupItems = expandableList[groupPosition].groupItems
            (holder as? ItemViewHolder)?.bind(groupItems, groupPosition, childPosition)
        }
    }

    override fun onBindGroupViewHolder(
        holder: ViewHolder,
        groupPosition: Int,
        expand: Boolean,
        payloads: List<Any>
    ) {
        if (payloads.isEmpty()) {
            val group = expandableList[groupPosition]
            (holder as? GroupViewHolder)?.bind(group)
        }
    }

    override fun onCreateChildViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return ItemViewHolder(ListItemBinding.inflate(inflater, viewGroup, false)) { url ->
            callback(url)
        }
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