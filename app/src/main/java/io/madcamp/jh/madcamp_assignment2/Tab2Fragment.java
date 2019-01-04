package io.madcamp.jh.madcamp_assignment2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.media.ExifInterface;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Tab2Fragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private Context context;
    private View top;

    private boolean isFabOpen;

    private static final int REQ_IMG_FILE = 1;
    private static final int REQ_TAKE_PHOTO = 2;

    private RecyclerView recyclerView;
    private Tab2Adapter adapter;

    private Uri tempPhotoUri;

    /* TabPagerAdapter에서 Fragment 생성할 때 사용하는 메소드 */
    public static Tab2Fragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        Tab2Fragment fragment = new Tab2Fragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        top = inflater.inflate(R.layout.fragment_tab2, container, false);
        this.context = (Context)getActivity();

        initializeFloatingActionButton();
        initializeRecyclerView();

        return top;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void initializeFloatingActionButton() {
        final FloatingActionButton[] fab = new FloatingActionButton[] {
                top.findViewById(R.id.fab),
                top.findViewById(R.id.fab1),
                top.findViewById(R.id.fab2),
                top.findViewById(R.id.fab3),
        };

        isFabOpen = false;

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch(id) {
                    case R.id.fab:
                        anim();
                        break;
                    case R.id.fab1:
                        addImageFromFile();
                        break;
                    case R.id.fab2:
                        addImageFromCamera();
                        break;
                    case R.id.fab3:
                        removeAllItems();
                        break;
                }
            }

            public void anim() {
                if(isFabOpen) {
                    for(int i = 1; i < fab.length; i++) {
                        Animation a = AnimationUtils.loadAnimation(context, R.anim.fab_close);
                        a.setStartOffset((fab.length - i - 1) * 50);
                        fab[i].startAnimation(a);
                        fab[i].setClickable(false);
                    }
                    isFabOpen = false;
                } else {
                    for(int i = 1; i < fab.length; i++) {
                        Animation a = AnimationUtils.loadAnimation(context, R.anim.fab_open);
                        a.setStartOffset((i - 1) * 50);
                        fab[i].startAnimation(a);
                        fab[i].setClickable(true);
                    }
                    isFabOpen = true;
                }
            }
        };

        for(int i = 0; i < 4; i++) {
            fab[i].setOnClickListener(onClickListener);
        }
    }

    final static String listPath = "images.json";

    public void saveImageListToInternal() {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(listPath, Context.MODE_PRIVATE);
            String res = packJSON();
            Log.d("saveImageListToInternal", res);
            fos.write(res.getBytes());
            fos.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Pair<Uri, String>> loadImageListFromInternal() {
        ArrayList<Pair<Uri, String>> list = new ArrayList<>();
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        StringBuilder sb;
        String line;
        try {
            fis = context.openFileInput(listPath);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            sb = new StringBuilder();
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            fis.close();
            String src = sb.toString();
            Log.d("loadImageListInternal", src);
            unpackJSON(src, list);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private String packJSON() {
        try {
            JSONArray arr = new JSONArray();
            ArrayList<Pair<Uri, String>> list = adapter.dataSet;
            if(list != null) {
                for (Pair<Uri, String> p : list) {
                    JSONObject item = new JSONObject();
                    String path = p.first.getPath();
                    Log.d("path", path);
                    item.put("uri", path);
                    item.put("tag", p.second.toString());
                    arr.put(item);
                }
            }
            return arr.toString();
        } catch(JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void unpackJSON(String src, ArrayList<Pair<Uri, String>> list) {
        try {
            JSONArray arr = new JSONArray(src);
            for(int i = 0; i < arr.length(); i++) {
                JSONObject item = arr.getJSONObject(i);
                Uri uri = Uri.parse(item.getString("uri"));
                String tag = item.getString("tag");
                list.add(new Pair<>(uri, tag));
            }
        } catch(JSONException e) {
            Log.d("Exception", "JSON Parsing Failed");
        }
    }

    public void initializeRecyclerView() {
        recyclerView = (RecyclerView)top.findViewById(R.id.recycler_view);
        adapter = new Tab2Adapter(loadImageListFromInternal());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                dialog.setContentView(R.layout.dialog_image);

                String oriPath = adapter.getOriginalPath(position);
                ((ImageView)dialog.findViewById(R.id.image_view)).setImageURI(Uri.parse(oriPath));
                Log.d("Test@oriPath", oriPath);

                dialog.findViewById(R.id.blank).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.getWindow().getAttributes().windowAnimations = R.style.ImageDialogAnimation;

                dialog.setCancelable(true);
                dialog.show();
            }

            @Override
            public void onLongItemClick(View view, int position) {
                final int idx = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Image " + idx);
                builder.setItems(new CharSequence[]{"삭제"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 0:
                                        Toast.makeText(context, "Delete " + idx, Toast.LENGTH_SHORT).show();
                                        removeItem(idx);

                                        Log.d("Deleted", "" + idx);
                                        break;
                                }
                            }
                        });
                builder.show();
            }
        }));
    }

    public void addImageFromFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        if(Build.VERSION.SDK_INT >= 18)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_IMG_FILE);
    }

    public void addImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            try {
                String imageFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                photoFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            } catch(IOException e) {
                e.printStackTrace();
            }
            if(photoFile != null) {
                tempPhotoUri = FileProvider.getUriForFile(context, "io.madcamp.jh.madcamp_assignment2.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoUri);
                startActivityForResult(intent, REQ_TAKE_PHOTO);
            }
        }
    }

    private void removeItem(int idx) {
        if(idx < 0 || idx >= adapter.dataSet.size()) return;
        Uri uri = adapter.remove(idx);
        File file = new File(uri.getPath());
        if(file.exists() && file.delete()) {
            Log.d("Delete File", uri.getPath());
        }
        saveImageListToInternal();
    }

    private void removeAllItems() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle("모든 이미지 삭제");
        alert.setMessage("정말로 모든 이미지를 삭제하시겠습니까?");
        alert.setNegativeButton("아니요", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int l = adapter.dataSet.size();
                for(int i = l; --i >= 0;) {
                    removeItem(i);
                }
                dialog.dismiss();
            }
        });
        alert.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK && (requestCode == REQ_IMG_FILE || requestCode == REQ_TAKE_PHOTO)) {
            switch(requestCode) {
                case REQ_IMG_FILE:
                    if(data.getData() != null) {
                        Log.d("Test@onRes", "Single");
                        addImage(data.getData());
                    } else if(Build.VERSION.SDK_INT >= 18) {
                        if (data.getClipData() != null) {
                            Log.d("Test@onRes", "Multiple");
                            ClipData clipData = data.getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                addImage(clipData.getItemAt(i).getUri());
                            }
                        }
                    }
                    break;
                case REQ_TAKE_PHOTO:
                    addImage(tempPhotoUri);
                    break;
            }
        }
    }

    private void addImage(Uri uri) {
        Uri newUri = copyToInternal(uri);
        adapter.add(newUri, new SimpleDateFormat("yyMMdd_HHmmss").format(new Date()));
        saveImageListToInternal();
    }

    /*private void addToMap(Uri uri){
        File pic = new File(uri.getPath());
        String datetime, lat, lng;
        try{
            ExifInterface exif = new ExifInterface(pic.getName());
            datetime = exif.getAttribute(ExifInterface.TAG_DATETIME);
            lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);


        } catch (IOException e){
            e.printStackTrace();
        }
    }*/


    private Uri copyToInternal(Uri uri) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();

            InputStream is = context.getContentResolver().openInputStream(uri);
            if(is != null) {
                ExifInterface ei = new ExifInterface(is);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                float angle = 0.0f;
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90: angle = 90.f; break;
                    case ExifInterface.ORIENTATION_ROTATE_180: angle = 180.f; break;
                    case ExifInterface.ORIENTATION_ROTATE_270: angle = 270.f; break;
                }
                matrix.postRotate(angle);
            }

            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
            FileOutputStream fosRotated = context.openFileOutput("O_" + imageFileName, Context.MODE_PRIVATE);
            rotated.compress(Bitmap.CompressFormat.JPEG, 100, fosRotated);
            fosRotated.close();

            /* scaling */
            float scaleFactor = 1.f;
            if(width < height) scaleFactor = 256.f / (float)width;
            else scaleFactor = 256.f / (float)height;

            if(scaleFactor < 1.f) {
                matrix.postScale(scaleFactor, scaleFactor);
            }

            Bitmap resized = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

            FileOutputStream fos = context.openFileOutput(imageFileName, Context.MODE_PRIVATE);
            resized.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            Uri newUri = Uri.parse(context.getFileStreamPath(imageFileName).getAbsolutePath());
            return newUri;
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
