package com.android.car.debuggingrestrictioncontroller.ui.token;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.android.car.debuggingrestrictioncontroller.R;
import com.android.car.debuggingrestrictioncontroller.ui.ViewModelFactory;
import java.util.HashMap;
import java.util.Map;

public class TokenActivity extends AppCompatActivity {

  private static final String TAG = TokenActivity.class.getSimpleName();
  private TokenViewModel tokenViewModel;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_token);
    tokenViewModel = new ViewModelProvider(this, new ViewModelFactory()).get(
        TokenViewModel.class);

    final TextView agreementTextView = findViewById(R.id.agreement);
    final Button agreeButton = findViewById(R.id.agree);
    final Button disagreeButton = findViewById(R.id.disagree);
    final ProgressBar loadingProgressBar = findViewById(R.id.token_loading);

    Spanned agreementString = Html
        .fromHtml(getString(R.string.agreement_text), Html.FROM_HTML_MODE_LEGACY);
    agreementTextView.setText(agreementString);

    tokenViewModel.getTokenResult().observe(this, new Observer<TokenResult>() {
      @Override
      public void onChanged(TokenResult result) {
        if (result == null) {
          return;
        }
        loadingProgressBar.setVisibility(View.GONE);
        if (result.getError() != null) {
          setResult(Activity.RESULT_CANCELED);
          finish();
        }
        if (result.getSuccess() != null) {
          setResult(Activity.RESULT_OK);
          finish();
        }
      }
    });

    agreeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Map<String, Object> query = new HashMap<>();
        loadingProgressBar.setVisibility(View.VISIBLE);
        tokenViewModel.requestAccessToken("", "", query);
      }
    });

    disagreeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        setResult(Activity.RESULT_CANCELED);
        finish();
      }
    });
  }
}
