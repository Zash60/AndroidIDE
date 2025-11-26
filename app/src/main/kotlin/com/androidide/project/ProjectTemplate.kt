package com.androidide.project

object ProjectTemplate {

    fun getManifest(project: Project): String = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="${project.packageName}">

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher"
        android:supportsRtl="true"
        android:theme="@style/Theme.${project.name.replace(" ", "")}">
        
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
    </application>

</manifest>
    """.trimIndent()

    fun getMainActivity(project: Project): String = """
package ${project.packageName}

import android.os.Bundle
import android.widget.TextView
import android.app.Activity

class MainActivity : Activity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = "Hello from ${project.name}!"
    }
}
    """.trimIndent()

    fun getMainLayout(): String = """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="16dp">

    <TextView
        android:id="@+id/textView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        android:textSize="24sp"
        android:textStyle="bold" />

    <Button
        android:id="@+id/button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="Click Me" />

</LinearLayout>
    """.trimIndent()

    fun getStrings(project: Project): String = """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">${project.name}</string>
</resources>
    """.trimIndent()

    fun getColors(): String = """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
    """.trimIndent()

    fun getThemes(project: Project): String = """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.${project.name.replace(" ", "")}" parent="android:Theme.Material.Light.NoActionBar">
        <item name="android:colorPrimary">@color/purple_500</item>
        <item name="android:colorPrimaryDark">@color/purple_700</item>
        <item name="android:colorAccent">@color/teal_200</item>
        <item name="android:statusBarColor">@color/purple_700</item>
    </style>
</resources>
    """.trimIndent()
}
