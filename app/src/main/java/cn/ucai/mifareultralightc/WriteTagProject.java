package cn.ucai.mifareultralightc;

import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by clawpo on 2017/6/16.
 */

public class WriteTagProject extends BasicActivity {
    private static final String TAG = "WriteTagProject";

    @BindView(R.id.editTextWriteTagData1)
    EditText mData1;
    @BindView(R.id.editTextWriteTagData2)
    EditText mData2;
    @BindView(R.id.editTextWriteTagData3)
    EditText mData3;
    @BindView(R.id.editTextWriteTagData4)
    EditText mData4;
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
        if (tag!=null){
            showData(Common.readTag(tag));
        }else{
            Log.e(TAG,"tag is null");
//            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void showData(String[] data) {
        if (data!=null){
            mData1.setText(data[0]);
            mData2.setText(data[1]);
            mData3.setText(data[2]);
            mData4.setText(data[3]);
        }
    }

    public void onWriteBlock(View view) {
        Common.writeTag(getInputData());
    }

    private String[] getInputData(){
        String[] data = new String[4];
        data[0] = mData1.getText().toString();
        data[1] = mData2.getText().toString();
        data[2] = mData3.getText().toString();
        data[3] = mData4.getText().toString();
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
