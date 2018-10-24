package com.myapp.bipul.traceyou;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ForgetPasswordActivity extends AppCompatActivity {

    LinearLayout get_otp_linear_layout, linear_layout;
    Button nextBtn, btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        get_otp_linear_layout=(LinearLayout)findViewById(R.id.get_otp_linear_layout);
        linear_layout=(LinearLayout)findViewById(R.id.linear_layout);
        btn_submit=(Button) findViewById(R.id.btn_submit);
        nextBtn=(Button)findViewById(R.id.nextBtn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_otp_linear_layout.setVisibility(View.GONE);
                linear_layout.setVisibility(View.VISIBLE);
            }
        });




    }
}
