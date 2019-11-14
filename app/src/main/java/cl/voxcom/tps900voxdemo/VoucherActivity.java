package cl.voxcom.tps900voxdemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.telpo.tps550.api.TelpoException;

import com.telpo.tps550.api.util.StringUtil;
import com.telpo.tps550.api.util.SystemUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import com.telpo.tps550.api.printer.UsbThermalPrinter;

public class VoucherActivity extends AppCompatActivity {


    private static String printVersion;
    private final int NOPAPER = 3;
    private final int LOWBATTERY = 4;
    private final int PRINTVERSION = 5;
    private final int PRINTBARCODE = 6;
    private final int PRINTQRCODE = 7;
    private final int PRINTPAPERWALK = 8;
    private final int PRINTCONTENT = 9;
    private final int CANCELPROMPT = 10;
    private final int PRINTERR = 11;
    private final int OVERHEAT = 12;
    private final int MAKER = 13;
    private final int PRINTPICTURE = 14;
    private final int EXECUTECOMMAND = 15;

    private LinearLayout print_text, print_pic, print_comm;
    private TextView text_index, pic_index, comm_index, textPrintVersion;
    MyHandler handler;

    private String Result;
    private Boolean nopaper = false;
    private boolean LowBattery = false;

    BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;

    public static String barcodeStr;
    public static String qrcodeStr;
    public static int paperWalk;
    public static String printContent;
    private int leftDistance = 0;
    private int lineDistance;
    private int wordFont;
    private int printGray;
    private ProgressDialog progressDialog;
    private final static int MAX_LEFT_DISTANCE = 255;
    ProgressDialog dialog;
    private String picturePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/111.bmp";

    private String Arabic, z, y, x;


    private String data = "|011   T I C K E T  P R O S U P U E S T O|011========================================|01100012/012 10-11-19 23:07 0146 78853601|011|011----------------------------------------|011CODIGO        DESCRIPCION         VALOR|011----------------------------------------|0112303594894405 BIKINI LISO            990 |0112126357955706 POLERON 8048         7.990 |0112104136304808 Jns 402011 g        21.411 |011----------------------------------------|011TOTAL                             30.391|011|011|110100131911101111382|011           1911101111382|011|011 V A L I D O  S O L O  P O R  E L  D I A|011|011";
    UsbThermalPrinter mUsbThermalPrinter = new UsbThermalPrinter(VoucherActivity.this);


    private EditText et_voucher;
    private Button btn_printVoucher;


    private final int NOBLACKBLOCK = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        wordFont = 1;
        printGray = 5;
        leftDistance = 4;
        lineDistance = 0;

        btn_printVoucher = findViewById(R.id.btn_printVoucher);
        et_voucher = findViewById(R.id.et_voucher);

        et_voucher.setText(data);


