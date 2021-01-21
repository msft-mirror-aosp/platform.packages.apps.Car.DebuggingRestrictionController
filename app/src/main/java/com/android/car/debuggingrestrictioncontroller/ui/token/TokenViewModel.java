package com.android.car.debuggingrestrictioncontroller.ui.token;

import android.util.Base64;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.android.car.debuggingrestrictioncontroller.BuildConfig;
import com.android.car.debuggingrestrictioncontroller.auth.TokenPayload;
import com.android.car.debuggingrestrictioncontroller.auth.TokenValidator;
import com.google.firebase.functions.FirebaseFunctions;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

public class TokenViewModel extends ViewModel {

  private static final String TAG = TokenViewModel.class.getSimpleName();
  private static final String TOKEN_ISSUER_API_NAME = BuildConfig.TOKEN_ISSUER_API_NAME;

  private static final String FIELD_NONCE = "nonce";
  private static final String FIELD_TOKEN = "token";

  private static final SecureRandom SECURE_RANDOM = new SecureRandom();
  private final FirebaseFunctions firebaseFunctions = FirebaseFunctions.getInstance();
  private final MutableLiveData<TokenResult> tokenResult = new MutableLiveData<>();

  LiveData<TokenResult> getTokenResult() {
    return tokenResult;
  }

  public void requestAccessToken(
      @NonNull Map<String, Object> query) {
    byte[] nonceBytes = new byte[16];
    SECURE_RANDOM.nextBytes(nonceBytes);
    final String nonce = Base64.encodeToString(nonceBytes, Base64.DEFAULT);
    query.put(FIELD_NONCE, nonce);

    firebaseFunctions
        .getHttpsCallable(TOKEN_ISSUER_API_NAME)
        .call(query)
        .continueWith(task -> {
          @SuppressWarnings("unchecked")
          Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
          return (String) result.get(FIELD_TOKEN);
        })
        .addOnCompleteListener(task -> {
          if (task.isSuccessful()) {
            try {
              TokenPayload validatedPayload = TokenValidator
                  .parseAndVerify(task.getResult(), nonce);
              HashMap<String, Boolean> approvedRestrictions = validatedPayload.getRestrictions();
              tokenResult.postValue(new TokenResult(new TokenView("OK", approvedRestrictions)));
            } catch (GeneralSecurityException e) {
              tokenResult.postValue(new TokenResult("Invalid access token"));
            }
          } else {
            tokenResult.postValue(new TokenResult(task.getException().getMessage()));
          }
        });
  }
}
