#!/bin/bash

# Define a raiz de resources
RES_DIR="app/src/main/res"

echo "🔨 Criando estrutura de diretórios em $RES_DIR..."

# Criar diretórios
mkdir -p "$RES_DIR/drawable"
mkdir -p "$RES_DIR/layout"
mkdir -p "$RES_DIR/menu"
mkdir -p "$RES_DIR/mipmap-hdpi"
mkdir -p "$RES_DIR/mipmap-mdpi"
mkdir -p "$RES_DIR/mipmap-xhdpi"
mkdir -p "$RES_DIR/mipmap-xxhdpi"
mkdir -p "$RES_DIR/mipmap-xxxhdpi"
mkdir -p "$RES_DIR/mipmap-anydpi-v26"
mkdir -p "$RES_DIR/values"
mkdir -p "$RES_DIR/xml"

# ==========================================
# VALUES (Colors, Strings, Themes)
# ==========================================
echo "🎨 Gerando values..."

# colors.xml
cat <<EOF > "$RES_DIR/values/colors.xml"
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
    
    <color name="background">#F5F5F5</color>
    <color name="surface">#FFFFFF</color>
    
    <!-- Code Editor Colors -->
    <color name="editor_bg">#2B2B2B</color>
    <color name="editor_line_number">#606366</color>
    <color name="editor_divider">#323232</color>
</resources>
EOF

# strings.xml
cat <<EOF > "$RES_DIR/values/strings.xml"
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Android IDE</string>
    <string name="drawer_open">Abrir menu</string>
    <string name="drawer_close">Fechar menu</string>
    
    <!-- Actions -->
    <string name="action_save">Salvar</string>
    <string name="action_build">Compilar</string>
    <string name="action_settings">Configurações</string>
    
    <!-- Labels -->
    <string name="project_name">Nome do Projeto</string>
    <string name="package_name">Nome do Pacote</string>
    <string name="create_project">Criar Projeto</string>
    <string name="cancel">Cancelar</string>
</resources>
EOF

# themes.xml
cat <<EOF > "$RES_DIR/values/themes.xml"
<?xml version="1.0" encoding="utf-8"?>
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.AndroidIDE" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
        <item name="android:statusBarColor">?attr/colorPrimaryVariant</item>
    </style>

    <!-- Splash Screen Theme -->
    <style name="Theme.AndroidIDE.Splash" parent="Theme.AndroidIDE">
        <item name="android:windowBackground">@color/purple_700</item>
    </style>
</resources>
EOF

# ==========================================
# DRAWABLES (Icons)
# ==========================================
echo "🖼️ Gerando drawables..."

# ic_file.xml
cat <<EOF > "$RES_DIR/drawable/ic_file.xml"
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
    <path android:fillColor="@android:color/white"
        android:pathData="M14,2H6c-1.1,0 -1.99,0.9 -1.99,2L4,20c0,1.1 0.89,2 1.99,2H18c1.1,0 2,-0.9 2,-2V8l-6,-6zM16,18H8v-2h8v2zM16,14H8v-2h8v2zM13,9V3.5L18.5,9H13z"/>
</vector>
EOF

# ic_folder.xml
cat <<EOF > "$RES_DIR/drawable/ic_folder.xml"
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="#FFCA28">
    <path android:fillColor="@android:color/white"
        android:pathData="M10,4l-2,2H4c-1.1,0 -1.99,0.9 -1.99,2L2,18c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2V8c0,-1.1 -0.9,-2 -2,-2h-8z"/>
</vector>
EOF

# ic_folder_open.xml
cat <<EOF > "$RES_DIR/drawable/ic_folder_open.xml"
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="#FFCA28">
    <path android:fillColor="@android:color/white"
        android:pathData="M20,6h-8l-2,-2H4c-1.1,0 -1.99,0.9 -1.99,2L2,18c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2V8c0,-1.1 -0.9,-2 -2,-2zM20,18H4V8h16v10z"/>
</vector>
EOF

# ic_kotlin.xml
cat <<EOF > "$RES_DIR/drawable/ic_kotlin.xml"
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path android:fillColor="#7F52FF"
        android:pathData="M2,2h20L12,12L22,22H2Z"/>
</vector>
EOF

# ic_java.xml
cat <<EOF > "$RES_DIR/drawable/ic_java.xml"
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="#F44336">
    <path android:fillColor="@android:color/white"
        android:pathData="M20,6h-8l-2,-2H4c-1.1,0 -1.99,0.9 -1.99,2L2,18c0,1.1 0.9,2 2,2h16c1.1,0 2,-0.9 2,-2V8c0,-1.1 -0.9,-2 -2,-2zM18,14h-2v-4h2v4z"/>
</vector>
EOF

# ic_xml.xml
cat <<EOF > "$RES_DIR/drawable/ic_xml.xml"
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="#8BC34A">
    <path android:fillColor="@android:color/white"
        android:pathData="M9.4,16.6L4.8,12l4.6,-4.6L8,6l-6,6l6,6l1.4,-1.4zM14.6,16.6l4.6,-4.6l-4.6,-4.6L16,6l6,6l-6,6l-1.4,-1.4z"/>
