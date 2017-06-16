package cn.ucai.mifareultralightc;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by clawpo on 2017/6/16.
 */

public class WriteTagProject extends BasicActivity {

    @BindView(R.id.editTextWriteTagData1)
    EditText mEditTextWriteTagData1;
    @BindView(R.id.editTextWriteTagData2)
    EditText mEditTextWriteTagData2;
    @BindView(R.id.editTextWriteTagData3)
    EditText mEditTextWriteTagData3;
    @BindView(R.id.editTextWriteTagData4)
    EditText mEditTextWriteTagData4;
    Unbinder bind;

    // It is checked but the IDE don't get it.
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_tag_project);
        bind = ButterKnife.bind(this);
    }


    public void onWriteBlock(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind!=null){
            bind.unbind();
        }
    }
}
