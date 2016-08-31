package main.app.tbee3app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;

import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Policy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CameraActivity extends Activity {
    int images_limit = 1;
    Boolean can_use_gal = false;
    Boolean can_post = false;
    Bitmap sample;
    private ArrayList<Bitmap> bitmap;
    private ArrayList<String> image_path;
    private ArrayList<String> image_path_id;
    private Uri mImageCaptureUri;
    boolean saving_pic = false;
    private Camera.PictureCallback mPicture;
    private SurfaceView surface_view;
    private Camera mCamera;
    SurfaceHolder surface_holder        = null;
    SurfaceHolder.Callback sh_callback  = null;
    ImageView take_photo_btn,first_frame,second_frame,third_frame,fourth_frame,next_btn,gallery_btn;
    ImageView  del1,del2,del3,del4;
    int image_count=0;
    String scrren_mode;
    Boolean hasflash,flash_on;
    List<Camera.Size> mSupportedPreviewSizes;
    //private String files[] = new String[20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Settings.forceRTLIfSupported(this);
        setContentView(R.layout.add_a_product_layout);
                hasflash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        flash_on = false;
        scrren_mode = getIntent().getStringExtra("mode");
        if(scrren_mode==null)
            scrren_mode = "no_edit";

        del1 = (ImageView) findViewById(R.id.del1);
        del2 = (ImageView) findViewById(R.id.del2);
        del3 = (ImageView) findViewById(R.id.del3);
        del4 = (ImageView) findViewById(R.id.del4);

        del1.setVisibility(View.GONE);
        del2.setVisibility(View.GONE);
        del3.setVisibility(View.GONE);
        del4.setVisibility(View.GONE);

        del1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(image_count>=1) {
                    del1.setVisibility(View.GONE);
                    if(scrren_mode.equals("edit")){
                        if(image_path.get(0).contains("http:"))
                            delete_alert(getIntent().getStringExtra("add_id"), image_path_id.get(0),0);
                        else{
                            image_path.remove(0);
                            display_image_update();
                        }

                    }
                    else{
                        image_path.remove(0);
                        display_image_update();
                    }
                }
            }
        });

        del2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image_count >= 2) {
                    del2.setVisibility(View.GONE);
                    if(scrren_mode.equals("edit")){
                        if(image_path.get(1).contains("http:"))
                            delete_alert(getIntent().getStringExtra("add_id"), image_path_id.get(1),1);
                        else{
                            image_path.remove(1);
                            display_image_update();
                        }

                    }
                    else{
                        image_path.remove(1);
                        display_image_update();
                    }
                }
                }

        });

        del3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image_count >= 3) {
                    del3.setVisibility(View.GONE);
                    if(scrren_mode.equals("edit")){
                        if(image_path.get(2).contains("http:"))
                            delete_alert(getIntent().getStringExtra("add_id"), image_path_id.get(2),2);
                        else{
                            image_path.remove(2);
                            display_image_update();
                        }

                    }
                    else{
                        image_path.remove(2);
                        display_image_update();
                    }

                }
            }
        });

        del4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (image_count >= 4) {
                    del4.setVisibility(View.GONE);
                    if(scrren_mode.equals("edit")){
                        if(image_path.get(3).contains("http:"))
                            delete_alert(getIntent().getStringExtra("add_id"), image_path_id.get(3),3);
                        else{
                            image_path.remove(3);
                            display_image_update();
                        }
                    }
                    else{
                        image_path.remove(3);
                        display_image_update();
                    }
                }
            }
        });


        image_path = new ArrayList<String>();
        image_path_id = new ArrayList<String>();
        bitmap = new ArrayList<Bitmap>();

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        take_photo_btn = (ImageView) findViewById(R.id.capture_photo_btn);
        first_frame = (ImageView) findViewById(R.id.imageView28);
        second_frame = (ImageView) findViewById(R.id.imageView29);
        third_frame = (ImageView) findViewById(R.id.imageView30);
        fourth_frame = (ImageView) findViewById(R.id.imageView31);
        next_btn = (ImageView) findViewById(R.id.next_btn);
        gallery_btn = (ImageView) findViewById(R.id.open_gallery_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(image_path.size()<1)
                {
                    Toast.makeText(CameraActivity.this,getString(R.string.please_select_at_least_one_image),Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent product_edit = new Intent(CameraActivity.this,AddProductActivity.class);
                    if(scrren_mode.equals("edit"))
                    product_edit.putExtra("mode","edit");

                    product_edit.putExtra("add_id",getIntent().getStringExtra("add_id"));
                    product_edit.putExtra("add_tittle", getIntent().getStringExtra("add_tittle"));
                    product_edit.putExtra("add_desc", getIntent().getStringExtra("add_desc"));
                    product_edit.putExtra("add_price",getIntent().getStringExtra("add_price"));
                    product_edit.putExtra("image_list", image_path);
                    product_edit.putExtra("image_list_id", image_path_id);

                    startActivity(product_edit);
                    finish();
                }
            }
        });
        take_photo_btn.setOnClickListener(captrureListener);
        surface_view = (SurfaceView) findViewById(R.id.surfaceviewcam);

        if (surface_holder == null) {
            surface_holder = surface_view.getHolder();
        }

        sh_callback = my_callback();
        surface_holder.addCallback(sh_callback);

        SharedPreferences sharedPref =  PreferenceManager.getDefaultSharedPreferences(this);
        String tbee3_pos = sharedPref.getString("tbee3_user","-1");
        if(tbee3_pos.equals("-1")){

            Intent intent=new Intent(CameraActivity.this,PackageActivity.class);
            intent.putExtra("mode","close");
            startActivityForResult(intent, 2);// Activity is started with request
        }
        else{
            set_limitations(Integer.parseInt(tbee3_pos));
        }

        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(can_use_gal)
                    //Toast.makeText(CameraActivity.this,"u can use gallery, but it is unavilable",Toast.LENGTH_SHORT).show();
                loadImagefromGallery();
                else {
                   // Toast.makeText(CameraActivity.this, "upgrade_your_account", Toast.LENGTH_SHORT).show();
                    //Intent intent=new Intent(CameraActivity.this,PackageActivity.class);
                    //intent.putExtra("mode","close");
                    //startActivityForResult(intent, 2);// Activity is started with request
                    AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                    builder.setMessage(getString(R.string.account_gallery_not_available)).setPositiveButton("Continue", dialogClickListener)
                            .setNegativeButton("Upgrade", dialogClickListener).show();


                }

            }
        });

        image_path = (ArrayList<String>) getIntent().getSerializableExtra("image_list");
        if(image_path!=null){
            image_path_id = (ArrayList<String>) getIntent().getSerializableExtra("image_list_id");

            display_image_update();
        }
        else {
            image_path = new ArrayList<>();
        }
        get_user_status();

        ImageView flash_icon = (ImageView) findViewById(R.id.flash_icon);
        flash_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("flash", "clicked");
                if (hasflash) {
                    Log.e("flash", "is ok");
                    if (flash_on) {
                        Camera.Parameters p = mCamera.getParameters();
                        if (p.getSupportedFocusModes().contains(
                                Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                            p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                        }
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        mCamera.setParameters(p);
                        mCamera.startPreview();
                        flash_on = false;
                    } else {
                        Camera.Parameters p = mCamera.getParameters();
                        if (p.getSupportedFocusModes().contains(
                                Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                            p.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                        }
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        mCamera.setParameters(p);
                        mCamera.startPreview();
                        flash_on = true;
                    }
                }
            }
        });

        ImageView clear_icon = (ImageView) findViewById(R.id.clear_icon);
        clear_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    SurfaceHolder.Callback my_callback() {
        SurfaceHolder.Callback ob1 = new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mCamera = Camera.open();
                try {

                    mCamera.setDisplayOrientation(90);
                    mCamera.setPreviewDisplay(holder);


                    mPicture = getPictureCallback();
                } catch (IOException exception) {
                    mCamera.release();
                    mCamera = null;
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {

                mCamera.startPreview();
            }

                    };
        return ob1;
    }

    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //make a new picture file

                File pictureFile = getOutputMediaFile();
                if (pictureFile == null) {
                    return;
                }
                try {
                    //write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                    Toast toast = Toast.makeText(getApplicationContext(), "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
//                    toast.show();

                } catch (FileNotFoundException e) {
                } catch (IOException e) {
                }

                //refresh camera to continue preview
               mCamera.startPreview();
                saving_pic = false;
               // files[image_count]=pictureFile.toURI().toString();
                image_path.add(pictureFile.getAbsolutePath());
                image_path_id.add("-1");
               // display_image(pictureFile);
                //show_image_edit(pictureFile);
                Intent intent = new Intent(CameraActivity.this,ImageEditActivity.class);
                intent.putExtra("image_path",pictureFile.getAbsolutePath());
                intent.putExtra("image_source","camera");
                int size = surface_view.getWidth();
                Log.e("size",String.valueOf(size));
                intent.putExtra("image_size",size);
                startActivityForResult(intent, 4);
            }
        };
        return picture;
    }

    View.OnClickListener captrureListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!can_post){
                AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                builder.setMessage("You have to upgrade your account to post a product, What you want to do?").setPositiveButton("Continue", dialogClickListener)
                        .setNegativeButton("Upgrade", dialogClickListener).show();

            }
            else{
                    if(image_count>=images_limit) {
                         AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                        builder.setMessage("You have to upgrade your account to add more photos, What you want to do?").setPositiveButton("Continue", dialogClickListener)
                                .setNegativeButton("Upgrade", dialogClickListener).show();
                    }
                    else if(image_count==4) {
                    Toast.makeText(CameraActivity.this, "you can add maximum of 4 images", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if (!saving_pic) {
                            saving_pic = true;
                            mCamera.takePicture(null, null, mPicture);
                        }
                    }
            }
        }
    };

    private static File getOutputMediaFile() {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", "Tbee_Camera");

        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    void display_image(File file){
        image_count++;
        Log.e("image_count",String.valueOf(image_count));
        switch (image_count){
            case 1:
                del1.setVisibility(View.VISIBLE);
                Picasso.with(getApplicationContext()).load(file).fit().into(first_frame);
                break;
            case 2:
                del2.setVisibility(View.VISIBLE);
                Picasso.with(getApplicationContext()).load(file).fit().into(second_frame);
                break;
            case 3:
                del3.setVisibility(View.VISIBLE);
                Picasso.with(getApplicationContext()).load(file).fit().into(third_frame);
                break;
            case 4:
                del4.setVisibility(View.VISIBLE);
                Picasso.with(getApplicationContext()).load(file).fit().into(fourth_frame);
                break;
            case 5:
                break;
        }
    }

    void display_image_update(){
        Log.e("IMAGE UPDATE","CALLED "+ String.valueOf(image_path.size()));
            image_count=0;
            first_frame.setImageResource(R.drawable.empty);
            second_frame.setImageResource(R.drawable.empty);
            third_frame.setImageResource(R.drawable.empty);
            fourth_frame.setImageResource(R.drawable.empty);

        for(int i= 0;i<image_path.size();i++)
        {
            Log.e("at"+String.valueOf(i),image_path.get(i));
                image_count++;
                switch (image_count) {
                    case 1:
                        del1.setVisibility(View.VISIBLE);
                        if(image_path.get(i).contains("http:")) {
                            Picasso.with(getApplicationContext()).load(image_path.get(i)).fit().into(first_frame);
                        }
                        else
                             Picasso.with(getApplicationContext()).load(new File(image_path.get(i))).fit().into(first_frame);
                        break;

                    case 2:
                        del2.setVisibility(View.VISIBLE);
                        if(image_path.get(i).contains("http:")) {
                            Picasso.with(getApplicationContext()).load(image_path.get(i)).fit().into(second_frame);
                        }
                        else {
                            Picasso.with(getApplicationContext()).load(new File(image_path.get(i))).fit().into(second_frame);
                        }
                        break;

                    case 3:
                        del3.setVisibility(View.VISIBLE);
                        if(image_path.get(i).contains("http:"))
                            Picasso.with(getApplicationContext()).load(image_path.get(i)).fit().into(third_frame);
                        else
                        Picasso.with(getApplicationContext()).load(new File(image_path.get(i))).fit().into(third_frame);
                        break;

                    case 4:
                        del4.setVisibility(View.VISIBLE);
                        if(image_path.get(i).contains("http:")) {

                            Picasso.with(getApplicationContext()).load(image_path.get(i)).fit().into(fourth_frame);
                        }
                        else
                            Picasso.with(getApplicationContext()).load(new File(image_path.get(i))).fit().into(fourth_frame);
                        break;
                    case 5:
                        break;
                }
        }
    }



    private boolean rotate_image(File file,float angle) {

        String photopath = file.getPath().toString();
        Bitmap bmp = BitmapFactory.decodeFile(photopath);

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            return true;
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    public void loadImagefromGallery() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,3);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            String message=data.getStringExtra("tbee3_pos");

                //set_limitations(Integer.parseInt(message));
            get_user_status();
        }
        try {
            // When an Image is picked
            if (requestCode == 3 && resultCode == RESULT_OK ) {
                // Get the Image from data
                if( null != data) {
                    Uri selectedImageUri = data.getData();
                    Log.e("data","not null");
                    if (null != selectedImageUri) {
                        Uri selectedImage = data.getData();
                        String[] filePath = { MediaStore.Images.Media.DATA };
                        Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                        c.moveToFirst();
                        int columnIndex = c.getColumnIndex(filePath[0]);
                        String picturePath = c.getString(columnIndex);
                        c.close();
                    Log.e("file_path",picturePath);
                        image_path.add(picturePath);
                        image_path_id.add("-1");

                        Intent intent = new Intent(CameraActivity.this,ImageEditActivity.class);
                    intent.putExtra("image_path",picturePath);
                        intent.putExtra("image_source", "gallery");
                    startActivityForResult(intent, 4);
                    }
                                    }
                    else{
                        Toast.makeText(this, "You haven't picked Image",
                                Toast.LENGTH_LONG).show();
                    }
                }
            else if(requestCode == 4){
                String file_path=data.getStringExtra("image_path");
                Log.e("ile_path",file_path);
                display_image(new File(file_path));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong, Please select another image", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public String getPathFromURI(Uri contentUri) {
        String res = contentUri.getPath();
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }



    private void set_limitations(int pos){
        switch(pos){
            case 1:
                images_limit = 1;
                can_use_gal = false;
                break;
            case 2:
                images_limit = 4;
                can_use_gal = false;
                break;
            case 3:
                images_limit = 4;
                can_use_gal = true;
                break;
            case 4:
                images_limit = 4;
                can_use_gal = true;
                break;

        }
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    Intent intent=new Intent(CameraActivity.this,PackageActivity.class);
                    intent.putExtra("mode","close");
                    startActivityForResult(intent, 2);// Activity is started with request

                    break;
            }
        }
    };


    private void get_user_status(){
        final ProgressDialog progressDialog = new ProgressDialog(CameraActivity.this);
        progressDialog.setMessage("please wait checking your package...");
        progressDialog.show();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String cust_id = sharedPref.getString("tbee3_user", "-1");
        String url = null;
        url = Settings.SERVER_URL+"user_eligible.php?cust_id="+cust_id;
        Log.e("set_url", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonArray) {
                if(progressDialog!=null)
                    progressDialog.dismiss();
                Log.e("response is: ", jsonArray.toString());
                try {

                        JSONObject details = jsonArray.getJSONObject("details");
                        String limit = details.getString("no_images");
                        String has_gallery = details.getString("gallery");
                        int is_eligible = details.getInt("eligible");
                    images_limit = Integer.parseInt(limit);
                    if(has_gallery.equals("0"))
                    can_use_gal = false;
                    else
                        can_use_gal = true;
                    if(is_eligible == 1){
                        can_post = true;
                    }
                    else{
                        can_post = false;
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:",error.toString());
                if(progressDialog!=null)
                    progressDialog.dismiss();
            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);


    }

    private void delete_image_from_server(String add_id,String img_id, final int pos) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait, removing image...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        String url = null;
        url = Settings.SERVER_URL + "del-product-img-json.php?product_id=" + add_id + "&img_id=" + img_id;
        Log.e("set_url", url);
        JsonArrayRequest jsObjRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.e("response is: ", jsonArray.toString());
                try {
                    Log.e("image", "removed");
                    image_path.remove(pos);
                    display_image_update();
                    if(progressDialog!=null)
                    progressDialog.dismiss();
                } catch (Exception e) {
                    Log.e("product", "not ");
                    if(progressDialog!=null)
                        progressDialog.dismiss();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.e("response is:", error.toString());
                Log.e("image", "not removed");
                if(progressDialog!=null)
                    progressDialog.dismiss();

            }
        });

// Access the RequestQueue through your singleton class.
        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

    private void delete_alert(final String add_id, final String img_id, final int pos){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This Image will be deleted permanently from server?")
                .setCancelable(false)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        delete_image_from_server(add_id,img_id,pos);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }



}