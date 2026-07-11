package xyz.lbres.customview.movingview.manager

import io.mockk.unmockkAll
import xyz.lbres.testutils.mockLog
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ContinuousLinearMovementManagerTest {

    @BeforeTest
    fun setupTest() {
        mockLog()
    }

    @AfterTest
    fun cleanupTest() {
        unmockkAll()
    }

    @Test
    fun testInit() {
        // TODO
    }

    @Test
    fun testUpdatePaused() {
        // TODO
    }

    @Test
    fun testUpdateMovementSize() {
        // TODO
    }

    @Test
    fun testUpdatePosition() {
        // TODO
    }

    @Test
    fun testSetInitialPosition() {
        // TODO
    }

    @Test
    fun testSetOnMoveCallback() {
        // TODO
    }

    @Test
    fun testSetOnPauseChangedCallback() {
        // TODO
    }
}