</vector>
EOF

# ic_launcher_background.xml (para adaptive icon)
cat <<EOF > "$RES_DIR/drawable/ic_launcher_background.xml"
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path android:fillColor="#3DDC84"
        android:pathData="M0,0h108v108h-108z"/>
</vector>
EOF

# ic_launcher_foreground.xml (para adaptive icon)
cat <<EOF > "$RES_DIR/drawable/ic_launcher_foreground.xml"
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path android:fillColor="#FFFFFF"
        android:pathData="M30,36h48v36h-48z"/>
    <path android:fillColor="#FFFFFF"
        android:pathData="M44,28h20v8h-20z"/>
</vector>
EOF

# ==========================================
# MIPMAP (Launcher Icons via ANYDPI)
# ==========================================
echo "📱 Configurando mipmaps..."

# mipmap-anydpi-v26/ic_launcher.xml
cat <<EOF > "$RES_DIR/mipmap-anydpi-v26/ic_launcher.xml"
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
EOF

# mipmap-anydpi-v26/ic_launcher_round.xml
cat <<EOF > "$RES_DIR/mipmap-anydpi-v26/ic_launcher_round.xml"
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
EOF

# ==========================================
# MENUS
# ==========================================
echo "☰ Gerando menus..."

# menu_project_manager.xml
cat <<EOF > "$RES_DIR/menu/menu_project_manager.xml"
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <item
        android:id="@+id/action_refresh"
        android:icon="@android:drawable/ic_popup_sync"
        android:title="Refresh"
        app:showAsAction="ifRoom" />
    <item
        android:id="@+id/action_settings"
        android:title="@string/action_settings"
        app:showAsAction="never" />
</menu>
EOF

# menu_editor.xml
cat <<EOF > "$RES_DIR/menu/menu_editor.xml"
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    
    <item
        android:id="@+id/action_save"
        android:icon="@android:drawable/ic_menu_save"
        android:title="@string/action_save"
        app:showAsAction="always" />
        
    <item
        android:id="@+id/action_build"
        android:icon="@android:drawable/ic_media_play"
        android:title="@string/action_build"
        app:showAsAction="always" />

    <item
        android:id="@+id/action_undo"
        android:icon="@android:drawable/ic_menu_revert"
        android:title="Undo"
        app:showAsAction="ifRoom" />

    <item
        android:id="@+id/action_redo"
        android:icon="@android:drawable/ic_menu_rotate"
        android:title="Redo"
        app:showAsAction="ifRoom" />
        
    <item
        android:id="@+id/action_save_all"
        android:title="Save All"
        app:showAsAction="never" />
        
    <item
        android:id="@+id/action_find"
        android:title="Find"
        app:showAsAction="never" />
        
    <item
        android:id="@+id/action_close_tab"
        android:title="Close Tab"
        app:showAsAction="never" />
</menu>
EOF

# ==========================================
# LAYOUTS
# ==========================================
echo "📐 Gerando layouts..."

# activity_main.xml (Splash/Loading)
cat <<EOF > "$RES_DIR/layout/activity_main.xml"
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:background="?attr/colorPrimary">

    <ImageView
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:src="@mipmap/ic_launcher"
        android:layout_marginBottom="16dp"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="@string/app_name"
        android:textSize="32sp"
        android:textStyle="bold"
        android:textColor="@color/white"/>
        
    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="32dp"
        android:indeterminate="true"
        android:indeterminateTint="@color/white"/>
        
    <TextView
        android:id="@+id/textStatus"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="Carregando..."
        android:textColor="@color/white"/>

    <Button
        android:id="@+id/buttonRetry"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Tentar Novamente"
        android:visibility="gone"
        android:layout_marginTop="16dp"/>

</LinearLayout>
EOF

# activity_project_manager.xml
cat <<EOF > "$RES_DIR/layout/activity_project_manager.xml"
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="?attr/colorPrimary"
            app:titleTextColor="@color/white" />

    </com.google.android.material.appbar.AppBarLayout>

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recyclerView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:padding="8dp"
            android:clipToPadding="false"/>

        <TextView
            android:id="@+id/emptyView"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Nenhum projeto encontrado"
            android:layout_gravity="center"
            android:visibility="gone"/>

    </FrameLayout>

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabNewProject"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="16dp"
        android:src="@drawable/ic_file"
        app:tint="@color/white" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
EOF

