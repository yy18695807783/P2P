package com.atguigu.p2pinvest.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 颜银 on 2016/11/16.
 * QQ:443098360
 * 微信：y443098360
 * 作用：
 */
public class FlowLayout extends ViewGroup {

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取宽度和高度的数值和模式（外面包括的视图）
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //如果是至多模式，需要我们自己去测量布局的宽度和高度
        int width = 0;
        int height = 0;

        //声明一行的宽度和高度
        int lineWidth = 0;
        int lineHeight = 0;

        //获取子视图的个数
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            //获取每一个子视图的宽度和高度，边距值
            View childView = getChildAt(i);
            //需要调用如下的方法之后，才可以获取子视图的宽高
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            //获取测量的宽高
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            //获取边距(要想获取边距，必须重写当前类的方法generateLayoutParams())
            MarginLayoutParams mp = (MarginLayoutParams) childView.getLayoutParams();

            if (lineWidth + childWidth + mp.leftMargin + mp.rightMargin < widthSize) {//不换行
                lineWidth += childWidth + mp.leftMargin + mp.rightMargin;
                lineHeight = Math.max(lineHeight, childHeight + mp.topMargin + mp.bottomMargin);

            } else {//换行
                width = Math.max(width, lineWidth);
                height += lineHeight;

                //重置
                lineWidth = childWidth + mp.leftMargin + mp.rightMargin;
                lineHeight = childHeight + mp.topMargin + mp.bottomMargin;

            }

            //单独的考虑一下最后一个！
            if (i == childCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
            Log.e("TAG", "widthSize = " + widthSize + ",heightSize = " + heightSize);
            Log.e("TAG", "width = " + width + ",height = " + height);
            //设置当前布局的宽高
            setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widthSize : width, widthMode == MeasureSpec.EXACTLY ? heightSize : height);


        }

    }

    private List<Integer> allHeights = new ArrayList<>();//每一行的高度构成的集合
    private List<List<View>> allViews = new ArrayList<>();//每一行view集合构成的集合

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getWidth();//获取布局的宽度

        //每一行的宽度和高度
        int lineWidth = 0;
        int lineHeight = 0;

        int childCount = getChildCount();
        //目的：给allHeights 和 allViews 赋值,方便下面遍历绘制
        List<View> lineViews = new ArrayList<>();//用于保存一行的所有的View
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            //获取测量的宽高
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            //获取边距(要想获取边距，必须重写当前类的方法generateLayoutParams())
            MarginLayoutParams mp = (MarginLayoutParams) childView.getLayoutParams();

            if (lineWidth + childWidth + mp.leftMargin + mp.rightMargin <= width) {//不换行
                lineWidth += childWidth + mp.leftMargin + mp.rightMargin;
                lineHeight = Math.max(lineHeight, childHeight + mp.topMargin + mp.bottomMargin);

                lineViews.add(childView);
            } else {//换行---保存，然后循环重来，从if开始
                allHeights.add(lineHeight);
                allViews.add(lineViews);

                lineViews = new ArrayList<>();
                lineViews.add(childView);

                lineWidth = childWidth + mp.leftMargin + mp.rightMargin;
                lineHeight = childHeight + mp.topMargin + mp.bottomMargin;

            }

            //单独考虑最后一个元素
            if (i == childCount - 1) {
                allViews.add(lineViews);
                allHeights.add(lineHeight);

            }
        }
        Log.e("TAG", "allViews.size = " + allViews.size() + "allHeight.size = " + allHeights.size());

        //记录每行的子视图数量
        int lineNumber = allViews.size();

        int x = 0;
        int y = 0;

        for (int i = 0; i < lineNumber; i++) {
            List<View> singleLineViews = allViews.get(i);//获取一行中元素构成的集合
            int singleLineHeight = allHeights.get(i);
            for (View view : singleLineViews) {

                MarginLayoutParams mp = (MarginLayoutParams) view.getLayoutParams();
                int left = x + mp.leftMargin;
                int top = y + mp.topMargin;
                int right = left + view.getMeasuredWidth();
                int bottom = top + view.getMeasuredHeight();


                view.layout(left, top, right, bottom);

                x += view.getMeasuredWidth() + mp.leftMargin + mp.rightMargin;

            }

            //换行
            x = 0;
            y += singleLineHeight;
        }

    }

    //FlowLayout中有了如下的方法，在onMeasure()中可通过child就可以getLayoutParams()，返回MarginLayoutParams类对象，进而计算margin的值
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        MarginLayoutParams marginLayoutParams = new MarginLayoutParams(getContext(), attrs);
        return marginLayoutParams;
    }
}
