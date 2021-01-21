package com.android.car.debuggingrestrictioncontroller.ui.token;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.HashMap;
import java.util.Map;

public class TokenViewModel extends ViewModel {

  private static final String TAG = TokenViewModel.class.getSimpleName();
  private final MutableLiveData<TokenResult> tokenResult = new MutableLiveData<>();

  LiveData<TokenResult> getTokenResult() {
    return tokenResult;
  }

  public void requestAccessToken(@NonNull String hostName, @NonNull String apiName,
      @NonNull Map<String, Object> query) {
    tokenResult.postValue(new TokenResult(new TokenView("OK", new HashMap<>())));
  }
}
