// SPDX-License-Identifier: GPL-3.0-only
package com.hongguotv.nativeapp

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.widget.Switch
import android.widget.TextView

/** Shared visual language for browsing pages, modal panels and playback controls. */
internal object TvVisual {
    private var sharedNoise: Bitmap? = null
    val accent = Color.rgb(255, 59, 48)          // 红果红
    val accentSoft = Color.rgb(255, 107, 94)     // 红的高亮版
    val neon = Color.rgb(123, 92, 255)           // 电光紫
    val cyan = Color.rgb(25, 227, 177)           // 霓虹青
    val value = Color.rgb(159, 179, 232)         // #9FB3E8 设置行右侧值
    val primary = Color.rgb(220, 231, 255)       // #DCE7FF 主文字
    val focusedValue = Color.rgb(127, 233, 255)  // #7FE9FF 聚焦值（霓虹青）

    fun panel(context: Context, modal: Boolean = false) = GradientDrawable(GradientDrawable.Orientation.TL_BR,
        intArrayOf(Color.argb(if (modal) 250 else 140, 10, 16, 44), Color.argb(if (modal) 250 else 140, 14, 22, 60))).apply {
        cornerRadius = 18 * context.resources.displayMetrics.density
        setStroke(maxOf(1, context.resources.displayMetrics.density.toInt()), Color.argb(40, 96, 150, 255))
    }

    fun row(context: Context, focused: Boolean, selected: Boolean = false, category: Boolean = false): Drawable =
        object : Drawable() {
            private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            private val density = context.resources.displayMetrics.density
            override fun draw(canvas: Canvas) {
                val rect = RectF(bounds)
                val radius = 9 * density
                // 舞台追光：光从行首扫入，越向右越暗（冷色，非暖橙）。
                paint.shader = if (focused) LinearGradient(rect.left, 0f, rect.right, 0f,
                    intArrayOf(Color.argb(150, 123, 92, 255), Color.argb(64, 90, 130, 255), Color.argb(10, 35, 60, 140)),
                    floatArrayOf(0f, .32f, 1f), Shader.TileMode.CLAMP) else null
                paint.color = if (focused) { Color.WHITE } else if (selected) Color.argb(30, 60, 120, 200) else if (category) Color.argb(45, 18, 28, 72) else Color.TRANSPARENT
                paint.style = Paint.Style.FILL
                canvas.drawRoundRect(rect, radius, radius, paint)
                paint.shader = null
                if (focused || selected) {
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = density
                    paint.shader = if (focused) LinearGradient(rect.left, 0f, rect.right, 0f,
                        intArrayOf(Color.argb(170, 190, 180, 255), Color.argb(60, 123, 92, 255), Color.argb(14, 60, 90, 200)),
                        floatArrayOf(0f, .32f, 1f), Shader.TileMode.CLAMP) else null
                    paint.color = if (focused) { Color.WHITE } else Color.argb(80, 123, 92, 255)
                    rect.inset(density / 2, density / 2)
                    canvas.drawRoundRect(rect, radius, radius, paint)
                    paint.shader = null
                    paint.style = Paint.Style.FILL
                    // 行首竖条 = 霓虹灯管；聚焦时用青色，选中时用紫。
                    paint.color = if (focused) cyan else neon
                    val barHeight = minOf(28 * density, rect.height() * .65f)
                    canvas.drawRoundRect(RectF(rect.left + 6 * density, rect.centerY() - barHeight / 2,
                        rect.left + 9 * density, rect.centerY() + barHeight / 2), 2 * density, 2 * density, paint)
                }
            }
            override fun setAlpha(alpha: Int) {}
            override fun setColorFilter(filter: ColorFilter?) {}
            @Deprecated("Deprecated in Android") override fun getOpacity() = PixelFormat.TRANSLUCENT
        }

    fun tab(context: Context, focused: Boolean, selected: Boolean): Drawable = object : Drawable() {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val density = context.resources.displayMetrics.density
        override fun draw(canvas: Canvas) {
            val rect = RectF(bounds)
            if (focused) {
                paint.color = Color.argb(34, 123, 92, 255)
                canvas.drawRoundRect(rect, 7 * density, 7 * density, paint)
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = density
                paint.color = Color.argb(165, 150, 130, 255)
                rect.inset(density / 2, density / 2)
                canvas.drawRoundRect(rect, 7 * density, 7 * density, paint)
                paint.style = Paint.Style.FILL
            }
            if (selected) {
                // 霓虹灯管下划线：加厚到 3dp 并用青色，模拟灯带。
                paint.color = cyan
                canvas.drawRoundRect(RectF(bounds.left + 9 * density, bounds.bottom - 4 * density,
                    bounds.right - 9 * density, bounds.bottom - density), density, density, paint)
            }
        }
        override fun setAlpha(alpha: Int) {}
        override fun setColorFilter(filter: ColorFilter?) {}
        @Deprecated("Deprecated in Android") override fun getOpacity() = PixelFormat.TRANSLUCENT
    }

