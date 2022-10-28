package cn.pedant.sweet_alert

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.*
import cn.pedant.sweet_alert.OptAnimationLoader.loadAnimation
import cn.pedant.sweet_alert.ViewUtils.getDrawable
import com.pnikosis.materialishprogress.ProgressWheel

class SweetAlertDialog : Dialog, View.OnClickListener {
    private var mDialogView: View? = null
    private var mModalInAnim: AnimationSet? = null
    private var mModalOutAnim: AnimationSet? = null
    private var mOverlayOutAnim: Animation? = null
    private var mErrorInAnim: Animation? = null
    private var mErrorXInAnim: AnimationSet? = null
    private var mSuccessLayoutAnimSet: AnimationSet? = null
    private var mSuccessBowAnim: Animation? = null
    private var mTitleTextView: TextView? = null
    private var mContentTextView: TextView? = null
    private var mCustomViewContainer: FrameLayout? = null
    private var mCustomView: View? = null
    private var mTitleText: String? = null
    private var mContentText: String? = null
    private var mShowCancel = false
    private var mShowContent = false
    private var mCancelText: String? = null
    private var mConfirmText: String? = null
    private var mNeutralText: String? = null
    private var mAlertType = 0
    private var mErrorFrame: FrameLayout? = null
    private var mSuccessFrame: FrameLayout? = null
    private var mProgressFrame: FrameLayout? = null
    private var mSuccessTick: SuccessTickView? = null
    private var mErrorX: ImageView? = null
    private var mSuccessLeftMask: View? = null
    private var mSuccessRightMask: View? = null
    private var mCustomImgDrawable: Drawable? = null
    private var mCustomImage: ImageView? = null
    private var mButtonsContainer: LinearLayout? = null
    private var mConfirmButton: Button? = null
    private var mHideConfirmButton = false
    private var mCancelButton: Button? = null
    private var mNeutralButton: Button? = null
    private var mConfirmButtonBackgroundColor: Int? = null
    private var mConfirmButtonTextColor: Int? = null
    private var mNeutralButtonBackgroundColor: Int? = null
    private var mNeutralButtonTextColor: Int? = null
    private var mCancelButtonBackgroundColor: Int? = null
    private var mCancelButtonTextColor: Int? = null
    private var mProgressHelper: ProgressHelper? = null
    private var mWarningFrame: FrameLayout? = null
    private var mCancelClickListener: OnSweetClickListener? = null
    private var mConfirmClickListener: OnSweetClickListener? = null
    private var mNeutralClickListener: OnSweetClickListener? = null
    private var mCloseFromCancel = false
    private var mHideKeyBoardOnDismiss = true
    private var contentTextSize = 0

    companion object {
        val NORMAL_TYPE = 0
        val ERROR_TYPE = 1
        val SUCCESS_TYPE = 2
        val WARNING_TYPE = 3
        val CUSTOM_IMAGE_TYPE = 4
        val PROGRESS_TYPE = 5
        var DARK_STYLE = false
    }

    //aliases
    val BUTTON_CONFIRM = DialogInterface.BUTTON_POSITIVE
    val BUTTON_CANCEL = BUTTON_NEGATIVE

    private var defStrokeWidth = 0f
    private var strokeWidth = 0f


    fun hideConfirmButton(): SweetAlertDialog {
        mHideConfirmButton = true
        return this
    }

    interface OnSweetClickListener {
        fun onClick(sweetAlertDialog: SweetAlertDialog?)
    }

    constructor(context: Context) : super(context, NORMAL_TYPE) {
//        this(context, NORMAL_TYPE)
    }

