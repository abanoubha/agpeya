package com.abanoubhanna.agpeya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.arasthel.asyncjob.AsyncJob;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class PrayerActivity extends AppCompatActivity {

    public static final String[] MIDNIGHT_PRAYERS = {"Midnight_1", "Midnight_2", "Midnight_3"};
    public static final String[] OTHER_PRAYERS = {"contrition", "before_confession", "after_confession", "before_communion", "after_communion", "before_eating", "after_eating", "consultation", "yearly", "before_exam"};
    public static final String[] PRAYERS = {"Prime", "Terce", "Sext", "None", "Vespers", "Compline", "Viel"};

    public int f2Id;
    public ProgressDialog Progress;
    public int cureentScreen = -1;
    public Typeface droid;
    public int parentId;
    public Cursor pray;
    public String prayerHeaderText;
    public List<String> titles;

    TextView title, content;

//    public class loadPrayer extends AsyncTask<String, Integer, String> {
//        public loadPrayer() {
//        }
//
//        public String doInBackground(String... params) {
//            String SelectIn = getSelectin();
//            AgpeyaDB db = new AgpeyaDB(PrayerActivity.this);
//            db.open();
//            PrayerActivity.this.pray = db.getPrayer(SelectIn);
//            if (PrayerActivity.this.pray != null) {
//                PrayerActivity.this.pray.moveToLast();
//                while (!PrayerActivity.this.pray.isBeforeFirst()) {
//                    ScrollView prayScroll = new ScrollView(PrayerActivity.this);
//                    LinearLayout linearLayout = new LinearLayout(PrayerActivity.this);
//                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//                    if (PrayerActivity.this.pray.getString(1).length() != 0) {
//                        TextView title = new TextView(PrayerActivity.this);
//                        title.setText(PrayerActivity.this.pray.getString(1));
//                        title.setTypeface(PrayerActivity.this.droid);
//                        title.setPadding(10, 10, 10, 10);
//                        title.setTextSize(2, (float) 20.0);
//                        title.setTextColor(Color.parseColor("#87410d"));
//                        title.setGravity(17);
//                        PrayerActivity.this.titles.add(PrayerActivity.this.pray.getString(1));
//                        linearLayout.addView(title);
//                        TextView content = new TextView(PrayerActivity.this);
//                        content.setText(unescape(PrayerActivity.this.pray.getString(2)));
//                        content.setTypeface(PrayerActivity.this.droid);
//                        content.setPadding(10, 10, 15, 10);
//                        content.setTextSize(2, (float) 18);
//                        content.setTextColor(Color.BLACK);
//                        content.setGravity(5);
//                        linearLayout.addView(content);
//                    } else {
//                        TextView content2 = new TextView(PrayerActivity.this);
//                        content2.setText(unescape(PrayerActivity.this.pray.getString(2)));
//                        content2.setTypeface(PrayerActivity.this.droid);
//                        content2.setTextSize(2, (float) 20.0);
//                        content2.setPadding(10, 140, 10, 10);
//                        content2.setTextColor(Color.parseColor("#87410d"));
//                        content2.setGravity(1);
//                        PrayerActivity.this.titles.add(PrayerActivity.this.getResources().getString(R.string.prayer_start));
//                        linearLayout.addView(content2);
//                    }
//                    prayScroll.addView(linearLayout);
//                    //PrayerActivity.this.space.addView(prayScroll);
//                    PrayerActivity.this.pray.moveToPrevious();
//                }
//            }
//            PrayerActivity.this.pray.close();
//            db.close();
//            return null;
//        }
//
//        private String getSelectin() {
//            String SelectIn = "";
//            if (PrayerActivity.this.parentId == 0) {
//                String SelectIn2 = PrayerActivity.PRAYERS[PrayerActivity.this.f2Id];
//                PrayerActivity.this.prayerHeaderText = PrayerActivity.this.getResources().getStringArray(R.array.prayers_array)[PrayerActivity.this.f2Id];
//                return SelectIn2;
//            } else if (PrayerActivity.this.parentId == 1) {
//                String SelectIn3 = PrayerActivity.MIDNIGHT_PRAYERS[PrayerActivity.this.f2Id];
//                PrayerActivity.this.prayerHeaderText = PrayerActivity.this.getResources().getStringArray(R.array.midnight_prayers_array)[PrayerActivity.this.f2Id];
//                return SelectIn3;
//            } else if (PrayerActivity.this.parentId != 2) {
//                return SelectIn;
//            } else {
//                String SelectIn4 = PrayerActivity.OTHER_PRAYERS[PrayerActivity.this.f2Id];
//                PrayerActivity.this.prayerHeaderText = PrayerActivity.this.getResources().getStringArray(R.array.other_prayers_array)[PrayerActivity.this.f2Id];
//                return SelectIn4;
//            }
//        }
//
//        private String unescape(String description) {
//            return description.replaceAll("\\\\n", "\\\n");
//        }
//
//        public void onPostExecute(String result) {
//            int toScreen;
//            super.onPostExecute(result);
//            PrayerActivity.this.setTitle(PrayerActivity.this.prayerHeaderText);
//            int childsTotal = this.getChildCount() - 1;
//            if (PrayerActivity.this.cureentScreen != -1) {
//                toScreen = PrayerActivity.this.cureentScreen;
//            } else {
//                toScreen = childsTotal;
//            }
//            PrayerActivity.this.space.setCurrentScreen(toScreen);
//            PrayerActivity.this.setContentView(PrayerActivity.this.space);
//            PrayerActivity.this.Progress.dismiss();
//        }
//    }

    public void onCreate(Bundle savedInstanceState) {
        int i;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer);

        title = findViewById(R.id.title);
        content = findViewById(R.id.content);

        title.setText("test title");
        title.setTypeface(PrayerActivity.this.droid);
        title.setPadding(10, 10, 10, 10);
        title.setTextSize(2, (float) 20.0);
        title.setTextColor(Color.parseColor("#87410d"));
        title.setGravity(17);

        content.setText("test content");
        content.setTypeface(PrayerActivity.this.droid);
        content.setPadding(10, 10, 15, 10);
        content.setTextSize(2, (float) 18);
        content.setTextColor(Color.BLACK);
        content.setGravity(5);

        this.droid = Typeface.createFromAsset(getAssets(), "DroidNaskh-Bold.ttf");
        //this.titles = new ArrayList();
        Bundle extras = getIntent().getExtras();
        this.f2Id = extras != null ? extras.getInt("Id") : -1;
        if (extras != null) {
            i = extras.getInt("parentId");
        } else {
            i = -1;
        }
        this.parentId = i;
        if (this.f2Id != -1) {
            this.Progress = ProgressDialog.show(this, "", getResources().getString(R.string.wait), true);
            //new loadPrayer().execute("");

            getTitleNContentFromDB();
        }
    }

    public void getTitleNContentFromDB(){
        new AsyncJob.AsyncJobBuilder<String[]>()
                .doInBackground(new AsyncJob.AsyncAction<String[]>() {
                    @Override
                    public String[] doAsync() {
                        String SelectIn = getSelectin();
                        String[] list = new String[]{""};

                        AgpeyaDB db = new AgpeyaDB(PrayerActivity.this);
                        db.open();
                        pray = db.getPrayer(String.valueOf(SelectIn));
                        if (pray != null) {
                            pray.moveToFirst();
                            while (!pray.isLast()) {
                                if (pray.getString(0).length() != 0) {
                                    list = new String[]{pray.getString(1), pray.getString(2)};
                                    pray.close();
                                    db.close();
                                    return list;
                                }
                            }
                        }
                        return list;
                    }
                })
                .doWhenFinished(new AsyncJob.AsyncResultAction<String[]>() {
                    @Override
                    public void onResult(String[] result) {
                        Progress.dismiss();
                        title.setText(result[0]);
                        content.setText(unescape(result[1]));
                    }
                }).create().start();
    }

