package rpf5573.simpli.co.kr.deepmind.application.mainFields.out;

import static android.view.View.GONE;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import com.blankj.utilcode.util.SizeUtils;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.ArrayList;
import rpf5573.simpli.co.kr.deepmind.R;
import rpf5573.simpli.co.kr.deepmind.application.mainFields.out.OutFragmentAdapter.ViewHolder;
import rpf5573.simpli.co.kr.deepmind.helper.hColor;
import rpf5573.simpli.co.kr.deepmind.model.mSettings;

/**
 * Created by mac88 on 2017. 8. 31..
 */

public class OutFragmentAdapter extends RecyclerView.Adapter<ViewHolder> {

  /* ------------------------------------------------------------------ */
  //  Property
  /* ------------------------------------------------------------------ */

  //  constant
  /* ------------------------------------ */

  //  view component
  /* ------------------------------------ */

  //  data
  /* ------------------------------------ */
  private Context context;
  private ArrayList<Integer> items;
  OutTeamBtnClickHandleDelegate delegate;


  /* ------------------------------------------------------------------ */
  //  Function
  /* ------------------------------------------------------------------ */

  //  init & life cycle & override
  /* ------------------------------------ */
  OutFragmentAdapter(ArrayList<Integer> items, Context context, OutTeamBtnClickHandleDelegate delegate) {
    this.context = context;
    this.items = items;
    this.delegate = delegate;
  }
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater
        .from(parent.getContext()).inflate(R.layout.out_fragment_cell, parent, false);
    GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) v.getLayoutParams();
    lp.height = ( parent.getMeasuredWidth() - context.getResources().getDimensionPixelSize(R.dimen.out_fragment_teamButtonOffset) * 6 )/4;
    v.setLayoutParams(lp);
    ViewHolder viewHolder = new ViewHolder(context, v);
    viewHolder.teamButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        int position = v.getId();
        int team = position + 1;
        delegate.onOutTeamBtnClick(team);
      }
    });
    return viewHolder;
  }
  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    int ourTeam = mSettings.instance.our_team;
    String team = (position+1)+"조";
    holder.teamButton.setText(team);
    holder.teamButton.setId(position);
    if ( (position+1) == ourTeam ) {
      holder.highlight();
    } else {
      holder.deHighlight();
    }
  }
  @Override
  public int getItemCount() {
    return items.size();
  }

  //  setup
  /* ------------------------------------ */

  //  listener
  /* ------------------------------------ */

  //  custom
  /* ------------------------------------ */

  class ViewHolder extends RecyclerView.ViewHolder {
    Context context;
    View root;
    Button teamButton;
    AVLoadingIndicatorView indicatorView;
    ViewHolder(Context context, View root) {
      super(root);
      this.root = root;
      this.context = context;
      teamButton = (Button) this.root.findViewById(R.id.out__recyclerView__teamButton);
      teamButton.setHeight(teamButton.getWidth());
      indicatorView = (AVLoadingIndicatorView) this.root.findViewById(R.id.out__recyclerView__indicator);
    }
    public void showIndicator() {
      teamButton.setVisibility(GONE);
      indicatorView.setVisibility(View.VISIBLE);
    }
    public void hideIndicator() {
      teamButton.setVisibility(View.VISIBLE);
      indicatorView.setVisibility(GONE);
    }
    public void highlight() {
      StateListDrawable gradientDrawable = (StateListDrawable) root.getBackground();
      DrawableContainerState drawableContainerState = (DrawableContainerState) gradientDrawable.getConstantState();
      Drawable[] children = drawableContainerState.getChildren();
      LayerDrawable selectedItem = (LayerDrawable) children[0];
      //LayerDrawable unselectedItem = (LayerDrawable) children[1];
      GradientDrawable selectedDrawable = (GradientDrawable) selectedItem.getDrawable(0);
      //GradientDrawable unselectedDrawable = (GradientDrawable) unselectedItem.getDrawable(0);
      selectedDrawable.setStroke(SizeUtils.dp2px(4), hColor.getColor(context, R.color.md_yellow_800));
      //selectedDrawable.setStroke(3, hColor.getColor(context, R.color.md_blue_800));
    }
    public void deHighlight() {
      StateListDrawable gradientDrawable = (StateListDrawable) root.getBackground();
      DrawableContainerState drawableContainerState = (DrawableContainerState) gradientDrawable.getConstantState();
      Drawable[] children = drawableContainerState.getChildren();
      LayerDrawable selectedItem = (LayerDrawable) children[0];
      //LayerDrawable unselectedItem = (LayerDrawable) children[1];
      GradientDrawable selectedDrawable = (GradientDrawable) selectedItem.getDrawable(0);
      //GradientDrawable unselectedDrawable = (GradientDrawable) unselectedItem.getDrawable(0);
      selectedDrawable.setStroke(SizeUtils.dp2px(4), hColor.getColor(context, R.color.md_black_1000));
      //selectedDrawable.setStroke(3, hColor.getColor(context, R.color.md_teal_800));
    }
  }
}