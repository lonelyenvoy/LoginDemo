package ink.envoy.logindemo;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class SignUpActivityInstrumentationTest {

    private static final String TEST_USER_USERNAME = "test";
    private static final String TEST_USER_PASSWORD = "hello_world";
    private static final String WHATEVER = "*#@^%!!";
    private static final String SHORT_WORD = "aa";
    private static final String LONG_WORD = "aaaaaaaaaaaaaaaaaaaaa";
    private static final String CERTAIN_USERNAME = "hahaha";

    private static final String ERROR_INVALID_USERNAME = "This username is invalid";
    private static final String ERROR_INVALID_USERNAME_TOO_SHORT = "This username is too short";
    private static final String ERROR_INVALID_USERNAME_TOO_LONG = "This username is too long";
    private static final String ERROR_INVALID_PASSWORD_TOO_SHORT = "This password is too short";
    private static final String ERROR_INVALID_PASSWORD_TOO_LONG = "This password is too long";
    private static final String ERROR_INCONSISTENT_PASSWORDS = "The two passwords do not match";

    @Rule
    public ActivityTestRule<SignUpActivity> mActivityRule = new ActivityTestRule<>(SignUpActivity.class);

    @Test
    public void registerTestUsers() {
        onView(withId(R.id.usernameEditText)).perform(typeText(TEST_USER_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(TEST_USER_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.repeatPasswordEditText)).perform(typeText(TEST_USER_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.SignUpActivityLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)));
    }

    @Test
    public void registerWithTooShortUsername() {
        onView(withId(R.id.usernameEditText)).perform(typeText(SHORT_WORD), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(WHATEVER), closeSoftKeyboard());
        onView(withId(R.id.repeatPasswordEditText)).perform(typeText(WHATEVER), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.usernameEditText)).check(matches(hasErrorText(ERROR_INVALID_USERNAME_TOO_SHORT)));
    }

    @Test
    public void registerWithTooLongUsername() {
        onView(withId(R.id.usernameEditText)).perform(typeText(WHATEVER), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(WHATEVER), closeSoftKeyboard());
        onView(withId(R.id.repeatPasswordEditText)).perform(typeText(WHATEVER), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.usernameEditText)).check(matches(hasErrorText(ERROR_INVALID_USERNAME)));
    }

    @Test
    public void registerWithInvalidUsername() {
        onView(withId(R.id.usernameEditText)).perform(typeText(LONG_WORD), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(WHATEVER), closeSoftKeyboard());
        onView(withId(R.id.repeatPasswordEditText)).perform(typeText(WHATEVER), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.usernameEditText)).check(matches(hasErrorText(ERROR_INVALID_USERNAME_TOO_LONG)));
    }

    @Test
    public void registerWithInconsistentPasswords() {
        onView(withId(R.id.usernameEditText)).perform(typeText(CERTAIN_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(WHATEVER), closeSoftKeyboard());
        onView(withId(R.id.repeatPasswordEditText)).perform(typeText(CERTAIN_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.repeatPasswordEditText)).check(matches(hasErrorText(ERROR_INCONSISTENT_PASSWORDS)));
    }

    @Test
    public void registerWithTooShortPasswords() {
        onView(withId(R.id.usernameEditText)).perform(typeText(CERTAIN_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(SHORT_WORD), closeSoftKeyboard());
        onView(withId(R.id.repeatPasswordEditText)).perform(typeText(SHORT_WORD), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.passwordEditText)).check(matches(hasErrorText(ERROR_INVALID_PASSWORD_TOO_SHORT)));
    }

    @Test
    public void registerWithTooLongPasswords() {
        onView(withId(R.id.usernameEditText)).perform(typeText(CERTAIN_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(LONG_WORD), closeSoftKeyboard());
        onView(withId(R.id.repeatPasswordEditText)).perform(typeText(LONG_WORD), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.passwordEditText)).check(matches(hasErrorText(ERROR_INVALID_PASSWORD_TOO_LONG)));
    }

}
