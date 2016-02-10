package com.capstone.innovationproject.routeguidance;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class SelectBus extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_bus);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new Adapter(this));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(SelectBus.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    public class Adapter extends BaseAdapter {

        private Context context;
        private String[] texts = {"aaa", "bbb", "ccc", "ddd", "eee", "fff", "eee", "hhh", "iii"};

        public Adapter(Context context) {
            this.context = context;
        }

        public int getCount() {
            return 9;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                tv = new TextView(context);
                tv.setLayoutParams(new GridView.LayoutParams(85, 85));
            }
            else {
                tv = (TextView) convertView;
            }

            tv.setText(texts[position]);
            return tv;
        }
    }
}