    constructor(context: Context, alertType: Int) : super(
        context,
        if (DARK_STYLE) R.style.alert_dialog_dark else R.style.alert_dialog_light
    ) {
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        defStrokeWidth = getContext().resources.getDimension(R.dimen.buttons_stroke_width)
        strokeWidth = defStrokeWidth
        mProgressHelper = ProgressHelper(context)
        mAlertType = alertType
        mErrorInAnim = loadAnimation(getContext(), R.anim.error_frame_in)
        mErrorXInAnim = loadAnimation(getContext(), R.anim.error_x_in) as AnimationSet?
        // 2.3.x system don't support alpha-animation on layer-list drawable
        // remove it from animation set
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            val childAnims = mErrorXInAnim!!.animations
            var idx = 0
            while (idx < childAnims.size) {
                if (childAnims[idx] is AlphaAnimation) {
                    break
                }
                idx++
            }
            if (idx < childAnims.size) {
                childAnims.removeAt(idx)
            }
        }
        mSuccessBowAnim = loadAnimation(getContext(), R.anim.success_bow_roate)
        mSuccessLayoutAnimSet =
            loadAnimation(getContext(), R.anim.success_mask_layout) as AnimationSet?
        mModalInAnim = loadAnimation(getContext(), R.anim.modal_in) as AnimationSet?
        mModalOutAnim = loadAnimation(getContext(), R.anim.modal_out) as AnimationSet?
        mModalOutAnim!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                mDialogView!!.visibility = View.GONE
                if (mHideKeyBoardOnDismiss) {
                    hideSoftKeyboard()
                }
                mDialogView!!.post {
                    if (mCloseFromCancel) {
                        super@SweetAlertDialog.cancel()
                    } else {
                        super@SweetAlertDialog.dismiss()
                    }
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        // dialog overlay fade out
        mOverlayOutAnim = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                val wlp: WindowManager.LayoutParams = window!!.attributes
                wlp.alpha = 1 - interpolatedTime
                window!!.attributes = wlp
            }
        }
        mOverlayOutAnim!!.duration = 120
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alert_dialog)
        mDialogView = window!!.decorView.findViewById(android.R.id.content)
        mTitleTextView = findViewById(R.id.title_text)
        mContentTextView = findViewById(R.id.content_text)
        mCustomViewContainer = findViewById(R.id.custom_view_container)
        mErrorFrame = findViewById(R.id.error_frame)
        mErrorX = mErrorFrame!!.findViewById(R.id.error_x)
        mSuccessFrame = findViewById(R.id.success_frame)
        mProgressFrame = findViewById(R.id.progress_dialog)
        mSuccessTick = mSuccessFrame!!.findViewById(R.id.success_tick)
        mSuccessLeftMask = mSuccessFrame!!.findViewById(R.id.mask_left)
        mSuccessRightMask = mSuccessFrame!!.findViewById(R.id.mask_right)
        mCustomImage = findViewById(R.id.custom_image)
        mWarningFrame = findViewById(R.id.warning_frame)
        mButtonsContainer = findViewById(R.id.buttons_container)
        mConfirmButton = findViewById(R.id.confirm_button)
        mConfirmButton!!.setOnClickListener(this)
        mConfirmButton!!.setOnTouchListener(Constants.FOCUS_TOUCH_LISTENER)
        mCancelButton = findViewById(R.id.cancel_button)
        mCancelButton!!.setOnClickListener(this)
        mCancelButton!!.setOnTouchListener(Constants.FOCUS_TOUCH_LISTENER)
        mNeutralButton = findViewById(R.id.neutral_button)
        mNeutralButton!!.setOnClickListener(this)
        mNeutralButton!!.setOnTouchListener(Constants.FOCUS_TOUCH_LISTENER)
        mProgressHelper!!.progressWheel = findViewById<View>(R.id.progressWheel) as ProgressWheel?
        setTitleText(mTitleText)
        setContentText(mContentText)
        setCustomView(mCustomView)
        setCancelText(mCancelText)
        setConfirmText(mConfirmText)
        setNeutralText(mNeutralText)
        applyStroke()
        setConfirmButtonBackgroundColor(mConfirmButtonBackgroundColor)
        setConfirmButtonTextColor(mConfirmButtonTextColor)
        setCancelButtonBackgroundColor(mCancelButtonBackgroundColor)
        setCancelButtonTextColor(mCancelButtonTextColor)
        setNeutralButtonBackgroundColor(mNeutralButtonBackgroundColor)
        setNeutralButtonTextColor(mNeutralButtonTextColor)
        changeAlertType(mAlertType, true)
    }

    private fun restore() {
        mCustomImage!!.visibility = View.GONE
        mErrorFrame!!.visibility = View.GONE
        mSuccessFrame!!.visibility = View.GONE
        mWarningFrame!!.visibility = View.GONE
        mProgressFrame!!.visibility = View.GONE
        mConfirmButton!!.visibility = if (mHideConfirmButton) View.GONE else View.VISIBLE
        adjustButtonContainerVisibility()
        mConfirmButton!!.setBackgroundResource(R.drawable.green_button_background)
        mErrorFrame!!.clearAnimation()
        mErrorX!!.clearAnimation()
        mSuccessTick!!.clearAnimation()
        mSuccessLeftMask!!.clearAnimation()
        mSuccessRightMask!!.clearAnimation()
    }

    /**
     * Hides buttons container if all buttons are invisible or gone.
     * This deletes useless margins
     */
    private fun adjustButtonContainerVisibility() {
        var showButtonsContainer = false
        for (i in 0 until mButtonsContainer!!.childCount) {
            val view = mButtonsContainer!!.getChildAt(i)
            if (view is Button && view.getVisibility() == View.VISIBLE) {
                showButtonsContainer = true
                break
            }
        }
        mButtonsContainer!!.visibility = if (showButtonsContainer) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        if (mAlertType == ERROR_TYPE) {
            mErrorFrame!!.startAnimation(mErrorInAnim)
            mErrorX!!.startAnimation(mErrorXInAnim)
        } else if (mAlertType == SUCCESS_TYPE) {
            mSuccessTick!!.startTickAnim()
            mSuccessRightMask!!.startAnimation(mSuccessBowAnim)
        }
    }

    private fun changeAlertType(alertType: Int, fromCreate: Boolean) {
        mAlertType = alertType
        // call after created views
        if (mDialogView != null) {
            if (!fromCreate) {
                // restore all of views state before switching alert type
                restore()
            }
            mConfirmButton!!.visibility = if (mHideConfirmButton) View.GONE else View.VISIBLE
            when (mAlertType) {
                ERROR_TYPE -> mErrorFrame!!.visibility = View.VISIBLE
                SUCCESS_TYPE -> {
                    mSuccessFrame!!.visibility = View.VISIBLE
                    // initial rotate layout of success mask
                    mSuccessLeftMask!!.startAnimation(mSuccessLayoutAnimSet!!.animations[0])
                    mSuccessRightMask!!.startAnimation(mSuccessLayoutAnimSet!!.animations[1])
                }
                WARNING_TYPE -> //                    mConfirmButton.setBackgroundResource(R.drawable.red_button_background);
                    mWarningFrame!!.visibility = View.VISIBLE
                CUSTOM_IMAGE_TYPE -> setCustomImage(mCustomImgDrawable)
                PROGRESS_TYPE -> {
                    mProgressFrame!!.visibility = View.VISIBLE
                    mConfirmButton!!.visibility = View.GONE
                }
            }
            adjustButtonContainerVisibility()
            if (!fromCreate) {
                playAnimation()
            }
        }
    }

    fun getAlertType(): Int {
        return mAlertType
    }

    fun changeAlertType(alertType: Int) {
        changeAlertType(alertType, false)
    }


    fun getTitleText(): String? {
        return mTitleText
    }

    fun setTitleText(text: String?): SweetAlertDialog? {
        mTitleText = text
        if (mTitleTextView != null && mTitleText != null) {
            if (text!!.isEmpty()) {
                mTitleTextView!!.visibility = View.GONE
            } else {
                mTitleTextView!!.visibility = View.VISIBLE
                mTitleTextView!!.text = Html.fromHtml(mTitleText)
            }
        }
        return this
    }

    fun setTitleText(resId: Int): SweetAlertDialog? {
        return setTitleText(context.resources.getString(resId))
    }

    fun setCustomImage(drawable: Drawable?): SweetAlertDialog? {
        mCustomImgDrawable = drawable
        if (mCustomImage != null && mCustomImgDrawable != null) {
            mCustomImage!!.visibility = View.VISIBLE
            mCustomImage!!.setImageDrawable(mCustomImgDrawable)
        }
        return this
    }

    fun setCustomImage(resourceId: Int): SweetAlertDialog? {
        return setCustomImage(context.resources.getDrawable(resourceId))
    }

    fun getContentText(): String? {
        return mContentText
    }

    /**
     * @param text text which can contain html tags.
     */
    fun setContentText(text: String?): SweetAlertDialog? {
        mContentText = text
        if (mContentTextView != null && mContentText != null) {
            showContentText(true)
            if (contentTextSize != 0) {
                mContentTextView!!.setTextSize(
                    TypedValue.COMPLEX_UNIT_PX,
                    spToPx(contentTextSize.toFloat(), context).toFloat()
                )
            }
            mContentTextView!!.text = Html.fromHtml(mContentText)
            mContentTextView!!.visibility = View.VISIBLE
            mCustomViewContainer!!.visibility = View.GONE
        }
        return this
    }

    fun spToPx(sp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        )
            .toInt()
    }

    /**
     * @param width in SP
     */
    fun setStrokeWidth(width: Float): SweetAlertDialog {
        strokeWidth = spToPx(width, context).toFloat()
        return this
    }

    private fun applyStroke() {
        if (defStrokeWidth.compareTo(strokeWidth) != 0) {
            val r: Resources = context.resources
            setButtonBackgroundColor(mConfirmButton, r.getColor(R.color.main_green_color))
            setButtonBackgroundColor(mNeutralButton, r.getColor(R.color.main_disabled_color))
            setButtonBackgroundColor(mCancelButton, r.getColor(R.color.red_btn_bg_color))
        }
    }

    fun isShowCancelButton(): Boolean {
        return mShowCancel
    }

    fun showCancelButton(isShow: Boolean): SweetAlertDialog {
        mShowCancel = isShow
        if (mCancelButton != null) {
            mCancelButton!!.visibility = if (mShowCancel) View.VISIBLE else View.GONE
        }
        return this
    }

    fun isShowContentText(): Boolean {
        return mShowContent
    }

    fun showContentText(isShow: Boolean): SweetAlertDialog {
        mShowContent = isShow
        if (mContentTextView != null) {
            mContentTextView!!.visibility = if (mShowContent) View.VISIBLE else View.GONE
        }
        return this
    }

    fun getCancelText(): String? {
        return mCancelText
    }

    fun setCancelText(text: String?): SweetAlertDialog {
        mCancelText = text
        if (mCancelButton != null && mCancelText != null) {
            showCancelButton(true)
            mCancelButton!!.text = mCancelText
        }
        return this
    }

    fun getConfirmText(): String? {
        return mConfirmText
    }

    fun setConfirmText(text: String?): SweetAlertDialog {
        mConfirmText = text
        if (mConfirmButton != null && mConfirmText != null) {
            mConfirmButton!!.text = mConfirmText
        }
        return this
    }

    fun setConfirmButtonBackgroundColor(color: Int?): SweetAlertDialog {
        mConfirmButtonBackgroundColor = color
        setButtonBackgroundColor(mConfirmButton, color)
        return this
    }

    fun getConfirmButtonBackgroundColor(): Int? {
        return mConfirmButtonBackgroundColor
    }

    fun setNeutralButtonBackgroundColor(color: Int?): SweetAlertDialog {
        mNeutralButtonBackgroundColor = color
        setButtonBackgroundColor(mNeutralButton, color)
        return this
    }

    fun getNeutralButtonBackgroundColor(): Int? {
        return mNeutralButtonBackgroundColor
    }

    fun setCancelButtonBackgroundColor(color: Int?): SweetAlertDialog {
        mCancelButtonBackgroundColor = color
        setButtonBackgroundColor(mCancelButton, color)
        return this
    }

    fun getCancelButtonBackgroundColor(): Int? {
        return mCancelButtonBackgroundColor
    }

    private fun setButtonBackgroundColor(btn: Button?, color: Int?) {
        if (btn != null && color != null) {
            val drawableItems = getDrawable(btn)
            if (drawableItems != null) {
                val gradientDrawableUnChecked = drawableItems[1] as GradientDrawable
                //solid color
                gradientDrawableUnChecked.setColor(color)
                //stroke
                gradientDrawableUnChecked.setStroke(strokeWidth.toInt(), genStrokeColor(color))
            }
        }
    }

    private fun genStrokeColor(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= 0.7f // decrease value component
        return Color.HSVToColor(hsv)
    }

    fun setConfirmButtonTextColor(color: Int?): SweetAlertDialog {
        mConfirmButtonTextColor = color
        if (mConfirmButton != null && color != null) {
            mConfirmButton!!.setTextColor(mConfirmButtonTextColor!!)
        }
        return this
    }

    fun getConfirmButtonTextColor(): Int? {
        return mConfirmButtonTextColor
    }

    fun setNeutralButtonTextColor(color: Int?): SweetAlertDialog {
        mNeutralButtonTextColor = color
        if (mNeutralButton != null && color != null) {
            mNeutralButton!!.setTextColor(mNeutralButtonTextColor!!)
        }
        return this
    }

    fun getNeutralButtonTextColor(): Int? {
        return mNeutralButtonTextColor
    }

    fun setCancelButtonTextColor(color: Int?): SweetAlertDialog {
        mCancelButtonTextColor = color
        if (mCancelButton != null && color != null) {
            mCancelButton!!.setTextColor(mCancelButtonTextColor!!)
        }
        return this
    }

    fun getCancelButtonTextColor(): Int? {
        return mCancelButtonTextColor
    }

    fun setCancelClickListener(listener: OnSweetClickListener?): SweetAlertDialog {
        mCancelClickListener = listener
        return this
    }

    fun setConfirmClickListener(listener: OnSweetClickListener?): SweetAlertDialog {
        mConfirmClickListener = listener
        return this
    }

    fun setNeutralText(text: String?): SweetAlertDialog {
        mNeutralText = text
        if (mNeutralButton != null && mNeutralText != null && text!!.isNotEmpty()) {
            mNeutralButton!!.visibility = View.VISIBLE
            mNeutralButton!!.text = mNeutralText
        }
        return this
    }

    fun setNeutralClickListener(listener: OnSweetClickListener?): SweetAlertDialog {
        mNeutralClickListener = listener
        return this
    }

