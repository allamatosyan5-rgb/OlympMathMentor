package lilit.hakobyan.olympmathmentor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ClassChatActivity extends AppCompatActivity {

    private String classId, className, classCode;
    private String currentUserId, currentUserName, currentUserRole;

    private RecyclerView rvClassChat;
    private EditText etChatInput;
    private ImageButton btnAttachImage, btnSendHomework;
    private FloatingActionButton btnSendChatMessage;

    private DatabaseReference chatRef;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_chat);

        classId = getIntent().getStringExtra("CLASS_ID");
        className = getIntent().getStringExtra("CLASS_NAME");
        classCode = getIntent().getStringExtra("CLASS_CODE");

        TextView tvClassName = findViewById(R.id.tvChatClassName);
        TextView tvClassCode = findViewById(R.id.tvChatClassCode);
        tvClassName.setText(className);
        tvClassCode.setText("Code: " + classCode);

        findViewById(R.id.btnBackFromChat).setOnClickListener(v -> finish());

        rvClassChat = findViewById(R.id.rvClassChat);
        etChatInput = findViewById(R.id.etChatInput);
        btnAttachImage = findViewById(R.id.btnAttachImage);
        btnSendHomework = findViewById(R.id.btnSendHomework);
        btnSendChatMessage = findViewById(R.id.btnSendChatMessage);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        rvClassChat.setLayoutManager(new LinearLayoutManager(this));
        rvClassChat.setAdapter(chatAdapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("class_chats").child(classId);

        checkUserRole();
        loadMessages();

        btnSendChatMessage.setOnClickListener(v -> {
            String text = etChatInput.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text, null, false);
                etChatInput.setText("");
            }
        });

        // Նկար ուղարկել
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                String base64Image = encodeImage(uri);
                if (base64Image != null) sendMessage("[Image]", base64Image, false);
            }
        });
        btnAttachImage.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        // Տնային ուղարկել (Միայն ուսուցիչների համար)
        btnSendHomework.setOnClickListener(v -> showHomeworkDialog());
    }

    private void checkUserRole() {
        DatabaseReference userRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("Users").child(currentUserId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserName = snapshot.child("name").getValue(String.class);
                    currentUserRole = snapshot.child("role").getValue(String.class);

                    // Եթե ուսուցիչ է, ցույց ենք տալիս տնային տալու կոճակը
                    if ("teacher".equals(currentUserRole)) {
                        btnSendHomework.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void sendMessage(String text, String base64Image, boolean isHomework) {
        String messageId = chatRef.push().getKey();
        ChatMessage message = new ChatMessage(messageId, currentUserId, currentUserName, text, base64Image, isHomework, System.currentTimeMillis());
        if (messageId != null) {
            chatRef.child(messageId).setValue(message);
        }
    }

    private void showHomeworkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Assign New Homework");

        final EditText input = new EditText(this);
        input.setHint("e.g. Solve IMO 2022 Q1 by Friday");
        input.setPadding(40, 40, 40, 40);
        builder.setView(input);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String hwText = input.getText().toString().trim();
            if (!hwText.isEmpty()) {
                sendMessage(hwText, null, true); // isHomework = true
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void loadMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatMessage msg = dataSnapshot.getValue(ChatMessage.class);
                    if (msg != null) messageList.add(msg);
                }
                chatAdapter.notifyDataSetChanged();
                if (!messageList.isEmpty()) {
                    rvClassChat.scrollToPosition(messageList.size() - 1);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private String encodeImage(Uri imageUri) {
        try {
            java.io.InputStream imageStream = getContentResolver().openInputStream(imageUri);
            Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Խիստ սեղմում ենք որ չկախի
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }

    // ==========================================
    // CHAT MESSAGE MODEL
    // ==========================================
    public static class ChatMessage {
        public String messageId, senderId, senderName, text, imageBase64;
        public boolean isHomework;
        public long timestamp;

        public ChatMessage() {} // Firebase-ի համար

        public ChatMessage(String id, String sId, String sName, String txt, String img, boolean isHw, long time) {
            this.messageId = id; this.senderId = sId; this.senderName = sName;
            this.text = txt; this.imageBase64 = img; this.isHomework = isHw; this.timestamp = time;
        }
    }

    // ==========================================
    // CHAT ADAPTER
    // ==========================================
    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private List<ChatMessage> messages;

        public ChatAdapter(List<ChatMessage> messages) { this.messages = messages; }

        @NonNull @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatMessage msg = messages.get(position);

            boolean isMe = msg.senderId.equals(currentUserId);

            // Անունը գրում ենք միայն եթե ուրիշն է գրել
            if (isMe) {
                holder.tvMessageSender.setVisibility(View.GONE);
                holder.layoutMessageRoot.setGravity(Gravity.END);
                holder.layoutMessageBubble.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EFEBE9")));
            } else {
                holder.tvMessageSender.setVisibility(View.VISIBLE);
                holder.tvMessageSender.setText(msg.senderName);
                holder.layoutMessageRoot.setGravity(Gravity.START);
                holder.layoutMessageBubble.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            }

            // Եթե Տնային է
            if (msg.isHomework) {
                holder.layoutHomeworkNotice.setVisibility(View.VISIBLE);
                holder.layoutMessageBubble.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFCDD2"))); // Կարմրավուն ֆոն
                holder.tvMessageText.setTypeface(null, android.graphics.Typeface.BOLD);

                // Սեղմելիս...
                holder.layoutMessageBubble.setOnClickListener(v -> {
                    if ("teacher".equals(currentUserRole)) {
                        Toast.makeText(ClassChatActivity.this, "Opening Matyan (Journal)...", Toast.LENGTH_SHORT).show();
                        // Intent intent = new Intent(ClassChatActivity.this, MatyanActivity.class);
                        // startActivity(intent);
                    } else {
                        Toast.makeText(ClassChatActivity.this, "Opening Homework Submission...", Toast.LENGTH_SHORT).show();
                        // Intent intent = new Intent(ClassChatActivity.this, SubmitHomeworkActivity.class);
                        // startActivity(intent);
                    }
                });
            } else {
                holder.layoutHomeworkNotice.setVisibility(View.GONE);
                holder.tvMessageText.setTypeface(null, android.graphics.Typeface.NORMAL);
                holder.layoutMessageBubble.setOnClickListener(null);
            }

            // Նկար կա թե չէ
            if (msg.imageBase64 != null && !msg.imageBase64.isEmpty()) {
                holder.ivMessageImage.setVisibility(View.VISIBLE);
                byte[] decodedString = Base64.decode(msg.imageBase64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.ivMessageImage.setImageBitmap(decodedByte);
                holder.tvMessageText.setVisibility(View.GONE);
            } else {
                holder.ivMessageImage.setVisibility(View.GONE);
                holder.tvMessageText.setVisibility(View.VISIBLE);
                holder.tvMessageText.setText(msg.text);
            }
        }

        @Override public int getItemCount() { return messages.size(); }

        class ChatViewHolder extends RecyclerView.ViewHolder {
            LinearLayout layoutMessageRoot, layoutMessageBubble, layoutHomeworkNotice;
            TextView tvMessageSender, tvMessageText;
            ImageView ivMessageImage;

            public ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                layoutMessageRoot = itemView.findViewById(R.id.layoutMessageRoot);
                layoutMessageBubble = itemView.findViewById(R.id.layoutMessageBubble);
                layoutHomeworkNotice = itemView.findViewById(R.id.layoutHomeworkNotice);
                tvMessageSender = itemView.findViewById(R.id.tvMessageSender);
                tvMessageText = itemView.findViewById(R.id.tvMessageText);
                ivMessageImage = itemView.findViewById(R.id.ivMessageImage);
            }
        }
    }
}