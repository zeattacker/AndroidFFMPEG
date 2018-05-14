package studio.indevia.ffmpeg;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.erikagtierrez.multiple_media_picker.Gallery;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.bravobit.ffmpeg.FFcommandExecuteResponseHandler;
import nl.bravobit.ffmpeg.FFmpeg;

public class MainActivity extends AppCompatActivity {
//    FFmpeg ffmpeg;
    Unbinder unbinder;

    @BindView(R.id.textview_upload_1) TextView textViewUpload1;
    @BindView(R.id.textview_upload_2) TextView textViewUpload2;
    @BindView(R.id.button_upload_1) Button buttonUpload1;
    @BindView(R.id.button_process) Button buttonProcess;
    @BindView(R.id.edittext_result) EditText editTextResult;
    String outputFileName = "";

    static final int OPEN_MEDIA_PICKER = 1;  // Request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
//        ffmpeg = FFmpeg.getInstance(this);
//        loadFFMpegBinary();

        buttonUpload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(MainActivity.this)
                        .withPermissions(Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                Intent intent= new Intent(MainActivity.this, Gallery.class);
                                // Set the title
                                intent.putExtra("title","Select media");
                                // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
                                intent.putExtra("mode",1);
                                intent.putExtra("maxSelection",3); // Optional
                                startActivityForResult(intent,OPEN_MEDIA_PICKER);
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                            }
                        }).check();

            }
        });

        buttonProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                outputFileName = Long.toHexString(Double.doubleToLongBits(Math.random()));
                String command = "-i " +
                        textViewUpload1.getText().toString() +
                        " -i " +
                        textViewUpload2.getText().toString() +
                        " -filter_complex hstack " +
                        Environment.getExternalStorageDirectory() +
                        "/DCIM/Camera/"+ outputFileName + ".mp4";

                Log.e("MainActivity","command : " + command);
                
                if(FFmpeg.getInstance(MainActivity.this).isSupported()) {
                    FFmpeg ffmpeg = FFmpeg.getInstance(MainActivity.this);
                    ffmpeg.execute(command.split(" "), new FFcommandExecuteResponseHandler() {
                        @Override
                        public void onSuccess(String message) {
                            Log.e("MainActivity","Success : " + message);
                            editTextResult.setText(editTextResult.getText() + "\n Success : " + message);
                        }

                        @Override
                        public void onProgress(String message) {
                            Log.e("MainActivity","Progress : " + message);
                            editTextResult.setText(editTextResult.getText() + "\n" + message);
                        }

                        @Override
                        public void onFailure(String message) {
                            Log.e("MainActivity","Failure : " + message);
                            editTextResult.setText(editTextResult.getText() + "\n Failure : " + message);
                        }

                        @Override
                        public void onStart() {
                            Log.e("MainActivity","Started");
                            editTextResult.setText("Started");
                        }

                        @Override
                        public void onFinish() {
                            Log.e("MainActivity","Finished");
                            editTextResult.setText(editTextResult.getText() + "\n Finished, file location : " + Environment.getExternalStorageDirectory() +
                                    "/DCIM/Camera/"+ outputFileName + ".mp4");
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "FFMPEG Not Supported on This Devices", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private void loadFFMpegBinary() {
//        try {
//            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
//                @Override
//                public void onFailure() {
//                    Toast.makeText(MainActivity.this, "Load Binary Failure", Toast.LENGTH_SHORT).show();
////                    showUnsupportedExceptionDialog();
//                }
//            });
//        } catch (FFmpegNotSupportedException e) {
////            showUnsupportedExceptionDialog();
//            Toast.makeText(this, "FFMPEG is not supported", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void execFFmpegBinary(final String[] command) {
//        try {
//            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
//                @Override
//                public void onFailure(String s) {
//                    Log.e("MainActivity","Failed commeand : " + command + " error : " + s);
////                    addTextViewToLayout("FAILED with output : "+s);
//                }
//
//                @Override
//                public void onSuccess(String s) {
////                    addTextViewToLayout("SUCCESS with output : "+s);
//                    editTextResult.setText(s);
//                }
//
//                @Override
//                public void onProgress(String s) {
//                    Log.e("MainActivity", "Started command : ffmpeg "+command);
////                    addTextViewToLayout("progress : "+s);
////                    progressDialog.setMessage("Processing\n"+s);
//                }
//
//                @Override
//                public void onStart() {
////                    outputLayout.removeAllViews();
////
//                    Log.e("MainActivity", "Started command : ffmpeg " + command);
////                    progressDialog.setMessage("Processing...");
////                    progressDialog.show();
//                }
//
//                @Override
//                public void onFinish() {
//                    Log.e("MainActivity", "Finished command : ffmpeg "+command);
////                    progressDialog.dismiss();
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            // do nothing for now
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check which request we're responding to
        if (requestCode == OPEN_MEDIA_PICKER) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> selectionResult=data.getStringArrayListExtra("result");
                textViewUpload1.setText(selectionResult.get(0));
                textViewUpload2.setText(selectionResult.get(1));
            }
        }
    }
}
