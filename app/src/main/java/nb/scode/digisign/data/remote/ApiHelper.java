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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import durdinapps.rxfirebase2.RxFirebaseStorage;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import nb.scode.digisign.data.remote.BusModel.UserBusPost;
import nb.scode.digisign.data.remote.FireModel.KeyUser;
import nb.scode.digisign.data.remote.FireModel.Post;
import nb.scode.digisign.data.remote.FireModel.User;
import timber.log.Timber;

/**
 * Created by neobyte on 4/28/2017.
 */

@Singleton public class ApiHelper implements ApiTask {

  public static final String USER_STORAGE_REF = "/users/";
  private FirebaseAuth auth;
  private FirebaseUser user;
  private FirebaseDatabase database;
  private FirebaseStorage storage;
  private List<KeyUser> keyUserList = new ArrayList<>();
  private KeyUser keyUserOwner;

  /**
   * Instantiates a new Api helper.
   */
  @Inject ApiHelper() {
    auth = FirebaseAuth.getInstance();
    database = FirebaseDatabase.getInstance();
    storage = FirebaseStorage.getInstance();
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
              getFirebaseToken();
              user = userx;
              listener.onSuccess();
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
                getFirebaseToken();
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
    StorageReference refpub = storage.getReference().child(publicFolderRef);
    Maybe<FileDownloadTask.TaskSnapshot> obs1 = RxFirebaseStorage.getFile(refpub, publickey);

    String privateFolderRef = USER_STORAGE_REF + user.getUid() + "/" + PRIVATE_KEY;
    StorageReference refPriv = storage.getReference().child(privateFolderRef);
    Maybe<FileDownloadTask.TaskSnapshot> obs2 = RxFirebaseStorage.getFile(refPriv, privatekey);

    Maybe.concat(obs1, obs2).observeOn(AndroidSchedulers.mainThread())
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
    StorageReference refpub = storage.getReference().child(publicFolderRef);
    Maybe<Uri> obs1 = RxFirebaseStorage.getDownloadUrl(refpub);

    String privateFolderRef = USER_STORAGE_REF + user.getUid() + "/" + PRIVATE_KEY;
    StorageReference refPriv = storage.getReference().child(privateFolderRef);
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
    boolean b = (user != null);
    if (b) {
      getFirebaseToken();
    }
    return b;
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
    StorageReference storageRef = storage.getReference();
    StorageReference pubref = storageRef.child(publicFolderRef);
    Maybe<UploadTask.TaskSnapshot> obs1 = RxFirebaseStorage.putFile(pubref, uri);

    String privateFolderRef = USER_STORAGE_REF + user.getUid() + "/" + PRIVATE_KEY;
    Uri uri1 = Uri.fromFile(privatekey);
    StorageReference privref = storageRef.child(privateFolderRef);
    Maybe<UploadTask.TaskSnapshot> obs2 = RxFirebaseStorage.putFile(privref, uri1);

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
    User userd = new User(user.getDisplayName(), user.getEmail(), "empty");

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

  @Override public void initListUser(final CommonAListener listener) {
    listener.onProcess();
    DatabaseReference mDatabase = database.getReference("users");
    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override public void onDataChange(final DataSnapshot dataSnapshot) {
        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
          String key = childDataSnapshot.getKey();
          User userx = childDataSnapshot.getValue(User.class);
          if (key.equals(user.getUid())) {
            keyUserOwner = new KeyUser(key, userx);
            continue;
          }
          KeyUser keyUser = new KeyUser(key, userx);
          keyUserList.add(keyUser);
          Timber.d("onDataChange(): key => " + key); //displays the key for the node (uid)
          Timber.d("onDataChange(): value => "
              + userx.getEmail());   //gives the value for given keyname (User)
        }
        Timber.d("onDataChange(): finished");
        listener.onSuccess();
      }

      @Override public void onCancelled(DatabaseError databaseError) {
        listener.onFailed(databaseError.getMessage());
        Timber.e("onCancelled(): " + databaseError.getDetails());
      }
    });
  }

  @Override public KeyUser getOwnerKey() {
    return keyUserOwner;
  }

  @Override public List<KeyUser> getListUser() {
    return keyUserList;
  }

  @Override public void uploadSignFile(File signFile, final UploadSignListener listener) {
    String signFolderRef = USER_STORAGE_REF + user.getUid() + "/public/" + signFile.getName();
    Uri uri = Uri.fromFile(signFile);
    Timber.d("uploadSignFile(): folder => " + signFolderRef);
    storage.getReference().child(signFolderRef)
        .putFile(uri)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
          @Override public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            //noinspection VisibleForTests
            String uri = taskSnapshot.getDownloadUrl().toString();
            listener.onSuccess(uri);
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {
            listener.onFailed(e.getMessage());
            Timber.e("onFailure(): " + e.getMessage());
          }
        });
  }

  @Override public void insertPostData(Post postData, final CommonAListener listener) {
    DatabaseReference mDatabase = database.getReference("posts");
    String key = mDatabase.push().getKey();

    mDatabase.child(key).setValue(postData).addOnSuccessListener(new OnSuccessListener<Void>() {
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

  private void getFirebaseToken() {
    String token = FirebaseInstanceId.getInstance().getToken();
    if (token != null) {
      saveToken(token);
    }
  }

  @Override public void saveToken(String token) {
    Timber.d("saveToken(): => " + token);
    DatabaseReference reference =
        database.getReference("users").child(user.getUid()).child("token");
    reference.setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
      @Override public void onSuccess(Void aVoid) {
        Timber.d("onSuccess(): success set token");
      }
    }).addOnFailureListener(new OnFailureListener() {
      @Override public void onFailure(@NonNull Exception e) {
        Timber.e("onFailure(): failed to set token :(");
      }
    });
  }

  @Override public void downloadFile(File filezip, String url, final CommonAListener listener) {
    StorageReference reference = storage.getReferenceFromUrl(url);
    reference.getFile(filezip)
        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
          @Override public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
            listener.onSuccess();
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {
            listener.onFailed(e.getMessage());
            Timber.e("onFailure(): download " + e.getMessage());
          }
        });
  }

  @Override public void downloadPublicKey(File filepub, String senderkey,
      final CommonAListener listener) {
    listener.onProcess();
    String publicFolderRef = USER_STORAGE_REF + senderkey + "/public/" + PUBLIC_KEY;
    StorageReference reference = storage.getReference().child(publicFolderRef);
    reference.getFile(filepub)
        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
          @Override public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
            listener.onSuccess();
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override public void onFailure(@NonNull Exception e) {
            listener.onFailed(e.getMessage());
            Timber.e("onFailure(): " + e.toString());
          }
        });
  }

  @Override public void getPostReceived(final GetListPostListener listener) {
    listener.onProcess();
    if (keyUserOwner.getKey() != null) {
      Timber.d("getPostReceived(): started");
      final List<Post> PostList = new ArrayList<>();

      maybeReceivePost().flatMapObservable(
          new Function<List<Maybe<DataSnapshot>>, Observable<Maybe<DataSnapshot>>>() {
            @Override public Observable<Maybe<DataSnapshot>> apply(
                @io.reactivex.annotations.NonNull List<Maybe<DataSnapshot>> maybes)
                throws Exception {
              return Observable.fromIterable(maybes);
            }
          })
          .flatMap(new Function<Maybe<DataSnapshot>, Observable<DataSnapshot>>() {
            @Override public Observable<DataSnapshot> apply(
                @io.reactivex.annotations.NonNull Maybe<DataSnapshot> dataSnapshotMaybe)
                throws Exception {
              return dataSnapshotMaybe.toObservable();
            }
          })
          .flatMap(new Function<DataSnapshot, Observable<Post>>() {
            @Override public Observable<Post> apply(
                @io.reactivex.annotations.NonNull DataSnapshot dataSnapshot) throws Exception {
              return Observable.just(dataSnapshot.getValue(Post.class));
            }
          })
          .toSortedList(new Comparator<Post>() {
            @Override public int compare(Post post, Post t1) {
              return (int) (t1.getTimestamp() - post.getTimestamp());
            }
          })
          .flatMapObservable(new Function<List<Post>, Observable<Post>>() {
            @Override public Observable<Post> apply(
                @io.reactivex.annotations.NonNull List<Post> postList) throws Exception {
              return Observable.fromIterable(postList);
            }
          })
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Consumer<Post>() {
            @Override public void accept(@io.reactivex.annotations.NonNull Post post)
                throws Exception {
              PostList.add(post);
            }
          }, new Consumer<Throwable>() {
            @Override public void accept(@io.reactivex.annotations.NonNull Throwable throwable)
                throws Exception {
              listener.onFailed(throwable.getMessage());
            }
          }, new Action() {
            @Override public void run() throws Exception {
              listener.onSuccess(PostList);
            }
          });
    } else {
      listener.onFailed("Can't get user key");
    }
  }

  @Override public void getPostSent(final GetListPostListener listener) {
    listener.onProcess();
    if (keyUserOwner.getKey() != null) {
      Timber.d("getPostSent(): started");
      final List<Post> PostList = new ArrayList<>();
      maybeSentPost().flatMapObservable(
          new Function<List<Maybe<DataSnapshot>>, Observable<Maybe<DataSnapshot>>>() {
            @Override public Observable<Maybe<DataSnapshot>> apply(
                @io.reactivex.annotations.NonNull List<Maybe<DataSnapshot>> maybes)
                throws Exception {
              return Observable.fromIterable(maybes);
            }
          })
          .flatMap(new Function<Maybe<DataSnapshot>, Observable<DataSnapshot>>() {
            @Override public Observable<DataSnapshot> apply(
                @io.reactivex.annotations.NonNull Maybe<DataSnapshot> dataSnapshotMaybe)
                throws Exception {
              return dataSnapshotMaybe.toObservable();
            }
          })
          .flatMap(new Function<DataSnapshot, Observable<Post>>() {
            @Override public Observable<Post> apply(
                @io.reactivex.annotations.NonNull DataSnapshot dataSnapshot) throws Exception {
              return Observable.just(dataSnapshot.getValue(Post.class));
            }
          })
          .toSortedList(new Comparator<Post>() {
            @Override public int compare(Post post, Post t1) {
              return (int) (t1.getTimestamp() - post.getTimestamp());
            }
          })
          .flatMapObservable(new Function<List<Post>, Observable<Post>>() {
            @Override public Observable<Post> apply(
                @io.reactivex.annotations.NonNull List<Post> postList) throws Exception {
              return Observable.fromIterable(postList);
            }
          })
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new Consumer<Post>() {
            @Override public void accept(@io.reactivex.annotations.NonNull Post post)
                throws Exception {
              PostList.add(post);
            }
          }, new Consumer<Throwable>() {
            @Override public void accept(@io.reactivex.annotations.NonNull Throwable throwable)
                throws Exception {
              listener.onFailed(throwable.getMessage());
            }
          }, new Action() {
            @Override public void run() throws Exception {
              listener.onSuccess(PostList);
            }
          });
    } else {
      listener.onFailed("Can't get user key");
    }
  }

  @Override public void getAllPost(final GetListPostListener listener) {
    listener.onProcess();
    Timber.d("getAllPost(): started");
    final List<Post> PostList = new ArrayList<>();
    Maybe.concat(maybeReceivePost(), maybeSentPost())
        .flatMapIterable(new Function<List<Maybe<DataSnapshot>>, Iterable<Maybe<DataSnapshot>>>() {
          @Override public Iterable<Maybe<DataSnapshot>> apply(
              @io.reactivex.annotations.NonNull List<Maybe<DataSnapshot>> maybes) throws Exception {
            return maybes;
          }
        })
        .toObservable()
        .flatMap(new Function<Maybe<DataSnapshot>, Observable<DataSnapshot>>() {
          @Override public Observable<DataSnapshot> apply(
              @io.reactivex.annotations.NonNull Maybe<DataSnapshot> dataSnapshotMaybe)
              throws Exception {
            return dataSnapshotMaybe.toObservable();
          }
        })
        .flatMap(new Function<DataSnapshot, Observable<Post>>() {
          @Override public Observable<Post> apply(
              @io.reactivex.annotations.NonNull DataSnapshot dataSnapshot) throws Exception {
            return Observable.just(dataSnapshot.getValue(Post.class));
          }
        })
        .toSortedList(new Comparator<Post>() {
          @Override public int compare(Post post, Post t1) {
            return (int) (t1.getTimestamp() - post.getTimestamp());
          }
        })
        .flatMapObservable(new Function<List<Post>, Observable<Post>>() {
          @Override public Observable<Post> apply(
              @io.reactivex.annotations.NonNull List<Post> postList) throws Exception {
            return Observable.fromIterable(postList);
          }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Post>() {
          @Override public void accept(@io.reactivex.annotations.NonNull Post post)
              throws Exception {
            PostList.add(post);
          }
        }, new Consumer<Throwable>() {
          @Override public void accept(@io.reactivex.annotations.NonNull Throwable throwable)
              throws Exception {
            listener.onFailed(throwable.getMessage());
          }
        }, new Action() {
          @Override public void run() throws Exception {
            listener.onSuccess(PostList);
          }
        });
  }

  private Maybe<List<Maybe<DataSnapshot>>> maybeSentPost() {
    DatabaseReference reference = database.getReference("users").child(keyUserOwner.getKey());
    final DatabaseReference postRef = database.getReference("posts");
    //Get Post Key from sentpost
    return RxFirebaseDatabase.observeSingleValueEvent(reference.child("sentpost"),
        new Function<DataSnapshot, List<String>>() {
          @Override public List<String> apply(
              @io.reactivex.annotations.NonNull DataSnapshot dataSnapshot) throws Exception {
            List<String> stringlist = new ArrayList<String>();
            for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
              stringlist.add(datasnapshot.getKey());
              Timber.d("apply() function : " + datasnapshot.getKey());
            }
            return stringlist;
          }
        }).map(new Function<List<String>, List<Maybe<DataSnapshot>>>() {
      @Override public List<Maybe<DataSnapshot>> apply(
          @io.reactivex.annotations.NonNull List<String> list) throws Exception {
        List<Maybe<DataSnapshot>> maybeList = new ArrayList<Maybe<DataSnapshot>>();
        for (String key : list) {
          Maybe<DataSnapshot> dataSnapshotMaybe =
              RxFirebaseDatabase.observeSingleValueEvent(postRef.child(key));
          Timber.d("apply() map : " + key);
          maybeList.add(dataSnapshotMaybe);
        }
        return maybeList;
      }
    });
  }

  private Maybe<List<Maybe<DataSnapshot>>> maybeReceivePost() {
    DatabaseReference reference = database.getReference("users").child(keyUserOwner.getKey());
    final DatabaseReference postRef = database.getReference("posts");
    //Get Post Key from receivepost
    return RxFirebaseDatabase.observeSingleValueEvent(reference.child("receivepost"),
        new Function<DataSnapshot, List<String>>() {
          @Override public List<String> apply(
              @io.reactivex.annotations.NonNull DataSnapshot dataSnapshot) throws Exception {
            List<String> stringlist = new ArrayList<String>();
            for (DataSnapshot datasnapshot : dataSnapshot.getChildren()) {
              stringlist.add(datasnapshot.getKey());
              Timber.d("apply() function : " + datasnapshot.getKey());
            }
            return stringlist;
          }
        }).map(new Function<List<String>, List<Maybe<DataSnapshot>>>() {
      @Override public List<Maybe<DataSnapshot>> apply(
          @io.reactivex.annotations.NonNull List<String> list) throws Exception {
        List<Maybe<DataSnapshot>> maybeList = new ArrayList<Maybe<DataSnapshot>>();
        for (String key : list) {
          Maybe<DataSnapshot> dataSnapshotMaybe =
              RxFirebaseDatabase.observeSingleValueEvent(postRef.child(key));
          Timber.d("apply() map : " + key);
          maybeList.add(dataSnapshotMaybe);
        }
        return maybeList;
      }
    });
  }
}