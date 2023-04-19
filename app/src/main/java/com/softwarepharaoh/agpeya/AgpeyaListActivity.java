package com.softwarepharaoh.agpeya;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.AbsListView.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class AgpeyaListActivity extends AppCompatActivity {

    public Typeface droid;
    private ExpandableListView expandableList;

    public class ListAdapter extends BaseExpandableListAdapter {
        private final String[][] children;
        private final String[] groups;

        public ListAdapter() {
            this.groups = new String[]{AgpeyaListActivity.this.getResources().getString(R.string.hours_prayers), AgpeyaListActivity.this.getResources().getString(R.string.midnight_prayers), AgpeyaListActivity.this.getResources().getString(R.string.other_prayers)};
            this.children = new String[][]{AgpeyaListActivity.this.getResources().getStringArray(R.array.prayers_array), AgpeyaListActivity.this.getResources().getStringArray(R.array.midnight_prayers_array), AgpeyaListActivity.this.getResources().getStringArray(R.array.other_prayers_array)};
        }

        public Object getChild(int groupPosition, int childPosition) {
            return this.children[groupPosition][childPosition];
        }

        public long getChildId(int groupPosition, int childPosition) {
            return (long) childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return this.children[groupPosition].length;
        }

//        public TextView getGenericView() {
//            LayoutParams lp = new LayoutParams(-2, 64);
//            TextView textView = new TextView(AgpeyaListActivity.this);
//            textView.setLayoutParams(lp);
//            return textView;
//        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView textView = (TextView) ((LayoutInflater) AgpeyaListActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.prayer_row, null).findViewById(R.id.prayer);
            textView.setGravity(5);
            textView.setText(getChild(groupPosition, childPosition).toString());
            textView.setTypeface(AgpeyaListActivity.this.droid);
            return textView;
        }

        public Object getGroup(int groupPosition) {
            return this.groups[groupPosition];
        }

        public int getGroupCount() {
            return this.groups.length;
        }

        public long getGroupId(int groupPosition) {
            return (long) groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView textView = (TextView) ((LayoutInflater) AgpeyaListActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.prayer_row, null).findViewById(R.id.prayer);
            textView.setText(getGroup(groupPosition).toString());
            textView.setTypeface(AgpeyaListActivity.this.droid);
            return textView;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agpeya_list);

        this.droid = Typeface.createFromAsset(getAssets(), "DroidNaskh-Bold.ttf");

        this.expandableList = findViewById(R.id.expandableList);
        this.expandableList.setAdapter(new ListAdapter());
        this.expandableList.expandGroup(0);
        onclickHandler();
    }

    private void onclickHandler() {
        this.expandableList.setOnChildClickListener(new OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent i = new Intent(AgpeyaListActivity.this.getApplicationContext(), PrayerActivity.class);
                i.putExtra("parentId", groupPosition);
                i.putExtra("Id", childPosition);
                AgpeyaListActivity.this.startActivity(i);
                return false;
            }
        });
    }
}
