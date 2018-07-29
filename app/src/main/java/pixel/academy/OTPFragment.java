package pixel.academy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class OTPFragment extends DialogFragment
{

    private ViewHolder viewHolder;

    private ViewHolder getViewHolder()
    {
        return viewHolder;
    }

    PasswordFragmentListener listener;

    public void setListener(PasswordFragmentListener listener)
    {
        this.listener = listener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_otp, null);

        viewHolder = new ViewHolder();
        viewHolder.populate(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
              .setPositiveButton(R.string.Verify, null)
              .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {

                  public void onClick(DialogInterface dialog, int id)
                  {

                      OTPFragment.this.getDialog().cancel();

                      if (listener != null)
                      {
                          listener.onDialogNegativeClick(OTPFragment.this);
                      }
                  }
              });

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface)
            {

                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view)
                {

                    if (dataIsValid())
                    {

                        String new_password = getViewHolder().passwordEditText.getText().toString();

                        if (listener != null)
                        {
                          listener.onDialogPositiveClick(OTPFragment.this, new_password);
                          dialog.dismiss();
                        }
                    }

                    else
                    {
                        showValidationErrorToast();
                    }
                }
            });
          }
      });

      return dialog;
    }

    // endregion
    // region Private

    private boolean dataIsValid()
    {

        boolean validData = true;

        String new_password = getViewHolder().passwordEditText.getText().toString();

        if(new_password.trim().length() != 6)
        {
            validData = false;
        }

        return validData;
    }


    private void showValidationErrorToast()
    {
        Toast.makeText(getActivity(), "Invalid OTP", Toast.LENGTH_SHORT).show();
    }

    // endregion
    // region Interfaces

    public interface PasswordFragmentListener
    {
        void onDialogPositiveClick(DialogFragment dialog, String new_password);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    // endregion
    // region Inner classes

    static class ViewHolder
    {

        EditText passwordEditText;

        public void populate(View v)
        {
            passwordEditText = (EditText) v.findViewById(R.id.editPassword);
        }
    }
}