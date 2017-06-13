package cn.ucai.mifareultralightc;

import android.app.Activity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends Activity {
    Unbinder bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        bind = ButterKnife.bind(this);
    }

    @OnClick(R.id.buttonMainReadTag) void onShowReadTag(){

    }

    @OnClick(R.id.buttonMainWriteTag) void onShowWriteTag(){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind!=null){
            bind.unbind();
        }
    }
}
