package es.ehu.ehernandez035.kea.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import es.ehu.ehernandez035.kea.R;
import es.ehu.ehernandez035.kea.RequestCallback;
import es.ehu.ehernandez035.kea.ServerRequest;
import es.ehu.ehernandez035.kea.SharedPrefManager;
import okhttp3.Response;

public class KonfigInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konfig_info);
        ImageButton editUsername = findViewById(R.id.userInfo_imageButton);
        ImageButton editEmail = findViewById(R.id.emailaInfo_imageButton);
        ImageButton editPassword = findViewById(R.id.pasahitzaInfo_imageButton);
        EditText username = findViewById(R.id.userInfo_ET);
        EditText email = findViewById(R.id.emailaInfo_ET);

        Button deleteAccount = findViewById(R.id.delete_account_btn);

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(KonfigInfoActivity.this);
                builder.setTitle(R.string.delete_account_dialog_title);
                builder.setMessage(R.string.delete_account_dialog_message);
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        new ServerRequest(KonfigInfoActivity.this, "http://elenah.duckdns.org/deleteAccount.php", new RequestCallback() {
                            @Override
                            public void onSuccess(Response response) throws IOException {
                                String result = response.body().string();
                                if ("0".equals(result)) {
                                    Snackbar.make(deleteAccount, R.string.delete_account_success, Snackbar.LENGTH_LONG).show();
                                    SharedPrefManager.getInstance(KonfigInfoActivity.this).logout();
                                    Intent intent = new Intent(KonfigInfoActivity.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Snackbar.make(deleteAccount, R.string.connection_error, Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }).execute();

                    }
                }).show();
            }
        });


        new ServerRequest(KonfigInfoActivity.this, "http://elenah.duckdns.org/userInfo.php", new RequestCallback() {
            @Override
            public void onSuccess(Response response) throws IOException {
                try {
                    JSONObject obj = new JSONObject(response.body().string());

                    if (obj.getInt("status") == 1) {
                        String usernamedata = obj.getString("username");
                        String emaildata = obj.getString("email");
                        email.setText(emaildata);
                        username.setText(usernamedata);
                    }
                } catch (JSONException e) {
                    Snackbar.make(editEmail, R.string.connection_error, Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }).execute();

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, String> params = new HashMap<>();
                params.put("username", username.getText().toString());
                new ServerRequest(KonfigInfoActivity.this, "http://elenah.duckdns.org/changeUsername.php", params, new RequestCallback() {
                    @Override
                    public void onSuccess(Response response) throws IOException {
                        String result = response.body().string();
                        if (result.equals("0")) {
                            Snackbar.make(editUsername, R.string.username_changed, Snackbar.LENGTH_LONG).show();
                        } else {
                            Snackbar.make(editEmail, R.string.username_taken, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }).execute();
            }
        });

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!RegisterActivity.isEmailValid(email.getText().toString())) {
                    email.setError(getString(R.string.email_invalid));
                    return;
                }
                Map<String, String> params = new HashMap<>();
                params.put("email", email.getText().toString());
                new ServerRequest(KonfigInfoActivity.this, "http://elenah.duckdns.org/changeEmail.php", params, new RequestCallback() {
                    @Override
                    public void onSuccess(Response response) throws IOException {
                        String result = response.body().string();
                        if (result.equals("0")) {
                            Snackbar.make(editUsername, R.string.email_changed, Snackbar.LENGTH_LONG).show();
                        } else {

                            Snackbar.make(editEmail, R.string.email_taken, Snackbar.LENGTH_LONG).show();
                        }
                    }

                }).execute();
            }
        });

        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(KonfigInfoActivity.this);
                builder.setMessage(R.string.change_pass).setTitle(R.string.pass_changeTittle);
                EditText oldPasswordText = new EditText(KonfigInfoActivity.this);
                oldPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                builder.setView(oldPasswordText);
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Map<String, String> params = new HashMap<>();
                        params.put("password", oldPasswordText.getText().toString());
                        new ServerRequest(KonfigInfoActivity.this, "http://elenah.duckdns.org/check_password.php", params, new RequestCallback() {
                            @Override
                            public void onSuccess(Response response) throws IOException {
                                String body = response.body().string();
                                if (!body.equals("0")) {
                                    Snackbar.make(email, "Incorrect password", Snackbar.LENGTH_LONG).show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(KonfigInfoActivity.this);
                                    builder.setMessage(R.string.new_password).setTitle(R.string.pass_changeTittle);
                                    EditText newPasswordText = new EditText(KonfigInfoActivity.this);
                                    newPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                    builder.setView(newPasswordText);
                                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(KonfigInfoActivity.this);
                                            builder.setMessage(R.string.confirm_newPass).setTitle(R.string.pass_changeTittle);
                                            EditText verifyPasswordText = new EditText(KonfigInfoActivity.this);
                                            verifyPasswordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                                            builder.setView(verifyPasswordText);
                                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    if (verifyPasswordText.getText().toString().equals(newPasswordText.getText().toString())) {
                                                        if (!RegisterActivity.isPasswordValid(newPasswordText.getText().toString())) {
                                                            Snackbar.make(editEmail, R.string.error_invalid_password, Snackbar.LENGTH_LONG).show();
                                                            return;
                                                        }
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put("password", newPasswordText.getText().toString());
                                                        new ServerRequest(KonfigInfoActivity.this, "http://elenah.duckdns.org/changePassword.php", params, new RequestCallback() {
                                                            @Override
                                                            public void onSuccess(Response response) throws IOException {
                                                                String result = response.body().string();
                                                                if (result.equals("0")) {
                                                                    Snackbar.make(editEmail, R.string.password_changed, Snackbar.LENGTH_LONG).show();
                                                                } else {
                                                                    Snackbar.make(editEmail, R.string.connection_error, Snackbar.LENGTH_LONG).show();
                                                                }
                                                            }
                                                        }).execute();
                                                    } else {
                                                        Snackbar.make(editEmail, R.string.error_confirmation_password, Snackbar.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                            builder.create().show();
                                        }
                                    });
                                    builder.create().show();
                                }
                            }
                        }).execute();
                    }
                });
                builder.create().show();
            }
        });
    }


}
