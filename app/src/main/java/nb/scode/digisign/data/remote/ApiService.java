package nb.scode.digisign.data.remote;

import io.reactivex.Observable;
import java.util.List;
import retrofit2.http.GET;

/**
 * Created by neobyte on 4/27/2017.
 */
public interface ApiService {
  /**
   * The constant ENDPOINT.
   */
  String ENDPOINT = "https://api.ribot.io/";

  /**
   * Gets ribots.
   *
   * @return the ribots
   */
  @GET("ribots") Observable<List<String>> getRibots();
}
