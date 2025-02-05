package com.example.mentalhealthcompanion.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mentalhealthcompanion.MainActivity
import com.example.mentalhealthcompanion.db.DailyCheckIn
import com.example.mentalhealthcompanion.db.DailyCheckInDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JournalViewModel(private val dao: DailyCheckInDao) : ViewModel() {
    @SuppressLint("SimpleDateFormat")
    fun saveCheckIn(feeling : String, sentiment : String){
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val checkIn = DailyCheckIn(date = date, feeling = feeling, sentiment = sentiment)

        viewModelScope.launch {
            try {
                dao.insertCheckIn(checkIn)
            } catch (e: Exception) {
                Log.e("JournalViewModel", "Error inserting check-in", e)
            }
        }
    }

    fun getAllCheckIns() : List<DailyCheckIn>{
        return runBlocking {
            dao.getAllCheckIns()
        }
    }

    fun getRecommendation(sentiment: String): String {
        val recommendations = mapOf(
            "admiration" to listOf(
                "Take a moment to truly appreciate the qualities you admire. Maybe even write them down—it helps solidify them in your mind.",
                "Reach out to the person or source of your admiration and express how much they inspire you. Genuine words of appreciation can brighten someone's day!",
                "Channel your admiration into action. Whether it's a new hobby, career path, or self-improvement, let your inspiration fuel your journey.",
                "Surround yourself with more of what you admire—books, music, art, or people. The more you're exposed to greatness, the more it shapes you.",
                "Reflect on why this admiration resonates with you so deeply. Sometimes, it reveals a hidden part of yourself that is longing to grow."
            ),
            "amusement" to listOf(
                "Laughter is contagious—share the joy! Send a funny meme or joke to a friend and keep the good vibes going.",
                "Let yourself fully enjoy the moment. Be present, laugh without holding back, and embrace the silliness of life.",
                "Find more of what amuses you! Whether it's stand-up comedy, funny videos, or a hilarious book, make time to indulge in lighthearted fun.",
                "Turn your amusement into creativity. Write down or act out a funny moment you experienced today—you might even make someone else's day better!",
                "Remind yourself how good it feels to laugh. Life can be serious, but moments like these remind you that joy is always within reach."
            ),
            "anger" to listOf(
                "Pause and take a deep breath. Count to ten if you need to. Let the intensity pass before you react—you deserve peace.",
                "Write down exactly what’s frustrating you. Getting your thoughts out on paper can help process your emotions before speaking or acting on them.",
                "Go for a walk or do something physical to release the tension. Movement helps transform anger into clarity and calm.",
                "Express how you feel, but in a way that promotes understanding. Talk it out with someone you trust or even write an imaginary letter to let it all out.",
                "Once the storm settles, ask yourself—what is this anger trying to teach me? Sometimes, strong emotions point to deeper needs that deserve attention."
            ),
            "annoyance" to listOf(
                "Take a deep breath and ask yourself—will this matter a year from now? If not, maybe it's best to let it go.",
                "Step away from whatever is irritating you, even if it’s just for a few minutes. Distance can bring clarity and ease the frustration.",
                "Find something that brings you joy. A favorite song, a quick stretch, or even a small treat can shift your focus away from what's annoying you.",
                "Try to laugh about it. Sometimes, the little things that bug us turn into funny stories later.",
                "If someone is annoying you, try seeing things from their perspective. Maybe they’re having a bad day too—it helps to show a little grace."
            ),
            "approval" to listOf(
                "Take a moment to soak in this feeling—what you’ve done is worth celebrating. You worked hard, and it shows!",
                "Share your approval with someone else. A kind word of encouragement to others can create a ripple effect of positivity.",
                "Reflect on what led to this moment. What choices did you make that got you here? Recognizing them helps you repeat success in the future.",
                "Give yourself permission to feel proud! Confidence grows when you acknowledge your own efforts.",
                "Use this as motivation to keep going. Every step forward matters, and this is proof that you're on the right path."
            ),
            "caring" to listOf(
                "Someone out there appreciates your kindness more than you realize. Keep spreading love—it truly makes a difference.",
                "Show yourself the same care you give to others. You deserve love and kindness just as much as anyone else.",
                "Sometimes, the smallest gestures mean the most. A simple text, a warm hug, or even a smile can brighten someone’s day.",
                "Take time to nurture the relationships that matter to you. Connection is one of the most beautiful parts of life.",
                "Remember, caring doesn’t mean carrying everything alone. It’s okay to set boundaries and take care of yourself too."
            ),
            "confusion" to listOf(
                "It’s okay not to have all the answers. Give yourself permission to sit with the uncertainty without pressure.",
                "Break it down into smaller pieces. Sometimes, tackling one small part at a time can bring clarity.",
                "Talk it through with someone. A fresh perspective might help you see things in a new light.",
                "Trust that understanding will come. Some things make sense in hindsight, so be patient with yourself.",
                "Confusion often means you're growing. It’s the space between what you knew and what you’re about to learn."
            ),
            "curiosity" to listOf(
                "Follow that spark! Whether it's a new topic, hobby, or idea—exploring it could lead to something amazing.",
                "Ask questions, even the weird ones. Curiosity thrives when you allow yourself to wonder freely.",
                "Pick up a book, watch a documentary, or talk to someone who knows more. Every new insight expands your world.",
                "Let yourself experiment without worrying about ‘getting it right.’ Discovery is about the journey, not the destination.",
                "Curiosity is a gift. It keeps life interesting, so never stop exploring what excites you!"
            ),
            "desire" to listOf(
                "Take a moment to truly visualize what you want. Picture it clearly in your mind—this is the first step to making it real.",
                "Break it down into actionable steps. Every big goal starts with a small first move.",
                "Allow yourself to feel excited about it! Desire is a powerful motivator when you let it drive you.",
                "Talk about it with someone who supports your dreams. Sometimes, saying it out loud makes it feel more possible.",
                "Trust that if you desire something deeply, it’s worth pursuing. Stay patient, stay focused, and keep moving forward."
            ),
            "disappointment" to listOf(
                "It’s okay to feel let down. Give yourself the space to acknowledge it before moving forward.",
                "Look for the lesson in this. Sometimes, setbacks guide us toward something even better.",
                "Talk to someone who understands. A shared perspective can make disappointment feel less lonely.",
                "Remind yourself that one moment doesn’t define you. You are still capable, still worthy, still moving forward.",
                "When you’re ready, turn the page. There are so many more chapters ahead, and this is just one part of the story."
            ),
            "disapproval" to listOf(
                "Take a deep breath and ask yourself—what exactly is bothering you? Understanding your own feelings is the first step toward addressing them.",
                "Express your disapproval in a way that fosters discussion, not conflict. A calm and thoughtful approach can lead to real change.",
                "Try to see things from another perspective. Even if you still disagree, understanding where someone else is coming from can ease frustration.",
                "If something doesn’t align with your values, stand by your beliefs, but do so with kindness. Respect goes both ways.",
                "Focus on what you *can* change. Not everything will go the way you want, but you can always choose how you respond."
            ),
            "disgust" to listOf(
                "Acknowledge your feeling of disgust without letting it consume you. It’s okay to have strong reactions, but they don’t have to control you.",
                "If possible, remove yourself from the situation or environment that’s causing discomfort. Distance can help reset your emotions.",
                "Try shifting your focus to something pleasant—music, fresh air, or a positive memory can help recalibrate your mind.",
                "Ask yourself what’s triggering this feeling. Sometimes disgust reveals deeper personal values or boundaries you might not have fully recognized.",
                "Take deep, slow breaths. Your body and mind are connected, and calming your physical response can ease the emotional reaction."
            ),
            "embarrassment" to listOf(
                "Remind yourself that everyone has embarrassing moments. Seriously, think of a funny one from your past—did it matter in the long run?",
                "Laugh it off if you can. Humor has a way of turning awkward moments into funny stories for later.",
                "Talk about it with someone you trust. You’ll probably realize it’s not as bad as it feels in the moment.",
                "Shift your perspective—will you even remember this in a week? A month? If not, let it go and move forward.",
                "Be kind to yourself. You’re human, and mistakes are part of life. Instead of dwelling on it, use it as a learning experience."
            ),
            "excitement" to listOf(
                "Let yourself fully feel the excitement—jump, dance, or do whatever makes you feel alive in the moment!",
                "Share your joy with someone! Happiness multiplies when it’s shared, so call a friend or send a message to celebrate together.",
                "Turn this excitement into action. What’s the next step you can take to keep this positive momentum going?",
                "Document this moment. Write it down, take a picture, or create something that will help you relive this feeling later.",
                "Embrace the energy that comes with excitement and pour it into something creative, adventurous, or meaningful!"
            ),
            "fear" to listOf(
                "Fear is a signal, not a stop sign. What is it trying to tell you? Facing it step by step can help it lose its power.",
                "Take deep, calming breaths. Fear often makes our bodies tense, and relaxing physically can help settle the mind.",
                "Talk to someone about what’s scaring you. Sometimes, saying it out loud makes it feel smaller and less overwhelming.",
                "Remind yourself of past times you’ve overcome fear. You’re stronger than you think, and you’ve handled challenges before.",
                "Turn fear into curiosity—what if this fear is leading you to something incredible? Every challenge is a chance for growth."
            ),
            "gratitude" to listOf(
                "Pause for a moment and truly feel grateful. Let that warmth fill you—it’s one of the most powerful emotions we have.",
                "Write down three things you’re grateful for today. It’s a simple habit, but it shifts your mindset in a powerful way.",
                "Express your gratitude to someone who has made a difference in your life. A simple ‘thank you’ can mean the world.",
                "Focus on the little joys—the sunlight, a kind word, a favorite song. Gratitude is found in the small moments.",
                "Turn gratitude into action—give back, pay it forward, or simply share kindness with someone today."
            ),
            "grief" to listOf(
                "Grief isn’t meant to be rushed. Allow yourself to feel it, even when it’s hard. Healing comes in its own time.",
                "Talk to someone who understands. You don’t have to go through this alone—lean on those who care about you.",
                "Find comfort in small, soothing activities—listening to music, journaling, or simply sitting quietly with your thoughts.",
                "Honor what you’ve lost in a way that feels meaningful. Remembering can be a part of healing.",
                "Be patient with yourself. Grief changes, but it doesn’t disappear overnight. It’s okay to take one step at a time."
            ),
            "joy" to listOf(
                "Let yourself *fully* enjoy this moment! Don’t hold back—smile, laugh, and soak it all in.",
                "Share your joy with someone! Happiness grows when it’s passed on to others.",
                "Capture the moment—write about it, take a picture, or find a way to remember this feeling.",
                "Express gratitude for this happiness. Recognizing joy makes it even more powerful.",
                "Let this moment inspire you to create more joyful experiences in your life!"
            ),
            "love" to listOf(
                "Tell the people you love how much they mean to you. A simple, heartfelt message can make their day.",
                "Show love in action—kindness, support, and presence matter more than words alone.",
                "Take time to appreciate the love in your life, whether from family, friends, or even yourself.",
                "Reflect on what love means to you. What moments have made you feel most loved? How can you create more of them?",
                "Remember, love isn’t just about others—it’s about loving yourself, too. Be kind to yourself, always."
            ),
            "nervousness" to listOf(
                "Take a few deep breaths and remind yourself—you’ve got this. Anxiety fades when you focus on what you *can* control.",
                "Prepare as much as possible, but don’t aim for perfection. You’re capable, even if things don’t go exactly as planned.",
                "Visualize success. Imagine yourself handling the situation with confidence, and your brain will start believing it.",
                "Talk to someone you trust—sometimes, just saying ‘I’m nervous’ makes it feel less overwhelming.",
                "Nervousness is just excitement in disguise. Reframe it as a sign that something important is happening!"
            ),
            "optimism" to listOf(
                "Use your positivity to uplift someone else today—kind words can change someone’s entire day.",
                "Set an inspiring goal for yourself. Optimism is powerful when it fuels action!",
                "Remind yourself of past challenges you’ve overcome. Proof that brighter days *always* come.",
                "Find the silver lining in something that didn’t go as planned. Growth often comes from unexpected places.",
                "Embrace today with open arms. With the right mindset, anything feels possible."
            ),
            "pride" to listOf(
                "Take a moment to truly appreciate your achievement—you earned it!",
                "Celebrate with the people who supported you on this journey.",
                "Use this feeling as a stepping stone for your next big goal.",
                "Write down what you're proud of and revisit it on tough days.",
                "Remember: Every small victory adds up to something amazing!"
            ),
            "realization" to listOf(
                "Write down your thoughts—it helps clarify your newfound understanding.",
                "Share your realization with someone you trust; their perspective matters too.",
                "Turn your insight into action and make a meaningful change.",
                "Sometimes, realizations are tough—give yourself time to process them.",
                "Growth often comes from deep realizations; embrace the journey!"
            ),
            "relief" to listOf(
                "Close your eyes, take a deep breath, and enjoy this peaceful moment.",
                "Reflect on what led to your stress and how you overcame it—you did great!",
                "Celebrate your relief with a self-care activity you love.",
                "Share your relief with someone who supported you through the tough times.",
                "Use this moment as a reminder that challenges are temporary."
            ),
            "remorse" to listOf(
                "It's okay to feel this way—acknowledge your emotions with kindness.",
                "If your actions hurt someone, a sincere apology can go a long way.",
                "Learn from this moment so you can grow into a better version of yourself.",
                "Write a letter to yourself about what you’ve learned from this experience.",
                "Mistakes happen, but they don’t define you—use them to become stronger."
            ),
            "sadness" to listOf(
                "Wrap yourself in a cozy blanket and listen to your favorite calming music.",
                "Talk to someone you trust—sometimes, sharing helps lighten the burden.",
                "Write your thoughts in a journal; it’s a great way to release emotions.",
                "Go for a gentle walk and let nature soothe your heart.",
                "It’s okay to feel sad—give yourself permission to rest and heal."
            ),
            "surprise" to listOf(
                "Take a deep breath and enjoy this unexpected moment!",
                "Share the surprise with someone—it’s always fun to see their reaction!",
                "Sometimes, surprises bring new opportunities—stay open to what’s next.",
                "Journal your feelings about this moment—you might appreciate it later.",
                "Life is full of surprises; embrace the adventure!"
            ),
            "neutral" to listOf(
                "Use this moment of balance to check in with yourself. How are you really feeling beneath the surface?",
                "Plan something to look forward to, even if it’s small. A little excitement goes a long way.",
                "Take time for self-care, whether that’s a quiet moment alone, a good meal, or something creative.",
                "Reflect on your day so far. What went well? What would you like to improve?",
                "Neutral moments are perfect for rest. Recharge, reset, and get ready for what’s next!"
            )
        )

        val emotionRecommendations = recommendations[sentiment.lowercase()]
        return emotionRecommendations?.random()
            ?: "Take some time to reflect on your emotions and focus on self-care."
    }


    fun addCheckIn(feeling: String, onSentimentAnalyzed: (String) -> Unit){
        viewModelScope.launch {
            try {
                val sentiment = analyzeSentiment(feeling)
                saveCheckIn(feeling = feeling,sentiment)
                onSentimentAnalyzed(sentiment)
            }catch (e : Exception){
                Log.e("JournalViewModel", "Error Adding Daily Check In", e)
            }
        }
    }

    suspend fun analyzeSentiment(feeling: String) : String{
        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val jsonBody = JSONObject()
        jsonBody.put("text", feeling)
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        return withContext(Dispatchers.IO){
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:5000/analyze")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful){
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody ?: "")
                    val predictions = jsonResponse.getJSONArray("predictions")
                    val topEmotion = predictions.getJSONObject(0).getString("Emotion")
                    topEmotion
                }else{
                    "Neutral"
                }
            }catch (e : Exception){
                Log.e("JournalViewModel", "Sentiment Analysis Failed", e)
                "Neutral"
            }
        }
    }

    fun deleteCheckIn(entry : DailyCheckIn){
        viewModelScope.launch {
            try {
                dao.deleteCheckIn(entry)
            }catch (e: Exception){
                Log.e("JournalViewModel", "Error deleting check-in", e)
            }
        }
    }
}