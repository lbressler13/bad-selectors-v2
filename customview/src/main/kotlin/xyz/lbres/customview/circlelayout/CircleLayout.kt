package xyz.lbres.customview.circlelayout

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.content.withStyledAttributes
import androidx.core.view.children
import androidx.core.view.isGone
import xyz.lbres.customview.R
import xyz.lbres.customview.ext.typedarray.getDimensionPixelSizeOrNull
import xyz.lbres.customview.ext.typedarray.getFloatOrNull
import xyz.lbres.customview.ext.typedarray.getRadiansOrNull
import xyz.lbres.kotlinutils.general.simpleIf
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Custom layout that displays children in a circle. See README for information about customizing layout.
 */
open class CircleLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : ViewGroup(
    context,
    attrs,
    defStyleAttr,
) {
    /**
     * Modes for method of calculating radius. See README for additional documentation.
     */
    enum class RadiusMode { FIT_CHILDREN, FIXED, PERCENT }

    /**
     * Modes for method of calculating angle between children. See README for additional documentation.
     */
    enum class AngleMode { DISTRIBUTED, FIXED }

    /**
     * Radius mode, determined based on values in attributes.
     * Defaults to [RadiusMode.FIT_CHILDREN].
     */
    private lateinit var _radiusMode: RadiusMode // set in init function
    val radiusMode: RadiusMode
        get() = _radiusMode

    /**
     * Fixed radius size, optionally passed in attributes.
     * Only used if the radius mode is [RadiusMode.FIXED].
     */
    private var _radiusSize: Int? = null
    val radiusSize: Int?
        get() = _radiusSize

    /**
     * Radius percent, optionally passed in attributes.
     * Only used if the radius mode is [RadiusMode.PERCENT].
     */
    private var _radiusPercent: Float? = null
    val radiusPercent: Float?
        get() = _radiusPercent

    /**
     * Angle mode, determined based on values in attributes.
     * Defaults to [AngleMode.DISTRIBUTED].
     */
    private lateinit var _angleMode: AngleMode // set in init function
    val angleMode: AngleMode
        get() = _angleMode

    /**
     * Angle between children, optionally passed in attributes.
     * Only used if the radius mode is [RadiusMode.FIXED].
     */
    private var _separationAngle: Double? = null
    val separationAngle: Double
        get() = _separationAngle ?: 0.0

    /**
     * Radius of circle
     */
    private var _radius: Float? = null
    val radius: Float
        get() = _radius ?: 0f

    /**
     * Angle of first child, optionally passed in attributes.
     * Defaults to 0.
     */
    private var _startAngle: Double = 0.0 // real value sit in init function
    val startAngle: Double
        get() = _startAngle

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    /**
     * Extract values from attributes
     */
    init {
        context.withStyledAttributes(attrs, R.styleable.CircleLayout) {
            _startAngle = getRadiansOrNull(R.styleable.CircleLayout_startAngle) ?: 0.0

            val attrRadiusSize = getDimensionPixelSizeOrNull(R.styleable.CircleLayout_radiusSize)
            val attrRadiusPercent = getFloatOrNull(R.styleable.CircleLayout_radiusPercent)
            val attrSeparationAngle = getRadiansOrNull(R.styleable.CircleLayout_separationAngle)

            val radiusModeValue =
                getInt(R.styleable.CircleLayout_radiusMode, RadiusMode.FIT_CHILDREN.ordinal)
            when {
                radiusModeValue == RadiusMode.FIXED.ordinal && attrRadiusSize != null -> {
                    _radiusMode = RadiusMode.FIXED
                    _radiusSize = attrRadiusSize
                }

                radiusModeValue == RadiusMode.PERCENT.ordinal && attrRadiusPercent != null -> {
                    _radiusMode = RadiusMode.PERCENT
                    _radiusPercent = attrRadiusPercent
                }

                else -> {
                    _radiusMode = RadiusMode.FIT_CHILDREN
                }
            }

            val angleModeValue = getInt(R.styleable.CircleLayout_angleMode, AngleMode.DISTRIBUTED.ordinal)
            when {
                angleModeValue == AngleMode.FIXED.ordinal && attrSeparationAngle != null -> {
                    _angleMode = AngleMode.FIXED
                    _separationAngle = attrSeparationAngle
                }

                else -> {
                    _angleMode = AngleMode.DISTRIBUTED
                }
            }
        }
    }

    /**
     * Set the radius based on the radius mode.
     * Does not account for future resizing to fit children.
     *
     * @param widthBound [Int]: width of layout
     * @param heightBound [Int]: height of layout
     */
    private fun setRadius(widthBound: Int, heightBound: Int) {
        val minDimension = min(widthBound, heightBound)

        _radius = when (radiusMode) {
            RadiusMode.FIXED -> radiusSize!!.toFloat()
            RadiusMode.PERCENT -> minDimension * radiusPercent!! / 2
            else -> minDimension / 2f
        }
    }

    /**
     * Set the angle between children based on angle mode.
     */
    private fun setSeparationAngle() {
        if (angleMode == AngleMode.DISTRIBUTED) {
            val nonGoneChildren = children.count { !it.isGone }
            _separationAngle = (2 * Math.PI) / nonGoneChildren
        }
    }

    /**
     * Display children on screen.
     *
     * @param changed [Boolean] if this is a new size/position for this layout
     * @param left [Int] left position, relative to parent
     * @param top [Int] top position, relative to parent
     * @param right [Int] right position, relative to parent
     * @param bottom [Int] bottom position, relative to parent
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val horizontalPadding = paddingLeft + paddingRight
        val verticalPadding = paddingTop + paddingBottom
        val widthBound = right - left - horizontalPadding
        val heightBound = bottom - top - verticalPadding

        val cx = widthBound / 2
        val cy = heightBound / 2
        setRadius(widthBound, heightBound)

        val metadata = getAllChildMetadata(cx, cy, widthBound, heightBound)

        // adjust radius to fit children
        if (radiusMode == RadiusMode.FIT_CHILDREN) {
            val overflow = metadata.fold(0) { acc, childData ->
                val childOverflow = childData["overflow"] as Int
                max(acc, childOverflow)
            }

            // cannot use -= due to possibility of _radius being null
            _radius = _radius!! - overflow - 10
        }

        positionChildrenFromMetadata(metadata, cx, cy)
    }

    /**
     * Gather metadata for all children that will take up space in the layout. View which are GONE are not included
     *
     * @param cx [Int]: center x position of circle
     * @param cy [Int]: center y position of circle
     * @param widthBound [Int]: width of layout, used to calculate overflow
     * @param heightBound [Int]: height of layout, used to calculate overflow
     * @return [List]: metadata for all children that are not GONE
     */
    private fun getAllChildMetadata(cx: Int, cy: Int, widthBound: Int, heightBound: Int): List<Map<String, Any>> {
        setSeparationAngle()

        // shift by 90 to put first child at top of circle
        var currentAngle = startAngle - Math.toRadians(90.0)
        val metadata: MutableList<Map<String, Any>> = mutableListOf()

        repeat(childCount) { index ->
            val child = getChildAt(index)

            if (!child.isGone) {
                val childData = getMetadataForSingleChild(index, cx, cy, currentAngle, widthBound, heightBound)
                metadata.add(childData)

                currentAngle += separationAngle
            }
        }

        return metadata
    }

    /**
     * Gather metadata for a single child view. Assumes view is not GONE
     *
     * @param index [Int]: index of view
     * @param cx [Int]: center x position of circle
     * @param cy [Int]: center y position of circle
     * @param angle [Double]: angle where view should be positioned, in radians
     * @param widthBound [Int]: width of layout, used to calculate overflow
     * @param heightBound [Int]: height of layout, used to calculate overflow
     * @return [Map]: metadata about the child view, including information about overflow past layout bounds
     */
    private fun getMetadataForSingleChild(
        index: Int,
        cx: Int,
        cy: Int,
        angle: Double,
        widthBound: Int,
        heightBound: Int,
    ): Map<String, Any> {
        val child = getChildAt(index)
        val x = (cos(angle) * radius + cx).toInt()
        val y = (sin(angle) * radius + cy).toInt()

        val widthSpec = MeasureSpec.makeMeasureSpec(widthBound, MeasureSpec.AT_MOST)
        val heightSpec = MeasureSpec.makeMeasureSpec(heightBound, MeasureSpec.AT_MOST)
        child.measure(widthSpec, heightSpec)

        val childLeft: Int = x - child.measuredWidth / 2
        val childTop = y - child.measuredHeight / 2
        val childRight = x + child.measuredWidth / 2
        val childBottom = y + child.measuredHeight / 2

        val childOverflow = getChildOverflow(childLeft, childTop, childRight, childBottom, widthBound, heightBound)

        return mapOf(
            "index" to index,
            "angle" to angle,
            "overflow" to childOverflow,
        )
    }

    /**
     * Use the data for each child, as well as the updated radius, to position children using the `layout` method.
     *
     * @param metadata [List]: list with metadata about each child to display, including angle and index of child
     * @param cx [Int]: x position for the center of the circle
     * @param cy [Int]: y position for the center of the circle
     */
    private fun positionChildrenFromMetadata(metadata: List<Map<String, Any>>, cx: Int, cy: Int) {
        metadata.forEach { childMetadata ->
            val angle = childMetadata["angle"] as Double

            // position must be recalculated because radius may have changed
            val x = (cos(angle) * radius + cx).toInt()
            val y = (sin(angle) * radius + cy).toInt()

            val index = childMetadata["index"] as Int
            val child = getChildAt(index)

            val childLeft = x - child.measuredWidth / 2
            val childTop = y - child.measuredHeight / 2
            val childRight = x + child.measuredWidth / 2
            val childBottom = y + child.measuredHeight / 2

            child.layout(childLeft, childTop, childRight, childBottom)
        }
    }

    /**
     * Find the amount that a child view overflows past the bounds of the layout
     *
     * @param childLeft [Int]: left border of child
     * @param childTop [Int]: top border of child
     * @param childRight [Int]: right border of child
     * @param childBottom [Int]: bottom border of child
     * @param widthBound [Int]: width of layout
     * @param heightBound [Int]: height of layout
     * @return [Int]: number of pixels of overflow past the allowed bounds, or 0 if there is none
     */
    private fun getChildOverflow(
        childLeft: Int,
        childTop: Int,
        childRight: Int,
        childBottom: Int,
        widthBound: Int,
        heightBound: Int,
    ): Int {
        return listOf(
            simpleIf(childLeft < 0, abs(childLeft), 0),
            simpleIf(childTop < 0, abs(childTop), 0),
            simpleIf(childRight > widthBound, childRight - widthBound, 0),
            simpleIf(childBottom > heightBound, childBottom - heightBound, 0),
        ).maxOrNull() ?: 0
    }
}
