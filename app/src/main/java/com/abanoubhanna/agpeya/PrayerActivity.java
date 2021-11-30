package com.abanoubhanna.agpeya;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PrayerActivity extends Activity {
    public static final String[] MIDNIGHT_PRAYERS = {"Midnight_1", "Midnight_2", "Midnight_3"};
    public static final String[] OTHER_PRAYERS = {"contrition", "before_confession", "after_confession", "before_communion", "after_communion", "before_eating", "after_eating", "consultation", "yearly", "before_exam"};
    public static final String[] PRAYERS = {"Prime", "Terce", "Sext", "None", "Vespers", "Compline", "Viel"};
    /* access modifiers changed from: private */

    /* renamed from: Id */
    public int f2Id;
    /* access modifiers changed from: private */
    public ProgressDialog Progress;
    private int bgColor;
    /* access modifiers changed from: private */
    public int contentColor;
    /* access modifiers changed from: private */
    public int contentSize;
    /* access modifiers changed from: private */
    public int cureentScreen = -1;
    private String currentKey = "currentScreen";
    /* access modifiers changed from: private */
    public Typeface droid;
    private boolean keepScreenOn;
    /* access modifiers changed from: private */
    public int parentId;
    public Cursor pray;
    /* access modifiers changed from: private */
    public String prayerHeaderText;
    /* access modifiers changed from: private */
    public DragableSpace space;
    private boolean supportArabic;
    /* access modifiers changed from: private */
    public int titleColor;
    /* access modifiers changed from: private */
    public int titleSize;
    public List<String> titles;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        int i;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.please_wait);
        this.space = new DragableSpace(getApplicationContext());
        this.droid = Typeface.createFromAsset(getAssets(), "DroidNaskh-Bold.ttf");
        SharedPreferences Preferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.titleSize = Preferences.getInt("titleSize", 20);
        this.contentSize = Preferences.getInt("contentSize", 18);
        this.titleColor = Preferences.getInt("titleColor", Color.parseColor("#87410d"));
        this.contentColor = Preferences.getInt("contentColor", -16777216);
        this.bgColor = Preferences.getInt("bgColor", Color.parseColor("#fefde9"));
        this.keepScreenOn = Preferences.getBoolean("keepScreenon", true);
        this.supportArabic = Preferences.getBoolean("supportArabic", false);
        this.space.setBackgroundColor(this.bgColor);
        this.space.setKeepScreenOn(this.keepScreenOn);
        this.titles = new ArrayList();
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
            new loadPrayer().execute(new String[]{""});
        }
    }

    public class loadPrayer extends AsyncTask<String, Integer, String> {
        public loadPrayer() {
        }

        /* access modifiers changed from: protected */
        public String doInBackground(String... params) {
            String SelectIn = getSelectin();
            AgpeyaDB db = new AgpeyaDB(PrayerActivity.this);
            db.open();
            PrayerActivity.this.pray = db.getPrayer(SelectIn);
            if (PrayerActivity.this.pray != null) {
                PrayerActivity.this.pray.moveToLast();
                while (!PrayerActivity.this.pray.isBeforeFirst()) {
                    ScrollView prayScroll = new ScrollView(PrayerActivity.this);
                    LinearLayout linearLayout = new LinearLayout(PrayerActivity.this);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    if (PrayerActivity.this.pray.getString(1).length() != 0) {
                        TextView title = new TextView(PrayerActivity.this);
                        title.setText(PrayerActivity.this.pray.getString(1));
                        title.setTypeface(PrayerActivity.this.droid);
                        title.setPadding(10, 10, 10, 10);
                        title.setTextSize(2, (float) PrayerActivity.this.titleSize);
                        title.setTextColor(PrayerActivity.this.titleColor);
                        title.setGravity(17);
                        PrayerActivity.this.titles.add(PrayerActivity.this.pray.getString(1));
                        linearLayout.addView(title);
                        TextView content = new TextView(PrayerActivity.this);
                        content.setText(unescape(PrayerActivity.this.pray.getString(2)));
                        content.setTypeface(PrayerActivity.this.droid);
                        content.setPadding(10, 10, 15, 10);
                        content.setTextSize(2, (float) PrayerActivity.this.contentSize);
                        content.setTextColor(PrayerActivity.this.contentColor);
                        content.setGravity(5);
                        linearLayout.addView(content);
                    } else {
                        TextView content2 = new TextView(PrayerActivity.this);
                        content2.setText(unescape(PrayerActivity.this.pray.getString(2)));
                        content2.setTypeface(PrayerActivity.this.droid);
                        content2.setTextSize(2, (float) PrayerActivity.this.titleSize);
                        content2.setPadding(10, 140, 10, 10);
                        content2.setTextColor(PrayerActivity.this.titleColor);
                        content2.setGravity(1);
                        PrayerActivity.this.titles.add(PrayerActivity.this.getResources().getString(R.string.prayer_start));
                        linearLayout.addView(content2);
                    }
                    prayScroll.addView(linearLayout);
                    PrayerActivity.this.space.addView(prayScroll);
                    PrayerActivity.this.pray.moveToPrevious();
                }
            }
            PrayerActivity.this.pray.close();
            db.close();
            return null;
        }

        private String getSelectin() {
            if (PrayerActivity.this.parentId == 0) {
                String SelectIn = PrayerActivity.PRAYERS[PrayerActivity.this.f2Id];
                PrayerActivity.this.prayerHeaderText = PrayerActivity.this.getResources().getStringArray(R.array.prayers_array)[PrayerActivity.this.f2Id];
                return SelectIn;
            } else if (PrayerActivity.this.parentId == 1) {
                String SelectIn2 = PrayerActivity.MIDNIGHT_PRAYERS[PrayerActivity.this.f2Id];
                PrayerActivity.this.prayerHeaderText = PrayerActivity.this.getResources().getStringArray(R.array.midnight_prayers_array)[PrayerActivity.this.f2Id];
                return SelectIn2;
            } else if (PrayerActivity.this.parentId != 2) {
                return "";
            } else {
                String SelectIn3 = PrayerActivity.OTHER_PRAYERS[PrayerActivity.this.f2Id];
                PrayerActivity.this.prayerHeaderText = PrayerActivity.this.getResources().getStringArray(R.array.other_prayers_array)[PrayerActivity.this.f2Id];
                return SelectIn3;
            }
        }

        private String unescape(String description) {
            return description.replaceAll("\\\\n", "\\\n");
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(String result) {
            int toScreen;
            super.onPostExecute(result);
            PrayerActivity.this.setTitle(PrayerActivity.this.prayerHeaderText);
            int childsTotal = PrayerActivity.this.space.getChildCount() - 1;
            if (PrayerActivity.this.cureentScreen != -1) {
                toScreen = PrayerActivity.this.cureentScreen;
            } else {
                toScreen = childsTotal;
            }
            PrayerActivity.this.space.setCurrentScreen(toScreen);
            PrayerActivity.this.setContentView(PrayerActivity.this.space);
            PrayerActivity.this.Progress.dismiss();
        }
    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//        if (this.titles.size() > 1) {
//            getMenuInflater().inflate(R.menu.prayer_menu, menu);
//        }
//        return true;
//    }

//    public boolean onOptionsItemSelected(MenuItem item) {
//        registerForContextMenu(this.space);
//        openContextMenu(this.space);
//        unregisterForContextMenu(this.space);
//        return super.onOptionsItemSelected(item);
//    }

//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        menu.setHeaderTitle(R.string.selectPrayer);
//        String lordsPrayer = getResources().getString(R.string.lords_prayer_db);
//        for (int i = this.titles.size() - 1; i > -1; i--) {
//            if (lordsPrayer.equals(this.titles.get(i))) {
//                menu.add(1, i, 1, reshapeText(getResources().getString(R.string.lords_prayer)));
//            } else {
//                menu.add(1, i, 1, reshapeText(this.titles.get(i)));
//            }
//        }
//    }

//    public boolean onContextItemSelected(MenuItem item) {
//        this.space.setToScreen(item.getItemId());
//        return super.onContextItemSelected(item);
//    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle icicle) {
        if (this.space.getCurrentScreen() != 0) {
            icicle.putInt(this.currentKey, this.space.getCurrentScreen());
        }
        super.onSaveInstanceState(icicle);
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(this.currentKey)) {
            this.cureentScreen = savedInstanceState.getInt(this.currentKey);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.Progress.dismiss();
    }
}