    fun backdrop(context: Context): Drawable = object : Drawable() {
        private val noise = sharedNoise ?: BitmapFactory.decodeResource(context.resources, R.drawable.tv_noise_tile).also { sharedNoise = it }
        private val grain = BitmapShader(noise, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        override fun draw(canvas: Canvas) {
            val w = bounds.width().toFloat().coerceAtLeast(1f)
            val h = bounds.height().toFloat().coerceAtLeast(1f)
            // 舞台顶光：顶部深蓝，落到底部近黑。
            paint.shader = RadialGradient(w * .5f, h * .18f, w * .85f,
                Color.rgb(24, 40, 96), Color.rgb(3, 6, 18), Shader.TileMode.CLAMP)
            canvas.drawRect(bounds, paint)
            // 第二束舞台灯（左下青紫对角光），替代原暖橙杂光。
            paint.shader = RadialGradient(w * .08f, h * .92f, w * .55f,
                Color.argb(34, 80, 120, 255), Color.TRANSPARENT, Shader.TileMode.CLAMP)
            canvas.drawRect(bounds, paint)
            // 右上一抹紫，强化霓虹层次。
            paint.shader = RadialGradient(w * .95f, h * .05f, w * .40f,
                Color.argb(30, 123, 92, 255), Color.TRANSPARENT, Shader.TileMode.CLAMP)
            canvas.drawRect(bounds, paint)
            paint.shader = grain
            paint.alpha = 8
            canvas.drawRect(bounds, paint)
            paint.alpha = 255
        }
        override fun setAlpha(alpha: Int) {}
        override fun setColorFilter(filter: ColorFilter?) {}
        @Deprecated("Deprecated in Android") override fun getOpacity() = PixelFormat.OPAQUE
    }
}

/** Keeps the complete label as accessible text; draws label and current value separately. */
internal class SettingsValueView(context: Context) : TextView(context) {
    private val ink = Paint(Paint.ANTI_ALIAS_FLAG)
    init { setSingleLine(true) }
    override fun onDraw(canvas: Canvas) {
        val parts = text.toString().split("  ·  ", limit = 2)
        val right = parts.getOrNull(1) ?: "›"
        ink.typeface = typeface
        ink.textSize = textSize
        val available = (width - paddingLeft - paddingRight).toFloat().coerceAtLeast(0f)
        val valueText = TextUtils.ellipsize(right, android.text.TextPaint(ink), available * .45f, TextUtils.TruncateAt.END).toString()
        val valueWidth = ink.measureText(valueText)
        val gap = 18 * resources.displayMetrics.density
        val title = TextUtils.ellipsize(parts[0], android.text.TextPaint(ink),
            (available - valueWidth - gap).coerceAtLeast(0f), TextUtils.TruncateAt.END).toString()
        val baseline = height / 2f - (ink.fontMetrics.ascent + ink.fontMetrics.descent) / 2
        ink.color = if (hasFocus()) Color.WHITE else TvVisual.primary
        canvas.drawText(title, paddingLeft.toFloat(), baseline, ink)
        ink.color = if (hasFocus()) TvVisual.focusedValue else TvVisual.value
        canvas.drawText(valueText, width - paddingRight - valueWidth, baseline, ink)
    }
}

/** One focus target per settings row; this child only displays the boolean value. */
internal class SettingsSwitch(context: Context) : Switch(context) {
    var rowFocused: Boolean = false
        set(value) { field = value; invalidate() }
    private val ink = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val d = resources.displayMetrics.density
        setMeasuredDimension(resolveSize((54 * d).toInt(), widthMeasureSpec), resolveSize((30 * d).toInt(), heightMeasureSpec))
    }
    override fun onDraw(canvas: Canvas) {
        val d = resources.displayMetrics.density
        val h = minOf(height.toFloat(), 26 * d)
        val top = (height - h) / 2
        val inset = 2 * d
        val rect = RectF(inset, top + inset, width - inset, top + h - inset)
        ink.color = if (isChecked) TvVisual.accent else Color.argb(50, 255, 255, 255)
        canvas.drawRoundRect(rect, h / 2, h / 2, ink)
        if (rowFocused) {
            ink.style = Paint.Style.STROKE
            ink.strokeWidth = d
            ink.color = TvVisual.focusedValue
            canvas.drawRoundRect(RectF(d / 2, top + d / 2, width - d / 2, top + h - d / 2), h / 2, h / 2, ink)
            ink.style = Paint.Style.FILL
        }
        val radius = h / 2 - 5 * d
        ink.color = Color.WHITE
        canvas.drawCircle(if (isChecked) width - h / 2 else h / 2, height / 2f, radius, ink)
    }
}
