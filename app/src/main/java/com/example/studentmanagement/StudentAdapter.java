package com.example.studentmanagement;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class StudentAdapter extends BaseAdapter {

    Cursor cs;

    public StudentAdapter(SQLiteDatabase db) {
        cs = db.rawQuery("select * from sinhvien", null);
    }

    public void setCs(Cursor cs) {
        this.cs = cs;
    }

    @Override
    public int getCount() {
        return cs.getCount();
    }

    @Override
    public Object getItem(int position) {
        cs.moveToPosition(position);
        return cs;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.textMSSV = convertView.findViewById(R.id.text_mssv);
            viewHolder.textHoten = convertView.findViewById(R.id.text_hoten);
            viewHolder.textEmail = convertView.findViewById(R.id.text_email);
            viewHolder.chkSelect = convertView.findViewById(R.id.chk_select);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        cs.moveToPosition(position);

        viewHolder.textMSSV.setText(cs.getString(cs.getColumnIndex("mssv")));
        viewHolder.textHoten.setText(cs.getString(cs.getColumnIndex("hoten")));
        viewHolder.textEmail.setText(cs.getString(cs.getColumnIndex("email")));

        return convertView;
    }

    private class ViewHolder {
        TextView textMSSV;
        TextView textHoten;
        TextView textEmail;
        CheckBox chkSelect;
    }
}
