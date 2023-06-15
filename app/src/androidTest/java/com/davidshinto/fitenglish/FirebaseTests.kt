package com.davidshinto.fitenglish

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.davidshinto.fitenglish.utils.CategoryList
import com.davidshinto.fitenglish.utils.WordList
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class FirebaseTests {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testLoadDataFromDB() {
        val latch = CountDownLatch(1)
        activityRule.scenario.onActivity { activity ->
            activity.loadDataFromDB()
            latch.await(2, TimeUnit.SECONDS)
            assertNotNull(CategoryList.categoryList)
            assertNotNull(WordList.wordList)
            assertEquals(258, WordList.wordList.size)
        }
        latch.countDown()
    }
}