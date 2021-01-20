package com.android.car.debuggingrestrictioncontroller;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Context;
import android.content.Intent;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.android.car.debuggingrestrictioncontroller.ui.login.LoginActivity;
import com.android.car.debuggingrestrictioncontroller.ui.token.TokenActivity;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UiTest {

  private static final String TEST_EMAIL = "aa-test@google.com";
  private static final String TEST_PASSWORD = "aa-test";
  private static final String INVALID_EMAIL = "invalid_email@";
  private static final String SHORT_PASSWORD = "word";
  @Rule
  public ActivityScenarioRule<LoginActivity> activityScenarioRule =
      new ActivityScenarioRule<LoginActivity>(LoginActivity.class);

  @Before
  public void setUp() {
    Intents.init();
  }

  @After
  public void tearDown() {
    Intents.release();
  }

  @Test
  public void initialButtonStates() {
    onView(withId(R.id.login)).check(matches(isDisplayed()));
    onView(withId(R.id.login)).check(matches(not(isEnabled())));

    onView(withId(R.id.next)).check(matches(isDisplayed()));
    onView(withId(R.id.next)).check(matches(not(isEnabled())));
  }

  @Test
  public void invalidEmail() {
    Context ctx = getApplicationContext();
    onView(withId(R.id.username))
        .perform(typeText(INVALID_EMAIL), ViewActions.closeSoftKeyboard());
    onView(withId(R.id.username))
        .check(matches(hasErrorText(ctx.getString(R.string.invalid_username))));
    onView(withId(R.id.login)).check(matches(not(isEnabled())));
  }

  @Test
  public void invalidPassword() {
    Context ctx = getApplicationContext();
    onView(withId(R.id.username))
        .perform(typeText(TEST_EMAIL), ViewActions.closeSoftKeyboard());
    onView(withId(R.id.password))
        .perform(typeText(SHORT_PASSWORD), ViewActions.closeSoftKeyboard());
    onView(withId(R.id.password))
        .check(matches(hasErrorText(ctx.getString(R.string.invalid_password))));
    onView(withId(R.id.login)).check(matches(not(isEnabled())));
  }

  @Test
  public void startTokenActivity() {
    onView(withId(R.id.username))
        .perform(typeText(TEST_EMAIL), ViewActions.closeSoftKeyboard());
    onView(withId(R.id.password))
        .perform(typeText(TEST_PASSWORD), ViewActions.closeSoftKeyboard());

    onView(withId(R.id.login)).check(matches(isEnabled()));
    onView(withId(R.id.login)).perform(click());
    onView(withId(R.id.next)).check(matches(isEnabled()));
    onView(withId(R.id.next)).perform(click());

    intended(allOf(
        toPackage(getApplicationContext().getPackageName()),
        hasComponent(TokenActivity.class.getName())));
    onView(withId(R.id.agreement)).check(matches(isDisplayed()));
    onView(withId(R.id.agree)).check(matches(isDisplayed()));
    onView(withId(R.id.disagree)).check(matches(isDisplayed()));
  }

  @Test
  public void returnedFromTokenActivityOK() {
    Intent intent = new Intent(getApplicationContext(), TokenActivity.class);
    ActivityResult result = new ActivityResult(Activity.RESULT_OK, intent);
    intending(allOf(
        toPackage(getApplicationContext().getPackageName()),
        hasComponent(TokenActivity.class.getName()))).respondWith(result);

    onView(withId(R.id.username))
        .perform(typeText(TEST_EMAIL), ViewActions.closeSoftKeyboard());
    onView(withId(R.id.password))
        .perform(typeText(TEST_PASSWORD), ViewActions.closeSoftKeyboard());

    onView(withId(R.id.login)).check(matches(isEnabled()));
    onView(withId(R.id.login)).perform(click());
    onView(withId(R.id.next)).check(matches(isEnabled()));
    onView(withId(R.id.next)).perform(click());

    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.token_authorized)));
  }

  @Test
  public void returnedFromTokenActivityCancelled() {
    Intent intent = new Intent(getApplicationContext(), TokenActivity.class);
    ActivityResult result = new ActivityResult(Activity.RESULT_CANCELED, intent);
    intending(allOf(
        toPackage(getApplicationContext().getPackageName()),
        hasComponent(TokenActivity.class.getName()))).respondWith(result);

    onView(withId(R.id.username))
        .perform(typeText(TEST_EMAIL), ViewActions.closeSoftKeyboard());
    onView(withId(R.id.password))
        .perform(typeText(TEST_PASSWORD), ViewActions.closeSoftKeyboard());

    onView(withId(R.id.login)).check(matches(isEnabled()));
    onView(withId(R.id.login)).perform(click());
    onView(withId(R.id.next)).check(matches(isEnabled()));
    onView(withId(R.id.next)).perform(click());

    onView(withId(com.google.android.material.R.id.snackbar_text))
        .check(matches(withText(R.string.token_unauthorized)));
  }
}