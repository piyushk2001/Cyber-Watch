package eu.tutorials.cyberwatch

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.util.Locale

class ChatbotActivity : AppCompatActivity() {

    private lateinit var interpreter: Interpreter
    private lateinit var words: List<String>
    private lateinit var classes: List<String>
    private lateinit var intents: List<ChatbotIntent>

    private lateinit var inputMessage: EditText
    private lateinit var sendButton: Button
    private lateinit var chatContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        inputMessage = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.sendButton)
        chatContainer = findViewById(R.id.chatContainer)

        interpreter = Interpreter(loadModelFile())
        words = loadWordsFile()
        classes = loadClassesFile()
        intents = loadIntentsFile()

        sendButton.setOnClickListener {
            val message = inputMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                inputMessage.text.clear()

                val response = getChatbotResponse(message)
                sendMessage(response)
            }
        }
    }

    private fun loadModelFile(): ByteBuffer {
        return FileUtil.loadMappedFile(this, "chatbot_model.tflite")
    }

    private fun loadWordsFile(): List<String> {
        val reader = BufferedReader(InputStreamReader(assets.open("words.pkl")))
        val wordsJson = reader.readText()
        return Gson().fromJson(wordsJson, object : TypeToken<List<String>>() {}.type)
    }

    private fun loadClassesFile(): List<String> {
        val reader = BufferedReader(InputStreamReader(assets.open("classes.pkl")))
        val classesJson = reader.readText()
        return Gson().fromJson(classesJson, object : TypeToken<List<String>>() {}.type)
    }

    private fun loadIntentsFile(): List<ChatbotIntent> {
        val reader = BufferedReader(InputStreamReader(assets.open("chatbot_dataset.json")))
        val intentsJson = reader.readText()
        return Gson().fromJson(intentsJson, object : TypeToken<List<ChatbotIntent>>() {}.type)
    }

    private fun sendMessage(message: String) {
        val messageView = TextView(this)
        messageView.text = message

        chatContainer.addView(messageView)
    }

    private fun getChatbotResponse(message: String): String {
        val cleanedMessage = cleanUpSentence(message)
        val bag = getBagOfWords(cleanedMessage)
        val result = FloatArray(classes.size)
        interpreter.run(bag, result)
        val maxIndex = result.indices.maxByOrNull { result[it] } ?: -1
        return classes[maxIndex]
    }

    private fun cleanUpSentence(sentence: String): List<String> {
        return sentence.lowercase(Locale.ROOT).split(" ")
    }

    private fun getBagOfWords(sentence: List<String>): FloatArray {
        val bag = FloatArray(words.size)
        for (word in sentence) {
            val index = words.indexOf(word)
            if (index != -1) {
                bag[index] = 1f
            }
        }
        return bag
    }

    data class ChatbotIntent(val question: String, val response: String)
}
