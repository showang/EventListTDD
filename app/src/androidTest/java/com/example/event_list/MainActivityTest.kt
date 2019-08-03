package com.example.event_list

import android.app.Application
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.example.event_list.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.closeSoftKeyboard as closeKeyboard

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    val app get() = ApplicationProvider.getApplicationContext<Application>()


    @Test
    fun testCreateEvent() {
        openActionBarOverflowOrOptionsMenu(app)
        onView(withText("Clear")).perform(click())

        onView(withId(R.id.fab)).perform(click())
        closeKeyboard()
        onView(withId(R.id.titleEditText)).perform(typeText("test title"))
        closeKeyboard()
        onView(withId(R.id.descEditText)).perform(typeText("test desc"))
        closeKeyboard()
        onView(withId(R.id.confirmButton)).perform(click())

        onView(withText("test title")).check(matches(isDisplayed()))
    }

    @Test
    fun testEditEvent() {
        openActionBarOverflowOrOptionsMenu(app)
        onView(withText("Clear")).perform(click())

        onView(withId(R.id.fab)).perform(click())
        closeKeyboard()
        onView(withId(R.id.titleEditText)).perform(typeText("test title"))
        closeKeyboard()
        onView(withId(R.id.descEditText)).perform(typeText("test desc"))
        closeKeyboard()
        onView(withId(R.id.confirmButton)).perform(click())

        onView(withId(R.id.itemRecyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        onView(withId(R.id.titleEditText)).perform(clearText())
        onView(withId(R.id.titleEditText)).perform(typeText("edited title"))
        closeKeyboard()
        onView(withId(R.id.confirmButton)).perform(click())

        onView(withText("edited title")).check(matches(isDisplayed()))
    }

}