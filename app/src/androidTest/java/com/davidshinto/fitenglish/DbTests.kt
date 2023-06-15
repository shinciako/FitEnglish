package com.davidshinto.fitenglish

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.davidshinto.fitenglish.db.Session
import com.davidshinto.fitenglish.db.SessionDao
import com.davidshinto.fitenglish.db.SessionDatabase
import com.davidshinto.fitenglish.utils.Mode
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import java.time.OffsetDateTime

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DbTests {
private lateinit var dao: SessionDao
    private lateinit var database: SessionDatabase
    private lateinit var offset: OffsetDateTime

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SessionDatabase::class.java
        ).build()
        dao = database.sessionDao()
        offset = OffsetDateTime.now()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertSessionTest() = runBlocking{
        val session = Session(
            id = 1,
            accuracy = 0.0,
            numberOfQuestions = 0,
            date = OffsetDateTime.now(),
            mode = Mode.Writer,
            category = "Animal",
            distance = 0
        )
        dao.insertSession(session)
        val allSessions = dao.getAllSessions()
        assertEquals(session, allSessions[0])
    }
}