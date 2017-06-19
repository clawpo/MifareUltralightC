package cn.ucai.mifareultralightc;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.ucai.mifareultralightc.local.ProjectHelper;

/**
 * Created by clawpo on 2017/6/16.
 */

public class WriteTagProject extends BasicActivity {
    private static final String TAG = "WriteTagProject";

    @BindView(R.id.editTextWriteTagData1)
    EditText mData1;
    Unbinder bind;

    // It is checked but the IDE don't get it.
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tag_project);
        bind = ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Tag tag = Common.getTag();
        Log.e(TAG,"tag is "+tag);
        if (tag!=null){
//            showData(Common.readTag(tag));
        }else{
            Log.e(TAG,"tag is null");
//            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"onResume...");
        initData();
    }

    private void showData(String data) {
        Log.e(TAG,"showData,data="+data);
        String name = ProjectHelper.getInstance().getData(data);
        if (name!=null){
            mData1.setText(name);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        int typeCheck = Common.treatAsNewTag(intent, this);
        Log.e(TAG,"onNewIntent,typeCheck="+typeCheck);
        if (typeCheck == -1 || typeCheck == -2) {
            showData(Common.readFromTag(intent));
        }
    }

    public void onWriteBlock(View view) {
        Common.writeTag(getInputData());
    }

    private String[] getInputData(){
        String[] data = new String[4];
        data[0] = mData1.getText().toString();
        return data;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind!=null){
            bind.unbind();
        }
    }
}
