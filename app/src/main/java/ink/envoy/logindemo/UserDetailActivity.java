package ink.envoy.logindemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.javatuples.Pair;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import ink.envoy.logindemo.util.UsersDatabaseHelper;

public class UserDetailActivity extends AppCompatActivity {

    private enum CurrentUserRole {
        UNKNOWN,
        ADMIN,
        NORMAL
    }
    private enum LoadDataStatus {
        IS_ADMIN,
        IS_NORMALUSER
    }

    private static final Map<Integer, String> dbFieldMap = new HashMap<Integer, String>() {{
        put(R.string.field_user_detail_activity_id, "_id");
        put(R.string.field_user_detail_activity_username, "username");
        put(R.string.field_user_detail_activity_password, "password");
        put(R.string.field_user_detail_activity_role, "isAdmin");
        put(R.string.field_user_detail_activity_sign_up_time, "createdAt");
        put(R.string.field_user_detail_activity_last_update_time, "updatedAt");
    }};

    private static final int[] detailFieldsInAdminView = {
            R.string.field_user_detail_activity_id,
            R.string.field_user_detail_activity_username,
            R.string.field_user_detail_activity_password,
            R.string.field_user_detail_activity_role,
            R.string.field_user_detail_activity_sign_up_time,
            R.string.field_user_detail_activity_last_update_time
    };

    private static final int[] detailFieldsInNormalView = {
            R.string.field_user_detail_activity_id,
            R.string.field_user_detail_activity_username,
            R.string.field_user_detail_activity_role,
            R.string.field_user_detail_activity_sign_up_time,
    };

    private static final Map<CurrentUserRole, int[]> userRoleFieldMap
            = new HashMap<CurrentUserRole, int[]>() {{
        put(CurrentUserRole.ADMIN, detailFieldsInAdminView);
        put(CurrentUserRole.NORMAL, detailFieldsInNormalView);
    }};

    private UsersDatabaseHelper usersDatabaseHelper;
    private TableView userDetailTableView;
    private Button removeUserButton;

    private CurrentUserRole currentLoginUserRole;
    private String currentUserId;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        initialize();
    }

    private void initialize() {
        usersDatabaseHelper = new UsersDatabaseHelper(getApplicationContext());
        Intent intent = getIntent();
        currentLoginUserRole = intent.getStringExtra("currentLoginUserRole").equals("ADMIN")
                ? CurrentUserRole.ADMIN
                : CurrentUserRole.NORMAL;
        currentUserId = intent.getStringExtra("id");
        retrieveUserInfoById(currentUserId);
        bindViews();
        LoadDataStatus status = loadData();
        if (status == LoadDataStatus.IS_ADMIN|| currentLoginUserRole != CurrentUserRole.ADMIN) {
            removeUserButton.setVisibility(View.INVISIBLE);
        }

        removeUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(UserDetailActivity.this)
                        .setMessage(getString(R.string.hint_are_you_sure_to_remove_user))
                        .setPositiveButton(getString(R.string.action_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeUserById(currentUserId);

                                Intent intent = new Intent();
                                intent.putExtra("removedUser", currentUserId);
                                setResult(1, intent);
                                UserDetailActivity.this.finish();
                            }
                        }).setNegativeButton(getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                }).show();
            }
        });
    }

    private void removeUserById(String id) {
        SQLiteDatabase db = usersDatabaseHelper.getWritableDatabase();
        db.delete("users", "_id=?", new String[] { id });
        db.close();
    }

    private void retrieveUserInfoById(String id) {
        SQLiteDatabase db = usersDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "users",
                new String[] { dbFieldMap.get(R.string.field_user_detail_activity_username) },
                "_id=?",
                new String[] { id },
                null,
                null,
                null);
        if (cursor.moveToNext()) {
            currentUsername = cursor.getString(0);
        } else {
            throw new RuntimeException("User id does not exist");
        }
        cursor.close();
        db.close();
    }

    private void bindViews() {
        userDetailTableView = (TableView) findViewById(R.id.userDetailTableView);
        removeUserButton = (Button) findViewById(R.id.removeUserButton);
    }

    private LoadDataStatus loadData() {
        boolean isAdmin = false;
        SQLiteDatabase db = usersDatabaseHelper.getReadableDatabase();
        List<String> requiredDBFieldsList = new LinkedList<>();
        int [] arr = userRoleFieldMap.get(CurrentUserRole.ADMIN);
        for (int key : userRoleFieldMap.get(currentLoginUserRole)) {
            requiredDBFieldsList.add(dbFieldMap.get(key));
        }
        Cursor cursor = db.query(
                "users",
                requiredDBFieldsList.toArray(new String[requiredDBFieldsList.size()]),
                "username=?",
                new String[] { currentUsername },
                null,
                null,
                null);
        List<String[]> results = new LinkedList<>();
        while (cursor.moveToNext()) {
            List<Pair<String, String>> singleUserDetail = new LinkedList<>();
            int[] fields = userRoleFieldMap.get(currentLoginUserRole);
            for (int i = 0; i < fields.length; ++i) {
                int field = fields[i];
                switch (field) {
                    case R.string.field_user_detail_activity_id:
                        singleUserDetail.add(new Pair<>(getString(field), cursor.getInt(i) + ""));
                        break;
                    case R.string.field_user_detail_activity_username:
                    case R.string.field_user_detail_activity_password:
                        singleUserDetail.add(new Pair<>(getString(field), cursor.getString(i)));
                        break;
                    case R.string.field_user_detail_activity_role:
                        isAdmin = cursor.getInt(i) == 1;
                        singleUserDetail.add(new Pair<>(getString(field), getString(
                                isAdmin
                                ? R.string.hint_user_detail_activity_isAdmin_true
                                : R.string.hint_user_detail_activity_isAdmin_false)));
                        break;
                    case R.string.field_user_detail_activity_sign_up_time:
                    case R.string.field_user_detail_activity_last_update_time:
                        singleUserDetail.add(new Pair<>(getString(field), SimpleDateFormat
                                .getDateTimeInstance().format(new Date(cursor.getLong(i)))));
                        break;
                }
            }
            for (Pair<String, String> userDetailItem : singleUserDetail) {
                results.add(new String[] { userDetailItem.getValue0(), userDetailItem.getValue1() });
            }
        }
        cursor.close();
        db.close();
        userDetailTableView.setHeaderVisible(false);
        userDetailTableView.setDataAdapter(new SimpleTableDataAdapter(getApplicationContext(), results));
        TableColumnWeightModel columnModel = new TableColumnWeightModel(2);
        columnModel.setColumnWeight(0, 2);
        columnModel.setColumnWeight(1, 3);
        userDetailTableView.setColumnModel(columnModel);
        return isAdmin ? LoadDataStatus.IS_ADMIN : LoadDataStatus.IS_NORMALUSER;
    }
}
