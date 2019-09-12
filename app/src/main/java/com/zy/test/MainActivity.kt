package com.zy.test

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    lateinit var linearLayoutManager: LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = ArrayList<GoodsEntity>()
        for (i in 0 until 20) {
            val bean = GoodsEntity()
            bean.labelPrice = "100.00"
            bean.oldlabelPrice = "200.00"
            bean.imgURL =
                "https://gss0.baidu.com/-4o3dSag_xI4khGko9WTAnF6hhy/zhidao/pic/item/b21bb051f8198618eaddf0f44ced2e738bd4e62c.jpg"
            list.add(bean)
        }

        linearLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerView.layoutManager = linearLayoutManager

        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.left = (resources.displayMetrics.density * 10 + 0.5).toInt()
            }
        })
        recyclerView.adapter =
            object : BaseQuickAdapter<GoodsEntity, BaseViewHolder>(R.layout.goods_item, list) {
                override fun convert(helper: BaseViewHolder, item: GoodsEntity) {
                    Glide.with(mContext).load(item.imgURL).into(helper.getView(R.id.picIv))
                    helper.setText(R.id.priceTv, item.labelPrice)
                    helper.setText(R.id.oldPriceTv, item.labelPrice)
                }

            }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItemCount = linearLayoutManager.findFirstVisibleItemPosition()
                //一个itme的宽度
                val w =
                    (resources.displayMetrics.widthPixels - (resources.displayMetrics.density * 65 + 0.5)) / 4
                //第一个view
                val viewItem = linearLayoutManager.findViewByPosition(firstVisibleItemCount)
                val left = viewItem!!.left.toFloat() * 1.0f / w
                setScroll(firstVisibleItemCount, left)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //静止的时候 牵引效果
                if (newState == 0) {
                    val w =
                        (resources.displayMetrics.widthPixels - (resources.displayMetrics.density * 16 + 0.5)) / 4
                    val firstVisibleItemCount = linearLayoutManager.findFirstVisibleItemPosition()
                    val findViewByPosition =
                        linearLayoutManager.findViewByPosition(firstVisibleItemCount)
                    if (findViewByPosition != null) {
                        val left = findViewByPosition.left
                        if (left == 0) {
                            return
                        }
                        if (findViewByPosition.left.toFloat() * 1.0f / w > -0.5f) {
                            recyclerView.smoothScrollBy(left, 0)
                        } else {
                            recyclerView.smoothScrollBy((w + left).toInt(), 0)
                        }
                    }
                }
            }
        })
    }

    private fun setScroll(firstVisibleItemCount: Int, left: Double) {
        val mar = resources.displayMetrics.density * 55 + 0.5
        val mar1 = resources.displayMetrics.density * 15 + 0.5
        //动态改变View的margin
        setMargin(firstVisibleItemCount, mar)
        setMargin(firstVisibleItemCount + 1, mar)
        setMargin(firstVisibleItemCount + 2, mar)
        setMargin1(firstVisibleItemCount + 3, mar1.toInt(), mar.toInt(), left)
        setMargin2(firstVisibleItemCount + 4, mar1.toInt())
    }

    private fun setMargin(lastVisibleItemCount: Int, mar: Double) {
        val viewItem = linearLayoutManager.findViewByPosition(lastVisibleItemCount)
        if (viewItem != null) {
            val layout = viewItem.findViewById(R.id.ll_quick_buy) as? LinearLayout
            if (layout != null) {
                layout.alpha = 0.0f
            }
            val textView = viewItem.findViewById(R.id.oldPriceTv) as? TextView
            if (textView != null) {
                textView.alpha = 1.0f
            }
            val layoupar = viewItem.layoutParams as RecyclerView.LayoutParams
            val w =
                (resources.displayMetrics.widthPixels - (resources.displayMetrics.density * 65 + 0.5)) / 4
            layoupar.width = (w - (resources.displayMetrics.density * 5 + 0.5)).toInt()
            layoupar.height = (w * 113 / 70 - (resources.displayMetrics.density * 10 + 0.5)).toInt()
            layoupar.topMargin = mar.toInt()
            viewItem.layoutParams = layoupar

        }
    }

    private fun setMargin1(lastVisibleItemCount: Int, mar2: Int, mar1: Int, left: Double) {
        val viewItem = recyclerView.layoutManager!!.findViewByPosition(lastVisibleItemCount)
        if (viewItem != null) {
            val layout = viewItem.findViewById(R.id.ll_quick_buy) as? LinearLayout
            if (layout != null) {
                layout.alpha = 1.0f
            }
            val textView = viewItem.findViewById(R.id.oldPriceTv) as? TextView
            if (textView != null) {
                textView.alpha = 0f
            }
            val layoupar = viewItem.layoutParams as RecyclerView.LayoutParams
            val w =
                (resources.displayMetrics.widthPixels - (resources.displayMetrics.density * 65 + 0.5)) / 4
            layoupar.topMargin = (mar2 - (mar2 - mar1) * Math.abs(left)).toInt()
            layoupar.width = (w - resources.displayMetrics.density * 5 + 0.5).toInt()
            val w1 = layoupar.width
            val w2 = w + resources.displayMetrics.density * 15 + 0.5
            layoupar.width = (w2 - (w2 - w1) * abs(left)).toInt()
            val h1 = w * 113 / 70 - resources.displayMetrics.density * 10 + 0.5
            val h2 = w * 113 / 70 + resources.displayMetrics.density * 30 + 0.5
            layoupar.height = (h2 - (h2 - h1) * abs(left)).toInt()
            viewItem.layoutParams = layoupar
        }
    }

    private fun setMargin2(lastVisibleItemCount: Int, mar: Int) {
        val viewItem = linearLayoutManager.findViewByPosition(lastVisibleItemCount)
        if (viewItem != null) {
            val layout = viewItem.findViewById(R.id.ll_quick_buy) as? LinearLayout
            if (layout != null) {
                layout.alpha = 1.0f
            }
            val textView = viewItem!!.findViewById(R.id.oldPriceTv) as? TextView
            if (textView != null) {
                textView.alpha = 0f
            }
            val layoupar = viewItem.layoutParams as RecyclerView.LayoutParams
            val w =
                (resources.displayMetrics.widthPixels - (resources.displayMetrics.density * 65 + 0.5)) / 4
            layoupar.width = (w + resources.displayMetrics.density * 15 + 0.5).toInt()
            layoupar.height = (w * 113 / 70 + resources.displayMetrics.density * 30 + 0.5).toInt()
            layoupar.topMargin = mar
            viewItem.layoutParams = layoupar
        }
    }

}
