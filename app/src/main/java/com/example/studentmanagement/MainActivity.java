package com.example.studentmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import io.bloco.faker.Faker;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    StudentAdapter adapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String dataPath = getFilesDir() + "/student_data";
        db = SQLiteDatabase.openDatabase(dataPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);

        createRandomData();

        ListView listStudent = findViewById(R.id.list_students);
        adapter = new StudentAdapter(db);
        listStudent.setAdapter(adapter);

        registerForContextMenu(listStudent);
        listStudent.setLongClickable(true);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();

        final Cursor cursor = (Cursor) adapter.getItem(info.position);
        final String mssv = cursor.getString(cursor.getColumnIndex("mssv"));

        if (item.getItemId() == R.id.action_delete) {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Bạn có chắc muốn xóa?")
                    .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int r = db.delete("sinhvien",
                                    "mssv = \'" + mssv + "\';", null);

                            Cursor cs = db.rawQuery("select * from sinhvien", null);
                            adapter.setCs(cs);
                            adapter.notifyDataSetChanged();

                            if (r > 0) {
                                Toast.makeText(MainActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .setNegativeButton("Không", null)
                    .create();

            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
        }

        return super.onContextItemSelected(item);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if ((requestCode == 222) && (resultCode == RESULT_OK)) {
//            Cursor cs = db.rawQuery("select * from sinhvien", null);
//            adapter.setCs(cs);
//            adapter.notifyDataSetChanged();
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String inputStr) {
                String sqlStr = "";

                if (inputStr.equals("")) {
                    sqlStr = "select * from sinhvien";
                } else {
                    sqlStr = "select * from sinhvien where mssv like \'%" + inputStr + "%\' or hoten like \'%" + inputStr + "%\';";
                }

                Cursor cs = db.rawQuery(sqlStr, null);
                adapter.setCs(cs);

                adapter.notifyDataSetChanged();

                invalidateOptionsMenu();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String inputStr) {

                String sqlStr = "";

                if (inputStr.equals("")) {
                    sqlStr = "select * from sinhvien";
                } else {
                    sqlStr = "select * from sinhvien where mssv like \'%" + inputStr + "%\' or hoten like \'%" + inputStr + "%\';";
                }

                Cursor cs = db.rawQuery(sqlStr, null);
                adapter.setCs(cs);

                adapter.notifyDataSetChanged();
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void createRandomData() {
        db.beginTransaction();
        try {
            db.execSQL("create table sinhvien(" +
                    "mssv char(8) primary key," +
                    "hoten text," +
                    "ngaysinh date," +
                    "email text," +
                    "diachi text);");

            Faker faker = new Faker();

            for (int i = 0; i < 50; i++) {
                String mssv = "2017" + faker.number.number(4);
                String hoten = faker.name.name();
                String ngaysinh = faker.date.birthday(18, 22).toString();
                String email = faker.internet.email();
                String diachi = faker.address.city() + ", " + faker.address.country();

                ContentValues contentValues = new ContentValues();
                contentValues.put("mssv", mssv);
                contentValues.put("hoten", hoten);
                contentValues.put("ngaysinh", ngaysinh);
                contentValues.put("email", email);
                contentValues.put("diachi", diachi);

                db.insert("sinhvien", null, contentValues);
            }

            db.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    @Override
    protected void onStop() {
        db.close();
        super.onStop();
    }
}