//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.setHeaderTitle(R.string.selectPrayer);
//        String lordsPrayer = getResources().getString(R.string.lords_prayer_db);
//        for (int i = this.titles.size() - 1; i > -1; i--) {
//            if (lordsPrayer.equals(this.titles.get(i))) {
//                menu.add(1, i, 1, getResources().getString(R.string.lords_prayer));
//            } else {
//                menu.add(1, i, 1, this.titles.get(i));
//            }
//        }
//    }

//    public boolean onContextItemSelected(MenuItem item) {
//        this.space.setToScreen(item.getItemId());
//        return super.onContextItemSelected(item);
//    }

//    public void onSaveInstanceState(Bundle icicle) {
//        if (this.space.getCurrentScreen() != 0) {
//                icicle.putInt(this.currentKey, this.space.getCurrentScreen());
//        }
//        super.onSaveInstanceState(icicle);
//    }

//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        if (savedInstanceState.containsKey(this.currentKey)) {
//            this.cureentScreen = savedInstanceState.getInt(this.currentKey);
//        }
//    }

    public void onPause() {
        super.onPause();
        this.Progress.dismiss();
    }

    private String getSelectin() {
        String SelectIn = "";
        if (PrayerActivity.this.parentId == 0) {
            String SelectIn2 = PrayerActivity.PRAYERS[PrayerActivity.this.f2Id];
            PrayerActivity.this.prayerHeaderText = PrayerActivity.this.getResources().getStringArray(R.array.prayers_array)[PrayerActivity.this.f2Id];
            return SelectIn2;
        } else if (PrayerActivity.this.parentId == 1) {
            String SelectIn3 = PrayerActivity.MIDNIGHT_PRAYERS[PrayerActivity.this.f2Id];
            PrayerActivity.this.prayerHeaderText = PrayerActivity.this.getResources().getStringArray(R.array.midnight_prayers_array)[PrayerActivity.this.f2Id];
            return SelectIn3;
        } else if (PrayerActivity.this.parentId != 2) {
            return SelectIn;
        } else {
            String SelectIn4 = PrayerActivity.OTHER_PRAYERS[PrayerActivity.this.f2Id];
            PrayerActivity.this.prayerHeaderText = PrayerActivity.this.getResources().getStringArray(R.array.other_prayers_array)[PrayerActivity.this.f2Id];
            return SelectIn4;
        }
    }

    private String unescape(String description) {
        return description.replaceAll("\\\\n", "\\\n");
    }

}
