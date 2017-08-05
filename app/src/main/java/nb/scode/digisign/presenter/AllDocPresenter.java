package nb.scode.digisign.presenter;

import nb.scode.digisign.view.AllDocView;
import nb.scode.digisign.view.adapter.AllDocAdapter;

public interface AllDocPresenter extends BasePresenter<AllDocView> {

  void processSpinnerMenu(int menuId);

  void getAllPost();

  void getSentPost();

  void getReceivedPost();

  int getPostSize();

  void bindRowView(AllDocAdapter.AllDocAdpListener rowview, int position);

}