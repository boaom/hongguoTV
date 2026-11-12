// SPDX-License-Identifier: GPL-3.0-only
package com.hongguotv.nativeapp

// 界面公共颜色及背景样式；调整公共外观时优先查看此处。
//
// 视觉方向：「直播舞台 · 赛博霓虹」
// - 深蓝逐底（近黑蓝）作为舞台底，顶部/对角各一束霓虹冷光
// - 红果红为品牌主色（按钮、播放、进度），电光紫为次级强调，霓虹青表在线/成功
// - 焦点态用冷色辉光（不是暖橙），模仿舞台追光灯扫过

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable

/** Shared native colors and shapes for the mobile portrait "live stage" theme. */
object TvStyle {
    // === 舞台底色（深蓝逐底）===
    val background = Color.rgb(10, 14, 22)     // #0A0E16 舞台底
    val surface = Color.rgb(16, 22, 36)        // #101624 卡片面
    val raised = Color.rgb(22, 30, 48)         // #161E30 抬升面

    // === 品牌与强调 ===
    val accent = Color.rgb(255, 59, 48)        // #FF3B30 红果红（主：播放/按钮/进度）
    val accentGlow = Color.rgb(255, 107, 94)   // #FF6B5E 红的高亮版（焦点描边）
    val neon = Color.rgb(123, 92, 255)         // #7B5CFF 电光紫（次级强调/名次）
    val neonSoft = Color.rgb(150, 130, 255)    // #9682FF 紫的高亮版
    val cyan = Color.rgb(25, 227, 177)         // #19E3B1 霓虹青（在线/成功/榜首）

    // === 文字阶 ===
    val text = Color.rgb(234, 240, 250)        // #EAF0FA 主文字（冷白）
    val muted = Color.rgb(138, 150, 184)       // #8A96B8 次要文字（蓝灰）
    val outline = Color.rgb(42, 53, 80)        // #2A3550 描边（靛蓝）

    /** 海报宽高比；竖屏卡片仍沿用 0.7 竖版比例。 */
    const val POSTER_ASPECT = .7f

    fun shape(context: Context, color: Int, border: Int = Color.TRANSPARENT, radius: Int = 12) =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius * context.resources.displayMetrics.density
            setStroke((2 * context.resources.displayMetrics.density).toInt(), border)
        }

    /**
     * 卡片三态：normal / selected / focused。
     * 焦点态用「深蓝→电光紫」的冷色辉光渐变 + 高亮紫描边，代替原先的暖橙。
     */
    fun card(context: Context, focused: Boolean, selected: Boolean = false) =
        GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            if (focused)
                intArrayOf(Color.argb(170, 123, 92, 255), Color.argb(235, 16, 26, 64))
            else if (selected)
                intArrayOf(Color.argb(100, 35, 60, 120), Color.argb(210, 12, 20, 52))
            else
                intArrayOf(Color.argb(205, 16, 24, 56), Color.argb(215, 8, 12, 36))
        ).apply {
            cornerRadius = 12 * context.resources.displayMetrics.density
            setStroke(
                maxOf(1, (context.resources.displayMetrics.density).toInt()),
                when {
                    focused -> Color.argb(220, 150, 130, 255)   // 亮紫霓虹描边
                    selected -> Color.argb(120, 123, 92, 255)
                    else -> Color.argb(22, 255, 255, 255)
                }
            )
        }
}