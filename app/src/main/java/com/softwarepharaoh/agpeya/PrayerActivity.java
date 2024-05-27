package com.softwarepharaoh.agpeya;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PrayerActivity extends Activity {
    public static final String[] MIDNIGHT_PRAYERS = {"Midnight_1", "Midnight_2", "Midnight_3"};
    public static final String[] OTHER_PRAYERS = {"contrition", "before_confession", "after_confession", "before_communion", "after_communion", "before_eating", "after_eating", "consultation", "yearly", "before_exam"};
    public static final String[] PRAYERS = {"Prime", "Terce", "Sext", "None", "Vespers", "Compline", "Viel"};

    private int prayerNumber;
    public ProgressDialog Progress;
    private final int contentColor = -16777216;
    private final int contentSize = 20;//18
    public int currentScreen = -1;
    private final String currentKey = "currentScreen";
    private int parentId;
    public Cursor pray;
    private String prayerHeaderText;
    private DragableSpace space;
    private int titleColor;
    private final int titleSize = 22;//20
    public List<String> titles;

    protected void onCreate(Bundle savedInstanceState) {
        int i;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.please_wait);
        this.space = new DragableSpace(getApplicationContext());

        //this.titleSize = 20;
        //this.contentSize = 18;
        //this.contentColor = -16777216;
        this.titleColor = Color.parseColor("#87410d");
        int bgColor = Color.parseColor("#fefde9");

        this.space.setBackgroundColor(bgColor);
        this.space.setKeepScreenOn(true);
        this.titles = new ArrayList();
        Bundle extras = getIntent().getExtras();
        this.prayerNumber = extras != null ? extras.getInt("Id") : -1;
        if (extras != null) {
            i = extras.getInt("parentId");
        } else {
            i = -1;
        }
        this.parentId = i;
        if (this.prayerNumber != -1) {
            this.Progress = ProgressDialog.show(this, "", getResources().getString(R.string.wait), true);
            new loadPrayer().execute(new String[]{""});
        }
    }

    public class loadPrayer extends AsyncTask<String, Integer, String> {
        public loadPrayer() {
        }

        protected String doInBackground(String... params) {
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
                        title.setPadding(10, 10, 10, 10);
                        title.setTextSize(2, (float) PrayerActivity.this.titleSize);
                        title.setTextColor(PrayerActivity.this.titleColor);
                        title.setGravity(17);
                        PrayerActivity.this.titles.add(PrayerActivity.this.pray.getString(1));
                        linearLayout.addView(title);
                        TextView content = new TextView(PrayerActivity.this);
                        content.setText(unescape(PrayerActivity.this.pray.getString(2)));
                        content.setPadding(10, 10, 15, 10);
                        content.setTextSize(2, (float) PrayerActivity.this.contentSize);
                        content.setTextColor(PrayerActivity.this.contentColor);
                        content.setGravity(5);
                        linearLayout.addView(content);
                    } else {
                        TextView content2 = new TextView(PrayerActivity.this);
                        content2.setText(unescape(PrayerActivity.this.pray.getString(2)));
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
            assert PrayerActivity.this.pray != null;
            PrayerActivity.this.pray.close();
            db.close();
            return null;
        }

        private String getSelectin() {
            if (PrayerActivity.this.parentId == 0) {
                String SelectIn = PrayerActivity.PRAYERS[PrayerActivity.this.prayerNumber];
                PrayerActivity.this.prayerHeaderText = PrayerActivity.this.getResources().getStringArray(R.array.prayers_array)[PrayerActivity.this.prayerNumber];
                return SelectIn;
            } else if (PrayerActivity.this.parentId == 1) {
                String SelectIn2 = PrayerActivity.MIDNIGHT_PRAYERS[PrayerActivity.this.prayerNumber];
                PrayerActivity.this.prayerHeaderText = PrayerActivity.this.getResources().getStringArray(R.array.midnight_prayers_array)[PrayerActivity.this.prayerNumber];
                return SelectIn2;
            } else if (PrayerActivity.this.parentId != 2) {
                return "";
            } else {
                String SelectIn3 = PrayerActivity.OTHER_PRAYERS[PrayerActivity.this.prayerNumber];
                PrayerActivity.this.prayerHeaderText = PrayerActivity.this.getResources().getStringArray(R.array.other_prayers_array)[PrayerActivity.this.prayerNumber];
                return SelectIn3;
            }
        }

        private String unescape(String description) {
            return description.replaceAll("\\\\n", "\\\n");
        }

        protected void onPostExecute(String result) {
            int toScreen;
            super.onPostExecute(result);
            PrayerActivity.this.setTitle(PrayerActivity.this.prayerHeaderText);
            int childsTotal = PrayerActivity.this.space.getChildCount() - 1;
            if (PrayerActivity.this.currentScreen != -1) {
                toScreen = PrayerActivity.this.currentScreen;
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

    protected void onSaveInstanceState(@NonNull Bundle icicle) {
        if (this.space.getCurrentScreen() != 0) {
            icicle.putInt(this.currentKey, this.space.getCurrentScreen());
        }
        super.onSaveInstanceState(icicle);
    }

    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(this.currentKey)) {
            this.currentScreen = savedInstanceState.getInt(this.currentKey);
        }
    }

    protected void onPause() {
        super.onPause();
        this.Progress.dismiss();
    }
}
