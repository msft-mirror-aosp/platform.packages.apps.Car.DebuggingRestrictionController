package com.android.car.debuggingrestrictioncontroller.ui.token;

import java.util.HashMap;

/** Class exposing access token details to the UI. */
class TokenView {

  private final String message;
  private final HashMap<String, Boolean> approvedRestrictions;

  TokenView(String message, HashMap<String, Boolean> approvedRestrictions) {
    this.message = message;
    this.approvedRestrictions = approvedRestrictions;
  }

  String getMessage() {
    return message;
  }

  HashMap<String, Boolean> getApprovedRestrictions() {
    return this.approvedRestrictions;
  }
}
