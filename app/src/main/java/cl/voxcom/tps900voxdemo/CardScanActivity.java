package cl.voxcom.tps900voxdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.telpo.tps550.api.TelpoException;
import com.telpo.tps550.api.magnetic.MagneticCard;

public class CardScanActivity extends AppCompatActivity {

    Button btnmagnetic;
    TextInputEditText et_track1, et_track2, et_track3;
    ProgressBar pbscan;
    TextView tvProgress;
    boolean reading = false;
    Handler handler;
    Thread readThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_scan);


        btnmagnetic = findViewById(R.id.btnmagnetic);
        et_track1 = findViewById(R.id.et_track1);
        et_track2 = findViewById(R.id.et_track2);
        et_track3 = findViewById(R.id.et_track3);
        et_track1.setEnabled(false);
        et_track2.setEnabled(false);
        et_track3.setEnabled(false);


        tvProgress = findViewById(R.id.tvProgress);
        pbscan = findViewById(R.id.pbscan);

        enableProgress(false);

        btnmagnetic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (reading) {
                    enableProgress(false);
                    et_track1.setText("");
                    et_track2.setText("");
                    et_track3.setText("");
                } else {
                    et_track1.setText("...leyendo");
                    et_track2.setText("...leyendo");
                    et_track3.setText("...leyendo");
                    enableProgress(true);
                    readThread = new ReadThread();
                    readThread.start();
                }

            }
        });


        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
//                et_track1.setText("");
//                et_track2.setText("");
//                et_track3.setText("");
                String[] TracData = (String[]) msg.obj;
                for (int i = 0; i < 3; i++) {
                    if (TracData[i] != null) {
                        switch (i) {
                            case 0:
                                et_track1.setText(TracData[i]);
                                break;
                            case 1:
                                et_track2.setText(TracData[i]);
                                break;
                            case 2:
                                et_track3.setText(TracData[i]);
                                break;
                        }


                    }else{
                        switch (i) {
                            case 0:
                                et_track1.setText("");
                                break;
                            case 1:
                                et_track2.setText("");
                                break;
                            case 2:
                                et_track3.setText("");
                                break;
                        }


                    }

                    if(i==2){
                        enableProgress(false);

                    }
                }
            }

        };

        try {
            MagneticCard.open(CardScanActivity.this);
        } catch (Exception e) {
            btnmagnetic.setEnabled(false);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Error al abrir dispositivo lector tarjeta magnetica");
            alertDialog.setPositiveButton(R.string.dialog_comfirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CardScanActivity.this.finish();
                }
            });
            alertDialog.show();
        }

    }


    private void enableProgress(boolean status) {

        if (status) {
            pbscan.setVisibility(View.VISIBLE);
            tvProgress.setVisibility(View.VISIBLE);
            btnmagnetic.setText("Cancelar");
            reading = status;
        } else {
            pbscan.setVisibility(View.GONE);
            tvProgress.setVisibility(View.GONE);
            btnmagnetic.setText("Leer Tarjeta");
            reading = status;

            if (readThread != null) {
                readThread.interrupt();
                readThread = null;
                readThread = null;
            }


        }


    }


    protected void onDestroy() {
        if (readThread != null) {
            readThread.interrupt();
        }
        MagneticCard.close();
        super.onDestroy();

    }

    private class ReadThread extends Thread {
        String[] TracData = null;

        @Override
        public void run() {
            MagneticCard.startReading();
            while (!Thread.interrupted()) {
                try {
                    TracData = MagneticCard.check(1000);
                    handler.sendMessage(handler.obtainMessage(1, TracData));
                    MagneticCard.startReading();
                } catch (TelpoException e) {
                }
            }
        }

    }

}
