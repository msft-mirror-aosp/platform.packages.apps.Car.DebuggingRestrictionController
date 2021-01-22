package com.android.car.debuggingrestrictioncontroller.ui.token;

import android.util.Base64;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.functions.FirebaseFunctions;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class TokenViewModel extends ViewModel {

  private static final String TAG = TokenViewModel.class.getSimpleName();
  private static final String FIELD_NONCE = "nonce";
  private static final String FIELD_TOKEN = "token";

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private final FirebaseFunctions firebaseFunctions = FirebaseFunctions.getInstance();
  private final MutableLiveData<TokenResult> tokenResult = new MutableLiveData<>();

  LiveData<TokenResult> getTokenResult() {
    return tokenResult;
  }

  public void requestAccessToken(@NonNull String hostName, @NonNull String apiName,
      @NonNull Map<String, Object> query) {
    byte[] nonceBytes = new byte[16];
    SECURE_RANDOM.nextBytes(nonceBytes);
    final String nonce = Base64.encodeToString(nonceBytes, Base64.DEFAULT);
    query.put(FIELD_NONCE, nonce);

    firebaseFunctions
        .getHttpsCallable(apiName)
        .call(query)
        .continueWith(task -> {
          Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
          return (String) result.get(FIELD_TOKEN);
        })
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            Log.d(TAG, "Token: " + task.getResult());
            tokenResult.postValue(new TokenResult(new TokenView("OK", new HashMap<>())));
          } else {
            tokenResult.postValue(new TokenResult(task.getException().getMessage()));
          }
        });
  }
}
