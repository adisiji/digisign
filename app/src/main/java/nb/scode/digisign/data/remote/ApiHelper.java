package nb.scode.digisign.data.remote;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import durdinapps.rxfirebase2.RxFirebaseStorage;
import io.reactivex.Maybe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import nb.scode.digisign.data.remote.BusModel.UserBusPost;
import nb.scode.digisign.data.remote.FireModel.User;
import timber.log.Timber;

/**
 * Created by neobyte on 4/28/2017.
 */

@Singleton public class ApiHelper implements ApiTask {

  private final String PUBLIC_KEY = "pubkey.pbk";
  private final String PRIVATE_KEY = "privkey.pvk";
  private final ApiService apiService;
  private final String USER_STORAGE_REF = "/users/";
  private FirebaseAuth auth;
  private FirebaseUser user;
  private FirebaseDatabase database;
  private StorageReference storageRef;
  private Map<String, User> userMap;

  /**
   * Instantiates a new Api helper.
   *
   * @param apiService the api service
   * @see ApiService for edit the EndPoint and Api Methods
   */
  @Inject ApiHelper(ApiService apiService) {
    this.apiService = apiService;
    auth = FirebaseAuth.getInstance();
    database = FirebaseDatabase.getInstance();
    storageRef = FirebaseStorage.getInstance().getReference();
    userMap = new HashMap<>();
    activateUsersListener();
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
            FirebaseUser userx = auth.getCurrentUser();
            if (userx != null && userx.isEmailVerified()) {
              user = userx;
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
            }
          }
        }).addOnFailureListener(new OnFailureListener() {
      @Override public void onFailure(@NonNull Exception e) {
        listener.onFailed(e.getMessage());
      }
    });
  }

  @Override public String getEmailUser() {
    return user.getEmail();
  }

  @Override public void downloadKeyPair(File publickey, File privatekey,
      final CommonAListener listener) {
    listener.onProcess();
    Timber.d("downloadKeyPair(): OK");
    String publicFolderRef = USER_STORAGE_REF + user.getUid() + "/public/" + PUBLIC_KEY;
    StorageReference refpub = storageRef.child(publicFolderRef);
    Maybe<FileDownloadTask.TaskSnapshot> obs1 = RxFirebaseStorage.getFile(refpub, publickey);

    String privateFolderRef = USER_STORAGE_REF + user.getUid() + "/" + PRIVATE_KEY;
    StorageReference refPriv = storageRef.child(privateFolderRef);
    Maybe<FileDownloadTask.TaskSnapshot> obs2 = RxFirebaseStorage.getFile(refPriv, privatekey);

    Maybe.concat(obs1, obs2)
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<FileDownloadTask.TaskSnapshot>() {
          @Override public void accept(
              @io.reactivex.annotations.NonNull FileDownloadTask.TaskSnapshot taskSnapshot)
              throws Exception {
            Timber.d("accept(): good");
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(@io.reactivex.annotations.NonNull Throwable throwable)
              throws Exception {
            Timber.e("accept(): error " + throwable.getMessage());
            listener.onFailed(throwable.getMessage());
          }
        }, new Action() {
          @Override public void run() throws Exception {
            listener.onSuccess();
          }
        });
  }

  @Override public void checkRemoteKeyPair(final CommonAListener listener) {
    listener.onProcess();

    String publicFolderRef = USER_STORAGE_REF + user.getUid() + "/public/" + PUBLIC_KEY;
    StorageReference refpub = storageRef.child(publicFolderRef);
    Maybe<Uri> obs1 = RxFirebaseStorage.getDownloadUrl(refpub);

    String privateFolderRef = USER_STORAGE_REF + user.getUid() + "/" + PRIVATE_KEY;
    StorageReference refPriv = storageRef.child(privateFolderRef);
    Maybe<Uri> obs2 = RxFirebaseStorage.getDownloadUrl(refPriv);

    Maybe.concat(obs1, obs2).subscribeOn(Schedulers.io()).subscribe(new Consumer<Uri>() {
      @Override public void accept(@io.reactivex.annotations.NonNull Uri uri) throws Exception {

      }
    }, new Consumer<Throwable>() {
      @Override public void accept(@io.reactivex.annotations.NonNull Throwable throwable)
          throws Exception {
        Timber.e("accept(): error " + throwable.getMessage());
        listener.onFailed(throwable.getMessage());
      }
    }, new Action() {
      @Override public void run() throws Exception {
        listener.onSuccess();
      }
    });
  }

  @Override public boolean isUserSignedIn() {
    user = auth.getCurrentUser();
    return (user != null);
  }

  @Override public UserBusPost getUserProfile() {
    Uri uri = user.getPhotoUrl();
    String email = user.getEmail();
    String name = user.getDisplayName();

    UserBusPost busPost = new UserBusPost();
    busPost.setDisplayName(name);
    busPost.setEmail(email);
    busPost.setUri(uri);
    return busPost;
  }

  @Override public void logout() {
    auth.signOut();
  }

  @Override public void uploadKeyPair(File publickey, File privatekey,
      final CommonAListener listener) {
    listener.onProcess();

    String publicFolderRef = USER_STORAGE_REF + user.getUid() + "/public/" + PUBLIC_KEY;
    Uri uri = Uri.fromFile(publickey);
    storageRef.child(publicFolderRef);
    Maybe<UploadTask.TaskSnapshot> obs1 = RxFirebaseStorage.putFile(storageRef, uri);

    String privateFolderRef = USER_STORAGE_REF + user.getUid() + "/" + PRIVATE_KEY;
    Uri uri1 = Uri.fromFile(privatekey);
    storageRef.child(privateFolderRef);
    Maybe<UploadTask.TaskSnapshot> obs2 = RxFirebaseStorage.putFile(storageRef, uri1);

    Maybe.concat(obs1, obs2)
        .subscribeOn(Schedulers.io())
        .subscribe(new Consumer<UploadTask.TaskSnapshot>() {
          @Override public void accept(
              @io.reactivex.annotations.NonNull UploadTask.TaskSnapshot taskSnapshot)
              throws Exception {

          }
        }, new Consumer<Throwable>() {
          @Override public void accept(@io.reactivex.annotations.NonNull Throwable throwable)
              throws Exception {
            listener.onFailed(throwable.getMessage());
          }
        }, new Action() {
          @Override public void run() throws Exception {
            insertUserData(listener);
          }
        });
  }

  private void insertUserData(final CommonAListener listener) {
    // new user node would be /users/$userid/
    DatabaseReference reference1 = database.getReference("users").child(user.getUid());

    // creating user object
    User userd = new User(user.getDisplayName(), user.getEmail());

    // pushing user to 'users' node using the userId
    reference1.setValue(userd).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override public void onSuccess(Void aVoid) {
        listener.onSuccess();
      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override public void onFailure(@NonNull Exception e) {
        listener.onFailed(e.getMessage());
        Timber.e("onFailure(): " + e.getMessage());
      }
    });
  }

  @Override public void getUserPost() {
    // First get User Data
    DatabaseReference mDatabase = database.getReference("users");
    String uid = user.getUid();

    mDatabase.child(uid).addValueEventListener(new ValueEventListener() {
      @Override public void onDataChange(DataSnapshot dataSnapshot) {
        User userData = dataSnapshot.getValue(User.class);
        userData.getEmail();
        Timber.d("onDataChange(): " + userData.getName());
      }

      @Override public void onCancelled(DatabaseError databaseError) {

      }
    });
  }

  private void activateUsersListener() {
    DatabaseReference mDatabase = database.getReference("users");
    mDatabase.addValueEventListener(new ValueEventListener() {
      @Override public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
          User user = childDataSnapshot.getValue(User.class);
          userMap.put(childDataSnapshot.getKey(), user);
          Timber.d("onDataChange(): key => "
              + childDataSnapshot.getKey()); //displays the key for the node (uid)
          Timber.d("onDataChange(): value => "
              + childDataSnapshot.getValue());   //gives the value for given keyname (User)
        }
        Timber.d("onDataChange(): finished");
      }

      @Override public void onCancelled(DatabaseError databaseError) {

      }
    });
  }

  @Override public Map<String, User> getListUser() {
    return userMap;
  }
}