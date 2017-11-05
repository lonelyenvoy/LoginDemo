package ink.envoy.logindemo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ink.envoy.logindemo.util.MD5Hasher;
import ink.envoy.logindemo.util.UsersDatabaseHelper;

public class SignUpActivity extends AppCompatActivity {

    private UsersDatabaseHelper usersDatabaseHelper;

    private Button signInButton;
    private Button cancelButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initialize();
    }

    private void initialize() {
        bindViews();

        usersDatabaseHelper = new UsersDatabaseHelper(getApplicationContext());

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // reset errors
                usernameEditText.setError(null);
                passwordEditText.setError(null);

                if (checkValidity() && executeSignUp()) {
                    Toast.makeText(
                            getApplicationContext(),
                            getString(R.string.hint_sign_up_successful),
                            Toast.LENGTH_SHORT).show();
                    SignUpActivity.this.finish();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpActivity.this.finish();
            }
        });

        // init gestures
        final ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.SignUpActivityLayout);
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

                // 向右手势
                if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY){
                    SignUpActivity.this.finish();
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
        repeatPasswordEditText = (EditText) findViewById(R.id.repeatPasswordEditText);
    }

    private boolean checkValidity() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String repeatPassword = repeatPasswordEditText.getText().toString();
        // check username
        if (username.equals("")) {
            usernameEditText.setError(getString(R.string.error_field_required));
            usernameEditText.requestFocus();
            return false;
        } else if (username.length() < 3) {
            usernameEditText.setError(getString(R.string.error_invalid_username_too_short));
            usernameEditText.requestFocus();
            return false;
        } else if (username.length() > 20) {
            usernameEditText.setError(getString(R.string.error_invalid_username_too_long));
            usernameEditText.requestFocus();
            return false;
        } else {
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
        // check password
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
        // check repeat password
        if (repeatPassword.equals("")) {
            repeatPasswordEditText.setError(getString(R.string.error_field_required));
            repeatPasswordEditText.requestFocus();
            return false;
        } else if (!repeatPassword.equals(password)) {
            repeatPasswordEditText.setError(getString(R.string.error_inconsistent_passwords));
            repeatPasswordEditText.requestFocus();
            return false;
        }
        return true;
    }

    private boolean executeSignUp() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        SQLiteDatabase db = usersDatabaseHelper.getWritableDatabase();
        Cursor cursor = db.query("users", null, "username=?", new String[]{username}, null, null, null);
        try {
            if (cursor.moveToNext()) { // username already exists
                usernameEditText.setError(getString(R.string.error_sign_up_failed_username_exists));
                usernameEditText.requestFocus();
                return false;
            } else {
                long time = System.currentTimeMillis();
                ContentValues values = new ContentValues();
                values.put("username", username);
                values.put("password", MD5Hasher.hash(password));
                values.put("isAdmin", 0);
                values.put("createdAt", time);
                values.put("updatedAt", time);
                db.insert("users", null, values);
                return true;
            }
        } finally {
            cursor.close();
            db.close();
        }
    }
}
