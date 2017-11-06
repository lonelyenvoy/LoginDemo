package ink.envoy.logindemo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import ink.envoy.logindemo.util.Counter;
import ink.envoy.logindemo.util.MD5Hasher;
import ink.envoy.logindemo.util.UsersDatabaseHelper;

public class MainActivity extends AppCompatActivity {

    final int MAX_ATTEMPTS = 3;

    private UsersDatabaseHelper usersDatabaseHelper;

    private Counter counter;
    private Button signInButton;
    private Button cancelButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView attemptsTextView;
    private TextView hintTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {
        bindViews();

        usersDatabaseHelper = new UsersDatabaseHelper(getApplicationContext());

        counter = new Counter(MAX_ATTEMPTS);
        updateAttemptsTextView(counter.getValue());
        hintTextView.setVisibility(View.INVISIBLE);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reset errors
                usernameEditText.setError(null);
                passwordEditText.setError(null);

                if (checkValidity()) {
                    executeLogin();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.finish();
            }
        });

        // init gestures
        final ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.mainActivityLayout);
        final GestureDetector gestureDetector = new GestureDetector(getApplicationContext(),
                new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocitY) {
                final int FLING_MIN_DISTANCE = 100;
                final int FLING_MIN_VELOCITY = 200;

                // 向左手势
                if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
                    clearEditTexts();
                    startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                }
                // 向右手势
                else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
                    // do nothing
                }

                return false;
            }
        });
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }
        });
        layout.setLongClickable(true);
    }

    private void bindViews() {
        signInButton = (Button) findViewById(R.id.signInButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        usernameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        attemptsTextView = (TextView) findViewById(R.id.attemptsTextView);
        hintTextView = (TextView) findViewById(R.id.hintTextView);
    }

    private LoginAuthenticationStatus authenticate(String username, String password) {
        SQLiteDatabase db = usersDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query("users", new String[] { "username", "password", "isAdmin" },
                "username=?", new String[]{ username }, null, null, null);
        try {
            if (cursor.moveToNext()) { // found
                String realPassword = cursor.getString(1);
                boolean isAdmin = cursor.getInt(2) == 1;
                return MD5Hasher.hash(password).equals(realPassword)
                        ? (isAdmin
                            ? LoginAuthenticationStatus.SUCCESS_ADMIN
                            : LoginAuthenticationStatus.SUCCESS_NORMAL)
                        : LoginAuthenticationStatus.WRONG_PASSWORD;
            } else {
                return LoginAuthenticationStatus.USERNAME_NOT_EXISTS;
            }
        } finally {
            cursor.close();
            db.close();
        }
    }

    private void clearEditTexts() {
        usernameEditText.setText("");
        passwordEditText.setText("");
    }

    private void updateAttemptsTextView(int attempts) {
        attemptsTextView.setText(attempts + " " + getString(R.string.hint_attempts));
    }

    private boolean checkValidity() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (username.equals("")) {
            usernameEditText.setError(getString(R.string.error_field_required));
            usernameEditText.requestFocus();
            return false;
        } else {
            // check username
            boolean usernameOK = true;
            for (int i = 0, len = username.length(); i < len; ++i) {
                char c = username.charAt(i);
                if (!((c >= 65 && c <= 90)
                        || (c >= 97 && c <= 122)
                        || (c >= 48 && c <= 57))) {
                    usernameOK = false;
                    break;
                }
            }
            if (!usernameOK) {
                usernameEditText.setError(getString(R.string.error_invalid_username));
                usernameEditText.requestFocus();
                return false;
            }
        }
        if (password.equals("")) {
            passwordEditText.setError(getString(R.string.error_field_required));
            passwordEditText.requestFocus();
            return false;
        } else if (password.length() < 4) {
            passwordEditText.setError(getString(R.string.error_invalid_password_too_short));
            passwordEditText.requestFocus();
            return false;
        } else if (password.length() > 20) {
            passwordEditText.setError(getString(R.string.error_invalid_password_too_long));
            passwordEditText.requestFocus();
            return false;
        }
        return true;
    }

    private void executeLogin() {
        // make keyboard disappear
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        LoginAuthenticationStatus status = authenticate(username, password);
        switch (status) {
            case SUCCESS_ADMIN:
                clearEditTexts();
                updateAttemptsTextView(counter.reset());
                startActivity(new Intent(getApplicationContext(), AdminActivity.class));
                break;
            case SUCCESS_NORMAL:
                clearEditTexts();
                //Toast.makeText(getApplicationContext(), getString(R.string.hint_login_successful), Toast.LENGTH_SHORT).show();
                updateAttemptsTextView(counter.reset());
                Intent intent = new Intent(getApplicationContext(), UserDetailActivity.class);
                intent.putExtra("id", getIdByUsername(username) + "");
                intent.putExtra("currentLoginUserRole", "NORMAL");
                startActivity(intent);
                break;
            case USERNAME_NOT_EXISTS:
                passwordEditText.setText("");
                usernameEditText.setError(getString(R.string.error_invalid_username_not_exists));
                usernameEditText.requestFocus();
                break;
            case WRONG_PASSWORD:
                passwordEditText.setText("");
                passwordEditText.setError(getString(R.string.error_incorrect_password));
                passwordEditText.requestFocus();

                int currentAttempts = counter.decrease();
                updateAttemptsTextView(currentAttempts);
                if (currentAttempts == 0) {
                    attemptsTextView.setBackgroundColor(Color.RED);
                    signInButton.setEnabled(false);
                    new SignInReenablecheduler().run();
                }
                break;
        }
    }

    private int getIdByUsername(String username) {
        SQLiteDatabase db = usersDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query("users", new String[] { "_id" }, "username=?",
                new String[]{ username }, null, null, null);
        try {
            if (cursor.moveToNext()) {
                return cursor.getInt(0);
            } else {
                throw new RuntimeException("User Id does not exist");
            }
        } finally {
            cursor.close();
            db.close();
        }
    }

    private class SignInReenablecheduler {

        final int REENABLE_DELAY = 10;

        private Timer timer = new Timer();
        private Counter counter = new Counter(REENABLE_DELAY);

        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        MainActivity.this.hintTextView.setVisibility(View.VISIBLE);
                        MainActivity.this.hintTextView.setText(
                                getString(R.string.hint_sign_in_reenable_countdown_front)
                                        + " "
                                        + counter.getValue()
                                        + " "
                                        + getString(R.string.hint_sign_in_reenable_countdown_rear));
                        break;
                    case 2:
                        MainActivity.this.hintTextView.setVisibility(View.INVISIBLE);
                        MainActivity.this.counter.reset();
                        MainActivity.this.updateAttemptsTextView(MainActivity.this.counter.getValue());
                        MainActivity.this.attemptsTextView.setBackgroundColor(Color.TRANSPARENT);
                        MainActivity.this.signInButton.setEnabled(true);

                        destroy();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        void run() {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(counter.decrease() > 0 ? 1 : 2);
                }
            }, 0, 1000);
        }

        private void destroy() {
            timer.cancel();
            timer = null;
            handler = null;
            counter = null;
        }
    }

    private enum LoginAuthenticationStatus {
        UNKNOWN,
        SUCCESS_ADMIN,
        SUCCESS_NORMAL,
        USERNAME_NOT_EXISTS,
        WRONG_PASSWORD
    }
}
