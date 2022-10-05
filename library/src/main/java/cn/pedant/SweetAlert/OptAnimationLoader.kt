package cn.pedant.SweetAlert

import android.content.Context
import kotlin.Throws
import android.content.res.Resources.NotFoundException
import android.view.animation.Animation
import android.content.res.XmlResourceParser
import android.util.AttributeSet
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParser
import android.view.animation.AnimationSet
import android.util.Xml
import cn.pedant.SweetAlert.OptAnimationLoader
import android.view.animation.AlphaAnimation
import android.view.animation.ScaleAnimation
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation
import java.io.IOException
import java.lang.Exception
import java.lang.RuntimeException

object OptAnimationLoader {
    @JvmStatic
    @Throws(NotFoundException::class)
    fun loadAnimation(context: Context, id: Int): Animation? {
        var parser: XmlResourceParser? = null
        return try {
            parser = context.resources.getAnimation(id)
            createAnimationFromXml(context, parser)
        } catch (ex: XmlPullParserException) {
            val rnf = NotFoundException(
                "Can't load animation resource ID #0x" +
                        Integer.toHexString(id)
            )
            rnf.initCause(ex)
            throw rnf
        } catch (ex: IOException) {
            val rnf = NotFoundException(
                "Can't load animation resource ID #0x" +
                        Integer.toHexString(id)
            )
            rnf.initCause(ex)
            throw rnf
        } finally {
            parser?.close()
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun createAnimationFromXml(
        c: Context,
        parser: XmlPullParser?,
        parent: AnimationSet? = null,
        attrs: AttributeSet = Xml.asAttributeSet(parser)
    ): Animation? {
        var anim: Animation? = null

        // Make sure we are on a start tag.
        var type: Int
        val depth = parser!!.depth
        while ((parser.next().also { type = it } != XmlPullParser.END_TAG || parser.depth > depth)
            && type != XmlPullParser.END_DOCUMENT) {
            if (type != XmlPullParser.START_TAG) {
                continue
            }
            val name = parser.name
            if (name == "set") {
                anim = AnimationSet(c, attrs)
                createAnimationFromXml(c, parser, anim as AnimationSet?, attrs)
            } else if (name == "alpha") {
                anim = AlphaAnimation(c, attrs)
            } else if (name == "scale") {
                anim = ScaleAnimation(c, attrs)
            } else if (name == "rotate") {
                anim = RotateAnimation(c, attrs)
            } else if (name == "translate") {
                anim = TranslateAnimation(c, attrs)
            } else {
                anim = try {
                    Class.forName(name)
                        .getConstructor(Context::class.java, AttributeSet::class.java)
                        .newInstance(c, attrs) as Animation
                } catch (te: Exception) {
                    throw RuntimeException("Unknown animation name: " + parser.name + " error:" + te.message)
                }
            }
            parent?.addAnimation(anim)
        }
        return anim
    }
}