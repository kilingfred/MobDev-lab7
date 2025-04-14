package com.example.lab7;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab7.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    try {
                        SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                        String idToken = credential.getGoogleIdToken();
                        if (idToken != null) {
                            firebaseAuthWithGoogle(idToken);
                        } else {
                            Log.e(TAG, "No ID token!");
                            updateUI(null);
                        }
                    } catch (ApiException e) {
                        Log.e(TAG, "Sign-in failed", e);
                        updateUI(null);
                    }
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        oneTapClient = Identity.getSignInClient(this);

        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id))  // add this to strings.xml
                                .setFilterByAuthorizedAccounts(false)
                                .build()
                )
                .build();

        binding.signInButton.setOnClickListener(v -> signIn());
        binding.signOutButton.setOnClickListener(v -> signOut());
    }

    private void signIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender())
                                .build();
                        signInLauncher.launch(intentSenderRequest);
                    } catch (Exception e) {
                        Log.e(TAG, "Could not launch sign-in intent", e);
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Sign-in failed", e);
                });
    }


    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                        // 🔄 Switch to the other activity
                        startActivity(new Intent(this, CurrencyExchanger.class)); // replace with your class
                        finish();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void updateUI(@Nullable FirebaseUser user) {
        if (user != null) {
            binding.status.setText("Signed in as: " + user.getEmail());
            binding.detail.setText("UID: " + user.getUid());
            binding.signInButton.setVisibility(View.GONE);
            binding.signOutButton.setVisibility(View.VISIBLE);
        } else {
            binding.status.setText("Signed out");
            binding.detail.setText("");
            binding.signInButton.setVisibility(View.VISIBLE);
            binding.signOutButton.setVisibility(View.GONE);
        }
    }
}
