package com.android.car.debuggingrestrictioncontroller.ui.token;

import androidx.annotation.Nullable;

public class TokenResult {

  @Nullable
  private TokenView success;

  @Nullable
  private String error;

  TokenResult(@Nullable TokenView success) {
    this.success = success;
  }

  TokenResult(@Nullable String error) {
    this.error = error;
  }

  @Nullable
  TokenView getSuccess() {
    return success;
  }

  @Nullable
  String getError() {
    return error;
  }
}
