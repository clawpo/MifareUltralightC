package cn.ucai.mifareultralightc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static cn.ucai.mifareultralightc.Preferences.Preference.UseInternalStorage;

public class MainActivity extends Activity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private final static int FILE_CHOOSER_DUMP_FILE = 1;
    private final static int FILE_CHOOSER_KEY_FILE = 2;
    private static final int REQUEST_WRITE_STORAGE_CODE = 1;

    Unbinder bind;
    @BindView(R.id.buttonMainReadTag) Button mReadTag;
    @BindView(R.id.buttonMainWriteTag) Button mWriteTag;
    @BindView(R.id.textViewMainFooter) TextView mAppVersion;
    private AlertDialog mEnableNfc;
    private boolean mResume = true;
    private Intent mOldIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        bind = ButterKnife.bind(this);
        mAppVersion.setText(getString(R.string.app_version)+ ": " + Common.getVersionCode());
        checkNfcPermissions();
        showUsageNotice();
        checkMIFAREClassicSupport();
    }

    private void checkMIFAREClassicSupport() {
        // Check if there is MIFARE Classic support.
        if (!Common.useAsEditorOnly() && !Common.hasMifareClassicSupport()) {
            // Disable read/write tag options.
            mReadTag.setEnabled(false);
            mWriteTag.setEnabled(false);
            CharSequence styledText = Html.fromHtml(
                    getString(R.string.dialog_no_mfc_support_device));
            AlertDialog ad = new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_no_mfc_support_device_title)
                    .setMessage(styledText)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.action_exit_app,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                    .setNegativeButton(R.string.action_continue,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    mResume = true;
                                    checkNfc();
                                }
                            })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    })
                    .show();
            // Make links clickable.
            ((TextView)ad.findViewById(android.R.id.message)).setMovementMethod(
                    LinkMovementMethod.getInstance());
            mResume = false;
        }
    }

    private void showUsageNotice() {
        // Show first usage notice.
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        final SharedPreferences.Editor sharedEditor = sharedPref.edit();
        boolean isFirstRun = sharedPref.getBoolean("is_first_run", true);
        if (isFirstRun) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_first_run_title)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.dialog_first_run)
                    .setPositiveButton(R.string.action_ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            if (Common.IS_DONATE_VERSION) {
                                mResume = true;
                                checkNfc();
                            }
                            sharedEditor.putBoolean("is_first_run", false);
                            sharedEditor.apply();
                        }
                    })
                    .show();
            mResume = false;
        }
    }

    private void checkNfcPermissions() {
        // Check if the user granted the app write permissions.
        if (Common.hasWritePermissionToExternalStorage(this)) {
            initFolders();
        } else {
            enableMenuButtons(false);
            // Request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE_CODE);
        }

        // Check if there is an NFC hardware component.
        Common.setNfcAdapter(NfcAdapter.getDefaultAdapter(this));
        if (Common.getNfcAdapter() == null) {
            createNfcEnableDialog();
            mEnableNfc.show();
            mReadTag.setEnabled(false);
            mWriteTag.setEnabled(false);
            mResume = false;
        }
    }

    /**
     * Enable or disable all menu buttons which provide functionality that
     * uses the external storage.
     * @param enable True to enable the buttons. False to disable them.
     */
    private void enableMenuButtons(boolean enable) {
        mWriteTag.setEnabled(enable);
        mReadTag.setEnabled(enable);
//        mKeyEditor.setEnabled(enable);
//        mDumpEditor.setEnabled(enable);
    }

    /**
     * Create the directories needed by MCT and clean out the tmp folder.
     */
    @SuppressLint("ApplySharedPref")
    private void initFolders() {
        boolean isUseInternalStorage = Common.getPreferences().getBoolean(
                UseInternalStorage.toString(), false);

        // Run twice and init the folders on the internal and external storage.
        for (int i = 0; i < 2; i++) {
            if (!isUseInternalStorage &&
                    !Common.isExternalStorageWritableErrorToast(this)) {
                continue;
            }

            // Create keys directory.
            File path = Common.getFileFromStorage(
                    Common.HOME_DIR + "/" + Common.KEYS_DIR);

            if (!path.exists() && !path.mkdirs()) {
                // Could not create directory.
                Log.e(LOG_TAG, "Error while creating '" + Common.HOME_DIR
                        + "/" + Common.KEYS_DIR + "' directory.");
                return;
            }

            // Create dumps directory.
            path = Common.getFileFromStorage(
                    Common.HOME_DIR + "/" + Common.DUMPS_DIR);
            if (!path.exists() && !path.mkdirs()) {
                // Could not create directory.
                Log.e(LOG_TAG, "Error while creating '" + Common.HOME_DIR
                        + "/" + Common.DUMPS_DIR + "' directory.");
                return;
            }

            // Create tmp directory.
            path = Common.getFileFromStorage(
                    Common.HOME_DIR + "/" + Common.TMP_DIR);
            if (!path.exists() && !path.mkdirs()) {
                // Could not create directory.
                Log.e(LOG_TAG, "Error while creating '" + Common.HOME_DIR
                        + Common.TMP_DIR + "' directory.");
                return;
            }
            // Clean up tmp directory.
            for (File file : path.listFiles()) {
                file.delete();
            }

            // Create std. key file if there is none.
            copyStdKeysFilesIfNecessary();

            // Change the storage for the second run.
            Common.getPreferences().edit().putBoolean(
                    UseInternalStorage.toString(),
                    !isUseInternalStorage).commit();
        }
        // Restore the storage preference.
        Common.getPreferences().edit().putBoolean(
                UseInternalStorage.toString(),
                isUseInternalStorage).commit();

    }
    /**
     * Copy the standard key files ({@link Common#STD_KEYS} and
     * {@link Common#STD_KEYS_EXTENDED}) form assets to {@link Common#KEYS_DIR}.
     * Key files are simple text files. Any plain text editor will do the trick.
     * All key and dump data from this App is stored in
     * getExternalStoragePublicDirectory(Common.HOME_DIR) to remain
     * there after App uninstallation.
     * @see Common#KEYS_DIR
     * @see Common#HOME_DIR
     * @see Common#copyFile(InputStream, OutputStream)
     */
    private void copyStdKeysFilesIfNecessary() {
        File std = Common.getFileFromStorage(Common.HOME_DIR + "/" +
                Common.KEYS_DIR + "/" + Common.STD_KEYS);
        File extended = Common.getFileFromStorage(Common.HOME_DIR + "/" +
                Common.KEYS_DIR + "/" + Common.STD_KEYS_EXTENDED);
        AssetManager assetManager = getAssets();

        if (!std.exists()) {
            // Copy std.keys.
            try {
                InputStream in = assetManager.open(
                        Common.KEYS_DIR + "/" + Common.STD_KEYS);
                OutputStream out = new FileOutputStream(std);
                Common.copyFile(in, out);
                in.close();
                out.flush();
                out.close();
            } catch(IOException e) {
                Log.e(LOG_TAG, "Error while copying 'std.keys' from assets "
                        + "to external storage.");
            }
        }
        if (!extended.exists()) {
            // Copy extended-std.keys.
            try {
                InputStream in = assetManager.open(
                        Common.KEYS_DIR + "/" + Common.STD_KEYS_EXTENDED);
                OutputStream out = new FileOutputStream(extended);
                Common.copyFile(in, out);
                in.close();
                out.flush();
                out.close();
            } catch(IOException e) {
                Log.e(LOG_TAG, "Error while copying 'extended-std.keys' "
                        + "from assets to external storage.");
            }
        }
    }

    @OnClick(R.id.buttonMainReadTag)
    void onShowReadTag() {

    }

    @OnClick(R.id.buttonMainWriteTag)
    void onShowWriteTag() {

    }

    /**
     * Check if NFC adapter is enabled. If not, show the user a dialog and let
     * him choose between "Goto NFC Setting", "Use Editor Only" and "Exit App".
     * Also enable NFC foreground dispatch system.
     *
     * @see Common#enableNfcForegroundDispatch(Activity)
     */
    private void checkNfc() {
        // Check if the NFC hardware is enabled.
        if (Common.getNfcAdapter() != null
                && !Common.getNfcAdapter().isEnabled()) {
            // NFC is disabled.
            // Use as editor only?
            if (!Common.useAsEditorOnly()) {
                //  Show dialog.
                if (mEnableNfc == null) {
                    createNfcEnableDialog();
                }
                mEnableNfc.show();
            }
            // Disable read/write tag options.
            mReadTag.setEnabled(false);
            mWriteTag.setEnabled(false);
        } else {
            // NFC is enabled. Hide dialog and enable NFC
            // foreground dispatch.
            if (mOldIntent != getIntent()) {
                int typeCheck = Common.treatAsNewTag(getIntent(), this);
                if (typeCheck == -1 || typeCheck == -2) {
                    // Device or tag does not support MIFARE Classic.
                    // Run the only thing that is possible: The tag info tool.
                    Intent i = new Intent(this, TagInfoTool.class);
                    startActivity(i);
                }
                mOldIntent = getIntent();
            }
            Common.enableNfcForegroundDispatch(this);
            Common.setUseAsEditorOnly(false);
            if (mEnableNfc == null) {
                createNfcEnableDialog();
            }
            mEnableNfc.hide();
            if (Common.hasMifareClassicSupport() &&
                    Common.hasWritePermissionToExternalStorage(this)) {
                mReadTag.setEnabled(true);
                mWriteTag.setEnabled(true);
            }
        }
    }

    /**
     * Create a dialog that send user to NFC settings if NFC is off (and save
     * the dialog in {@link #mEnableNfc}). Alternatively the user can choos to
     * use the App in editor only mode or exit the App.
     */
    private void createNfcEnableDialog() {
        mEnableNfc = new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_nfc_not_enabled_title)
                .setMessage(R.string.dialog_nfc_not_enabled)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.action_nfc,
                        new DialogInterface.OnClickListener() {
                            @Override
                            @SuppressLint("InlinedApi")
                            public void onClick(DialogInterface dialog, int which) {
                                // Goto NFC Settings.
                                if (Build.VERSION.SDK_INT >= 16) {
                                    startActivity(new Intent(
                                            Settings.ACTION_NFC_SETTINGS));
                                } else {
                                    startActivity(new Intent(
                                            Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            }
                        })
                .setNeutralButton(R.string.action_editor_only,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Only use Editor.
                                Common.setUseAsEditorOnly(true);
                            }
                        })
                .setNegativeButton(R.string.action_exit_app,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // Exit the App.
                                finish();
                            }
                        }).create();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bind != null) {
            bind.unbind();
        }
    }
}
