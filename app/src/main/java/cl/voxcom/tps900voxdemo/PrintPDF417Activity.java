package cl.voxcom.tps900voxdemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.pdf417.PDF417Writer;
import com.google.zxing.pdf417.encoder.Compaction;
import com.google.zxing.pdf417.encoder.Dimensions;
import com.telpo.tps550.api.printer.UsbThermalPrinter;

import java.util.EnumMap;
import java.util.Map;

public class PrintPDF417Activity extends AppCompatActivity {
    private  String data = "<TED version=\"1.0\"><DD><RE>81537600-5</RE><TD>39</TD><F>1079568037</F><FE>2019-07-01</FE><RR>44444444-4</RR><RSR>RUT CONTINGENCIA CONTINGENCIA</RSR><MNT>2970</MNT><IT1>PAPEL HIG DH CONFORT 4X27 MT</IT1><CAF version=\"1.0\"><DA><RE>81537600-5</RE><RS>RENDIC HERMANOS S A</RS><TD>39</TD><RNG><D>1072279120</D><H>1082279118</H></RNG><FA>2019-05-14</FA><RSAPK><M>7xbl18fp26ZEoQIJiN8GaKrrhZ5uU9DBh3EaXwWdZxRACKWoguRNpKiNY7mDQy6fAE+dXAiDji4JdYBHzVneNw==</M><E>Aw==</E></RSAPK><IDK>300</IDK></DA><FRMA algoritmo=\"SHA1withRSA\">ZD0KHPDZzV00K9/T8T4exNJKxALGYqb8EXb2sU5b8VsjQaLbXur1z6eXg7h1B1NJuzSrU4XEaFydCYs/37ZvDA==</FRMA></CAF><TSTED>2019-07-01T13:02:51</TSTED></DD><FRMT algoritmo=\"SHA1withRSA\">e7D7IHwvhmGWeEgFrloIi6Cpg62rAAi3S3dxcPbER/z6SbQkEk2ZlH3lcqww/MaMbXDSVAPJvP3yu5DLSZX9+w==</FRMT></TED>";
    UsbThermalPrinter mUsbThermalPrinter = new UsbThermalPrinter(PrintPDF417Activity.this);
    private int leftDistance = 0;
    private int lineDistance;
    private int wordFont;
    private String Result;
    private Boolean nopaper = false;
    private boolean LowBattery = false;
    private int printGray;
    public static String printContent;






    private EditText etSamplePDF417;
    private Button btnpdf417print;

    private String printVersion;
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
    private final int NOBLACKBLOCK = 15;

    private ProgressDialog progressDialog;

    ProgressDialog dialog;

