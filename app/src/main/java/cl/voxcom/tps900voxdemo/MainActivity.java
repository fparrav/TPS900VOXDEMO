package cl.voxcom.tps900voxdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button btnPrintPDF417, btncard,btnopencodescan, btnPrintVoucher ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnPrintPDF417  = findViewById(R.id.btnPrintPDF417);

        btnPrintPDF417.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(v.getContext(), PrintPDF417Activity.class);
                //           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                v.getContext().startActivity(intent);

            }
        });

        btncard = findViewById(R.id.btncard);
        btncard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CardScanActivity.class);
                //           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                v.getContext().startActivity(intent);
            }
        });

        btnopencodescan = findViewById(R.id.btnopencodescan);

        btnopencodescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CodeScanActivity.class);
                //           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                v.getContext().startActivity(intent);
            }
        });

        btnPrintVoucher = findViewById(R.id.btnPrintVoucher);

        btnPrintVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), VoucherActivity.class);
                view.getContext().startActivity(intent);
            }
        });


    }
}
