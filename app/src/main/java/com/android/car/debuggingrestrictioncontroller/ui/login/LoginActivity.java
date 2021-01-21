package com.android.car.debuggingrestrictioncontroller.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.android.car.debuggingrestrictioncontroller.R;
import com.android.car.debuggingrestrictioncontroller.ui.ViewModelFactory;
import com.android.car.debuggingrestrictioncontroller.ui.token.TokenActivity;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

  private static final String TAG = LoginActivity.class.getSimpleName();
  private static final int REQUEST_TOKEN = 1;
  private LoginViewModel loginViewModel;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    loginViewModel = new ViewModelProvider(this, new ViewModelFactory())
        .get(LoginViewModel.class);

    final EditText usernameEditText = findViewById(R.id.username);
    final EditText passwordEditText = findViewById(R.id.password);
    final Button loginButton = findViewById(R.id.login);
    final Button nextButton = findViewById(R.id.next);
    final ProgressBar loadingProgressBar = findViewById(R.id.loading);

    loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
      @Override
      public void onChanged(@Nullable LoginFormState loginFormState) {
        if (loginFormState == null) {
          return;
        }
        loginButton.setEnabled(loginFormState.isDataValid());
        if (loginFormState.getUsernameError() != null) {
          usernameEditText.setError(getString(loginFormState.getUsernameError()));
        }
        if (loginFormState.getPasswordError() != null) {
          passwordEditText.setError(getString(loginFormState.getPasswordError()));
        }
      }
    });

    loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
      @Override
      public void onChanged(@Nullable LoginResult loginResult) {
        if (loginResult == null) {
          return;
        }
        loadingProgressBar.setVisibility(View.GONE);
        if (loginResult.getError() != null) {
          showSnackBar(R.string.not_signed_in);
          loginButton.setText(R.string.button_sign_in);
          nextButton.setEnabled(false);
        }
        if (loginResult.getSuccess() != null) {
          loginButton.setText(R.string.button_sign_out);
          nextButton.setEnabled(true);
        }
      }
    });

    TextWatcher afterTextChangedListener = new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // ignore
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        // ignore
      }

      @Override
      public void afterTextChanged(Editable s) {
        loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
            passwordEditText.getText().toString());
      }
    };
    usernameEditText.addTextChangedListener(afterTextChangedListener);
    passwordEditText.addTextChangedListener(afterTextChangedListener);
    passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          loginViewModel.login(usernameEditText.getText().toString(),
              passwordEditText.getText().toString());
        }
        return false;
      }
    });

    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loadingProgressBar.setVisibility(View.VISIBLE);
        if (!loginViewModel.isUserSignedIn()) {
          loginViewModel.login(usernameEditText.getText().toString(),
              passwordEditText.getText().toString());
        } else {
          loginViewModel.logout();
        }
      }
    });

    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), TokenActivity.class);
        startActivityForResult(intent, REQUEST_TOKEN);
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (requestCode == REQUEST_TOKEN) {
      if (resultCode == RESULT_OK) {
        showSnackBar(R.string.token_authorized);
      } else if (resultCode == RESULT_CANCELED) {
        showSnackBar(R.string.token_unauthorized);
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void showSnackBar(@StringRes Integer id) {
    final Snackbar snackbar = Snackbar
        .make(findViewById(R.id.login_container), id, Snackbar.LENGTH_SHORT);
    snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        snackbar.dismiss();
      }
    });
    snackbar.show();
  }
}