# activity_create_project.xml
cat <<EOF > "$RES_DIR/layout/activity_create_project.xml"
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            app:titleTextColor="@color/white"/>
    </com.google.android.material.appbar.AppBarLayout>

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:padding="16dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <com.google.android.material.textfield.TextInputLayout
                android:id="@+id/inputLayoutName"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:hint="@string/project_name">

                <com.google.android.material.textfield.TextInputEditText
                    android:id="@+id/editProjectName"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:inputType="textCapWords"/>
            </com.google.android.material.textfield.TextInputLayout>

            <com.google.android.material.textfield.TextInputLayout
                android:id="@+id/inputLayoutPackage"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="16dp"
                android:hint="@string/package_name">

                <com.google.android.material.textfield.TextInputEditText
                    android:id="@+id/editPackageName"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:inputType="textNoSuggestions"/>
            </com.google.android.material.textfield.TextInputLayout>

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Min SDK"
                android:layout_marginTop="16dp"/>

            <Spinner
                android:id="@+id/spinnerMinSdk"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Target SDK"
                android:layout_marginTop="16dp"/>

            <Spinner
                android:id="@+id/spinnerTargetSdk"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:gravity="end"
                android:layout_marginTop="24dp">

                <Button
                    android:id="@+id/buttonCancel"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="@string/cancel"
                    style="@style/Widget.MaterialComponents.Button.TextButton"/>

                <Button
                    android:id="@+id/buttonCreate"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="@string/create_project"
                    android:layout_marginStart="8dp"/>
            </LinearLayout>
            
            <ProgressBar
                android:id="@+id/progressBar"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                android:visibility="gone"/>

        </LinearLayout>
    </ScrollView>
</LinearLayout>
EOF

# activity_editor.xml
cat <<EOF > "$RES_DIR/layout/activity_editor.xml"
<?xml version="1.0" encoding="utf-8"?>
<androidx.drawerlayout.widget.DrawerLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/drawerLayout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <com.google.android.material.appbar.AppBarLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content">
            <androidx.appcompat.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:titleTextColor="@color/white"/>
        </com.google.android.material.appbar.AppBarLayout>

        <com.google.android.material.tabs.TabLayout
            android:id="@+id/tabLayout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:tabMode="scrollable"/>

        <io.github.rosemoe.sora.widget.CodeEditor
            android:id="@+id/codeEditor"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@color/editor_bg"/>

    </LinearLayout>

    <LinearLayout
        android:layout_width="280dp"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:background="?android:attr/windowBackground"
        android:orientation="vertical">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Project Files"
            android:padding="16dp"
            android:textSize="18sp"
            android:textStyle="bold"
            android:background="?attr/colorPrimary"
            android:textColor="@color/white"/>

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/fileTreeRecyclerView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>

    </LinearLayout>

</androidx.drawerlayout.widget.DrawerLayout>
EOF

# activity_build.xml
cat <<EOF > "$RES_DIR/layout/activity_build.xml"
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <androidx.appcompat.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorPrimary" />

    <ProgressBar
        android:id="@+id/progressBar"
        style="?android:attr/progressBarStyleHorizontal"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:indeterminate="true" />

    <TextView
        android:id="@+id/textStatus"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="8dp"
        android:text="Building..."
        android:textStyle="bold" />

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#DDDDDD">

        <TextView
            android:id="@+id/textLogs"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:fontFamily="monospace"
            android:padding="8dp"
            android:textSize="12sp"
            android:text="Iniciando build..." />
    </ScrollView>

</LinearLayout>
EOF

# activity_settings.xml (Placeholder)
cat <<EOF > "$RES_DIR/layout/activity_settings.xml"
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <androidx.appcompat.widget.Toolbar
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:background="?attr/colorPrimary"
        android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"/>

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="16dp"
        android:text="Configurações (Em breve)" />

</LinearLayout>
EOF

# item_project.xml
cat <<EOF > "$RES_DIR/layout/item_project.xml"
<?xml version="1.0" encoding="utf-8"?>
<androidx.cardview.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="8dp"
    app:cardCornerRadius="4dp"
    app:cardElevation="2dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:id="@+id/textProjectName"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:textSize="18sp"
            android:textStyle="bold"
            android:text="Project Name"/>

        <TextView
            android:id="@+id/textPackageName"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="com.example.project"
            android:textColor="#666666"/>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:layout_marginTop="8dp">

            <TextView
                android:id="@+id/textSdkVersion"
                android:layout_width="0dp"
                android:layout_weight="1"
                android:layout_height="wrap_content"
                android:text="SDK: 24-34"
                android:textSize="12sp"/>

            <TextView
                android:id="@+id/textLastModified"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="Modified: Today"
                android:textSize="12sp"/>
        </LinearLayout>
    </LinearLayout>
</androidx.cardview.widget.CardView>
EOF

# item_file.xml
cat <<EOF > "$RES_DIR/layout/item_file.xml"
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:gravity="center_vertical"
    android:padding="8dp"
    android:background="?android:attr/selectableItemBackground">

    <ImageView
        android:id="@+id/imageFileIcon"
        android:layout_width="24dp"
        android:layout_height="24dp"
        android:src="@drawable/ic_file"
        android:contentDescription="File Icon"/>

    <TextView
        android:id="@+id/textFileName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginStart="12dp"
        android:text="filename.kt"
        android:textSize="14sp"/>

</LinearLayout>
EOF

# ==========================================
# XML (File Paths)
# ==========================================
echo "⚙️ Gerando configurações XML..."

# file_paths.xml
cat <<EOF > "$RES_DIR/xml/file_paths.xml"
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-path name="external_files" path="."/>
    <files-path name="internal_files" path="."/>
</paths>
EOF

echo "✅ Script de recursos concluído com sucesso!"
