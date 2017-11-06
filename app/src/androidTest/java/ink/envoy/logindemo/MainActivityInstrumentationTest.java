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
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.hasErrorText;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentationTest {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String TEST_USER_USERNAME = "test";
    private static final String TEST_USER_PASSWORD = "hello_world";
    private static final String NON_EXIST_USERNAME = "hahaha";
    private static final String WHATEVER = "*#@^%!!";
    private static final String SHORT_WORD = "aa";
    private static final String LONG_WORD = "aaaaaaaaaaaaaaaaaaaaa";

    private static final String ERROR_INCORRECT_PASSWORD = "This password is incorrect";
    private static final String ERROR_INVALID_USERNAME_NOT_EXISTS = "Username does not exist";
    private static final String ERROR_INVALID_USERNAME = "This username is invalid";
    private static final String ERROR_INVALID_PASSWORD_TOO_SHORT = "This password is too short";
    private static final String ERROR_INVALID_PASSWORD_TOO_LONG = "This password is too long";
    private static final String ERROR_FIELD_REQUIRED = "This field is required";

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void loginWithAdmin(){
        onView(withId(R.id.usernameEditText)).perform(typeText(ADMIN_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(ADMIN_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.userTableView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void loginWithTestUser(){
        onView(withId(R.id.usernameEditText)).perform(typeText(TEST_USER_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(TEST_USER_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.userDetailTableView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void loginWithEmptyUsername(){
        onView(withId(R.id.passwordEditText)).perform(typeText(WHATEVER), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.usernameEditText)).check(matches(hasErrorText(ERROR_FIELD_REQUIRED)));
    }

    @Test
    public void loginWithEmptyPassword(){
        onView(withId(R.id.usernameEditText)).perform(typeText(ADMIN_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.passwordEditText)).check(matches(hasErrorText(ERROR_FIELD_REQUIRED)));
    }

    @Test
    public void loginWithIncorrectPassword(){
        onView(withId(R.id.usernameEditText)).perform(typeText(ADMIN_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(WHATEVER), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.passwordEditText)).check(matches(hasErrorText(ERROR_INCORRECT_PASSWORD)));
    }

    @Test
    public void loginWithNonExistUsername(){
        onView(withId(R.id.usernameEditText)).perform(typeText(NON_EXIST_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(WHATEVER), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.usernameEditText)).check(matches(hasErrorText(ERROR_INVALID_USERNAME_NOT_EXISTS)));
    }

    @Test
    public void loginWithInvalidUsername() {
        onView(withId(R.id.usernameEditText)).perform(typeText(WHATEVER), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(WHATEVER), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.usernameEditText)).check(matches(hasErrorText(ERROR_INVALID_USERNAME)));
    }

    @Test
    public void loginWithTooShortPassword() {
        onView(withId(R.id.usernameEditText)).perform(typeText(ADMIN_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(SHORT_WORD), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.passwordEditText)).check(matches(hasErrorText(ERROR_INVALID_PASSWORD_TOO_SHORT)));
    }

    @Test
    public void loginWithTooLongPassword() {
        onView(withId(R.id.usernameEditText)).perform(typeText(ADMIN_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText(LONG_WORD), closeSoftKeyboard());
        onView(withId(R.id.signInButton)).perform(click());
        onView(withId(R.id.passwordEditText)).check(matches(hasErrorText(ERROR_INVALID_PASSWORD_TOO_LONG)));
    }

    @Test
    public void loginFailsTooManyTimes() {
        onView(withId(R.id.usernameEditText)).perform(typeText(ADMIN_USERNAME), closeSoftKeyboard());
        for (int i = 0; i < 3; ++i) {
            onView(withId(R.id.passwordEditText)).perform(typeText(WHATEVER), closeSoftKeyboard());
            onView(withId(R.id.signInButton)).perform(click());
            onView(withId(R.id.passwordEditText)).check(matches(hasErrorText(ERROR_INCORRECT_PASSWORD)));
        }
        onView(withId(R.id.hintTextView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void intentToSignUpView() {
        onView(withId(R.id.mainActivityLayout)).perform(swipeLeft());
        onView(withId(R.id.SignUpActivityLayout)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
}

