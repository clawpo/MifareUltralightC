package cn.ucai.mifareultralightc;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
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
    @BindView(R.id.editTextWriteTagData2)
    EditText mData2;
    boolean isBatch = false;
    @BindView(R.id.cb_batch)
    CheckBox mCbBatch;

    // It is checked but the IDE don't get it.
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tag_project);
        bind = ButterKnife.bind(this);
        initData();
        mCbBatch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    private void initData() {
        Tag tag = Common.getTag();
        Log.e(TAG, "tag is " + tag);
        if (tag != null) {
//            showData(Common.readTag(tag));
        } else {
            Log.e(TAG, "tag is null");
//            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume...");
        initData();
    }

//    private void showData(String data) {
//        Log.e(TAG, "showData,data=" + data);
//        String name = ProjectHelper.getInstance().getData(data);
//        if (name != null) {
//            mData1.setText(name);
//        }
//    }

    @Override
    public void onNewIntent(Intent intent) {
        int typeCheck = Common.treatAsNewTag(intent, this);
        Log.e(TAG, "onNewIntent,typeCheck=" + typeCheck);
        if (typeCheck == -1 || typeCheck == -2) {
//            showData(Common.readFromTag(intent));
            if (isBatch) {
                onWriteBlock(null);
            }
        }
    }

    public void onWriteBlock(View view) {
//        Common.writeTag(getInputData());
        Common.writeTagByMessage(ProjectHelper.getInstance().getDataFromDB(getInputData()));
    }

    private String[] getInputData() {
        String data[] = new String[2];
        data[0] = mData1.getText().toString();
        data[1] = mData2.getText().toString();
        return data;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }
    }

    @OnCheckedChanged(R.id.cb_batch)
    void batchWrite(boolean isChecked) {
        isBatch = isChecked;
    }
}
