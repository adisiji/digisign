package nb.scode.digisign.view.adapter;

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

  @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.card_all_files, parent, false);
    return new MyViewHolder(view);
  }

  @Override public void onBindViewHolder(MyViewHolder holder, int position) {
    holder.bind(postList.get(position), listener);
  }

  @Override public int getItemCount() {
    return postList.size();
  }

  public interface AllDocAdpListener {

    void onClick(String key);
  }

  static class MyViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.tv_file_name) TextView tvFilename;
    @BindView(R.id.tv_file_from) TextView tvFilefrom;
    @BindView(R.id.tv_file_date) TextView tvFiledate;
    @BindView(R.id.iv_status) ImageView ivStatus;
    @BindView(R.id.view) View view;

    public MyViewHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void bind(final ItemAllDoc post, final AllDocAdpListener listener) {
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
