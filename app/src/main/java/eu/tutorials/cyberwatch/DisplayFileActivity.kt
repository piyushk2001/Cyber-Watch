package eu.tutorials.cyberwatch

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageView

class DisplayFileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_file)

        val fileUri = intent.getStringExtra("fileUri")
        if (fileUri != null) {
            val webView: WebView = findViewById(R.id.webView)
            webView.settings.javaScriptEnabled = true

            if (fileUri.endsWith(".pdf")) {
                // Load PDF file
                webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$fileUri")
            } else  {
                // Display image file
                val html = """
                    <html>
                        <head>
                            <style>
                                html, body {
                                    margin: 0;
                                    padding: 0;
                                }
                                img {
                                    display: block;
                                    max-width: 100%;
                                    height: auto;
                                }
                            </style>
                        </head>
                        <body>
                            <img src="$fileUri" />
                        </body>
                    </html>
                """.trimIndent()
                webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
            }
        }
    }
}