        btn_printVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrintVoucher(data);
            }
        });

        handler = new MyHandler();


        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    mUsbThermalPrinter.start(0);
                    mUsbThermalPrinter.reset();
                    printVersion = mUsbThermalPrinter.getVersion();
                } catch (TelpoException e) {
                    e.printStackTrace();
                } finally {
                    if (printVersion != null) {
                        Message message = new Message();
                        message.what = PRINTVERSION;
                        message.obj = "1";
                        handler.sendMessage(message);
                    } else {
                        Message message = new Message();
                        message.what = PRINTVERSION;
                        message.obj = "0";
                        handler.sendMessage(message);
                    }
                    // mUsbThermalPrinter.stop();
                }
            }
        }).start();

    }


    private boolean PrintVoucher(String voucher) {

        boolean status = false;

        String lineasBoleta[] = voucher.split("\\|");

        printBlankLines(8);


        for (int n = 0; n < lineasBoleta.length; n++) {

            // String buffertext = "";

            if (lineasBoleta[n].length() > 2) {

                String tipolinea = lineasBoleta[n].substring(0, 2);


                switch (tipolinea) {

                    case "01": {

                        int lines = Integer.valueOf(lineasBoleta[n].substring(2, 3));
                        if(lines>1) {
                            printBlankLines(lines - 1);
                        }
                        if (lineasBoleta[n].length() > 3) {
                            PrintContent(lineasBoleta[n].substring(3));

                        }

                        break;
                    }
                    case "11": {

                        String codetype = lineasBoleta[n].substring(2, 4);
                        int length = Integer.valueOf(lineasBoleta[n].substring(4, 8));

                        switch (codetype) {

                            case "01": {

                                printGray = 5;
                                barcodeFormat = BarcodeFormat.EAN_13;
                                PrintBarcode(lineasBoleta[n].substring(8));

                                break;
                            }
                            case "02": {

                                printGray = 5;
                                barcodeFormat = BarcodeFormat.CODE_39;
                                PrintBarcode(lineasBoleta[n].substring(8));
                                break;

                            }
                        /*case "03": {

                            printPDF417(lineasBoleta[n].substring(8), 1);

                            break;
                        }*/
                        }


                        break;
                    }
                    default: {

                        PrintContent(lineasBoleta[n]);

                        /*                            while (!printtext(buffertext)){

                         *//* try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*//*
                            }*/
                        break;
                    }
                }


                status = true;
            }


        }
        printBlankLines(12);
        return status;
    }

    private void printBlankLines(int lines) {

        if (lines < 1 || lines > 255) {
            Toast.makeText(VoucherActivity.this, getString(R.string.walk_paper_intput_value), Toast.LENGTH_LONG).show();
            return;
        }

        // paperWalk = data;

        if (LowBattery == true) {
            handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
        } else {
            if (!nopaper) {
                progressDialog = ProgressDialog.show(VoucherActivity.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                handler.sendMessage(handler.obtainMessage(PRINTPAPERWALK, 1, lines, null));
            } else {
                Toast.makeText(VoucherActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
            }
        }
    }


    private void PrintBarcode(String barcode) {


        // barcodeStr = barcode;
        if (barcode == null || barcode.length() == 0) {
            Toast.makeText(VoucherActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
            return;
        }
        if (LowBattery == true) {
            handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
        } else {
            if (!nopaper) {
                progressDialog = ProgressDialog.show(VoucherActivity.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                handler.sendMessage(handler.obtainMessage(PRINTBARCODE, 1, 0, barcode));
            } else {
                Toast.makeText(VoucherActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
            }
        }

    }


    private void PrintContent(String mprintContent) {


        if (mprintContent == null || mprintContent.length() == 0) {
            Toast.makeText(VoucherActivity.this, getString(R.string.empty), Toast.LENGTH_LONG).show();
            return;
        }
        if (LowBattery == true) {
            handler.sendMessage(handler.obtainMessage(LOWBATTERY, 1, 0, null));
        } else {
            if (!nopaper) {
                progressDialog = ProgressDialog.show(VoucherActivity.this, getString(R.string.bl_dy), getString(R.string.printing_wait));

                // printContent = mprintContent;
                handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, mprintContent));

            } else {
                Toast.makeText(VoucherActivity.this, getString(R.string.ptintInit), Toast.LENGTH_LONG).show();
            }
        }

    }


    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOPAPER:
                    noPaperDlg();
                    break;
                case LOWBATTERY:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(VoucherActivity.this);
                    alertDialog.setTitle(R.string.operation_result);
                    alertDialog.setMessage(getString(R.string.LowBattery));
                    alertDialog.setPositiveButton(getString(R.string.dialog_comfirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alertDialog.show();
                    break;
                case PRINTVERSION:
                    // dialog.dismiss();
                    if (msg.obj.equals("1")) {
                        //textPrintVersion.setText(printVersion);
                    } else {
                        Toast.makeText(VoucherActivity.this, R.string.operation_fail, Toast.LENGTH_LONG).show();
                    }
                    break;
                case PRINTBARCODE:

                    Thread t2 = new barcodePrintThread((String) msg.obj);
                    t2.start();

                    try {
                        t2.join();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //t2.destroy();

                    break;
                case PRINTQRCODE:
                    new qrcodePrintThread().start();
                    break;
                case PRINTPAPERWALK:

                    Thread t = new paperWalkPrintThread(msg.arg2);
                    t.start();

                    try {
                        t.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case PRINTCONTENT:

                    Thread t1 = new contentPrintThread((String) msg.obj);
                    t1.start();

                    try {
                        t1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
              /*  case MAKER:
                    new MakerThread().start();
                    break;
              */
                case PRINTPICTURE:
                    new printPicture().start();
                    break;
                case CANCELPROMPT:
                    if (progressDialog != null && !VoucherActivity.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
               /* case EXECUTECOMMAND:
                    new executeCommand().start();
                    break;
               */
                case OVERHEAT:
                    AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(VoucherActivity.this);
                    overHeatDialog.setTitle(R.string.operation_result);
                    overHeatDialog.setMessage(getString(R.string.overTemp));
                    overHeatDialog.setPositiveButton(getString(R.string.dialog_comfirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    overHeatDialog.show();
                    break;
                default:
                    Toast.makeText(VoucherActivity.this, "Print Error!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


    /* Called when the application resumes */
    @Override
    protected void onResume() {
        super.onResume();
    }

    private final BroadcastReceiver printReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_NOT_CHARGING);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                //TPS390 can not print,while in low battery,whether is charging or not charging
                if (SystemUtil.getDeviceType() == StringUtil.DeviceModelEnum.TPS390.ordinal()) {
                    if (level * 5 <= scale) {
                        LowBattery = true;
                    } else {
                        LowBattery = false;
                    }
                } else {
                    if (status != BatteryManager.BATTERY_STATUS_CHARGING) {
                        if (level * 5 <= scale) {
                            LowBattery = true;
                        } else {
                            LowBattery = false;
                        }
                    } else {
                        LowBattery = false;
                    }
                }
            }
            //Only use for TPS550MTK devices
            else if (action.equals("android.intent.action.BATTERY_CAPACITY_EVENT")) {
                int status = intent.getIntExtra("action", 0);
                int level = intent.getIntExtra("level", 0);
                if (status == 0) {
                    if (level < 1) {
                        LowBattery = true;
                    } else {
                        LowBattery = false;
                    }
                } else {
                    LowBattery = false;
                }
            }
        }
    };

    private void noPaperDlg() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(VoucherActivity.this);
        dlg.setTitle(getString(R.string.noPaper));
        dlg.setMessage(getString(R.string.noPaperNotice));
        dlg.setCancelable(false);
        dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // mUsbThermalPrinter.stop();
            }
        });
        dlg.show();
    }

    private class paperWalkPrintThread extends Thread {

        private int contenido;

        public paperWalkPrintThread(int contenido) {
            this.contenido = contenido;
        }

        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.start(0);
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.walkPaper(contenido);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper) {
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
                //  mUsbThermalPrinter.stop();
            }
        }
    }


    private class barcodePrintThread extends Thread {

        private String codigo;

        public barcodePrintThread(String contenido) {
            this.codigo = contenido;
        }

        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.start(0);
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.setGray(printGray);


                Bitmap bitmap = CreateCode(codigo, BarcodeFormat.EAN_13, 320, 176);
                if (bitmap != null) {
                    mUsbThermalPrinter.printLogo(bitmap, false);
                }
                // mUsbThermalPrinter.addString(codigo);
                // mUsbThermalPrinter.printString();
                //  mUsbThermalPrinter.walkPaper(100);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper) {
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    //  printContent = "";
                    return;
                }
                //mUsbThermalPrinter.stop();
            }
        }
    }

    private class qrcodePrintThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.start(0);
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.setGray(printGray);
                Bitmap bitmap = CreateCode(qrcodeStr, BarcodeFormat.QR_CODE, 256, 256);
                if (bitmap != null) {
                    mUsbThermalPrinter.printLogo(bitmap, false);
                }
                mUsbThermalPrinter.addString(qrcodeStr);
                mUsbThermalPrinter.printString();
                mUsbThermalPrinter.walkPaper(100);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper) {
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;

                    return;
                }
