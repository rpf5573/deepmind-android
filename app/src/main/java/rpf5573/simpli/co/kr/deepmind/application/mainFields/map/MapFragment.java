package rpf5573.simpli.co.kr.deepmind.application.mainFields.map;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.orhanobut.logger.Logger;
import me.yokeyword.fragmentation.SupportFragment;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.helper.hRequestQueue;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;

/**
 * Created by mac88 on 2017. 8. 10..
 */

public class MapFragment extends SupportFragment {

  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */

  //  view component
  /* ------------------------------------ */
  ViewGroup root;
  PhotoView photoView;
  PhotoViewAttacher photoViewAttacher;

  //  data
  /* ------------------------------------ */


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
    root = (ViewGroup) inflater.inflate(R.layout.map_fragment, container, false);
    setup();
    return root;
  }

  @Override
  public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
    super.onViewStateRestored(savedInstanceState);
    Logger.d(savedInstanceState);
  }

  //  setup
  /* ------------------------------------ */
  private void setup() {
    setupphotoView();
  }
  private void setupphotoView() {
    photoView = (PhotoView) root.findViewById(R.id.photo_view);
    String imgUrl = hRequestQueue.BASE_URL+"/Whole_Map/"+ mSettings.instance.whole_map_name;
    Glide.with(this)
        .load(imgUrl)
        .listener(new RequestListener<Drawable>() {
          @Override
          public boolean onLoadFailed(@Nullable GlideException e, Object model,
              Target<Drawable> target,
              boolean isFirstResource) {

            return false;
          }

          @Override
          public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
              DataSource dataSource, boolean isFirstResource) {
            photoView.setImageDrawable(resource);
            photoViewAttacher = new PhotoViewAttacher(photoView);

            return false;
          }
        }).into(photoView);
  }

  //  listener
  /* ------------------------------------ */

  //  custom
  /* ------------------------------------ */

}
