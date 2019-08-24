package enjoy.activitys;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import BaseActivity.BaseActivity;
import ViewModel.MainActivity_ViewModel;
import enjoy.app.R;
import enjoy.app.databinding.ActivityMainBinding;


public class ActivityMain extends BaseActivity {

    private MainActivity_ViewModel viewModel;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.navigation_home:
                    viewModel.Text_Info.Text.set(getResources().getString(R.string.title_home));
                    return true;
                case R.id.navigation_dashboard:
                    viewModel.Text_Info.Text.set(getResources().getString(R.string.title_dashboard));
                    return true;
                case R.id.navigation_notifications:
                    viewModel.Text_Info.Text.set(getResources().getString(R.string.title_notifications));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel=new MainActivity_ViewModel(this);
        ActivityMainBinding dataBinding =DataBindingUtil.setContentView(this,R.layout.activity_main);
        dataBinding.setViewModel(viewModel);
        viewModel.pushActivity(this);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            viewModel.AddVipImg(picturePath);



        }

    }

}
