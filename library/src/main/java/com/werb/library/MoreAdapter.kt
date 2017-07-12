package com.werb.library

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup
import com.werb.library.exception.MultiModelNotRegisterException
import kotlin.reflect.KClass

/**
 * [MoreAdapter] build viewHolder with data
 * Created by wanbo on 2017/7/2.
 */
class MoreAdapter : Adapter<ViewHolder>(), MoreLink {

    private var list: MutableList<Any> = mutableListOf()
    private val linkManager: MoreLink = MoreLinkManager()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val moreViewType = buildViewType(viewType)
        if (moreViewType == null){
            return attachViewType(list[viewType]).onCreateViewHolder(LayoutInflater.from(parent?.context), parent as ViewGroup)
        }else {
            return moreViewType.onCreateViewHolder(LayoutInflater.from(parent?.context), parent as ViewGroup)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val any = list[position]
        val attachViewType = attachViewType(any)
        attachViewType.buildHolder(holder as MoreViewHolder)
        attachViewType.bindData(any, holder)
    }

    fun loadData(data: Any) {
        if (data is List<*>) {
            for (d in data){
                d?.let {
                    list.add(it)
                    notifyItemChanged(list.indexOf(d))
                }
            }
        } else {
            list.add(data)
            notifyItemChanged(list.indexOf(data))
        }
    }

    fun attachTo(view: RecyclerView): MoreLink{
        view.adapter = this
        return this
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        val any = list[position]
        val viewLayout = attachViewTypeLayout(any)
        if (viewLayout == -1){
            return position
        }else {
            return viewLayout
        }
    }

    override fun register(viewType: MoreViewType<*>): MoreLink {
        linkManager.register(viewType)
        return this
    }

    override fun multiRegister(clazz: KClass<out Any>, link: MultiLink<*>): MoreLink {
        linkManager.multiRegister(clazz, link)
        return this
    }

    override fun attachViewType(any: Any): MoreViewType<Any> = linkManager.attachViewType(any)

    override fun attachViewTypeLayout(any: Any): Int = linkManager.attachViewTypeLayout(any)

    override fun buildViewType(type: Int): MoreViewType<Any>? = linkManager.buildViewType(type)
}