/*
                mUsbThermalPrinter.stop();
*/
            }
        }
    }

    private class contentPrintThread extends Thread {

        private String contenido;

        public contentPrintThread(String contenido) {
            this.contenido = contenido;
        }

        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.start(0);
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.setAlgin(mUsbThermalPrinter.ALGIN_LEFT);
                mUsbThermalPrinter.setLeftIndent(leftDistance);
                mUsbThermalPrinter.setLineSpace(lineDistance);
              //  mUsbThermalPrinter.setTextSize(64);
                mUsbThermalPrinter.setMonoSpace(true);

                if (wordFont == 4) {
                    mUsbThermalPrinter.setFontSize(2);
                    mUsbThermalPrinter.enlargeFontSize(2, 2);
                } else if (wordFont == 3) {
                    mUsbThermalPrinter.setFontSize(1);
                    mUsbThermalPrinter.enlargeFontSize(2, 2);
                } else if (wordFont == 2) {
                    mUsbThermalPrinter.setFontSize(2);
                } else if (wordFont == 1) {
                    mUsbThermalPrinter.setFontSize(1);
                }
                mUsbThermalPrinter.setGray(printGray);
                mUsbThermalPrinter.addString(contenido);
                mUsbThermalPrinter.printString();
                //mUsbThermalPrinter.walkPaper(100);
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper) {
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    // printContent =  "";
                    return;
                }
                /*mUsbThermalPrinter.stop();*/
            }
        }
    }

