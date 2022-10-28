package cn.pedant.sweet_alert

import android.content.Context
import com.pnikosis.materialishprogress.ProgressWheel

class ProgressHelper(ctx: Context) {
    private var mProgressWheel: ProgressWheel? = null
    var isSpinning = true
        private set
    private var mSpinSpeed = 0.75f
    private var mBarWidth: Int
    private var mBarColor: Int
    private var mRimWidth: Int
    private var mRimColor: Int
    private var mIsInstantProgress: Boolean
    private var mProgressVal: Float
    private var mCircleRadius: Int
    var progressWheel: ProgressWheel?
        get() = mProgressWheel
        set(progressWheel) {
            mProgressWheel = progressWheel
            updatePropsIfNeed()
        }

    private fun updatePropsIfNeed() {
        if (mProgressWheel != null) {
            if (!isSpinning && mProgressWheel!!.isSpinning) {
                mProgressWheel!!.stopSpinning()
            } else if (isSpinning && !mProgressWheel!!.isSpinning) {
                mProgressWheel!!.spin()
            }
            if (mSpinSpeed != mProgressWheel!!.getSpinSpeed()) {
                mProgressWheel!!.setSpinSpeed(mSpinSpeed)
            }
            if (mBarWidth != mProgressWheel!!.getBarWidth()) {
                mProgressWheel!!.setBarWidth(mBarWidth)
            }
            if (mBarColor != mProgressWheel!!.getBarColor()) {
                mProgressWheel!!.setBarColor(mBarColor)
            }
            if (mRimWidth != mProgressWheel!!.getRimWidth()) {
                mProgressWheel!!.setRimWidth(mRimWidth)
            }
            if (mRimColor != mProgressWheel!!.getRimColor()) {
                mProgressWheel!!.setRimColor(mRimColor)
            }
            if (mProgressVal != mProgressWheel!!.progress) {
                if (mIsInstantProgress) {
                    mProgressWheel!!.setInstantProgress(mProgressVal)
                } else {
                    mProgressWheel!!.progress = mProgressVal
                }
            }
            if (mCircleRadius != mProgressWheel!!.getCircleRadius()) {
                mProgressWheel!!.setCircleRadius(mCircleRadius)
            }
        }
    }

    fun resetCount() {
        if (mProgressWheel != null) {
            mProgressWheel!!.resetCount()
        }
    }

    fun spin() {
        isSpinning = true
        updatePropsIfNeed()
    }

    fun stopSpinning() {
        isSpinning = false
        updatePropsIfNeed()
    }

    var progress: Float
        get() = mProgressVal
        set(progress) {
            mIsInstantProgress = false
            mProgressVal = progress
            updatePropsIfNeed()
        }

    fun setInstantProgress(progress: Float) {
        mProgressVal = progress
        mIsInstantProgress = true
        updatePropsIfNeed()
    }

    /**
     * @param circleRadius units using pixel
     */
    var circleRadius: Int
        get() = mCircleRadius
        set(circleRadius) {
            mCircleRadius = circleRadius
            updatePropsIfNeed()
        }
    var barWidth: Int
        get() = mBarWidth
        set(barWidth) {
            mBarWidth = barWidth
            updatePropsIfNeed()
        }
    var barColor: Int
        get() = mBarColor
        set(barColor) {
            mBarColor = barColor
            updatePropsIfNeed()
        }
    var rimWidth: Int
        get() = mRimWidth
        set(rimWidth) {
            mRimWidth = rimWidth
            updatePropsIfNeed()
        }
    var rimColor: Int
        get() = mRimColor
        set(rimColor) {
            mRimColor = rimColor
            updatePropsIfNeed()
        }
    var spinSpeed: Float
        get() = mSpinSpeed
        set(spinSpeed) {
            mSpinSpeed = spinSpeed
            updatePropsIfNeed()
        }

    init {
        mBarWidth = ctx.resources.getDimensionPixelSize(R.dimen.common_circle_width) + 1
        mBarColor = ctx.resources.getColor(R.color.success_stroke_color)
        mRimWidth = 0
        mRimColor = 0x00000000
        mIsInstantProgress = false
        mProgressVal = -1f
        mCircleRadius = ctx.resources.getDimensionPixelOffset(R.dimen.progress_circle_radius)
    }
}