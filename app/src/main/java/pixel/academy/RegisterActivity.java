package pixel.academy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import pixel.academy.api.OTPVerification;
import pixel.academy.api.RegisterUser;
import pixel.academy.helper.GenerateUniqueId;
import pixel.academy.helper.OnTaskCompleted;
import pixel.academy.model.User;
import pixel.academy.network.InternetConnectionDetector;

import static pixel.academy.app.Global.FIRST_RUN;
import static pixel.academy.app.Global.TOKEN;
import static pixel.academy.app.MyApplication.prefs;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, OTPFragment.PasswordFragmentListener, OnTaskCompleted
{

    private static final int READ_SMS_PERMISSION_REQUEST_CODE = 1;

    private int back_pressed = 0;

    private static User user;

    private EditText editName, editPhoneNo;
    private TextView tvStatus;
    private Button btnSignUp, btnConfirmationCode;
    private ProgressBar progressBar;
    private RelativeLayout relative_main;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (prefs.getBoolean(FIRST_RUN, true))
        {
            prefs.edit().putBoolean(FIRST_RUN, false).apply();
        }

        findViewById();
        hideKeyboard();
        permissionCheckerReadSMS();
    }


    private void findViewById()
    {

        editName = (EditText) findViewById(R.id.editName);
        editPhoneNo = (EditText) findViewById(R.id.editPhoneNumber);
        tvStatus = (TextView) findViewById(R.id.status);
        progressBar = (ProgressBar) findViewById(R.id.pbLoading);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnConfirmationCode = (Button) findViewById(R.id.btnConfirmationCode);
        relative_main = (RelativeLayout) findViewById(R.id.relative_main);
    }


    @Override
    public void onClick(View view)
    {

        switch (view.getId())
        {

            case R.id.btnSignUp:

                if(validateForm())
                {

                    if (!new InternetConnectionDetector(getApplicationContext()).isConnected())
                    {
                        makeSnackbar("Internet Connection Fail");
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);
                    tvStatus.setText(String.valueOf("Waiting for OTP"));

                    user = initUserObject();

                    btnConfirmationCode.setVisibility(View.GONE);
                    new OTPVerification(getApplicationContext(), this).execute(user);
                }

                break;

            case R.id.btnConfirmationCode:

                FragmentManager fm = getSupportFragmentManager();

                OTPFragment dialogFragment = new OTPFragment();
                dialogFragment.setListener(this);
                dialogFragment.setRetainInstance(true);
                dialogFragment.show(fm, "OTPFragment");

                break;
        }
    }


    @Override
    public void onDialogPositiveClick(android.support.v4.app.DialogFragment dialog, String new_password)
    {

        if (user.getConfirmationCode().equals(new_password))
        {
            btnConfirmationCode.setVisibility(View.VISIBLE);
            new RegisterUser(getApplicationContext(), this).execute(user);
            tvStatus.setText(String.valueOf("Registering ..."));
        }

        else
        {
            makeSnackbar("Verification Fail. Try Again");
        }
    }


    @Override
    public void onDialogNegativeClick(android.support.v4.app.DialogFragment dialog)
    {
        // Do nothing
    }


    private boolean validateForm()
    {

        if(editName.getText().toString().trim().length() < 3)
        {

            makeSnackbar("Name should be minimum 3 characters");
            return false;
        }

        if(editPhoneNo.getText().toString().trim().length() != 10)
        {

            makeSnackbar("Invalid Phone Number");
            return false;
        }

        /*if(!isValidEmail(editEmail.getText().toString()))
        {
            makeSnackbar("Invalid Email");
            return false;
        }*/

        return  true;
    }


    private void makeSnackbar(String msg)
    {

        Snackbar snackbar = Snackbar.make(relative_main, msg, Snackbar.LENGTH_SHORT);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.myPrimaryColor));
        snackbar.show();
    }


    private User initUserObject()
    {

        WifiManager m_wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        User user = new User();

        user.name = editName.getText().toString();
        user.phone_number = editPhoneNo.getText().toString();
        user.fcm_reg_id = prefs.getString(TOKEN, "");
        user.device_id = String.valueOf(m_wm.getConnectionInfo().getMacAddress());
        user.confirmation_code = String.valueOf(GenerateUniqueId.getRandomNo(999999, 100000));

        return user;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {

            case android.R.id.home:

                if(back_pressed == 0)
                {
                    back_pressed++;
                    Toast.makeText(getApplicationContext(), "Press Back Button again to Exit", Toast.LENGTH_LONG).show();
                }

                else
                {
                    finish();
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed()
    {

        if(back_pressed == 0)
        {
            back_pressed++;
            Toast.makeText(getApplicationContext(), "Press Back Button again to Exit", Toast.LENGTH_LONG).show();
        }

        else
        {
            finish();
        }
    }


    private boolean permissionCheckerReadSMS() {

        if (!checkPermissionReadSMS()) {
            requestPermissionReadSMS();
            return false;
        }

        return true;
    }


    private boolean checkPermissionReadSMS()
    {

        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);

        if (result == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }

        else
        {
            return false;
        }
    }


    private void requestPermissionReadSMS(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS))
        {
            makeToast("SMS permission allows us to read or receive SMS. Please allow in App Settings for read or receive SMS.");
        }

        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, READ_SMS_PERMISSION_REQUEST_CODE);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {

        switch (requestCode)
        {

            case READ_SMS_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeToast("Permission Granted");
                } else {
                    makeToast("Permission Denied");
                }

                break;
        }
    }


    private void makeToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onTaskCompleted(boolean flag, int code, String message)
    {

        try
        {

            if (flag && code == 100)
            {
                btnConfirmationCode.setVisibility(View.VISIBLE);
                new RegisterUser(getApplicationContext(), this).execute(user);
            }

            else if (flag && code == 200)
            {
                startActivity(new Intent(RegisterActivity.this, ServiceLocationActivity.class));
                finish();
            }

            else
            {

                tvStatus.setText("");
                progressBar.setVisibility(View.GONE);
                btnConfirmationCode.setVisibility(View.GONE);
                makeSnackbar("Failed to Register. Try Again");
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void hideKeyboard() {

        editPhoneNo.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (editPhoneNo.getText().toString().trim().length() == 10) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(relative_main.getWindowToken(), 0);
                }
            }
        });
    }
}