    MyHandler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_pdf417);

        btnpdf417print = findViewById(R.id.btnpdf417print);
        etSamplePDF417 = findViewById(R.id.etSamplePDF417);

        etSamplePDF417.setText(data);

        btnpdf417print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printPDF417(etSamplePDF417.getText().toString());

                //progressDialog = ProgressDialog.show(PrintPDF417Activity.this, getString(R.string.bl_dy), getString(R.string.printing_wait));
                //handler.sendMessage(handler.obtainMessage(PRINTCONTENT, 1, 0, null));
            }
        });


        handler = new MyHandler();


    }

    private void printPDF417(String data)
    {
        try {
            mUsbThermalPrinter.start(0);

            /* Characteristic of printer */
            mUsbThermalPrinter.reset();
            mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
            mUsbThermalPrinter.setLeftIndent(leftDistance);
            mUsbThermalPrinter.setLineSpace(lineDistance);

            if (wordFont == 4) {
                mUsbThermalPrinter.setFontSize(55);
            } else if (wordFont == 3) {
                mUsbThermalPrinter.setTextSize(45);
            } else if (wordFont == 2) {
                mUsbThermalPrinter.setTextSize(35);
            } else if (wordFont == 1) {
                mUsbThermalPrinter.setTextSize(25);
            }

            //mUsbThermalPrinter.setGray(printGray);
            //mUsbThermalPrinter.addString(printContent);



				/*PDF417Writer multiFormatWriter = new PDF417Writer();
				try {
					BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.PDF_417,700,900, );
					BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
					Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
					//bitmap.createBitmap()

					//mUsbThermalPrinter.printLogo(bitmap, false);
					//imageView.setImageBitmap(bitmap);
				} catch (WriterException e) {
					e.printStackTrace();
				}*/

            //String text = "<TED version=\\\"1.0\\\"><DD><RE>81537600-5</RE><TD>39</TD><F>1079568037<";

            mUsbThermalPrinter.printLogo(encodeAsBitmap(data, BarcodeFormat.PDF_417, 30, 90), false);

            mUsbThermalPrinter.walkPaper(10);
        } catch (Exception e) {
            e.printStackTrace();
            Result = e.toString();
            if (Result.equals("com.telpo.tps550.api.printer.NoPaperException")) {
                nopaper = true;
            } else if (Result.equals("com.telpo.tps550.api.printer.OverHeatException")) {
//                handler.sendMessage(handler.obtainMessage(OVERHEAT, 1, 0, null));
            } else {
//                handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
            }
        } finally {
//            handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
            if (nopaper) {
//                handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                nopaper = false;
                return;
            }
        }

    }


    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private static Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        if (contents == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        // Dimensions dimensions = new Dimensions(18, 18, 1, 150);
        Dimensions dimensions = new Dimensions(9, 9, 1, 300);
        int margin = 0;

        hints = new EnumMap<>(EncodeHintType.class);
        // hints.put(EncodeHintType.CHARACTER_SET, encoding);
        // hints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        if (format.equals(BarcodeFormat.PDF_417)) {
            hints.put(EncodeHintType.PDF417_DIMENSIONS, dimensions);
            hints.put(EncodeHintType.MARGIN, margin);
            hints.put(EncodeHintType.PDF417_COMPACT, false);
            hints.put(EncodeHintType.PDF417_COMPACTION, Compaction.TEXT);

        }

        //MultiFormatWriter writer = new MultiFormatWriter();
        PDF417Writer writer = new PDF417Writer();
        BitMatrix result;
        try {
            result = writer.encode(contents, format, img_width, img_height, hints);

        } catch (Exception e) {
            // Unsupported format
            Log.e("USBPRINTER", e.toString());
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }


        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        int oriwidth = bitmap.getWidth();
        int oriheight = bitmap.getHeight();
        int destwidth = 384;

        if (oriwidth>destwidth){
            double scale = (double)oriwidth/(double) destwidth;
            double destheight = oriheight / scale;
            bitmap = getResizedBitmap(bitmap,destwidth,(int)Math.round(destheight));

        }


        return bitmap;
    }



    // metodo para rotar bitmap
    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap output = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        //output = getResizedBitmap(output,output.getWidth()/2,output.getHeight()/2);
        //output = getResizedBitmap(output, 384, 560);

        return output;
    }

    // metodo para ajustar tamaño de bitmap
    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }






    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NOPAPER:
                    noPaperDlg();
                    break;
                case LOWBATTERY:
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(PrintPDF417Activity.this);
                    alertDialog.setTitle(R.string.operation_result);
                    alertDialog.setMessage(getString(R.string.LowBattery));
                    alertDialog.setPositiveButton(getString(R.string.dialog_comfirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    alertDialog.show();
                    break;
                case NOBLACKBLOCK:
                    Toast.makeText(PrintPDF417Activity.this, R.string.maker_not_find, Toast.LENGTH_SHORT).show();
                    break;
                case PRINTVERSION:
                    dialog.dismiss();
                    if (msg.obj.equals("1")) {
                        //textPrintVersion.setText(printVersion);
                    } else {
                        Toast.makeText(PrintPDF417Activity.this, R.string.operation_fail, Toast.LENGTH_LONG).show();
                    }
                    break;
                case PRINTBARCODE:
                    //new barcodePrintThread().start();
                    break;
                case PRINTQRCODE:
                    //new qrcodePrintThread().start();
                    break;
                case PRINTPAPERWALK:
                    //new paperWalkPrintThread().start();
                    break;
                case PRINTCONTENT:
                    new contentPrintThread().start();
                    break;
                case MAKER:
                    //new MakerThread().start();
                    break;
                case PRINTPICTURE:
                    //new printPicture().start();
                    break;
                case CANCELPROMPT:
                    if (progressDialog != null && !PrintPDF417Activity.this.isFinishing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                    break;
                case OVERHEAT:
                    AlertDialog.Builder overHeatDialog = new AlertDialog.Builder(PrintPDF417Activity.this);
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
                    Toast.makeText(PrintPDF417Activity.this, "Print Error!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


    private void noPaperDlg() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(PrintPDF417Activity.this);
        dlg.setTitle(getString(R.string.noPaper));
        dlg.setMessage(getString(R.string.noPaperNotice));
        dlg.setCancelable(false);
        dlg.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        dlg.show();
    }

/*

    private class MakerThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
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
                } else if (Result.equals("com.telpo.tps550.api.printer.BlackBlockNotFoundException")) {
                    handler.sendMessage(handler.obtainMessage(NOBLACKBLOCK, 1, 0, null));
                } else {
                    handler.sendMessage(handler.obtainMessage(PRINTERR, 1, 0, null));
                }
            } finally {
                handler.sendMessage(handler.obtainMessage(CANCELPROMPT, 1, 0, null));
                if (nopaper){
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
            }
        }
    }
*/

    private class contentPrintThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
                mUsbThermalPrinter.setLeftIndent(leftDistance);
                mUsbThermalPrinter.setLineSpace(lineDistance);
                if (wordFont == 4) {
                    mUsbThermalPrinter.setFontSize(55);
                } else if (wordFont == 3) {
                    mUsbThermalPrinter.setTextSize(45);
                } else if (wordFont == 2) {
                    mUsbThermalPrinter.setTextSize(35);
                } else if (wordFont == 1) {
                    mUsbThermalPrinter.setTextSize(25);
                }
                mUsbThermalPrinter.setGray(printGray);
                mUsbThermalPrinter.addString(data);
                mUsbThermalPrinter.printString();
                mUsbThermalPrinter.walkPaper(20);
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
                if (nopaper){
                    handler.sendMessage(handler.obtainMessage(NOPAPER, 1, 0, null));
                    nopaper = false;
                    return;
                }
            }
        }
    }



}
