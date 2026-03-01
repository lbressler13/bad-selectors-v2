package xyz.lbres.customview.circlelayout

import io.mockk.unmockkAll
import kotlin.test.AfterTest
import kotlin.test.Test

class CircleLayoutInitTest {

    @AfterTest
    fun cleanupMockk() {
        unmockkAll()
    }

    // all default
    @Test
    fun testDefault() {
        val mockArray = createMockTypedArray(0, 0, emptySet())
        val layout = CircleLayout(createMockContext(mockArray))
        checkMatchesDefaults(layout)
    }

    // set radius to FIT_CHILDREN, no radius properties
    @Test fun testFitChildren() = runFitChildrenTest()

    // set radius to FIXED, no radius properties
    @Test fun testFixedRadiusWithoutSize() = runFixedRadiusWithoutSizeTest()

    // set radius to PERCENT, no radius properties
    @Test fun testPercentRadiusWithoutPercent() = runPercentRadiusWithoutPercentTest()

    // set radius to FIXED, with radiusSize
    @Test fun testFixedRadiusWithSize() = runFixedRadiusWithSizeTest()

    // set radius to PERCENT, with radiusPercent
    @Test fun testPercentRadiusWithPercent() = runPercentRadiusWithPercentTest()

    // set radiusSize without radiusMode
    @Test fun testRadiusSizeWithoutRadiusMode() = runRadiusSizeWithoutRadiusModeTest()

    // set radiusPercent without radiusMode
    @Test fun testRadiusPercentWithoutRadiusMode() = runRadiusPercentWithoutRadiusModeTest()

    // set radiusPercent with incorrect radius mode
    @Test fun testRadiusPercentWithIncorrectMode() = runRadiusPercentWithIncorrectModeTest()

    // set radiusSize with incorrect radius mode
    @Test fun testRadiusSizeWithIncorrectMode() = runRadiusSizeWithIncorrectModeTest()

    // set angle to DISTRIBUTED, no angle properties
    @Test fun testDistributedAngle() = runDistributedAngleTest()

    // set angle to FIXED, no angle properties
    @Test fun testFixedAngleWithoutSeparationAngle() = runFixedAngleWithoutSeparationAngleTest()

    // set angle to FIXED, with separationAngle
    @Test fun testFixedAngleWithSeparationAngle() = runFixedAngleWithSeparationAngleTest()

    // set separationAngle without angleMode
    @Test fun testSeparationAngleWithoutAngleMode() = runSeparationAngleWithoutAngleModeTest()

    // set angle property with incorrect angle mode
    @Test fun testSeparationAngleWithIncorrectMode() = runSeparationAngleWithIncorrectModeTest()

    // set startAngle
    @Test fun testStartAngle() = runStartAngleTest()

    // angle and radius properties without angleMode or radiusMode
    @Test fun testPropertiesWithoutModes() = runPropertiesWithoutModesTest()

    // radius properties and angleMode
    @Test fun testRadiusPropertiesWithAngleMode() = runRadiusPropertiesWithAngleModeTest()

    // angle properties and radiusMode
    @Test fun testAnglePropertiesWithRadiusMode() = runAnglePropertiesWithRadiusModeTest()

    // angle and radius properties with radiusMode
    @Test fun testPercentRadiusWithSeparationAngle() = runPercentRadiusWithSeparationAngleTest()

    // angle and radius properties with angleMode
    @Test fun testFixedAngledWithRadiusSizeTest() = runFixedAngleWithRadiusSizeTest()

    // start angle and separation angle
    @Test fun testStartAngleAndSeparationAngle() = runStartAngleAndSeparationAngleTest()

    // FIXED radius and FIXED angle
    @Test fun testFixedRadiusAndFixedAngle() = runFixedRadiusAndFixedAngleTest()

    // radius mode, angle mode, and start angle
    @Test fun testAllPropertiesSet() = runAllPropertiesSetTest()
}
