package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var radius = 0.0f
    private val time = 2000L
    private var widthAnimator = ValueAnimator()
    private var arcContainer: RectF = RectF()

    private var circleAnimator = ValueAnimator.ofInt(0, 360).apply {
        duration = time
        interpolator = DecelerateInterpolator()
        addUpdateListener {
            circleCircumference = it.animatedValue as Int
            invalidate()
        }
    }

    private var textWidth = 0
    private var animatedWidth = 0.0f
    private var circleCircumference = 0
    private var backgroundColor = Color.GRAY
    private var loadingColor = Color.GRAY

    private var label = ""
    private var labelInProgress = ""
    private var text = ""

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
        color = Color.GRAY
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when(new) {
            ButtonState.Loading -> {
                widthAnimator.start()
                circleAnimator.start()
                text = labelInProgress
            }
            ButtonState.Completed ->  {
                animatedWidth = 0F
                circleCircumference = 0
                widthAnimator.cancel()
                circleAnimator.cancel()
                text = label
                invalidate()
            }
            ButtonState.Clicked -> TODO()
        }
    }

    fun state(newState: ButtonState){
        buttonState = newState
    }

    init {
        isClickable = true
        val a = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton, defStyleAttr, 0)
        label = a.getString(R.styleable.LoadingButton_text) ?: ""
        text = label
        labelInProgress = a.getString(R.styleable.LoadingButton_loading_text) ?: ""
        backgroundColor = a.getColor(R.styleable.LoadingButton_background_color, Color.GRAY)
        loadingColor = a.getColor(R.styleable.LoadingButton_loading_color, Color.GRAY)
        a.recycle()

        val rect = Rect()
        paint.getTextBounds(labelInProgress, 0, label.length, rect)
        textWidth = rect.right - rect.left
    }

    override fun performClick(): Boolean {
        if (buttonState == ButtonState.Loading) return true

        buttonState = ButtonState.Loading
        return super.performClick()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) / 2.0 * 0.8).toFloat()

        widthAnimator = ValueAnimator.ofInt(0, width)
        widthAnimator.duration = time
        widthAnimator.interpolator = DecelerateInterpolator()
        widthAnimator.addUpdateListener {
            animatedWidth = (it.animatedValue as Int).toFloat()
            if (animatedWidth >= width) {
                startOver()
            }
            invalidate()
        }

        arcContainer = RectF(width / 2F + textWidth / 2F + 100,
            height / 2F - 30,
            width / 2F + textWidth / 2F + 100 + 60,
            height / 2F + 30 )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = backgroundColor
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
        paint.color = loadingColor
        canvas.drawRect(0F, 0F, animatedWidth, height.toFloat(), paint)
        paint.color = Color.YELLOW
        canvas.drawArc(arcContainer,
            0.0F, circleCircumference.toFloat(), true, paint
        )

        paint.color = Color.WHITE
        canvas.drawText(text, width / 2.0F, height / 2.0F + 10, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun startOver() {
        animatedWidth = 0F
        circleCircumference = 0
        widthAnimator.cancel()
        circleAnimator.cancel()
        widthAnimator.start()
        circleAnimator.start()
    }
}