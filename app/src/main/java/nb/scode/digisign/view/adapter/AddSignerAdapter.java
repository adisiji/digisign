package nb.scode.digisign.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by neobyte on 5/22/2017.
 */

public class AddSignerAdapter extends ArrayAdapter<String> implements Filterable {

  private List<String> tempItems;
  private List<String> suggestions;

  public AddSignerAdapter(@NonNull Context context, @NonNull List<String> userList) {
    super(context, 0, userList);
    tempItems = new ArrayList<>(userList);
    suggestions = new ArrayList<>();
  }

  @NonNull @Override public View getView(int position, @Nullable View convertView,
      @NonNull ViewGroup parent) {
    View view = convertView;
    if (convertView == null) {
      view = LayoutInflater.from(getContext())
          .inflate(android.R.layout.simple_list_item_1, parent, false);
    }

    String item = getItem(position);

    if (item != null && view instanceof TextView) {
      ((TextView) view).setText(item);
    }

    return view;
  }

  @NonNull @Override public Filter getFilter() {
    return new Filter() {
      @NonNull @Override public CharSequence convertResultToString(@NonNull Object resultValue) {
        return (String) resultValue;
      }

      @NonNull @Override protected FilterResults performFiltering(
          @Nullable CharSequence constraint) {
        if (constraint != null) {
          suggestions.clear();
          for (String names : tempItems) {
            if (names.toLowerCase().contains(constraint.toString().toLowerCase())) {
              suggestions.add(names);
            }
          }
          FilterResults filterResults = new FilterResults();
          filterResults.values = suggestions;
          filterResults.count = suggestions.size();
          return filterResults;
        } else {
          return new FilterResults();
        }
      }

      @Override protected void publishResults(CharSequence constraint,
          @NonNull FilterResults results) {
        List<String> filterList = (ArrayList<String>) results.values;
        if (results != null && results.count > 0) {
          clear();
          for (String item : filterList) {
            add(item);
            notifyDataSetChanged();
          }
        }
      }
    };
  }
}
