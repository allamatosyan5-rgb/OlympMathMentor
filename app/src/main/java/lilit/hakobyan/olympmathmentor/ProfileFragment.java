package lilit.hakobyan.olympmathmentor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private LinearLayout containerAchievements, containerCurrentGoals, containerCompletedGoals;
    private TextView tvFullName, tvEmail;
    private ImageView profileImage;
    private Button btnLogout; // Նախորդ քայլերից եկած LogOut կոճակը

    private FirebaseUser currentUser;
    private DatabaseReference userRef;

    private List<String> achievementsList = new ArrayList<>();
    private List<String> currentGoalsList = new ArrayList<>();
    private List<String> completedGoalsList = new ArrayList<>();

    // 1. ԳԱԼԵՐԵԱՅԻՑ ՆԿԱՐ ԸՆՏՐԵԼՈՒ ԳՈՐԾԻՔԸ
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        try {
                            requireContext().getContentResolver().takePersistableUriPermission(imageUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            profileImage.setImageURI(imageUri);
                            userRef.child("profileImageUrl").setValue(imageUri.toString());
                        } catch (Exception e) {
                            profileImage.setImageURI(imageUri);
                            userRef.child("profileImageUrl").setValue(imageUri.toString());
                        }
                    }
                }
            }
    );

    // 2. ՏԵՍԱԽՑԻԿՈՎ (CAMERA) ՆԿԱՐԵԼՈՒ ԳՈՐԾԻՔԸ
    private final ActivityResultLauncher<Void> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            bitmap -> {
                if (bitmap != null) {
                    // Եթե նկարել է, դնում ենք էկրանին
                    profileImage.setImageBitmap(bitmap);
                    // Պահպանում ենք հեռախոսի հիշողության մեջ և հղումը պահում Firebase-ում
                    Uri tempUri = saveBitmapToLocalCache(bitmap);
                    if (tempUri != null) {
                        userRef.child("profileImageUrl").setValue(tempUri.toString());
                    }
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        containerAchievements = view.findViewById(R.id.containerAchievements);
        containerCurrentGoals = view.findViewById(R.id.containerCurrentGoals);
        containerCompletedGoals = view.findViewById(R.id.containerCompletedGoals);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvEmail = view.findViewById(R.id.tvEmail);
        profileImage = view.findViewById(R.id.profileImage);
        btnLogout = view.findViewById(R.id.btnLogout);

        Button btnAddAchievement = view.findViewById(R.id.btnAddAchievement);
        Button btnAddGoal = view.findViewById(R.id.btnAddGoal);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            tvEmail.setText(currentUser.getEmail());
            userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            loadDataFromFirebase();
        }

        // ԵՐԲ ՍԵՂՄՈՒՄ ԵՆ ՆԿԱՐԻ ՎՐԱ, ԲԱՑՎՈՒՄ Է ԸՆՏՐՈՒԹՅԱՆ ՊԱՏՈՒՀԱՆԸ
        profileImage.setOnClickListener(v -> showImageOptionsDialog());

        tvFullName.setOnClickListener(v -> showEditNameDialog());
        btnAddAchievement.setOnClickListener(v -> showInputDialog("Add Achievement", true));
        btnAddGoal.setOnClickListener(v -> showInputDialog("Add Goal", false));

        // Log out ֆունկցիան
        if(btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if (getActivity() != null) getActivity().finish();
            });
        }

        return view;
    }

    // --- ՆԿԱՐԻ ԸՆՏՐՈՒԹՅԱՆ ՄԵՆՅՈՒ ---
    private void showImageOptionsDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Delete Photo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Profile Picture");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                // 1. Միացնել տեսախցիկը
                cameraLauncher.launch(null);
            } else if (which == 1) {
                // 2. Բացել գալերեան
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            } else if (which == 2) {
                // 3. Ջնջել նկարը
                profileImage.setImageResource(android.R.drawable.ic_menu_camera); // Դնում ենք դատարկ նկար
                if (userRef != null) {
                    userRef.child("profileImageUrl").removeValue(); // Ջնջում ենք բազայից
                }
                Toast.makeText(getContext(), "Photo removed", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    // --- ՖՈՒՆԿՑԻԱ ՏԵՍԱԽՑԻԿԻ ՆԿԱՐԸ ՀԵՌԱԽՈՍՈՒՄ ՊԱՀԵԼՈՒ ՀԱՄԱՐ ---
    private Uri saveBitmapToLocalCache(Bitmap bitmap) {
        try {
            File cachePath = new File(requireContext().getCacheDir(), "images");
            cachePath.mkdirs();
            File newFile = new File(cachePath, "profile_pic.png");
            FileOutputStream stream = new FileOutputStream(newFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            return Uri.fromFile(newFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadDataFromFirebase() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;

                if (snapshot.exists()) {
                    achievementsList.clear();
                    currentGoalsList.clear();
                    completedGoalsList.clear();
                    containerAchievements.removeAllViews();
                    containerCurrentGoals.removeAllViews();
                    containerCompletedGoals.removeAllViews();

                    String name = snapshot.child("name").getValue(String.class);
                    String surname = snapshot.child("surname").getValue(String.class);

                    if (name != null && surname != null && !name.isEmpty()) {
                        tvFullName.setText(name + " " + surname);
                    } else {
                        tvFullName.setText("Tap to set Name & Surname");
                    }

                    String imageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                    if (imageUrl != null) {
                        try {
                            profileImage.setImageURI(Uri.parse(imageUrl));
                        } catch (Exception e) {
                            profileImage.setImageResource(android.R.drawable.ic_menu_camera);
                        }
                    } else {
                        profileImage.setImageResource(android.R.drawable.ic_menu_camera);
                    }

                    for (DataSnapshot item : snapshot.child("achievements").getChildren()) {
                        String val = item.getValue(String.class);
                        if (val != null) { achievementsList.add(val); addAchievementToView(val); }
                    }
                    for (DataSnapshot item : snapshot.child("currentGoals").getChildren()) {
                        String val = item.getValue(String.class);
                        if (val != null) { currentGoalsList.add(val); addGoalToView(val, false); }
                    }
                    for (DataSnapshot item : snapshot.child("completedGoals").getChildren()) {
                        String val = item.getValue(String.class);
                        if (val != null) { completedGoalsList.add(val); addGoalToView(val, true); }
                    }
                } else {
                    tvFullName.setText("Tap to set Name & Surname");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showEditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Profile Name");
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);
        final EditText etName = new EditText(getContext());
        etName.setHint("First Name");
        layout.addView(etName);
        final EditText etSurname = new EditText(getContext());
        etSurname.setHint("Last Name");
        layout.addView(etSurname);
        builder.setView(layout);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String surname = etSurname.getText().toString().trim();
            if (!name.isEmpty() && !surname.isEmpty()) {
                tvFullName.setText(name + " " + surname);
                userRef.child("name").setValue(name);
                userRef.child("surname").setValue(surname);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showInputDialog(String title, boolean isAchievement) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);
        final EditText input = new EditText(getContext());
        builder.setView(input);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                if (isAchievement) {
                    achievementsList.add(text); addAchievementToView(text);
                    userRef.child("achievements").setValue(achievementsList);
                } else {
                    currentGoalsList.add(text); addGoalToView(text, false);
                    userRef.child("currentGoals").setValue(currentGoalsList);
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addAchievementToView(String text) {
        TextView newItem = new TextView(getContext());
        newItem.setText("🏆 " + text);
        newItem.setTextColor(getResources().getColor(R.color.deep_brown));
        newItem.setPadding(0, 15, 0, 15);
        newItem.setOnClickListener(v -> new AlertDialog.Builder(getContext())
                .setTitle("Delete Achievement?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    containerAchievements.removeView(newItem);
                    achievementsList.remove(text);
                    userRef.child("achievements").setValue(achievementsList);
                }).setNegativeButton("Cancel", null).show());
        containerAchievements.addView(newItem);
    }

    private void addGoalToView(String text, boolean isCompleted) {
        TextView goalItem = new TextView(getContext());
        goalItem.setText(isCompleted ? "✅ " + text : "📌 " + text);
        goalItem.setTextColor(getResources().getColor(isCompleted ? R.color.deep_brown : R.color.earth_brown));
        goalItem.setPadding(0, 15, 0, 15);
        goalItem.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Goal Options");
            String[] options = isCompleted ? new String[]{"Delete"} : new String[]{"Mark Completed", "Delete"};
            builder.setItems(options, (dialog, which) -> {
                if (isCompleted) {
                    containerCompletedGoals.removeView(goalItem);
                    completedGoalsList.remove(text);
                    userRef.child("completedGoals").setValue(completedGoalsList);
                } else {
                    if (which == 0) {
                        containerCurrentGoals.removeView(goalItem);
                        currentGoalsList.remove(text);
                        userRef.child("currentGoals").setValue(currentGoalsList);
                        completedGoalsList.add(text);
                        addGoalToView(text, true);
                        userRef.child("completedGoals").setValue(completedGoalsList);
                    } else if (which == 1) {
                        containerCurrentGoals.removeView(goalItem);
                        currentGoalsList.remove(text);
                        userRef.child("currentGoals").setValue(currentGoalsList);
                    }
                }
            });
            builder.show();
        });
        if (isCompleted) containerCompletedGoals.addView(goalItem);
        else containerCurrentGoals.addView(goalItem);
    }
}