/*    private class MakerThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.start(0);
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.searchMark(Integer.parseInt(edittext_maker_search_distance.getText().toString()),
                        Integer.parseInt(edittext_maker_walk_distance.getText().toString()));
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper) {
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
                mUsbThermalPrinter.stop(VoucherActivity.this);
            }
        }
    }*/

    private class printPicture extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.start(0);
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.setGray(printGray);
                mUsbThermalPrinter.setAlgin(mUsbThermalPrinter.ALGIN_MIDDLE);
                File file = new File(picturePath);
                if (file.exists()) {
                    mUsbThermalPrinter.printLogo(BitmapFactory.decodeFile(picturePath), false);
                    mUsbThermalPrinter.walkPaper(100);
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(VoucherActivity.this, getString(R.string.not_find_picture), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Result = e.toString();
                if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                    nopaper = true;
                } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                    handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper) {
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
                // mUsbThermalPrinter.stop();
            }
        }
    }

    /*  private class executeCommand extends Thread {

          @Override
          public void run() {
              super.run();
              try {
                  mUsbThermalPrinter.start(0);
                  mUsbThermalPrinter.reset();
                  mUsbThermalPrinter.sendCommand(edittext_input_command.getText().toString());
              } catch (Exception e) {
                  e.printStackTrace();
                  Result = e.toString();
                  if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                      nopaper = true;
                  } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
                      handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
                  } else {
                      handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                  }
              } finally {
                  try {
                      Thread.sleep(3000);
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }
                  handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                  if (nopaper) {
                      handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                      nopaper = false;
                      return;
                  }
                  mUsbThermalPrinter.stop(VoucherActivity.this);
              }
          }

      }
  */
    @Override
    protected void onDestroy() {
        if (progressDialog != null && !VoucherActivity.this.isFinishing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        unregisterReceiver(printReceive);
        mUsbThermalPrinter.stop();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 生成条码
     *
     * @param str       条码内容
     * @param type      条码类型： AZTEC, CODABAR, CODE_39, CODE_93, CODE_128, DATA_MATRIX,
     *                  EAN_8, EAN_13, ITF, MAXICODE, PDF_417, QR_CODE, RSS_14,
     *                  RSS_EXPANDED, UPC_A, UPC_E, UPC_EAN_EXTENSION;
     * @param bmpWidth  生成位图宽,宽不能大于384，不然大于打印纸宽度
     * @param bmpHeight 生成位图高，8的倍数
     */

    public Bitmap CreateCode(String str, com.google.zxing.BarcodeFormat type, int bmpWidth, int bmpHeight) throws WriterException {
        Hashtable<EncodeHintType, String> mHashtable = new Hashtable<EncodeHintType, String>();
        mHashtable.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 生成二维矩阵,编码时要指定大小,不要生成了图片以后再进行缩放,以防模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight, mHashtable);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组（一直横着排）
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

/*    public void selectIndex(View view) {
        switch (view.getId()) {
            case R.id.index_text:
                text_index.setEnabled(false);
                pic_index.setEnabled(true);
                comm_index.setEnabled(true);
                print_text.setVisibility(View.VISIBLE);
                print_pic.setVisibility(View.GONE);
                print_comm.setVisibility(View.GONE);

                break;

            case R.id.index_pic:

                text_index.setEnabled(true);
                pic_index.setEnabled(false);
                comm_index.setEnabled(true);
                print_text.setVisibility(View.GONE);
                print_pic.setVisibility(View.VISIBLE);
                print_comm.setVisibility(View.GONE);
                break;
            case R.id.index_comm:
                text_index.setEnabled(true);
                pic_index.setEnabled(true);
                comm_index.setEnabled(false);
                print_text.setVisibility(View.GONE);
                print_pic.setVisibility(View.GONE);
                print_comm.setVisibility(View.VISIBLE);

                break;
        }
    }*/

    private void savepic() {
        File file = new File(picturePath);
        if (!file.exists()) {
            InputStream inputStream = null;
            FileOutputStream fos = null;
            byte[] tmp = new byte[1024];
            try {
                inputStream = getApplicationContext().getAssets().open("syhlogo.png");
                fos = new FileOutputStream(file);
                int length = 0;
                while ((length = inputStream.read(tmp)) > 0) {
                    fos.write(tmp, 0, length);
                }
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


