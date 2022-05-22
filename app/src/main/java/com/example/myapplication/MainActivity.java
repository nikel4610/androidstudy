package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText editText_test;
    EditText et_save;
    private Button btn_test;
    private EditText et_test;
    private String str;
    ImageView test;
    String share = "file";
    private WebView webView;
    private String url = "https://www.google.com";
    private DrawerLayout drawerLayout;
    private View drawerView;

    private static final int REQUEST_IMAGE_CAPTURE = 672; // 카메라 설정
    private String imageFilePath;
    private Uri photoUri;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 권한 체크
        Object permissionlistener = new Object();
        TedPermission.with(getApplicationContext())
                        .setPermissionListener((PermissionListener) permissionlistener)
                                .setRationaleMessage("카메라 권한이 필요합니다.")
                                        .setDeniedMessage("거부하셨습니다.")
                                                .setPermissions(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                        .check();

        findViewById(R.id.btn_capture).setOnClickListener(new View.OnClickListener()
                                                          {
                                                              @Override
                                                              public void onClick(View v) {
                                                                  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                                  if (intent.resolveActivity(getPackageManager()) != null) {
                                                                      File photoFile = null;
                                                                      try {
                                                                          photoFile = createImageFile();
                                                                      } catch (IOException e) {
                                                                          e.printStackTrace();
                                                                      }
                                                                  }
                                                                  if (photoFile != null) {
                                                                      photoUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName(), photoFile);
                                                                      intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                                                      startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                                                                  }
                                                              }
                                                          });


                drawerLayout = (DrawerLayout) findViewById(R.id.app_bar_drawer);
        drawerView = (View) findViewById(R.id.nav_view);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebChromeClient(new WebChromeClient()); // 구글 크롬 클라이언트로 오픈
        webView.setWebViewClient(new WebViewCientClass());

        et_test = findViewById(R.id.et_test);
        et_save = (EditText)findViewById(R.id.et_save);

        SharedPreferences sharedPreferences = getSharedPreferences(share, 0);
        String value = sharedPreferences.getString("amuname", "");
        et_save.setText(value);

        editText_test = findViewById(R.id.editText_test);
        btn_test = findViewById(R.id.btn_test);

        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override // 메인에서 서브로 str 넘어가는 버튼
            public void onClick(View v) {

                str = et_test.getText().toString();
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                intent.putExtra( "str", str );
                startActivity(intent); // move activity -> str

            }
        });

        test = (ImageView) findViewById(R.id.test);
        test.setOnClickListener(new View.OnClickListener() {
            @Override // 클릭시 메시지 팝업
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "안녕하세요",
                        Toast.LENGTH_SHORT).show(); // 토스트 메시지 팝업
            }
        });

    }

    private File createImageFile() throws IOException { // 이미지 파일 생성 (카메라)
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override // 뒤로가기 버튼 누르면 종료
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override // 앱 종료시 값 저장
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences sharedPreferences = getSharedPreferences(share, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String value = et_save.getText().toString();
        editor.putString("amuname", value); // 밸류값 이름 아무거나 가능
        editor.commit(); // 저장
    }

    private class WebViewCientClass extends WebViewClient {
        @Override // 웹뷰 로드
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imageFilePath);

            } catch (IOException e) {
                e.printStackTrace();

            }

            int exifOrientation;
            int exifDegree;

            if (exif != null) {
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                exifDegree = exifOrientationToDegrees(exifOrientation);
            } else {
                exifDegree = 0;
            }

            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(rotate(bitmap, exifDegree));
        }
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap src, float degree) {
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한 허가", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한 거부", Toast.LENGTH_SHORT).show();
        }
    };
}


