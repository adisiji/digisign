package nb.scode.digisign.data.remote;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import javax.inject.Inject;
import javax.inject.Singleton;
import nb.scode.digisign.data.remote.model.SignOutEvent;
import nb.scode.digisign.data.remote.model.UserBusPost;
import org.greenrobot.eventbus.EventBus;
import timber.log.Timber;

/**
 * Created by neobyte on 4/28/2017.
 */
@Singleton public class ApiHelper implements ApiTask {

  private final ApiService apiService;
  private FirebaseAuth auth;
  private FirebaseUser user;

  /**
   * Instantiates a new Api helper.
   *
   * @param apiService the api service
   * @see ApiService for edit the EndPoint and Api Methods
   */
  @Inject ApiHelper(ApiService apiService) {
    this.apiService = apiService;
    auth = FirebaseAuth.getInstance();
  }

  @Override public void register(String email, String pass, final CommonAListener listener) {
    listener.onProcess();
    // Create new user with email and password
    auth.createUserWithEmailAndPassword(email, pass)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              sendEmailVerification(listener);
            } else {
              listener.onFailed("Authentication Failed !");
            }
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {
            Timber.e("onFailure(): " + e.toString());
            listener.onFailed(e.getMessage());
          }
        });
  }

  private void sendEmailVerification(final CommonAListener listener) {
    user = auth.getCurrentUser();
    if (user != null && !user.isEmailVerified()) {
      user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override public void onComplete(@NonNull Task<Void> task) {
          if (task.isSuccessful()) {
            listener.onSuccess();
          } else {
            listener.onFailed("Failed to send Email Verification");
          }
        }
      });
    } else {
      listener.onFailed("Can't get user");
    }
  }

  @Override public void login(String email, String pass, final CommonAListener listener) {
    listener.onProcess();
    auth.signInWithEmailAndPassword(email, pass)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override public void onComplete(@NonNull Task<AuthResult> task) {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null && user.isEmailVerified()) {

              listener.onSuccess();
            } else {
              listener.onFailed("Please verify your email first");
            }
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {
            Timber.e("onFailure(): " + e.toString());
            listener.onFailed(e.getMessage());
          }
        });
  }

  @Override public void firebaseAuthWithGoogle(GoogleSignInAccount account,
      final CommonAListener listener) {
    listener.onProcess();
    auth = FirebaseAuth.getInstance();
    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
    auth.signInWithCredential(credential)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              user = auth.getCurrentUser();
              if (user.isEmailVerified()) {
                listener.onSuccess();
              } else {
                sendEmailVerification(listener);
              }
            } else {
              listener.onFailed("Failed to Sign In with this account");
            }
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {
            listener.onFailed(e.getMessage());
          }
        });
  }

  @Override public boolean isUserSignedIn() {
    user = auth.getCurrentUser();
    return (user != null);
  }

  @Override public void getPhotoUri() {
    Uri uri = user.getPhotoUrl();
    String email = user.getEmail();
    String name = user.getDisplayName();

    UserBusPost busPost = new UserBusPost();
    busPost.setDisplayName(name);
    busPost.setEmail(email);
    busPost.setUri(uri);
    EventBus.getDefault().post(busPost);
  }

  @Override public void logout() {
    auth.signOut();
    EventBus.getDefault().post(new SignOutEvent());
  }

  @Override public void getRootCertificate(CommonAListener listener) {

  }
}
