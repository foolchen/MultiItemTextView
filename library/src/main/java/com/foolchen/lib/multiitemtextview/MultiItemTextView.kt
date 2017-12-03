package com.foolchen.lib.multiitemtextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

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
  private var mItemWidth = -1
  private var mItemHeight = -1

  // 每个item的内边距
  private val mItemPadding = IntArray(4)

  private val mBound = Rect()
  private val mPaint = Paint()

  private var mText = ""


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
      mItemWidth = ta.getDimensionPixelSize(R.styleable.MultiItemTextView_mitv_item_width, -1)
      mItemHeight = ta.getDimensionPixelSize(R.styleable.MultiItemTextView_mitv_item_height, -1)
      val padding = ta.getDimensionPixelSize(R.styleable.MultiItemTextView_mitv_item_padding, 0)
      mItemPadding.fill(padding, 0, mItemPadding.size - 1)
      mItemPadding[0] = ta.getDimensionPixelSize(
          R.styleable.MultiItemTextView_mitv_item_padding_start, 0)
      mItemPadding[1] = ta.getDimensionPixelSize(
          R.styleable.MultiItemTextView_mitv_item_padding_top, 0)
      mItemPadding[2] = ta.getDimensionPixelSize(
          R.styleable.MultiItemTextView_mitv_item_padding_end, 0)
      mItemPadding[3] = ta.getDimensionPixelSize(
          R.styleable.MultiItemTextView_mitv_item_padding_bottom, 0)
      mText = ta.getString(R.styleable.MultiItemTextView_mitv_texts)
      ta.recycle()
    }

    mPaint.isAntiAlias = true
    mPaint.style = Paint.Style.FILL
    mPaint.textSize = mTextSize
    mPaint.getTextBounds(mText, 0, mText.length, mBound)

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
    canvas.drawText(mText, startX, startY, mPaint)

    if (isInEditMode) {
      // 绘制中线，用于查看文本的位置
      mPaint.color = Color.GRAY
      mPaint.strokeWidth = 5F
      canvas.drawLine(0F, height / 2F, width.toFloat(), height / 2F, mPaint)
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
      MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> {
        if (isWidth) {
          newSize = paddingLeft + mPaint.measureText(mText) + paddingRight
        } else {
          newSize = (paddingTop + getDisplayFontSize() + paddingBottom).toFloat()
        }
      }
    }
    return newSize.toInt()
  }

  private fun getDisplayFontSize(): Int {
    val fm = mPaint.fontMetricsInt
    return fm.bottom - fm.top
  }
}