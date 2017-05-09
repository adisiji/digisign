package nb.scode.digisign.data.remote;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by neobyte on 4/28/2017.
 */
@Singleton public class ApiHelper implements ApiTask {

  private final ApiService apiService;

  /**
   * Instantiates a new Api helper.
   *
   * @param apiService the api service
   * @see ApiService for edit the EndPoint and Api Methods
   */
  @Inject ApiHelper(ApiService apiService) {
    this.apiService = apiService;
  }
}
