package dev.amin.curvybarlibrary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.RelativeLayout
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CurvyBottomNavigation @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    /* The Path which is used to Draw the Navigation Container */
    private val path = Path()

    /* The Path which is used to Draw the Navigation Stroke */
    private val strokePath = Path()

    /* This is used to Fill the Navigation Container Path */
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            style = Paint.Style.FILL
            color = ResourcesCompat.getColor(resources, android.R.color.white, null)
        }

    /* This is used to Stroke on Container */
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        .apply {
            color = ResourcesCompat.getColor(resources, R.color.colorBlackTransparent, null)
        }

    /* The Radius of bottom navigation corners, this was a failed design idea tbh :) */
    private val borderRadius =
        0f //TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics)

    /* The Width of the FAB in the middle, default is 56f */
    private val fabWidth =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56f, resources.displayMetrics)

    private val fabMarginBottom =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36f, resources.displayMetrics)

    /* Height of the NavigationBar, which is used also for
        the starting point of the paints  */
    private val barHeight = context.resources.getDimension(R.dimen.navBarHeight)

    /* Radius of the FAB, used in Point Calculations */
    private val fabRadius = fabWidth / 2

    /* Curve Points */
    private val firstCurveStartPoint = PointF()
    private val firstCurveControlPointA = PointF()
    private val firstCurveEndPoint = PointF()
    private val firstCurveControlPointB = PointF()
    private val secondCurveStartPoint = PointF()
    private val secondCurveControlPointA = PointF()
    private val secondCurveEndPoint = PointF()
    private val secondCurveControlPointB = PointF()

    fun init() {

        // Set the exact height for View to be neat!
        val params = layoutParams

        // Height = barHeight - fabHeight + marginBottom
        params.height = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            barHeight - fabWidth + fabMarginBottom,
            resources.displayMetrics
        ).toInt()

        // Stretch it all the way across
        params.width = LayoutParams.MATCH_PARENT

        layoutParams = params

        // I like my views clean brudda
        setBackgroundColor(Color.TRANSPARENT)

        /* I had a very funny issue of placing this code in onSizeChanged :D,
            Took me a nice 20 minutes to find out */
        makeViews()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Draw the whitish background
        canvas?.drawPath(path, backgroundPaint)

        // Draw the stroke
        canvas?.drawPath(strokePath, strokePaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        /* Points are all the anchor points
            used to draw the paths */
        calculatePoints()

        /* Makes the Path which is used for
            drawing the background */
        initPath()

        /* Makes the Path which is used to draw
            the stroke, I should implement custom elevation some day :)*/
        initStrokePath()

        // Rise from the ashes
        invalidate()
    }

    /***
     * This guy just creates and then
     * adds the NavigationBar and
     * the FloatingActionButton */
    private fun makeViews() {

        /* Remove in case of force refresh */
        removeAllViews()

        addNavigationBar()

        addFab()
    }

    private fun addNavigationBar() {

        val navigationBar = BottomNavigationView(context)

        val params = LayoutParams(
            LayoutParams.MATCH_PARENT,
            barHeight.toInt()
        )

        params.addRule(ALIGN_PARENT_START, TRUE)
        params.addRule(ALIGN_PARENT_BOTTOM, TRUE)

        navigationBar.apply {

            setBackgroundColor(Color.TRANSPARENT)
            layoutParams = params
            translationZ = 1f
        }

        addView(navigationBar, params)

        invalidate()
    }

    private fun addFab() {

        val fab = FloatingActionButton(context)

        val params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )

        params.bottomMargin = fabMarginBottom.toInt()

        params.addRule(CENTER_HORIZONTAL, TRUE)
        params.addRule(ALIGN_PARENT_BOTTOM, TRUE)

        fab.apply {

            layoutParams = params
            translationZ = 2f
            size = FloatingActionButton.SIZE_NORMAL

            setOnClickListener {

            }
        }

        addView(fab, params)

        invalidate()
    }

    private fun calculatePoints() {

        val yStartingPoint = height - barHeight

        firstCurveStartPoint.apply {
            x = (width / 2) - fabWidth - fabRadius / 2
            y = yStartingPoint
        }

        firstCurveControlPointA.apply {
            x = firstCurveStartPoint.x + fabRadius + (fabRadius / 4)
            y = firstCurveStartPoint.y
        }

        firstCurveEndPoint.apply {
            x = width / 2f
            y = yStartingPoint + (fabWidth - fabRadius / 2)
        }

        firstCurveControlPointB.apply {
            x = firstCurveEndPoint.x - fabRadius - fabRadius / 3
            y = firstCurveEndPoint.y
        }

        secondCurveStartPoint.apply {
            x = firstCurveEndPoint.x
            y = firstCurveEndPoint.y // height of the end Point
        }

        secondCurveControlPointA.apply {
            x = secondCurveStartPoint.x + fabRadius + fabRadius / 3
            y = secondCurveStartPoint.y
        }

        secondCurveEndPoint.apply {
            x = width / 2f + fabWidth + fabRadius / 2
            y = yStartingPoint
        }

        secondCurveControlPointB.apply {
            x = secondCurveEndPoint.x - fabRadius - fabRadius / 4
            y = secondCurveEndPoint.y
        }
    }

    private fun initStrokePath() {

        val shadowHeight =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, .5f, resources.displayMetrics)

        /* Cubic Quest Begins */

        strokePath.moveTo(0f, firstCurveStartPoint.y)

        strokePath.lineTo(firstCurveStartPoint.x, firstCurveStartPoint.y)

        // Control Point A, Control Point B and the Target Point
        strokePath.cubicTo(
            firstCurveControlPointA.x, firstCurveControlPointA.y,
            firstCurveControlPointB.x, firstCurveControlPointB.y,
            firstCurveEndPoint.x, firstCurveEndPoint.y
        )

        strokePath.cubicTo(
            secondCurveControlPointA.x, secondCurveControlPointA.y,
            secondCurveControlPointB.x, secondCurveControlPointB.y,
            secondCurveEndPoint.x, secondCurveEndPoint.y
        )

        strokePath.lineTo(width - borderRadius, firstCurveStartPoint.y)

        // Now let's go vertically down by a small percentage

        strokePath.lineTo(width - borderRadius, firstCurveStartPoint.y + shadowHeight)

        strokePath.lineTo(secondCurveEndPoint.x, secondCurveEndPoint.y + shadowHeight)

        strokePath.cubicTo(
            secondCurveControlPointB.x, secondCurveControlPointB.y + shadowHeight,
            secondCurveControlPointA.x, secondCurveControlPointA.y + shadowHeight,
            secondCurveStartPoint.x, secondCurveStartPoint.y + shadowHeight
        )

        strokePath.cubicTo(
            firstCurveControlPointB.x, firstCurveControlPointB.y + shadowHeight,
            firstCurveControlPointA.x, firstCurveControlPointA.y + shadowHeight,
            firstCurveStartPoint.x, firstCurveStartPoint.y + shadowHeight
        )

        strokePath.lineTo(0f, firstCurveStartPoint.y + shadowHeight)

        /* End of Cubic Hunt era */

        strokePath.close()
    }

    private fun initPath() {

        /* Cubic Quest Begins */

        path.moveTo(0f, firstCurveStartPoint.y)

        //path.quadTo(0f, 0f, borderRadius, 0f)

        path.lineTo(firstCurveStartPoint.x, firstCurveStartPoint.y)

        // Control Point A, Control Point B and the Target Point
        path.cubicTo(
            firstCurveControlPointA.x, firstCurveControlPointA.y,
            firstCurveControlPointB.x, firstCurveControlPointB.y,
            firstCurveEndPoint.x, firstCurveEndPoint.y
        )

        path.cubicTo(
            secondCurveControlPointA.x, secondCurveControlPointA.y,
            secondCurveControlPointB.x, secondCurveControlPointB.y,
            secondCurveEndPoint.x, secondCurveEndPoint.y
        )

        path.lineTo(width - borderRadius, firstCurveStartPoint.y)

        /* End of Cubic Hunt era */

        path.quadTo(width.toFloat(), 0f, width.toFloat(), borderRadius)

        path.lineTo(width.toFloat(), height - borderRadius)

        path.quadTo(width.toFloat(), height.toFloat(), width - borderRadius, height.toFloat())

        path.lineTo(borderRadius, height.toFloat())

        path.quadTo(0f, height.toFloat(), 0f, height - borderRadius)

        path.close()
    }
}