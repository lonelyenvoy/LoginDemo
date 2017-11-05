package ink.envoy.logindemo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import ink.envoy.logindemo.util.UsersDatabaseHelper;

public class AdminActivity extends AppCompatActivity {

    private class DataModel {
        String id;
        String username;
        String password;
        String isAdmin;
        String createdAt;
    }

    private UsersDatabaseHelper usersDatabaseHelper;
    private TableView userTableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        initialize();
    }

    private void initialize() {
        usersDatabaseHelper = new UsersDatabaseHelper(getApplicationContext());
        bindViews();
        loadData();

        userTableView.addDataClickListener(new TableDataClickListener<String[]>() {
            @Override
            public void onDataClicked(int rowIndex, String[] clickedData) {
                Intent intent = new Intent(AdminActivity.this, UserDetailActivity.class);
                intent.putExtra("id", clickedData[0]);
                intent.putExtra("currentLoginUserRole", "ADMIN");
                startActivityForResult(intent, 1);
            }
        });
    }

    private void bindViews() {
        userTableView = (TableView) findViewById(R.id.userTableView);
    }

    private void loadData() {
        SQLiteDatabase db = usersDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "users",
                new String[] {"_id", "username", "password", "isAdmin", "createdAt" },
                "",
                null,
                null,
                null,
                null);
        List<String[]> results = new LinkedList<>();
        while (cursor.moveToNext()) {
            results.add(new String[] {
                    cursor.getInt(0) + "", // id
                    cursor.getString(1), // username
                    cursor.getString(2), // password
                    cursor.getInt(3) == 1 ? "√" : "", // isAdmin
                    SimpleDateFormat.getDateTimeInstance().format(new Date(cursor.getLong(4))) // createdAt
            });
        }
        cursor.close();
        db.close();
        userTableView.setHeaderAdapter(new SimpleTableHeaderAdapter(getApplicationContext(),
                "#",
                getString(R.string.hint_admin_activity_table_header_username),
                getString(R.string.hint_admin_activity_table_header_password),
                getString(R.string.hint_admin_activity_table_header_isAdmin),
                getString(R.string.hint_admin_activity_table_header_createdAt)));
        userTableView.setDataAdapter(new SimpleTableDataAdapter(getApplicationContext(), results));
        TableColumnWeightModel columnModel = new TableColumnWeightModel(5);
        columnModel.setColumnWeight(0, 2);
        columnModel.setColumnWeight(1, 4);
        columnModel.setColumnWeight(2, 5);
        columnModel.setColumnWeight(3, 3);
        columnModel.setColumnWeight(4, 7);
        userTableView.setColumnModel(columnModel);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            loadData();
        }
    }
}
