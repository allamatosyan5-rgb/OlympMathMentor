package lilit.hakobyan.olympmathmentor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AIFragment extends Fragment {

    private RecyclerView rvChatMessages;
    private EditText etChatMessage;
    private ImageButton btnSendChat;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai, container, false);

        rvChatMessages = view.findViewById(R.id.rvChatMessages);
        etChatMessage = view.findViewById(R.id.etChatMessage);
        btnSendChat = view.findViewById(R.id.btnSendChat);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        rvChatMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChatMessages.setAdapter(chatAdapter);

        loadChatHistory();

        if (messageList.isEmpty()) {
            addMessage("Hello! I am your OlympMath Mentor AI. Ready to solve some challenging math problems today?", false);
        }

        btnSendChat.setOnClickListener(v -> {
            String userText = etChatMessage.getText().toString().trim();
            if (!userText.isEmpty()) {
                addMessage(userText, true);
                etChatMessage.setText("");
                generateAiResponse();
            }
        });

        androidx.drawerlayout.widget.DrawerLayout drawerLayout = view.findViewById(R.id.drawerLayout);
        ImageButton btnOpenMenu = view.findViewById(R.id.btnOpenMenu);
        LinearLayout btnNewChat = view.findViewById(R.id.btnNewChat);

        if (btnOpenMenu != null && drawerLayout != null) {
            btnOpenMenu.setOnClickListener(v -> {
                drawerLayout.openDrawer(androidx.core.view.GravityCompat.START);
            });
        }

        if (btnNewChat != null && drawerLayout != null) {
            btnNewChat.setOnClickListener(v -> {
                messageList.clear();
                chatAdapter.notifyDataSetChanged();
                addMessage("Hello! I am your OlympMath Mentor AI. Ready to solve some challenging math problems today?", false);
                drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
            });
        }

        return view;
    }

    private void addMessage(String text, boolean isUser) {
        messageList.add(new ChatMessage(text, isUser));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.scrollToPosition(messageList.size() - 1);

        saveChatHistory();

        if (isUser && messageList.size() == 2) {
            addSidebarHistoryItem(text);
        }
    }

    private void saveChatHistory() {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences("AiChatPrefs", Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();
        try {
            for (ChatMessage msg : messageList) {
                JSONObject obj = new JSONObject();
                obj.put("text", msg.text);
                obj.put("isUser", msg.isUser);
                jsonArray.put(obj);
            }
            prefs.edit().putString("chat_history", jsonArray.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadChatHistory() {
        if (getContext() == null) return;
        SharedPreferences prefs = getContext().getSharedPreferences("AiChatPrefs", Context.MODE_PRIVATE);
        String historyData = prefs.getString("chat_history", null);

        if (historyData != null) {
            try {
                JSONArray jsonArray = new JSONArray(historyData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    messageList.add(new ChatMessage(obj.getString("text"), obj.getBoolean("isUser")));
                }
                chatAdapter.notifyDataSetChanged();
                rvChatMessages.scrollToPosition(messageList.size() - 1);

                if (messageList.size() > 1 && messageList.get(1).isUser) {
                    addSidebarHistoryItem(messageList.get(1).text);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void generateAiResponse() {
        String apiKey = BuildConfig.GEMINI_API_KEY;

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        JSONObject jsonBody = new JSONObject();
        try {
            JSONArray contents = new JSONArray();
            JSONObject part = new JSONObject();
            JSONObject textObj = new JSONObject();

            StringBuilder conversationMemory = new StringBuilder();

            // 👇 ԱՅՍՏԵՂ Է ՔՈ ՈՒԶԱԾ ՆՈՐ, ԽԵԼԱՑԻ ՈՒՍՈՒՑՉԻ ՀՐԱՀԱՆԳԸ 👇
            conversationMemory.append("You are an expert Olympiad Mathematics Teacher and Mentor for the app 'OlympMath Mentor'. Your goal is to guide students through complex math problems. Use simple, encouraging language. You know many advanced theorems. When a student asks a question or shares a solution, assess their understanding. Do NOT give the direct solution immediately. Instead, provide helpful hints and guide them to find the answer themselves. Once they solve it or if they are completely stuck, provide a short, meaningful, and step-by-step explanation of the solution. Always respond in English.\n\n");

            int startIndex = Math.max(0, messageList.size() - 10);
            for (int i = startIndex; i < messageList.size(); i++) {
                ChatMessage msg = messageList.get(i);
                String sender = msg.isUser ? "Student: " : "Mentor: ";
                conversationMemory.append(sender).append(msg.text).append("\n");
            }
            conversationMemory.append("Mentor: ");

            textObj.put("text", conversationMemory.toString());
            JSONArray partsArray = new JSONArray();
            partsArray.put(textObj);
            part.put("parts", partsArray);
            contents.put(part);

            jsonBody.put("contents", contents);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder().url(url).post(body).build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> addMessage("Network Error: Could not connect to AI.", false));
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray candidates = jsonResponse.getJSONArray("candidates");
                        JSONObject firstCandidate = candidates.getJSONObject(0);
                        JSONObject content = firstCandidate.getJSONObject("content");
                        JSONArray parts = content.getJSONArray("parts");
                        String aiText = parts.getJSONObject(0).getString("text");

                        final String cleanText = aiText.replace("**", "").trim();

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> addMessage(cleanText, false));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> addMessage("Error reading AI response.", false));
                        }
                    }
                } else {
                    int statusCode = response.code();
                    String customErrorMessage;

                    if (statusCode == 503) {
                        customErrorMessage = "The Math Servers are currently busy. Please wait a moment.";
                    } else if (statusCode == 429) {
                        customErrorMessage = "I have reached my problem-solving limit for now. Try again later!";
                    } else {
                        customErrorMessage = "System Error (" + statusCode + ").";
                    }

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> addMessage(customErrorMessage, false));
                    }
                }
            }
        });
    }

    private void addSidebarHistoryItem(String chatTitle) {
        if (getView() == null || getContext() == null) return;

        LinearLayout layoutSidebarHistory = getView().findViewById(R.id.layoutSidebarHistory);
        if (layoutSidebarHistory == null) return;

        layoutSidebarHistory.removeAllViews();

        TextView historyItem = new TextView(getContext());

        if (chatTitle.length() > 25) {
            chatTitle = chatTitle.substring(0, 25) + "...";
        }

        historyItem.setText(" 📝 " + chatTitle);
        historyItem.setTextColor(Color.parseColor("#E0E0E0"));
        historyItem.setTextSize(14f);
        historyItem.setPadding(24, 24, 24, 24);

        historyItem.setBackgroundResource(android.R.attr.selectableItemBackground);
        historyItem.setClickable(true);
        historyItem.setFocusable(true);

        layoutSidebarHistory.addView(historyItem, 0);
    }

    private static class ChatMessage {
        String text;
        boolean isUser;

        ChatMessage(String text, boolean isUser) {
            this.text = text;
            this.isUser = isUser;
        }
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
        private List<ChatMessage> messages;

        ChatAdapter(List<ChatMessage> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_bubble, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            ChatMessage message = messages.get(position);
            holder.tvChatMessage.setText(message.text);

            GradientDrawable shape = new GradientDrawable();
            shape.setCornerRadius(24f);

            if (message.isUser) {
                holder.layoutMessageContainer.setGravity(Gravity.END);
                shape.setColor(Color.parseColor("#5D4037"));
                holder.tvChatMessage.setBackground(shape);
                holder.tvChatMessage.setTextColor(Color.WHITE);
            } else {
                holder.layoutMessageContainer.setGravity(Gravity.START);
                shape.setColor(Color.parseColor("#E0D5C1"));
                holder.tvChatMessage.setBackground(shape);
                holder.tvChatMessage.setTextColor(Color.parseColor("#212121"));
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        class ChatViewHolder extends RecyclerView.ViewHolder {
            LinearLayout layoutMessageContainer;
            TextView tvChatMessage;

            ChatViewHolder(@NonNull View itemView) {
                super(itemView);
                layoutMessageContainer = itemView.findViewById(R.id.layoutMessageContainer);
                tvChatMessage = itemView.findViewById(R.id.tvChatMessage);
            }
        }
    }
}