//    fun setTitle(title: CharSequence) {
//        this.setTitleText(title.toString())
//    }


    override fun setTitle(titleId: Int) {
        this.setTitleText(context.resources.getString(titleId))
    }

    fun getButton(buttonType: Int): Button? {
        return when (buttonType) {
            BUTTON_CONFIRM -> mConfirmButton
            BUTTON_CANCEL -> mCancelButton
            DialogInterface.BUTTON_NEUTRAL -> mNeutralButton
            else -> mConfirmButton
        }
    }

    fun setConfirmButton(text: String?, listener: OnSweetClickListener?): SweetAlertDialog? {
        setConfirmText(text)
        setConfirmClickListener(listener)
        return this
    }

    fun setConfirmButton(resId: Int, listener: OnSweetClickListener?): SweetAlertDialog? {
        val text: String = context.resources.getString(resId)
        setConfirmButton(text, listener)
        return this
    }


    fun setCancelButton(text: String?, listener: OnSweetClickListener?): SweetAlertDialog? {
        setCancelText(text)
        setCancelClickListener(listener)
        return this
    }

    fun setCancelButton(resId: Int, listener: OnSweetClickListener?): SweetAlertDialog? {
        val text: String = context.resources.getString(resId)
        setCancelButton(text, listener)
        return this
    }

    fun setNeutralButton(text: String?, listener: OnSweetClickListener?): SweetAlertDialog? {
        setNeutralText(text)
        setNeutralClickListener(listener)
        return this
    }

    fun setNeutralButton(resId: Int, listener: OnSweetClickListener?): SweetAlertDialog? {
        val text: String = context.resources.getString(resId)
        setNeutralButton(text, listener)
        return this
    }

    /**
     * Set content text size
     *
     * @param value text size in sp
     */
    fun setContentTextSize(value: Int): SweetAlertDialog? {
        contentTextSize = value
        return this
    }

    fun getContentTextSize(): Int {
        return contentTextSize
    }

    protected override fun onStart() {
        mDialogView!!.startAnimation(mModalInAnim)
        playAnimation()
    }

    /**
     * set custom view instead of message
     *
     * @param view
     */
    fun setCustomView(view: View?): SweetAlertDialog? {
        mCustomView = view
        if (mCustomView != null && mCustomViewContainer != null) {
            mCustomViewContainer!!.addView(view)
            mCustomViewContainer!!.visibility = View.VISIBLE
            mContentTextView!!.visibility = View.GONE
        }
        return this
    }

    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    override fun cancel() {
        dismissWithAnimation(true)
    }

    /**
     * The real Dialog.dismiss() will be invoked async-ly after the animation finishes.
     */
    fun dismissWithAnimation() {
        dismissWithAnimation(false)
    }

    private fun dismissWithAnimation(fromCancel: Boolean) {
        mCloseFromCancel = fromCancel
        //several view animations can't be launched at one view, that's why apply alpha animation on child
        (mDialogView as ViewGroup?)!!.getChildAt(0)
            .startAnimation(mOverlayOutAnim) //alpha animation
        mDialogView!!.startAnimation(mModalOutAnim) //scale animation
    }

    override fun onClick(v: View) {
        if (v.id == R.id.cancel_button) {
            if (mCancelClickListener != null) {
                mCancelClickListener!!.onClick(this@SweetAlertDialog)
            } else {
                dismissWithAnimation()
            }
        } else if (v.id == R.id.confirm_button) {
            if (mConfirmClickListener != null) {
                mConfirmClickListener!!.onClick(this@SweetAlertDialog)
            } else {
                dismissWithAnimation()
            }
        } else if (v.id == R.id.neutral_button) {
            if (mNeutralClickListener != null) {
                mNeutralClickListener!!.onClick(this@SweetAlertDialog)
            } else {
                dismissWithAnimation()
            }
        }
    }

    fun getProgressHelper(): ProgressHelper? {
        return mProgressHelper
    }

    fun setHideKeyBoardOnDismiss(hide: Boolean): SweetAlertDialog? {
        mHideKeyBoardOnDismiss = hide
        return this
    }

    fun isHideKeyBoardOnDismiss(): Boolean {
        return mHideKeyBoardOnDismiss
    }

    private fun hideSoftKeyboard() {
        val activity: Activity? = ownerActivity
        if (activity != null) {
            val inputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager != null && activity.currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }
    }
}