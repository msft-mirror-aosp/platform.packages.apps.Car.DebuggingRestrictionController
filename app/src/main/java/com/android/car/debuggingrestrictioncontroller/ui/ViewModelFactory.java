package com.android.car.debuggingrestrictioncontroller.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.android.car.debuggingrestrictioncontroller.ui.login.LoginViewModel;
import com.android.car.debuggingrestrictioncontroller.ui.token.TokenViewModel;

/**
 * ViewModel provider factory to instantiate LoginViewModel. Required given LoginViewModel has a
 * non-empty constructor
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

  @NonNull
  @Override
  @SuppressWarnings("unchecked")
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    if (modelClass.isAssignableFrom(LoginViewModel.class)) {
      return (T) new LoginViewModel();
    } else if (modelClass.isAssignableFrom(TokenViewModel.class)) {
      return (T) new TokenViewModel();
    } else {
      throw new IllegalArgumentException("Unknown ViewModel class");
    }
  }
}
