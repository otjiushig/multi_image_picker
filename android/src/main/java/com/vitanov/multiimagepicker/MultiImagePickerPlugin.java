package com.vitanov.multiimagepicker;

import java.nio.file.Path;
import java.nio.file.Paths;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import android.content.pm.ActivityInfo;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.Manifest;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import static android.media.ThumbnailUtils.OPTIONS_RECYCLE_INPUT;


/**
 * MultiImagePickerPlugin
 */
public class MultiImagePickerPlugin implements MethodCallHandler, PluginRegistry.ActivityResultListener {
    private static final String CHANNEL_NAME = "multi_image_picker";
    private static final String REQUEST_THUMBNAIL = "requestThumbnail";
    private static final String REQUEST_ORIGINAL = "requestOriginal";
    private static final String PICK_IMAGES = "pickImages";
    private static final String MAX_IMAGES = "maxImages";
    private static final int REQUEST_CODE_CHOOSE = 1001;
    private static final int REQUEST_CODE_GRANT_PERMISSIONS = 2001;
    private final MethodChannel channel;
    private Activity activity;
    private Context context;
    private BinaryMessenger messenger;
    private Result pendingResult;
    private MethodCall methodCall;

    private MultiImagePickerPlugin(Activity activity, Context context, MethodChannel channel, BinaryMessenger messenger) {
        this.activity = activity;
        this.context = context;
        this.channel = channel;
        this.messenger = messenger;
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL_NAME);
        MultiImagePickerPlugin instance = new MultiImagePickerPlugin(registrar.activity(), registrar.context(), channel, registrar.messenger());
        registrar.addActivityResultListener(instance);
        channel.setMethodCallHandler(instance);
    }

    private class GetThumbnailTask extends AsyncTask<String, Void, Void> {
        BinaryMessenger messenger;
        String path;
        String identifier;
        int width;
        int height;

        public GetThumbnailTask(BinaryMessenger messenger, String identifier, String path, int width, int height) {
            super();
            this.messenger = messenger;
            this.identifier = identifier;
            this.path = path;
            this.width = width;
            this.height = height;
        }

        @Override
        protected Void doInBackground(String... strings) {
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = 10;
            Bitmap bitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(this.path, bitmapOptions), this.width, this.height, OPTIONS_RECYCLE_INPUT);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, stream);
            byte[] byteArray = stream.toByteArray();
            bitmap.recycle();


            final ByteBuffer buffer = ByteBuffer.allocateDirect(byteArray.length);
            buffer.put(byteArray);
            this.messenger.send("multi_image_picker/image/" + this.identifier, buffer);
            return null;
        }
    }

    private class GetImageTask extends AsyncTask<String, Void, Void> {
        BinaryMessenger messenger;
        String path;
        String identifier;

        public GetImageTask(BinaryMessenger messenger, String identifier, String path) {
            super();
            this.messenger = messenger;
            this.identifier = identifier;
            this.path = path;
        }

        @Override
        protected Void doInBackground(String... strings) {
            File file = new File(this.path);

            FileInputStream fileInputStream = null;
            byte[] bytesArray = null;

            try {
                bytesArray = new byte[(int) file.length()];

                //read file into bytes[]
                fileInputStream = new FileInputStream(file);
                fileInputStream.read(bytesArray);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            final ByteBuffer buffer = ByteBuffer.allocateDirect(bytesArray.length);
            buffer.put(bytesArray);
            this.messenger.send("multi_image_picker/image/" + this.identifier, buffer);
            return null;
        }
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (!setPendingMethodCallAndResult(call, result)) {
            finishWithAlreadyActiveError();
            return;
        }

        if (PICK_IMAGES.equals(call.method)) {
            openImagePicker();
        } else if (REQUEST_ORIGINAL.equals(call.method)) {
            String identifier = call.argument("identifier");
            Uri uri = Uri.parse(identifier);
            String path = getDataColumn(this.context, uri, null, null);

            GetImageTask task = new GetImageTask(this.messenger, identifier, path);
            task.execute("");
            finishWithSuccess(true);

        } else if (REQUEST_THUMBNAIL.equals(call.method)) {
            String identifier = call.argument("identifier");
            Integer width = call.argument("width");
            Integer height = call.argument("height");
            Uri uri = Uri.parse(identifier);
            String path = getDataColumn(this.context, uri, null, null);

            GetThumbnailTask task = new GetThumbnailTask(this.messenger, identifier, path, width, height);
            task.execute("");
            finishWithSuccess(true);
        } else {
            pendingResult.notImplemented();
        }
    }

    private void openImagePicker() {

        if (ContextCompat.checkSelfPermission(this.activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this.activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE_GRANT_PERMISSIONS);
            }
            clearMethodCallAndResult();
        } else {
            presentPicker();
        }

    }

    private void presentPicker() {
        int maxImages = this.methodCall.argument(MAX_IMAGES);
        Matisse.from(this.activity)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .countable(true)
                .maxSelectable(maxImages)
                .theme(R.style.Matisse_Custom)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .imageEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE);

    }

    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            List<Uri> photos = Matisse.obtainResult(data);
            List<HashMap<String, String>> result = new ArrayList<>(photos.size());
            for (Uri uri : photos) {
                HashMap<String, String> map = new HashMap<>();
                map.put("identifier", uri.toString());
                // TODO implement this
                map.put("width", null);
                map.put("height", null);
                result.add(map);
            }
            finishWithSuccess(result);
            return true;
        } else if (requestCode == REQUEST_CODE_GRANT_PERMISSIONS && resultCode == Activity.RESULT_OK) {
            presentPicker();
            return true;
        } else {
            finishWithSuccess(Collections.emptyList());
            clearMethodCallAndResult();
        }
        return false;
    }

    private void finishWithSuccess(List imagePathList) {
        if (pendingResult != null)
            pendingResult.success(imagePathList);
<<<<<<< HEAD
        clearMethodCallAndResult();
    }


    private void finishWithSuccess(String imagePath) {
        pendingResult.success(imagePath);
=======
>>>>>>> f9f3edbe93acaef4e2474e9b74f103da3a19e1a9
        clearMethodCallAndResult();
    }

    private void finishWithSuccess(Boolean result) {
        if (pendingResult != null)
            pendingResult.success(result);
        clearMethodCallAndResult();
    }

    private void finishWithAlreadyActiveError() {
        finishWithError("already_active", "Image picker is already active");
    }

    private void finishWithError(String errorCode, String errorMessage) {
        if (pendingResult != null)
            pendingResult.error(errorCode, errorMessage, null);
        clearMethodCallAndResult();
    }

    private void clearMethodCallAndResult() {
        methodCall = null;
        pendingResult = null;
    }

    private boolean setPendingMethodCallAndResult(
            MethodCall methodCall, MethodChannel.Result result) {
        if (pendingResult != null) {
            return false;
        }

        this.methodCall = methodCall;
        pendingResult = result;
        return true;
    }
}
