package com.foolchen.lib.multiitemtextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import java.util.*

/**
 * 可以显示多个项目的TextView
 * @author chenchong
 * 2017/12/3 0003
 * 16:04
 */
open class MultiItemTextView : View {

  // 要显示的item的数量，如果为-1则按照传入的item的数量显示
  private var mItemCount = -1
  // 文本显示的颜色，默认为灰色
  private var mTextColor = Color.GRAY
  // 文字大小，默认为24px
  private var mTextSize = 24F

  // item的宽高，如果未设置则根据文本变化
  private var mItemWidth = -1F
  private var mItemHeight = -1F
  // 是否根据条目数量平分宽度
  private var mDividerEqually = false
  private var mDividerColor = Color.GRAY
  private var mDividerWidth = 0F

  // 每个item的内边距
  private val mItemPadding = IntArray(4)

  private val mBound = Rect()
  private val mPaint = Paint()

  private val mItems = ArrayList<String>()
  // 分割后的字符串，用于绘制多行文本
  private val mDividedItems = ArrayList<List<String>>()


  constructor(context: Context?) : super(context) {
    init(context, null)
  }

  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
    init(context, attrs)
  }

  constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs,
      defStyleAttr) {
    init(context, attrs)
  }

  private fun init(context: Context?, attrs: AttributeSet?) {
    if (context != null && attrs != null) {
      val ta = context.obtainStyledAttributes(attrs, R.styleable.MultiItemTextView)

      mItemCount = ta.getInt(R.styleable.MultiItemTextView_mitv_count, -1)
      mTextColor = ta.getColor(R.styleable.MultiItemTextView_mitv_color, Color.GRAY)
      mTextSize = ta.getDimension(R.styleable.MultiItemTextView_mitv_text_size, 24F)
      mItemWidth = ta.getDimension(R.styleable.MultiItemTextView_mitv_item_width, -1F)
      mItemHeight = ta.getDimension(R.styleable.MultiItemTextView_mitv_item_height, -1F)
      mDividerColor = ta.getDimensionPixelSize(R.styleable.MultiItemTextView_mitv_divider_color,
          Color.GRAY)
      mDividerWidth = ta.getDimension(R.styleable.MultiItemTextView_mitv_divider_width, 1F)
      val padding = ta.getDimensionPixelSize(R.styleable.MultiItemTextView_mitv_item_padding, 0)
      mItemPadding.fill(padding, 0, mItemPadding.size)
      ta.getDimensionPixelSize(
          R.styleable.MultiItemTextView_mitv_item_padding_start, 0).let {
        if (it != 0) {
          mItemPadding[0] = it
        }
      }
      ta.getDimensionPixelSize(
          R.styleable.MultiItemTextView_mitv_item_padding_top, 0).let {
        if (it != 0) {
          mItemPadding[1] = it
        }
      }
      ta.getDimensionPixelSize(
          R.styleable.MultiItemTextView_mitv_item_padding_end, 0).let {
        if (it != 0) {
          mItemPadding[2] = it
        }
      }
      ta.getDimensionPixelSize(
          R.styleable.MultiItemTextView_mitv_item_padding_bottom, 0).let {
        if (it != 0) {
          mItemPadding[3] = it
        }
      }
      val text = ta.getString(R.styleable.MultiItemTextView_mitv_texts)
      text?.let {
        val items = text.split("|")
        mItemCount = items.size
        mItems.addAll(items)
      }
      ta.recycle()
    }

    mPaint.isAntiAlias = true
    mPaint.style = Paint.Style.FILL
    mPaint.textSize = mTextSize
    //mPaint.getTextBounds(mText, 0, mText.length, mBound)

  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val width = measureSelf(widthMeasureSpec, true)
    val height = measureSelf(heightMeasureSpec, false)
    setMeasuredDimension(width, height)
  }


  override fun onDraw(canvas: Canvas) {
    mPaint.color = mTextColor

    // 计算绘制文本的起始位置
    val startX = width / 2F - mBound.width() / 2F
    // 绘制文本时从文本的左下角开始，故如果要使文本居中，则开始位置需要+fontSize * 0.5f
    // 而此处绘制时不应该包含字体本身的边距，故使用Rect来获取高度
    val startY = height / 2F + mBound.height() / 2F
    // 绘制文本
    //canvas.drawText(mText, startX, startY, mPaint)

    drawItems(canvas)

    if (isInEditMode) {
      // 绘制中线，用于查看文本的位置
      mPaint.color = Color.GRAY
      mPaint.strokeWidth = 5F
      canvas.drawLine(0F, height / 2F, width.toFloat(), height / 2F, mPaint)
    }
  }

  // 绘制各个条目
  private fun drawItems(canvas: Canvas) {
    val itemWidth = calItemWidth()
    val top = calTop()
    val bottom = calBottom()
    for (i in 0 until mItemCount) {
      val start = calStart(i, itemWidth)
      val end = start + itemWidth

      // 绘制整个条目的区域
      mPaint.color = if (i % 2 == 0) Color.BLUE else Color.YELLOW
      mPaint.style = Paint.Style.FILL
      canvas.drawRect(start, top, end, bottom, mPaint)

      // 绘制内容区域
      mPaint.color = Color.RED
      mPaint.strokeWidth = 1F
      mPaint.style = Paint.Style.STROKE
      canvas.drawRect(start + mItemPadding[0], top + mItemPadding[1], end - mItemPadding[2],
          bottom - mItemPadding[3], mPaint)

      if (mDividerWidth > 0) {
        // 绘制分隔线
        mPaint.color = mDividerColor
        mPaint.strokeWidth = mDividerWidth
        mPaint.style = Paint.Style.FILL
        canvas.drawRect(end, top, end + mDividerWidth, bottom, mPaint)
      }
    }
  }

  /**
   * 测量当前View宽高的方法
   */
  private fun measureSelf(spec: Int, isWidth: Boolean): Int {
    val oldSize = MeasureSpec.getSize(spec).toFloat()
    var newSize = 0F
    when (MeasureSpec.getMode(spec)) {
      MeasureSpec.EXACTLY -> {
        // 需要精确高度，则直接使用原先定义的大小即可
        newSize = oldSize
      }
      MeasureSpec.AT_MOST -> {
        // 指定了match_parent
        newSize = if (isWidth) {
          oldSize
        } else {
          calItemHeight()
        }
      }
      MeasureSpec.UNSPECIFIED -> {
        // 指定了wrap_content
        newSize = if (isWidth) {
          if (mItemWidth == -1F) {
            // 未指定每个条目的宽度，则无法计算总宽度
            throw IllegalArgumentException("在View的宽度未精确指定时，需要指定每个条目的宽度(mitv_item_width)")
          }
          paddingLeft + mItemCount * mItemWidth + paddingRight
        } else {
          calItemHeight()
        }
      }
    }
    if (isWidth) {
      divideItems(newSize)
    }
    return newSize.toInt()
  }

  private fun getDisplayFontSize(): Int {
    val fm = mPaint.fontMetricsInt
    return fm.bottom - fm.top
  }

  // 计算每个条目的宽度
  private fun calItemWidth(): Float {
    return (width - paddingLeft - paddingRight - mDividerWidth * (mItemCount - 2)) / mItemCount.toFloat()
  }

  // 根据当前条目的位置，计算其起始位置
  private fun calStart(index: Int, itemWidth: Float): Float {
    return paddingLeft + index * (itemWidth + mDividerWidth)
  }

  private fun calTop(): Float {
    return paddingTop.toFloat()
  }

  private fun calBottom(): Float {
    return (height - paddingBottom).toFloat()
  }

  /** 计算条目的高度（在未指定高度的情况下） */
  private fun calItemHeight(): Float {
    var lines = 0
    mDividedItems
        .asSequence()
        .filter { it.size > lines }
        .forEach { lines = it.size }
    return paddingTop + paddingBottom + lines * getDisplayFontSize().toFloat()
  }

  /** 分割各个item */
  private fun divideItems(width: Float) {
    // 首先需要计算可绘制的最大宽度
    val drawableWidth = (width - paddingLeft - paddingRight - mItemPadding[0] - mItemPadding[2]) / mItemCount.toFloat()
    if (drawableWidth > 0) {
      // 需要保证每一行的宽度都小于drawableWidth
      mItems.mapTo(mDividedItems) { divideItem(drawableWidth, it) }
    }
  }

  /** 分割特定的item */
  private fun divideItem(drawableWidth: Float, item: String): List<String> {
    val texts = ArrayList<String>()
    var text = item
    var end = text.length
    while (true) {
      mPaint.getTextBounds(item, 0, end, mBound)
      if (mBound.width() <= drawableWidth) {
        // 宽度小于可绘制宽度，则添加到列表中
        texts.add(text.substring(0, end))
        text = text.substring(end)
        end = text.length
        if (text.isEmpty()) break// 没有需要继续处理的字符串时，跳出循环
      } else {
        // 宽度大于可绘制宽度，则缩减上限
        end--
      }
    }
    return texts
  }
}