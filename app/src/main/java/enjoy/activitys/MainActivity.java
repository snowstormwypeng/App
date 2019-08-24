package enjoy.activitys;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.widget.ImageView;

import BaseActivity.BaseActivity;

import Entity.ActivityResult;
import ViewModel.MainActivity_ViewModel;
import enjoy.app.BuildConfig;
import enjoy.app.R;
import enjoy.app.databinding.ActivityMainBinding;


public class MainActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String mainActivityName="enjoy.activitys.ActivityMain";
        try {
            Class c=Class.forName(mainActivityName);
            StartActivityForResult(c);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
