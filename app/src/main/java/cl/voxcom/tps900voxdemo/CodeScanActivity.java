package cl.voxcom.tps900voxdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.sudar.zxingorient.Barcode;
import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

public class CodeScanActivity extends AppCompatActivity {

    TextView tvCardText,tvCardText2;
    Button btStartScan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_scan);

        tvCardText = (TextView)findViewById(R.id.tv_code_text);
        tvCardText2 = (TextView)findViewById(R.id.tv_code_text2);

        btStartScan = (Button)findViewById(R.id.btn_scan);

        btStartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQRScanner();
            }
        });

    }

    private void startQRScanner() {

       // new ZxingOrient(CodeScanActivity.this).initiateScan();

        ZxingOrient integrator = new ZxingOrient(CodeScanActivity.this);
        integrator
                //.setIcon(R.drawable.custom_icon)   // Sets the custom icon
                .setToolbarColor("#AA3F51B5")       // Sets Tool bar Color
                .setInfoBoxColor("#AA3F51B5")       // Sets Info box color
                .setInfo("Scanear cualquier tipo de codigo")
                .showInfoBox(true) // Doesn't display the info box
                .setBeep(true)  // Doesn't play beep sound
                .setVibration(false) // Sets info message in the info box
                .initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //IntentResult result =   IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        ZxingOrientResult scanResult =
                ZxingOrient.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            if (scanResult.getContents() == null) {
                Toast.makeText(this,    "Cancelled",Toast.LENGTH_LONG).show();
            } else {
                updateText(scanResult.getContents(),scanResult.getFormatName());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateText(String scanCode,String format) {
        tvCardText.setText(scanCode);
        tvCardText2.setText(format);

    }
}
