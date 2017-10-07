package rpf5573.simpli.co.kr.deepmind.application.mainFields.post;

import static android.view.View.GONE;
import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract.Constants;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.blankj.utilcode.util.FileUtils;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import mbanje.kurt.fabbutton.FabButton;
import me.yokeyword.fragmentation.SupportFragment;
import net.gotev.uploadservice.Logger.LoggerDelegate;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;
import rpf5573.simpli.co.kr.deepmind.BuildConfig;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.application.entrance.login.LogInActivity;
import rpf5573.simpli.co.kr.deepmind.helper.hAlert;
import rpf5573.simpli.co.kr.deepmind.helper.hCallBack;
import rpf5573.simpli.co.kr.deepmind.helper.hEnum.activity;
import rpf5573.simpli.co.kr.deepmind.helper.hFileProvider;
import rpf5573.simpli.co.kr.deepmind.helper.hRealPathUtil;
import rpf5573.simpli.co.kr.deepmind.helper.hRequestQueue;
import rpf5573.simpli.co.kr.deepmind.helper.hStartBrotherEvent;
import rpf5573.simpli.co.kr.deepmind.model.mPostCrate;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;
import rpf5573.simpli.co.kr.deepmind.parent.BaseFragment;

/**
 * Created by mac88 on 2017. 8. 10..
 */

public class PostFragment extends BaseFragment implements PermissionCallbacks, BackStackDelegate {
  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */
  private static class Protagonist {
    static final int PICTURE = 1;
    static final int VIDEO = 2;
    static final int POST = 3;
  }
  private static class MediaType {
    static final int PICTURE = 4;
    static final int VIDEO = 5;
  }
  private static class RequestCode {
    static final int CAPTURE_PICTURE = 6;
    static final int CAPTURE_VIDEO = 7;
    static final int GALLERY = 8;
  }
  static final String DEEPMIND_DIRECTORY_NAME = "deepmind";
  static final String[] PERMISSIONS = {permission.RECORD_AUDIO, permission.WRITE_EXTERNAL_STORAGE, permission.CAMERA};

  //  view component
  /* ------------------------------------ */
  ViewGroup root;
  FloatingActionMenu floatingActionMenu;
  PhotoView imageView;
  VideoView videoView;
  FabButton progressBtn;

  //  data
  /* ------------------------------------ */
  PostListFragment postListFragment;
  private Uri fileUri;
  int currentPost = 0;


  /* ------------------------------------------------------------------ */
  //  Function
  /* ------------------------------------------------------------------ */

