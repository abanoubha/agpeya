Based on an analysis of the codebase, here are the key areas where you can optimize the Android application for better performance,
  memory footprint, and stability:
  ──────
  ### 1. View Reusage in ExpandableListView (Critical Performance Optimization)

  In AgpeyaListActivity.java, the custom AgpeyaListActivity.java overrides AgpeyaListActivity.java and AgpeyaListActivity.java but ignores
  the  convertView  argument:

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView textView = (TextView) ((LayoutInflater) AgpeyaListActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.
  layout.prayer_row, null).findViewById(R.id.prayer);
        ...
        return textView;
    }

  • The Problem: It inflates the  R.layout.prayer_row  XML layout from scratch every time a row scrolls onto the screen. This causes high
  CPU overhead and heavy garbage collection (GC) churn.
  • The Fix: Reuse  convertView . Since  prayer_row.xml  is a single  TextView , you don't even need  findViewById  if you reuse the root
  element:

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) AgpeyaListActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
            textView = (TextView) inflater.inflate(R.layout.prayer_row, parent, false);
        } else {
            textView = (TextView) convertView;
        }
        textView.setGravity(5);
        textView.setText(getChild(groupPosition, childPosition).toString());
        return textView;
    }
  ──────
  ### 2. Thread Safety / UI Instantiation on Background Thread (Stability & Performance)

  In PrayerActivity.java, the PrayerActivity.java  AsyncTask  performs UI object creation in PrayerActivity.java:

    protected String doInBackground(String... params) {
        ...
        ScrollView prayScroll = new ScrollView(PrayerActivity.this);
        LinearLayout linearLayout = new LinearLayout(PrayerActivity.this);
        TextView title = new TextView(PrayerActivity.this);
        ...
        linearLayout.addView(title);
        prayScroll.addView(linearLayout);
        PrayerActivity.this.space.addView(prayScroll); // Danger! Modifying view hierarchy on background thread
        ...
    }

  • The Problem: Android views are not thread-safe. Constructing UI views ( TextView ,  ScrollView ) and calling  addView  on a background
  thread can cause UI corruption, memory leaks, and application crashes.
  • The Fix: Read only raw data (the text strings) from the database in  doInBackground  and return a structured list. Then, construct the
  views and add them to DragableSpace.java inside  onPostExecute  (which runs safely on the Main/UI thread).
  ──────
  ### 3. Database Copying & Verification Optimization

  In DBFile.java:

  • Buffer Size: In DBFile.java, the byte copy buffer size is  1024  (1 KB). Increasing it to  8192  (8 KB) speeds up the file-copy
  operation on app startup.
  • Redundant Asset Checking: In DBFile.java, it opens the database asset input stream and checks its size on every single launch:
    return ((long) this.myContext.getAssets().open(DB_NAME, 0).available()) - f.length() < 500;
  This is slow. Instead, use  SharedPreferences  to save a flag once the database is successfully copied, and skip the asset check entirely
  in subsequent launches.
  ──────
  ### 4. Lazy Layout Loading with ViewPager2

  The application instantiates all pages of a prayer at startup and adds them to DragableSpace.java.

  • The Problem: For long prayers, this creates dozens of heavy layout and text views in memory all at once.
  • The Fix: Replace the custom DragableSpace.java class with a standard  ViewPager2  coupled with a  FragmentStateAdapter  or  RecyclerView -
  based adapter. This lazily inflates only the active/adjacent pages and recycles off-screen views, reducing peak memory usage drastically.
  ──────
  ### 5. Architectural Improvements (Migration Roadmap)

  As highlighted in your  README.md  roadmap, migrating from Java to Kotlin and updating the layout system will resolve a lot of these
  inefficiencies:

  • Coroutines vs AsyncTask: Replace the deprecated  AsyncTask  and raw threads (e.g., in SplashActivity.java) with Kotlin Coroutines for
  lightweight, lifecycle-aware background execution.
  • Jetpack Compose: Compose eliminates XML layout inflation entirely. A  HorizontalPager  in Compose handles lazy swipe pages out-of-the-
  box with minimal resource footprint.
  • Room Database: Migrating from manual SQLite helper classes like AgpeyaDB.java to the modern Android Room library will handle thread
  pooling, caching, and model parsing automatically.
  ──────
  ### Summary of Actions Taken

  • Analyzed the project layout, configurations, and core components.
  • Identified major performance bottlenecks in custom view adapters and raw asset-copying operations.
  • Uncovered thread safety issues in background prayer rendering and proposed standard fixes.


