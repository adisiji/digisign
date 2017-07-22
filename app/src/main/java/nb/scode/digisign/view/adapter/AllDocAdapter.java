package nb.scode.digisign.view.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.List;
import nb.scode.digisign.R;
import nb.scode.digisign.view.model.ItemAllDoc;

/**
 * Created by neobyte on 5/25/2017.
 */

public class AllDocAdapter extends RecyclerView.Adapter<AllDocAdapter.MyViewHolder> {

  private final List<ItemAllDoc> postList;
  private final AllDocAdpListener listener;

  public AllDocAdapter(List<ItemAllDoc> postList, AllDocAdpListener listener) {
    this.postList = postList;
    this.listener = listener;
  }

  @NonNull @Override public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
      int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.card_all_files, parent, false);
    return new MyViewHolder(view);
  }

  @Override public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    holder.bind(postList.get(position), listener);
  }

  @Override public int getItemCount() {
    return postList.size();
  }

  public interface AllDocAdpListener {

    void onClick(String key);
  }

  static class MyViewHolder extends RecyclerView.ViewHolder {

    @Nullable @BindView(R.id.tv_file_name) TextView tvFilename;
    @Nullable @BindView(R.id.tv_file_from) TextView tvFilefrom;
    @Nullable @BindView(R.id.tv_file_date) TextView tvFiledate;
    @Nullable @BindView(R.id.iv_status) ImageView ivStatus;
    @Nullable @BindView(R.id.view) View view;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void bind(@NonNull final ItemAllDoc post, @NonNull final AllDocAdpListener listener) {
      tvFilename.setText(post.getFilename());
      tvFilefrom.setText(post.getFromname());
      tvFiledate.setText(post.getFiledate());
      if (post.isSent()) {
        ivStatus.setImageResource(R.drawable.ic_arrow_upward_yellow_a700_36dp);
      } else {
        ivStatus.setImageResource(R.drawable.ic_arrow_downward_blue_700_36dp);
      }
      view.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
          listener.onClick(post.getFilekey());
        }
      });
    }
  }
}
