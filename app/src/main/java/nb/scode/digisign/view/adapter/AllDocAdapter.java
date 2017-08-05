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
import nb.scode.digisign.R;
import nb.scode.digisign.presenter.AllDocPresenter;

/**
 * Created by neobyte on 5/25/2017.
 */

public class AllDocAdapter extends RecyclerView.Adapter<AllDocAdapter.MyViewHolder> {

  private final AllDocPresenter presenter;

  public AllDocAdapter(AllDocPresenter presenter) {
    this.presenter = presenter;
  }

  @NonNull @Override public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
      int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.card_all_files, parent, false);
    return new MyViewHolder(view);
  }

  @Override public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
    presenter.bindRowView(holder, position);
  }

  @Override public int getItemCount() {
    return presenter.getPostSize();
  }

  public interface AllDocAdpListener {

    //void onClick(String key);

    void setFileName(String fileName);

    void setFileFrom(String fileFrom);

    void setFileDate(String fileDate);

    void setIvStatus(boolean isSent);
  }

  static class MyViewHolder extends RecyclerView.ViewHolder implements AllDocAdpListener {

    @Nullable @BindView(R.id.tv_file_name) TextView tvFilename;
    @Nullable @BindView(R.id.tv_file_from) TextView tvFilefrom;
    @Nullable @BindView(R.id.tv_file_date) TextView tvFiledate;
    @Nullable @BindView(R.id.iv_status) ImageView ivStatus;
    @Nullable @BindView(R.id.view) View view;

    MyViewHolder(@NonNull View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    @Override public void setFileName(String fileName) {
      tvFilename.setText(fileName);
    }

    @Override public void setFileFrom(String fileFrom) {
      tvFilefrom.setText(fileFrom);
    }

    @Override public void setFileDate(String fileDate) {
      tvFiledate.setText(fileDate);
    }

    @Override public void setIvStatus(boolean isSent) {
      if (isSent) {
        ivStatus.setImageResource(R.drawable.ic_arrow_upward_yellow_a700_36dp);
      } else {
        ivStatus.setImageResource(R.drawable.ic_arrow_downward_blue_700_36dp);
      }
    }
  }
}
