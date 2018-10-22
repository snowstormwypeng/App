package enjoy.app;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;

import BaseActivity.BaseActivity;
import enjoy.ViewModel.MainActivity_ViewModel;
import enjoy.app.databinding.ActivityMainBinding;


public class MainActivity extends BaseActivity {

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
        ActivityMainBinding dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        dataBinding.setViewModel(viewModel);
        viewModel.pushActivity(this);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
