package xyz.lbres.badselectorsv2.ui.phone.selectincorrect

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.not
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import xyz.lbres.badselectorsv2.R

@Category(Robolectric::class)
@RunWith(AndroidJUnit4::class)
class SelectIncorrectFragmentTest {
    private val mockGeneratedValues = listOf(
        listOf(7, 4, 0, 2, 5, 6, 8, 1, 3, 9),
        listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
        listOf(3, 6, 2, 7, 9, 8, 5, 4, 1, 0),
        listOf(9, 8, 7, 4, 5, 6, 2, 1, 0, 3),
        listOf(1, 3, 5, 7, 9, 0, 2, 4, 6, 8),
        listOf(5, 6, 2, 9, 1, 0, 3, 4, 8, 7),
    )

    private val generateButton = onView(withId(R.id.generateNumberButton))
    private val restartButton = onView(withId(R.id.restartButton))
}
