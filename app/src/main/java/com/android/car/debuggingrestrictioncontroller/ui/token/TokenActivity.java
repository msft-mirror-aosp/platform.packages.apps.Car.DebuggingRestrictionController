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
import com.android.car.debuggingrestrictioncontroller.R;
import java.util.HashMap;
import java.util.Map;

public class TokenActivity extends AppCompatActivity {

  private static final String TAG = TokenActivity.class.getSimpleName();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_token);

    final TextView agreementTextView = findViewById(R.id.agreement);
    final Button agreeButton = findViewById(R.id.agree);
    final Button disagreeButton = findViewById(R.id.disagree);
    final ProgressBar loadingProgressBar = findViewById(R.id.token_loading);

    Spanned agreementString = Html
        .fromHtml(getString(R.string.agreement_text), Html.FROM_HTML_MODE_LEGACY);
    agreementTextView.setText(agreementString);

    agreeButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Map<String, Object> query = new HashMap<>();
        loadingProgressBar.setVisibility(View.VISIBLE);
        setResult(Activity.RESULT_OK);
        finish();
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
