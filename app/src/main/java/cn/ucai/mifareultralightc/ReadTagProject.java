package cn.ucai.mifareultralightc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.ucai.mifareultralightc.local.ProjectHelper;

/**
 * Created by clawpo on 2017/6/20.
 */

public class ReadTagProject extends BasicActivity {
    private static final String TAG = "ReadTagProject";
    @BindView(R.id.editTextReadTagData1)
    EditText mData1;
    @BindView(R.id.editTextReadTagData2)
    EditText mData2;
    Unbinder bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        bind = ButterKnife.bind(this);
    }

//    private void initData() {
//        Tag tag = Common.getTag();
//        Log.e(TAG, "tag is " + tag);
//        if (tag != null) {
//            showData(Common.readTag(tag));
//        } else {
//            Log.e(TAG, "tag is null");
////            finish();
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.e(TAG, "onResume...");
//        initData();
//    }
//
    private void showData(String[] data) {
        String pname = ProjectHelper.getInstance().getData(data[0]);
        Log.e(TAG, "showData,pname=" + pname);
        if (pname != null) {
            mData1.setText(pname);
        }

        String cname = ProjectHelper.getInstance().getData(data[1]);
        Log.e(TAG, "showData,cname=" + cname);
        if (cname != null) {
            mData2.setText(cname);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        int typeCheck = Common.treatAsNewTag(intent, this);
        Log.e(TAG, "onNewIntent,typeCheck=" + typeCheck);
        if (typeCheck == -1 || typeCheck == -2) {
            String[] data = Common.readFromTag(intent);
            showData(data);
            Log.e(TAG,"onNewIntent,data="+data);
        }
    }

    @OnClick(R.id.buttonWriteTagBlock)
    public void onBack() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind!=null){
            bind.unbind();
        }
    }
}
