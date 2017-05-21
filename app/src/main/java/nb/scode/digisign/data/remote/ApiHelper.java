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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import nb.scode.digisign.data.remote.BusModel.SignOutEvent;
import nb.scode.digisign.data.remote.BusModel.UserBusPost;
import nb.scode.digisign.data.remote.FireModel.ListUid;
import nb.scode.digisign.data.remote.FireModel.User;
import org.greenrobot.eventbus.EventBus;
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
  private StorageReference storageRef;
  private int init = 0;

  /**
   * Instantiates a new Api helper.
   *
   * @param apiService the api service
   * @see ApiService for edit the EndPoint and Api Methods
   */
  @Inject ApiHelper(ApiService apiService) {
    this.apiService = apiService;
    auth = FirebaseAuth.getInstance();
    storageRef = FirebaseStorage.getInstance().getReference();
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

  @Override public void downloadKeyPair(File publickey, File privatekey, CommonAListener listener) {
    init = 0;
  }

  @Override public void checkRemoteKeyPair(final CommonAListener listener) {
    init = 0;
    String publicFolderRef = USER_STORAGE_REF + user.getUid() + "/public/" + PUBLIC_KEY;
    storageRef.child(publicFolderRef)
        .getDownloadUrl()
        .addOnSuccessListener(new OnSuccessListener<Uri>() {
          @Override public void onSuccess(Uri uri) {
            if (init != 1) {
              init++;
            } else {
              listener.onSuccess();
            }
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {
            listener.onFailed(e.getMessage());
            Timber.d("onFailure(): Key Pair Not available");
          }
        });

    String privateFolderRef = USER_STORAGE_REF + user.getUid() + "/" + PRIVATE_KEY;
    storageRef.child(privateFolderRef)
        .getDownloadUrl()
        .addOnSuccessListener(new OnSuccessListener<Uri>() {
          @Override public void onSuccess(Uri uri) {
            if (init != 1) {
              init++;
            } else {
              listener.onSuccess();
            }
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {
            listener.onFailed(e.getMessage());
            Timber.d("onFailure(): Key Pair Not available");
          }
        });
  }

  @Override public boolean isUserSignedIn() {
    user = auth.getCurrentUser();
    return (user != null);
  }

  @Override public void getUserProfile() {
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

  @Override public void uploadKeyPair(File publickey, File privatekey,
      final CommonAListener listener) {
    listener.onProcess();
    init = 0;
    String publicFolderRef = USER_STORAGE_REF + user.getUid() + "/public/" + PUBLIC_KEY;
    Uri uri = Uri.fromFile(publickey);
    Timber.d("uploadKeyPair(): " + publicFolderRef);
    storageRef.child(publicFolderRef)
        .putFile(uri)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
          @Override public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            if (init != 1) {
              init++;
            } else {
              insertUserData(listener);
            }
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {
            listener.onFailed(e.getMessage());
            Timber.e("onFailure(): " + e.getMessage());
          }
        });

    String privateFolderRef = USER_STORAGE_REF + user.getUid() + "/" + PRIVATE_KEY;
    Uri uri1 = Uri.fromFile(privatekey);
    storageRef.child(privateFolderRef)
        .putFile(uri1)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
          @Override public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            if (init != 1) {
              init++;
            } else {
              insertUserData(listener);
            }
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {

          }
        });
  }

  private void insertUserData(final CommonAListener listener) {
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference1 = mDatabase.getReference("users");
    // Creating new user node, which returns the unique key value
    // new user node would be /users/$userid/

    // creating user object
    User userd = new User(user.getDisplayName(), user.getEmail());
    init = 0;
    // pushing user to 'users' node using the userId
    reference1.child(user.getUid())
        .setValue(userd)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override public void onSuccess(Void aVoid) {
            if (init != 1) {
              init++;
            } else {
              listener.onSuccess();
            }
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {
            listener.onFailed(e.getMessage());
            Timber.e("onFailure(): " + e.getMessage());
          }
        });
    // Insert to list uid
    DatabaseReference reference2 = mDatabase.getReference("listuid");
    Map<String, String> uidMap = new HashMap<>();
    uidMap.put(user.getUid(), user.getEmail());
    reference2.setValue(uidMap).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override public void onSuccess(Void aVoid) {
        if (init != 1) {
          init++;
        } else {
          getListUid();
          listener.onSuccess();
        }
      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override public void onFailure(@NonNull Exception e) {
        listener.onFailed(e.getMessage());
        Timber.e("onFailure(): " + e.getMessage());
      }
    });

    //Observable.merge()
  }

  @Override public void getUserPost() {
    // First get User Data
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
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

  @Override public List<ListUid> getListUid() {
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("listuid");
    mDatabase.addValueEventListener(new ValueEventListener() {
      @Override public void onDataChange(DataSnapshot dataSnapshot) {
        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
          Timber.d("onDataChange(): key => "
              + childDataSnapshot.getKey()); //displays the key for the node
          Timber.d("onDataChange(): value => "
              + childDataSnapshot.getValue());   //gives the value for given keyname
        }
        Timber.d("onDataChange(): finished");
      }

      @Override public void onCancelled(DatabaseError databaseError) {

      }
    });
    return null;
  }
}
