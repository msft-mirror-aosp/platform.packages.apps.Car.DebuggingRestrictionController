package com.android.car.debuggingrestrictioncontroller.ui.login;

import android.util.Patterns;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.android.car.debuggingrestrictioncontroller.R;

public class LoginViewModel extends ViewModel {

  private static final String TAG = LoginViewModel.class.getSimpleName();
  private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
  private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
  private boolean isUserSignedIn = false;

  LiveData<LoginFormState> getLoginFormState() {
    return loginFormState;
  }

  LiveData<LoginResult> getLoginResult() {
    return loginResult;
  }

  public boolean isUserSignedIn() {
    return isUserSignedIn;
  }

  public void login(String email, String password) {
    isUserSignedIn = true;
    updateUI(true);
  }

  public void logout() {
    isUserSignedIn = false;
    updateUI(false);
  }

  private void updateUI(boolean userSignedIn) {
    if (userSignedIn) {
      loginResult.postValue(
          new LoginResult(
              new LoggedInUserView("Anonymous")));
    } else {
      loginResult.postValue(new LoginResult(R.string.not_signed_in));
    }
  }

  public void loginDataChanged(String username, String password) {
    if (!isUserNameValid(username)) {
      loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
    } else if (!isPasswordValid(password)) {
      loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
    } else {
      loginFormState.setValue(new LoginFormState(true));
    }
  }

  // A placeholder username validation check
  private boolean isUserNameValid(String username) {
    if (username == null) {
      return false;
    }
    if (username.contains("@")) {
      return Patterns.EMAIL_ADDRESS.matcher(username).matches();
    } else {
      return !username.trim().isEmpty();
    }
  }

  // A placeholder password validation check
  private boolean isPasswordValid(String password) {
    return password != null && password.trim().length() > 5;
  }
}