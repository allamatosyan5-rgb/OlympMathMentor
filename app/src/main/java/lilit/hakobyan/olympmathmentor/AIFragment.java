package lilit.hakobyan.olympmathmentor;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AIFragment extends Fragment {

    private EditText etChatInput;
    private ImageButton btnSendMessage;
    private LinearLayout chatMessagesContainer;
    private ScrollView chatScroll;

    private final OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai, container, false);

        etChatInput = view.findViewById(R.id.et_chat_input);
        btnSendMessage = view.findViewById(R.id.btn_send_message);
        chatMessagesContainer = view.findViewById(R.id.chat_messages_container);
        chatScroll = view.findViewById(R.id.chat_scroll);

        btnSendMessage.setOnClickListener(v -> {
            String message = etChatInput.getText().toString().trim();
            if (!message.isEmpty()) {
                addUserMessage(message);
                etChatInput.setText("");
                callGeminiAPI(message);
            }
        });

        return view;
    }

    private void callGeminiAPI(String userQuestion) {

        String apiKey = "AIzaSyA2M8xzvy4YD0-6_xkppIiXndMp2jefbVU";
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;

        String promptContext = "You are a helpful AI assistant for an Olympiad Math app called OlympMath Mentor. Keep your answers short, friendly, and to the point. The user asks: " + userQuestion;

        String jsonBody = "{\"contents\": [{\"parts\": [{\"text\": \"" + promptContext + "\"}]}]}";
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> addAIMessage("Internet Blocked: " + e.getMessage()));
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() == null) return;
                String responseData = response.body().string();

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(responseData);
                                JSONArray candidates = jsonObject.getJSONArray("candidates");
                                JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                                JSONArray parts = content.getJSONArray("parts");
                                String aiReply = parts.getJSONObject(0).getString("text");

                                addAIMessage(aiReply);

                            } catch (Exception e) {
                                addAIMessage("Parsing Error. Google sent this instead: " + responseData);
                            }
                        } else {
                            addAIMessage("Google API Error (Code " + response.code() + "): " + responseData);
                        }
                    });
                }
            }
        });
    }

    private void addUserMessage(String text) {
        if (getContext() == null) return;
        TextView userText = new TextView(getContext());
        userText.setText(text);

        userText.setBackgroundColor(Color.parseColor("#3E2723"));
        userText.setTextColor(Color.WHITE);
        userText.setPadding(32, 24, 32, 24);
        userText.setTextSize(16f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.END;
        params.setMargins(100, 16, 0, 16);
        userText.setLayoutParams(params);

        chatMessagesContainer.addView(userText);
        scrollToBottom();
    }

    private void addAIMessage(String text) {
        if (getContext() == null) return;
        TextView aiText = new TextView(getContext());
        aiText.setText(text);

        aiText.setBackgroundColor(Color.parseColor("#E0D5C1"));
        aiText.setTextColor(Color.parseColor("#212121"));
        aiText.setPadding(32, 24, 32, 24);
        aiText.setTextSize(16f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.START;
        params.setMargins(0, 8, 100, 16);
        aiText.setLayoutParams(params);

        chatMessagesContainer.addView(aiText);
        scrollToBottom();
    }

    private void scrollToBottom() {
        chatScroll.post(() -> chatScroll.fullScroll(ScrollView.FOCUS_DOWN));
    }
}