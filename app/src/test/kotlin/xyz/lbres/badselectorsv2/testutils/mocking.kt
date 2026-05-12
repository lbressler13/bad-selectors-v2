package xyz.lbres.badselectorsv2.testutils

import android.util.Log
import io.mockk.every
import io.mockk.mockkStatic

/**
 * Mock the Log class. Must be unmocked at the end of each test
 */
fun mockLog() {
    mockkStatic(Log::class)
    every { Log.v(any(), any()) } returns 1
    every { Log.d(any(), any()) } returns 1
    every { Log.i(any(), any()) } returns 1
    every { Log.w(any(), any<String>()) } returns 1
    every { Log.w(any(), any<Throwable>()) } returns 1
    every { Log.e(any(), any()) } returns 1
}
