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
import androidx.fragment.app.Fragment;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private String classId;
    private String currentUserId, currentUserName;

    private RecyclerView rvChat;
    private EditText etInput;
    private FloatingActionButton btnSend;
    private ImageButton btnImage;

    private DatabaseReference chatRef;
    private ChatAdapter adapter;
    private List<ChatMessage> messageList;

    private ActivityResultLauncher<String> galleryLauncher;

    public static ChatFragment newInstance(String classId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString("CLASS_ID", classId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            classId = getArguments().getString("CLASS_ID");
        }

        // Նկար կցելու ֆունկցիան
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                String base64 = encodeImage(uri);
                if (base64 != null) sendMessage("[Image]", base64);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        rvChat = view.findViewById(R.id.rvChatMessages);
        etInput = view.findViewById(R.id.etChatInput);
        btnSend = view.findViewById(R.id.btnSendChatMessage);
        btnImage = view.findViewById(R.id.btnAttachImage);

        messageList = new ArrayList<>();
        adapter = new ChatAdapter(messageList);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(adapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "test_user";
        chatRef = FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("class_chats").child(classId);

        fetchUserName();
        loadMessages();

        btnSend.setOnClickListener(v -> {
            String text = etInput.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text, null);
                etInput.setText("");
            }
        });

        btnImage.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        return view;
    }

    private void fetchUserName() {
        FirebaseDatabase.getInstance("https://olympmath-mentor-default-rtdb.firebaseio.com/").getReference("Users").child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            currentUserName = snapshot.child("name").getValue(String.class);
                        } else {
                            currentUserName = "Unknown";
                        }
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void sendMessage(String text, String imageBase64) {
        String msgId = chatRef.push().getKey();
        ChatMessage msg = new ChatMessage(msgId, currentUserId, currentUserName, text, imageBase64, System.currentTimeMillis());
        if (msgId != null) {
            chatRef.child(msgId).setValue(msg);
        }
    }

    private void loadMessages() {
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    ChatMessage msg = s.getValue(ChatMessage.class);
                    if (msg != null) messageList.add(msg);
                }
                adapter.notifyDataSetChanged();
                if (!messageList.isEmpty()) {
                    rvChat.scrollToPosition(messageList.size() - 1);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private String encodeImage(Uri uri) {
        try {
            InputStream is = getContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos); // Սեղմում ենք հիշողության համար
            return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Image error", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // ================= MODEL =================
    public static class ChatMessage {
        public String messageId, senderId, senderName, text, imageBase64;
        public long timestamp;

        public ChatMessage() {}

        public ChatMessage(String id, String sId, String sName, String txt, String img, long time) {
            this.messageId = id; this.senderId = sId; this.senderName = sName;
            this.text = txt; this.imageBase64 = img; this.timestamp = time;
        }
    }

    // ================= ADAPTER =================
    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private List<ChatMessage> list;
        public ChatAdapter(List<ChatMessage> list) { this.list = list; }

        @NonNull @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
            return new ChatViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatMessage msg = list.get(position);
            boolean isMe = msg.senderId.equals(currentUserId);

            if (isMe) {
                holder.tvSender.setVisibility(View.GONE);
                holder.rootLayout.setGravity(Gravity.END);
                holder.bubbleLayout.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#EFEBE9")));
            } else {
                holder.tvSender.setVisibility(View.VISIBLE);
                holder.tvSender.setText(msg.senderName);
                holder.rootLayout.setGravity(Gravity.START);
                holder.bubbleLayout.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            }

            if (msg.imageBase64 != null && !msg.imageBase64.isEmpty()) {
                holder.ivImage.setVisibility(View.VISIBLE);
                holder.tvText.setVisibility(View.GONE);
                try {
                    byte[] bytes = Base64.decode(msg.imageBase64, Base64.DEFAULT);
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.ivImage.setImageBitmap(bmp);
                } catch (Exception e) {}
            } else {
                holder.ivImage.setVisibility(View.GONE);
                holder.tvText.setVisibility(View.VISIBLE);
                holder.tvText.setText(msg.text);
            }
        }

        @Override public int getItemCount() { return list.size(); }

        class ChatViewHolder extends RecyclerView.ViewHolder {
            LinearLayout rootLayout, bubbleLayout;
            TextView tvSender, tvText;
            ImageView ivImage;
            public ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                rootLayout = itemView.findViewById(R.id.layoutMessageRoot);
                bubbleLayout = itemView.findViewById(R.id.layoutMessageBubble);
                tvSender = itemView.findViewById(R.id.tvMessageSender);
                tvText = itemView.findViewById(R.id.tvMessageText);
                ivImage = itemView.findViewById(R.id.ivMessageImage);
            }
        }
    }
}