  //  init & life cycle & override
  /* ------------------------------------ */
  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
     Logger.d("called");
    Logger.d(savedInstanceState);
    if ( savedInstanceState != null ) {
      fileUri = (Uri) savedInstanceState.getParcelable("fileUri");
      Logger.d(fileUri.getPath());
    }
    root = (ViewGroup) inflater.inflate(R.layout.post_fragment, container, false);
    setup();
    return root;
  }
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Logger.d("called");
    Logger.d(fileUri);

    // 캔슬
    if ( resultCode == Activity.RESULT_CANCELED ) {
      Logger.d("cancle");
    }
    // 정상 작동
    else {
      if ( requestCode == RequestCode.CAPTURE_PICTURE || requestCode == RequestCode.CAPTURE_VIDEO ) {
        refreshGallery(fileUri);
        if ( requestCode == RequestCode.CAPTURE_PICTURE ) {
          changeProtagonist(Protagonist.PICTURE);
          setPicture(fileUri);
        }
        if ( requestCode == RequestCode.CAPTURE_VIDEO ) {
          changeProtagonist(Protagonist.VIDEO);
          setVideo(fileUri);
        }
      }
      else if ( requestCode == RequestCode.GALLERY ) {
        String realPath = hRealPathUtil.getPath(getActivity(), data.getData());
        Logger.d(realPath);
        if ( realPath == null ) {
          hAlert.show(getActivity(), "출처가 올바르지 않습니다. 앱에서 찍은 사진/영상만 업로드 해주세요");
        } else {
          if ( ! realPath.contains(DEEPMIND_DIRECTORY_NAME) ) {
            hAlert.show(getActivity(), "딥마인드 앱에서 찍은 사진/영상만 사용 가능 합니다");
            return;
          }
          fileUri = Uri.parse(realPath);
          File f = new File(fileUri.getPath());
          long size = f.length();
          int kb = (int)(size/1000);
          int mb = kb/1000;
          if ( mb > 90 ) {
            hAlert.show(getActivity(), "파일의 용량이 너무 큽니다");
          } else {
            String type = FileUtils.getFileExtension(realPath);
            Logger.d(type);
            if (type.equals("jpg") || type.equals("jpeg") || type.equals("png")) {
              changeProtagonist(Protagonist.PICTURE);
              setPicture(fileUri);
            } else if (type.equals("3gp") || type.equals("mp4")) {
              Logger.d(" 비디오가 맞아유~~ ");
              changeProtagonist(Protagonist.VIDEO);
              setVideo(fileUri);
            }
          }
        }
      }
    }
  }
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    Logger.d(outState);
    outState.putParcelable("fileUri", this.fileUri);
    Logger.d("called");
    Logger.d(outState);
  }
  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    Logger.d("called");
    Logger.d(savedInstanceState);
  }
  @Override
  public void onPermissionsGranted(int requestCode, List<String> perms) {
    if ( requestCode == RequestCode.CAPTURE_PICTURE ) {
      openCamera(MediaType.PICTURE);
    } else if ( requestCode == RequestCode.CAPTURE_VIDEO ) {
      openCamera(MediaType.VIDEO);
    }
  }
  @Override
  public void onPermissionsDenied(int requestCode, List<String> perms) {
    Logger.d("called");
    if ( requestCode == RequestCode.CAPTURE_PICTURE || requestCode == RequestCode.CAPTURE_VIDEO || requestCode == RequestCode.GALLERY ) {
      new AppSettingsDialog.Builder(this)
          .setNegativeButton("닫기")
          .setPositiveButton("설정")
          .setTitle("요청")
          .setRationale("원활한 교육 진행을 위해서 권한획득이 필요합니다. 설정창에서 권한 획득에 동의해 주세요")
          .build().show();
    }
  }
  @Override
  public void onBackStack(int post) {
    setPostImage(getActivity(), post);
  }

  //  setup
  /* ------------------------------------ */
  private void setup() {
    setupFloatingActionMenu();
    setupImageView();
    setupVideoView();
    setupProgressBtn();
  }
  private void setupFloatingActionMenu() {
    floatingActionMenu = (FloatingActionMenu) root.findViewById(R.id.post__floatingActionMenu);
    ((FloatingActionButton) floatingActionMenu.findViewById(R.id.post__floatingActionMenu__picture)).setOnClickListener( pictureMenuItemClickListener );
    ((FloatingActionButton) floatingActionMenu.findViewById(R.id.post__floatingActionMenu__video)).setOnClickListener( videoMenuItemClickListener );
    ((FloatingActionButton) floatingActionMenu.findViewById(R.id.post__floatingActionMenu__gallery)).setOnClickListener( galleryMenuItemClickListener );
    ((FloatingActionButton) floatingActionMenu.findViewById(R.id.post__floatingActionMenu__post)).setOnClickListener( postMenuItemClickListener );
    ((FloatingActionButton) floatingActionMenu.findViewById(R.id.post__floatingActionMenu__upload)).setOnClickListener( uploadMenuItemClickListener );
  }
  private void setupImageView() {

    imageView = (PhotoView) root.findViewById(R.id.post__imageView);
    getCurrentPostFromServer(new hCallBack() {
      @Override
      public void call(String json) {
        Logger.d(json);
        JSONObject jsonObj = null;
        try {
          jsonObj = new JSONObject(json);
          int responseCode = jsonObj.getInt("response_code");
          if ( responseCode == 201 ) {
            currentPost = jsonObj.getInt("value");
            setPostImage(getActivity(), currentPost);
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
  }
  private void setupVideoView() {
    videoView = (VideoView) root.findViewById(R.id.post__videoView);
    MediaController mediaController = new MediaController(getActivity());
    this.videoView.setMediaController(mediaController);
  }
  private void setupProgressBtn() { progressBtn = (FabButton) root.findViewById(R.id.post__progressBtn); }

  @Override
  public void onResume() {
    super.onResume();

  }

  @Override
  public void onStart() {
    super.onStart();

  }

  //  listener
  /* ------------------------------------ */
  OnClickListener pictureMenuItemClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {

      floatingActionMenu.close(true);
      if ( ! EasyPermissions.hasPermissions(getActivity(), PERMISSIONS ) ) {
        EasyPermissions.requestPermissions(getActivity(), "원활한 교육 진행을 위해서 권한획득이 필요합니다", RequestCode.CAPTURE_PICTURE, PERMISSIONS);
      } else {
        Logger.d(" Has Camera Permission ");
        openCamera( MediaType.PICTURE );
      }
    }
  };
  OnClickListener videoMenuItemClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {

      floatingActionMenu.close(true);
      if ( ! EasyPermissions.hasPermissions(getActivity(), PERMISSIONS ) ) {
        EasyPermissions.requestPermissions(getActivity(), "원활한 교육 진행을 위해서 권한획득이 필요합니다", RequestCode.CAPTURE_VIDEO, PERMISSIONS);
      } else {
        Logger.d(" Has Camera Permission ");
        openCamera( MediaType.VIDEO );
      }
    }
  };
  OnClickListener galleryMenuItemClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {

      floatingActionMenu.close(true);
      if ( ! EasyPermissions.hasPermissions(getActivity(), PERMISSIONS ) ) {
        EasyPermissions.requestPermissions(getActivity(), "원활한 교육 진행을 위해서 권한획득이 필요합니다", RequestCode.GALLERY, PERMISSIONS);
      } else {
        Logger.d(" Has Camera Permission ");
        openGallery();
      }
    }
  };
  OnClickListener postMenuItemClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {

      floatingActionMenu.close(true);
      if ( postListFragment == null ) {
        postListFragment = PostListFragment.newInstance(PostFragment.this);
      }
      EventBus.getDefault().post(new hStartBrotherEvent(postListFragment));
    }
  };
  OnClickListener uploadMenuItemClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {

      Logger.d(currentPost);

      Context context = getActivity();
      // 검사 1 - 업로드 할 자료가 있는가?
      if ( fileUri == null ) {
        hAlert.show(context, "업로드 할 자료가 없습니다");
        return;
      }
      // 검사 2 - 1포스트를 먼저 찍었는가? ( 맨 처음 들어왔을때, 중간에 앱을 끄고 다시 들어왔을때, 처음 이후에 )
      // 맨 처음에 들어왔을때
      if ( postListFragment == null && currentPost == 0 ) {
        hAlert.show(context, "1포스트를 먼저 선택해주세요");
        return;
      }
      // 맨 처음에 걸려서 -> 포스트 페이지 봤지만, 1포스트 찍지도 않고 돌아왔을때
      if ( postListFragment != null && postListFragment.getCurrentPost() == 0 ) {
        hAlert.show(context, "1포스트를 먼저 선택해주세요");
        return;
      }

      if ( (postListFragment != null && postListFragment.getCurrentPost() == 0) && currentPost == 0 ) {
        hAlert.show(context, "1포스트를 먼저 선택해주세요");
        return;
      }

      // 모든 관문을 통과하고 나서 upload를 합니다!
      uploadMedia(fileUri, new hCallBack() {
        @Override
        public void call(String json) {
          floatingActionMenu.close(true);
          JSONObject jsonObj = null;
          try {
            jsonObj = new JSONObject(json);
            int responseCode = jsonObj.getInt("response_code");
            if ( responseCode == 201 ) {
              String value = jsonObj.getString("value");
              Gson gson = new Gson();
              hAlert.show(getActivity(), jsonObj.getString("success_message"));
            } else {
              hAlert.show(getActivity(), jsonObj.getString("error_message"));
            }
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      });
    }
  };
  UploadStatusDelegate uploadStatusDelegate = new UploadStatusDelegate() {
    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {

      floatingActionMenu.close(true);
      int percent = uploadInfo.getProgressPercent();
      System.out.println(percent);
      progressBtn.setProgress(percent);
    }
    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse,
        Exception exception) {

      Logger.e(exception.getMessage());
    }
    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
      progressBtn.setVisibility(GONE);
      String response = serverResponse.getBodyAsString();
      Gson gson = new Gson();
      JSONObject jsonObj = null;
      try {
        Logger.d(response);
        jsonObj = new JSONObject( response );
        int responseCode = jsonObj.getInt("response_code");
        if ( responseCode == 201 ) {
          hAlert.show(getActivity(), jsonObj.getString("success_message"));
          if ( postListFragment != null ) {
            setPostImage(context, postListFragment.getCurrentPost()); // 업로드가 끝나면 현재 포스트 이미지를 뿌려줘야지!!!
          } else {
            setPostImage(context, currentPost); // 업로드가 끝나면 현재 포스트 이미지를 뿌려줘야지!!!
          }
        } else {
          hAlert.show(getActivity(), jsonObj.getString("error_message"));
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {
      progressBtn.setVisibility(GONE);
      hAlert.show(getActivity(), "업로드 실패 - 에러가 발생하였습니다");
    }
  };

  //  custom
  /* ------------------------------------ */
  private void openCamera( int mediaType ) {
    floatingActionMenu.toggle(true);
    Context context = getActivity();
    if ( mediaType == MediaType.PICTURE ) {

      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      PackageManager packageManager = this.getActivity().getPackageManager();
      List<ResolveInfo> listCam = packageManager.queryIntentActivities(intent, 0);
      intent.setPackage(listCam.get(0).activityInfo.packageName);
      fileUri = getUriFromFile(getMediaFile(MediaType.PICTURE));
      Logger.d(fileUri);
      intent.putExtra(MediaStore.EXTRA_OUTPUT , fileUri);
      startActivityForResult(intent, RequestCode.CAPTURE_PICTURE);

    } else if ( mediaType == MediaType.VIDEO ) {

      Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
      PackageManager packageManager = this.getActivity().getPackageManager();
      List<ResolveInfo> listCam = packageManager.queryIntentActivities(intent, 0);
      intent.setPackage(listCam.get(0).activityInfo.packageName);
      File mediaFile = getMediaFile(MediaType.VIDEO);
      fileUri = getUriFromFile(mediaFile);

      intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60); //제한 시간 ==> 60초
      long limit_time = (long)(95*1024*1024); //제한 용량 ==> 95MB
      intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, limit_time);
      intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

      startActivityForResult(intent, RequestCode.CAPTURE_VIDEO);
    }
  }
  private void openGallery() {
    final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    intent.setType("*/*");
    startActivityForResult(intent, RequestCode.GALLERY);
  }
  private void setPostImage(Context context, int post) {
    Logger.d(post);
    if ( post > 0 ) {
      changeProtagonist(Protagonist.POST); // 주인공을 바꾸고!
      String postUrl = hRequestQueue.BASE_URL+"/Posts/"+post+".jpg";
      Glide.with(context).load(postUrl).into(imageView);
    } else {
      Logger.e("post가 0입니다!");
    }
  }
  private void refreshGallery(Uri uri) {
    MediaScannerConnection.scanFile(getActivity(),
        new String[]{uri.getPath()}, null,
        new MediaScannerConnection.OnScanCompletedListener() {
          public void onScanCompleted(String path, Uri uri) {

          }
        });
  }
  private void changeProtagonist( int protagonist ) {
    if ( protagonist == Protagonist.PICTURE ) {
      videoView.setVisibility(GONE);
      imageView.setVisibility(View.VISIBLE);
    } else if ( protagonist == Protagonist.POST ) {
      videoView.setVisibility(GONE);
      imageView.setVisibility(View.VISIBLE);
      fileUri = null;
    }
    else if ( protagonist == Protagonist.VIDEO ) {
      imageView.setVisibility(GONE);
      videoView.setVisibility(View.VISIBLE);
    }
  }
  private void setPicture(Uri fileUri) {
    File file = new File(fileUri.getPath());
    try {
      InputStream ims = new FileInputStream(file);
      Bitmap rotatedBitmap = rotateImage(BitmapFactory.decodeStream(ims), fileUri);
      if ( rotatedBitmap != null ) {
        imageView.setImageBitmap( rotatedBitmap );
      } else {
        hAlert.show(getActivity(), "이미지 회전 실패");
      }
    } catch (FileNotFoundException e) {
      Logger.e(e.getMessage());
    }
  }
  private void setVideo(Uri fileUri) {
    Logger.d(fileUri);
    videoView.setVideoPath(fileUri.getPath());
    videoView.start();
  }
  private Uri getUriFromFile(File file){
    return Uri.fromFile(file);
  }
  private File getMediaFile(int type){
    File mediaStorageDir = new File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
        DEEPMIND_DIRECTORY_NAME);
    if(!mediaStorageDir.exists()){
      Logger.d("mediaStorageDir 가 없습니다.");
      if(!mediaStorageDir.mkdir()){
        Logger.d("mediaStorageDir.mkdir() 못한다!!");
        return null;
      }
    }
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
        Locale.getDefault()).format(new Date());
    File mediaFile = null;
    String fileName;
    if(type == MediaType.PICTURE){
      fileName = mSettings.instance.our_team+"Team_IMG_"+timeStamp+".jpg";
      mediaFile = new File(mediaStorageDir.getPath() + File.separator
          + fileName);
    }else if(type == MediaType.VIDEO){
      fileName = mSettings.instance.our_team+"Team_VIDEO_"+timeStamp+"_c.mp4";
      mediaFile = new File(mediaStorageDir.getPath() + File.separator
          + fileName);
    }

    Logger.d( mediaFile.getAbsolutePath() );

    return mediaFile;
  }
  private void uploadMedia(Uri fileUri, final hCallBack callBack) {
    //Uploading code
    beforeUpload();
    try {
      String fileName = FileUtils.getFileName(fileUri.getPath());
      String url = hRequestQueue.BASE_URL + "/upload.php";
      final String uploadId = UUID.randomUUID().toString();
      String team = mSettings.instance.our_team + "";

      Logger.d(fileUri.getPath());

      //Creating a multi part request
      new MultipartUploadRequest(getActivity(), uploadId, url)
          .addFileToUpload(fileUri.getPath(), "upload_file", fileName) //Adding file
          .addParameter("team", team) //Adding text parameter to the request
          .setMaxRetries(2)
          .setMethod("POST")
          .setDelegate(uploadStatusDelegate) // Ananymos Object를 안만들고, 여기 Fragment에 Implement시켜서 만들었음!
          .startUpload(); //Starting the upload

    } catch (Exception exc) {
      Logger.d(exc.getMessage());
    }
  }
  private void beforeUpload() {

    floatingActionMenu.close(true);
    progressBtn.setVisibility(View.VISIBLE);
  }
  private void getCurrentPostFromServer(final hCallBack callBack) {
    String url = hRequestQueue.BASE_URL + "/post.php";
    StringRequest request = new StringRequest(Request.Method.POST, url, new Listener<String>() {
      @Override
      public void onResponse(String response) {
        Logger.d(response);
        callBack.call(response);
      }
    }, new ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Logger.e(error.getMessage());
      }
    }) {
      @Override
      public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        String ourTeam = mSettings.instance.our_team + "";
        String totalTeamCount = mSettings.instance.total_team_count + "";
        params.put("team", ourTeam);
        params.put("get_current_post", "true");
        return params;
      }
    };
    hRequestQueue.getInstance(getActivity()).add(request);
  }
  private Bitmap rotateImage(Bitmap _bitmap , Uri _uri){
    ExifInterface exifInterface = null;
    Bitmap bitmap = null;
    try {
      exifInterface = new ExifInterface(_uri.getPath());
      int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION , ExifInterface.ORIENTATION_UNDEFINED);
      Matrix matrix = new Matrix();
      switch (orientation){
        case ExifInterface.ORIENTATION_ROTATE_90 :
          Logger.d("ORIENTATION_ROTATE_90");
          matrix.setRotate(90);
          break;
        case ExifInterface.ORIENTATION_ROTATE_180 :
          Logger.d("ORIENTATION_ROTATE_180");
          matrix.setRotate(180);
          break;
        case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
          Logger.d("FLIP_HORIZONTAL");
          break;
        case ExifInterface.ORIENTATION_ROTATE_270:
          matrix.setRotate(270);
          Logger.d("ORIENTATION_ROTATE_270");
          break;
        default:
          break;
      }
      bitmap = Bitmap.createBitmap(_bitmap , 0, 0, _bitmap.getWidth() , _bitmap.getHeight() , matrix, true);
    }catch (IOException e) {
      Logger.d(e);
    }
    return bitmap;
  }
}
