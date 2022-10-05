package cn.pedant.sweet_alert

import android.content.Context
import android.graphics.Camera
import android.util.AttributeSet
import android.util.TypedValue
import android.view.animation.Animation
import android.view.animation.Transformation

class Rotate3dAnimation : Animation {
    private var mPivotXType = ABSOLUTE
    private var mPivotYType = ABSOLUTE
    private var mPivotXValue = 0.0f
    private var mPivotYValue = 0.0f
    private var mFromDegrees: Float
    private var mToDegrees: Float
    private var mPivotX = 0f
    private var mPivotY = 0f
    private var mCamera: Camera? = null
    private var mRollType: Int

    protected class Description {
        var type = 0
        var value = 0f
    }

    private fun parseValue(value: TypedValue?): Description {
        val d = Description()
        if (value == null) {
            d.type = ABSOLUTE
            d.value = 0f
        } else {
            if (value.type == TypedValue.TYPE_FRACTION) {
                d.type = if (value.data and TypedValue.COMPLEX_UNIT_MASK ==
                    TypedValue.COMPLEX_UNIT_FRACTION_PARENT
                ) RELATIVE_TO_PARENT else RELATIVE_TO_SELF
                d.value = TypedValue.complexToFloat(value.data)
                return d
            } else if (value.type == TypedValue.TYPE_FLOAT) {
                d.type = ABSOLUTE
                d.value = value.float
                return d
            } else if (value.type >= TypedValue.TYPE_FIRST_INT &&
                value.type <= TypedValue.TYPE_LAST_INT
            ) {
                d.type = ABSOLUTE
                d.value = value.data.toFloat()
                return d
            }
        }
        d.type = ABSOLUTE
        d.value = 0.0f
        return d
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.Rotate3dAnimation)
        mFromDegrees = a.getFloat(R.styleable.Rotate3dAnimation_fromDeg, 0.0f)
        mToDegrees = a.getFloat(R.styleable.Rotate3dAnimation_toDeg, 0.0f)
        mRollType = a.getInt(R.styleable.Rotate3dAnimation_rollType, ROLL_BY_X)
        var d = parseValue(a.peekValue(R.styleable.Rotate3dAnimation_pivotX))
        mPivotXType = d.type
        mPivotXValue = d.value
        d = parseValue(a.peekValue(R.styleable.Rotate3dAnimation_pivotY))
        mPivotYType = d.type
        mPivotYValue = d.value
        a.recycle()
        initializePivotPoint()
    }

    constructor(rollType: Int, fromDegrees: Float, toDegrees: Float) {
        mRollType = rollType
        mFromDegrees = fromDegrees
        mToDegrees = toDegrees
        mPivotX = 0.0f
        mPivotY = 0.0f
    }

    constructor(rollType: Int, fromDegrees: Float, toDegrees: Float, pivotX: Float, pivotY: Float) {
        mRollType = rollType
        mFromDegrees = fromDegrees
        mToDegrees = toDegrees
        mPivotXType = ABSOLUTE
        mPivotYType = ABSOLUTE
        mPivotXValue = pivotX
        mPivotYValue = pivotY
        initializePivotPoint()
    }

    constructor(
        rollType: Int,
        fromDegrees: Float,
        toDegrees: Float,
        pivotXType: Int,
        pivotXValue: Float,
        pivotYType: Int,
        pivotYValue: Float
    ) {
        mRollType = rollType
        mFromDegrees = fromDegrees
        mToDegrees = toDegrees
        mPivotXValue = pivotXValue
        mPivotXType = pivotXType
        mPivotYValue = pivotYValue
        mPivotYType = pivotYType
        initializePivotPoint()
    }

    private fun initializePivotPoint() {
        if (mPivotXType == ABSOLUTE) {
            mPivotX = mPivotXValue
        }
        if (mPivotYType == ABSOLUTE) {
            mPivotY = mPivotYValue
        }
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        mCamera = Camera()
        mPivotX = resolveSize(mPivotXType, mPivotXValue, width, parentWidth)
        mPivotY = resolveSize(mPivotYType, mPivotYValue, height, parentHeight)
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val fromDegrees = mFromDegrees
        val degrees = fromDegrees + (mToDegrees - fromDegrees) * interpolatedTime
        val matrix = t.matrix
        mCamera!!.save()
        when (mRollType) {
            ROLL_BY_X -> mCamera!!.rotateX(degrees)
            ROLL_BY_Y -> mCamera!!.rotateY(degrees)
            ROLL_BY_Z -> mCamera!!.rotateZ(degrees)
        }
        mCamera!!.getMatrix(matrix)
        mCamera!!.restore()
        matrix.preTranslate(-mPivotX, -mPivotY)
        matrix.postTranslate(mPivotX, mPivotY)
    }

    companion object {
        const val ROLL_BY_X = 0
        const val ROLL_BY_Y = 1
        const val ROLL_BY_Z = 2
    }
}