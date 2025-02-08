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
    fun saveCheckIn(feeling: String, sentiment: String) {
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

    fun getAllCheckIns(): List<DailyCheckIn> {
        return runBlocking {
            dao.getAllCheckIns()
        }
    }

    fun getVoiceRecommendation(sentiment: String): String {
        val recommendations = mapOf(
            "admiration" to listOf(
                "Hey, take a moment to really appreciate those qualities you admire in others, okay? Maybe even jot them down – it can really help solidify them in your mind and give you a clearer picture of what you value.",
                "You know what might be really cool? Reach out to the person or source of your admiration and tell them how much they inspire you. Seriously, genuine words of appreciation can totally brighten someone's day and make you feel good too!",
                "How about channeling that admiration into something you do? Whether it's picking up a new hobby, considering a different career path, or just working on yourself, let that inspiration be the fuel for your journey. You got this!",
                "Try surrounding yourself with more of what you admire, like great books, inspiring music, beautiful art, or even just awesome people. The more you're exposed to greatness, the more it'll rub off on you and shape who you become.",
                "Have you ever thought about why this admiration resonates with you so deeply? Sometimes, it can reveal a hidden part of yourself that's just longing to grow and shine. It's like a little clue about what you're meant to do!"
            ),
            "amusement" to listOf(
                "Hey, you know laughter is contagious, right? Why not share the joy and send a funny meme or joke to a friend? It'll definitely keep the good vibes going, and make their day!",
                "Seriously, let yourself fully enjoy the moment when you're feeling amused. Be totally present, laugh without holding anything back, and embrace the silliness of life. It's good for the soul!",
                "Why not find more of what amuses you? Whether it's stand-up comedy, hilarious videos, or a really funny book, make some time to indulge in lighthearted fun. You deserve a good laugh!",
                "Here's a thought: turn your amusement into something creative! You could write down or even act out a funny moment you experienced today. You might even make someone else's day better in the process!",
                "Just a little reminder of how good it feels to laugh. Life can be serious sometimes, but moments like these remind you that joy is always within reach. Never forget that!"
            ),
            "anger" to listOf(
                "Hey, how about just pausing for a sec and taking a deep breath? Count to ten if you need to. Seriously, let the intensity pass before you react – you deserve some peace and calm, you know?",
                "You know what sometimes helps me? Writing down exactly what's frustrating me. Getting those thoughts out on paper can really help process your emotions before you say or do anything you might regret.",
                "Maybe go for a walk or do something physical to release some of that tension? Movement can really help transform anger into clarity and calm. Give it a try!",
                "Try expressing how you feel, but in a way that helps the other person understand where you're coming from, okay? Talk it out with someone you trust, or even write an imaginary letter to get all those feelings out.",
                "Once the storm settles a bit, ask yourself: what is this anger trying to teach me? Sometimes, really strong emotions point to deeper needs that deserve your attention. It's worth exploring!"
            ),
            "annoyance" to listOf(
                "Take a deep breath and ask yourself – will this even matter a year from now? If not, maybe it's best to just let it go, you know? Don't sweat the small stuff!",
                "Why not step away from whatever's irritating you, even if it's just for a few minutes? Sometimes, a little distance can bring clarity and really ease the frustration.",
                "Try to find something that brings you a little joy. Maybe a favorite song, a quick stretch, or even a small treat could shift your focus away from what's annoying you. Treat yourself!",
                "Here's an idea: try to laugh about it. Sometimes, the little things that bug us turn into funny stories later on. Perspective is everything!",
                "If someone else is annoying you, try seeing things from their perspective. Maybe they're just having a really bad day too. Showing a little grace can really help."
            ),
            "approval" to listOf(
                "Take a moment to really soak in this feeling – what you've done is totally worth celebrating! You worked hard, and it definitely shows!",
                "Share your approval with someone else. A kind word of encouragement to others can create a ripple effect of positivity. Spread the love!",
                "Take a moment to reflect on what led to this moment. What choices did you make that got you here? Recognizing those choices can help you repeat your success in the future!",
                "Give yourself permission to feel proud! Confidence really grows when you acknowledge your own efforts and hard work.",
                "Use this feeling as motivation to keep going. Every step forward matters, and this is proof that you're totally on the right path. Keep it up!"
            ),
            "caring" to listOf(
                "Hey, just a reminder that someone out there appreciates your kindness more than you even realize. Keep spreading the love – it truly makes a difference in the world!",
                "Make sure you show yourself the same care that you give to others. You deserve love and kindness just as much as anyone else does, okay?",
                "Sometimes, the smallest gestures really mean the most. A simple text, a warm hug, or even just a smile can brighten someone’s day. It's the little things!",
                "Make some time to nurture the relationships that really matter to you. Connection is one of the most beautiful parts of life, you know?",
                "Remember, caring doesn’t mean you have to carry everything alone. It’s absolutely okay to set boundaries and take care of yourself too. Don't forget that!"
            ),
            "confusion" to listOf(
                "It’s okay not to have all the answers right now. Give yourself permission to sit with the uncertainty without any pressure, okay? You don't need to know everything!",
                "Try breaking the problem down into smaller, more manageable pieces. Sometimes, tackling just one small part at a time can bring a little bit of clarity.",
                "Talk it through with someone you trust. A fresh perspective might really help you see things in a new light. Two heads are better than one!",
                "Trust that understanding will come eventually. Some things just make sense in hindsight, so try to be patient with yourself in the meantime.",
                "Just remember, confusion often means you're growing and learning. It’s like the space between what you knew before and what you’re about to learn. Embrace it!"
            ),
            "curiosity" to listOf(
                "Definitely follow that spark! Whether it's a new topic, hobby, or just a cool idea – exploring it could lead to something amazing. You never know!",
                "Ask questions, even the weird ones! Curiosity really thrives when you allow yourself to wonder freely and explore different possibilities.",
                "Pick up a book, watch a documentary, or talk to someone who knows more about it. Every new insight you gain expands your world in some way.",
                "Let yourself experiment without even worrying about 'getting it right.' Discovery is really about the journey itself, not just the destination.",
                "Remember, curiosity is a gift. It keeps life interesting and exciting, so never stop exploring the things that excite you! It's worth it!"
            ),
            "desire" to listOf(
                "Take a moment to really visualize what you want. Picture it super clearly in your mind – this is actually the very first step to making it real. See it to believe it!",
                "Try breaking it down into small, actionable steps. Every really big goal starts with just one small first move. What's yours?",
                "Allow yourself to feel genuinely excited about it! Desire can be a really powerful motivator when you let it drive you forward.",
                "Talk about it with someone who really supports your dreams and encourages you. Sometimes, just saying it out loud makes it feel a lot more possible.",
                "Trust that if you desire something deeply, it’s absolutely worth pursuing. Stay patient, stay focused, and just keep moving forward, one step at a time."
            ),
            "disappointment" to listOf(
                "Hey, it’s okay to feel let down sometimes. Give yourself a little space to acknowledge it before you try to move forward, okay? It's a valid feeling.",
                "Try to look for the lesson in this situation. Sometimes, setbacks actually guide us toward something even better, even if it doesn't feel like it now.",
                "Why not talk to someone who really understands what you're going through? A shared perspective can make disappointment feel a lot less lonely and isolating.",
                "Remind yourself that this one moment doesn’t define who you are as a person. You are still capable, still worthy, and still moving forward, no matter what.",
                "Whenever you're ready, try to turn the page and start a new chapter. There are so many more chapters ahead of you, and this is just one small part of the story."
            ),
            "disapproval" to listOf(
                "Take a deep breath and ask yourself – what exactly is bothering you here? Understanding your own feelings is always the first step toward addressing them effectively.",
                "Express your disapproval in a way that fosters discussion, not conflict or argument. A calm and thoughtful approach can actually lead to real change and understanding.",
                "Try to see things from the other person's perspective. Even if you still disagree with them, understanding where they're coming from can ease the frustration a bit.",
                "If something doesn’t align with your values, stand by your beliefs, but do it with kindness and respect. Remember, respect goes both ways, okay?",
                "Focus on what you *can* change in the situation. Not everything will go the way you want it to, but you can always choose how you respond and react."
            ),
            "disgust" to listOf(
                "Acknowledge your feeling of disgust without letting it consume you entirely. It’s okay to have strong reactions, but they don’t have to control your every move.",
                "If it's possible, try to remove yourself from the situation or the environment that’s causing you discomfort. A little distance can really help reset your emotions.",
                "Try shifting your focus to something more pleasant – maybe some music, fresh air, or a positive memory can help recalibrate your mind and mood.",
                "Ask yourself what's actually triggering this feeling of disgust. Sometimes, disgust can reveal deeper personal values or boundaries that you might not have fully recognized yet.",
                "Try taking some deep, slow breaths. Your body and your mind are connected, and calming your physical response can ease the emotional reaction you're having."
            ),
            "embarrassment" to listOf(
                "Remind yourself that everyone has embarrassing moments sometimes. Seriously, try to think of a funny one from your past – did it really matter in the long run?",
                "If you can, try to laugh it off a little bit. Humor has a way of turning awkward moments into funny stories that you can tell later on.",
                "Talk about it with someone you trust and feel comfortable with. You'll probably realize that it's not nearly as bad as it feels in the moment, trust me.",
                "Try to shift your perspective a little bit – will you even remember this in a week? A month? If not, try to let it go and move forward with your day.",
                "Be kind and gentle with yourself. You're human, and mistakes are just a part of life, you know? Instead of dwelling on it, use it as a learning experience for the future."
            ),
            "excitement" to listOf(
                "Let yourself *fully* feel the excitement that you're experiencing! Jump, dance, or do whatever makes you feel alive and happy in the moment!",
                "Why not share your joy and excitement with someone else? Happiness multiplies when it’s shared, so call a friend or send a message to celebrate together!",
                "Try to turn this excitement into action by channeling it into something productive. What’s the next step you can take to keep this positive momentum going strong?",
                "Document this moment somehow. Write it down in a journal, take a picture, or create something that will help you relive this feeling later on down the road.",
                "Embrace all the energy that comes with excitement and pour it into something creative, adventurous, or really meaningful to you!"
            ),
            "fear" to listOf(
                "Remember that fear is a signal, not a stop sign in your life. What is it trying to tell you right now? Facing it step by step can actually help it lose a lot of its power.",
                "Try taking some deep, calming breaths to center yourself. Fear often makes our bodies tense up, and relaxing physically can really help settle your mind.",
                "Talk to someone about what’s scaring you the most. Sometimes, just saying it out loud can make it feel smaller and less overwhelming.",
                "Remind yourself of past times when you’ve overcome fear and challenges. You’re stronger than you think you are, and you’ve definitely handled tough situations before.",
                "Try to turn your fear into curiosity instead – what if this fear is actually leading you to something incredible or unexpected? Every challenge is a chance for growth."
            ),
            "gratitude" to listOf(
                "Pause for a moment and truly feel grateful for what you have. Let that warmth fill you up – it’s one of the most powerful emotions we have at our disposal.",
                "Write down at least three things you’re genuinely grateful for today. It’s a simple habit, but it can really shift your mindset in a powerful way.",
                "Express your gratitude to someone who has made a difference in your life recently. A simple ‘thank you’ can really mean the world to someone.",
                "Focus on the little joys in your life – the sunlight, a kind word from a stranger, a favorite song on the radio. Gratitude is often found in the small moments.",
                "Turn your gratitude into action by giving back to your community, paying it forward to someone in need, or simply sharing kindness with someone today."
            ),
            "grief" to listOf(
                "Remember that grief isn’t something that’s meant to be rushed through. Allow yourself to feel it, even when it’s really hard. Healing comes in its own time, okay?",
                "Talk to someone who really understands what you're going through right now. You don’t have to go through this all alone – lean on the people who truly care about you and your well-being.",
                "Find comfort in small, soothing activities that you enjoy – listening to calming music, journaling your thoughts and feelings, or simply sitting quietly with your thoughts.",
                "Honor what you’ve lost in a way that feels meaningful and personal to you. Remembering can be a really important part of the healing process.",
                "Be patient and gentle with yourself during this time. Grief changes over time, but it doesn’t disappear overnight. It’s perfectly okay to take things one step at a time."
            ),
            "joy" to listOf(
                "Let yourself *fully* enjoy this moment! Don’t hold anything back – smile, laugh, and soak it all in while you can!",
                "Share your joy and happiness with someone you care about! Happiness really grows when it’s passed on to others.",
                "Capture this moment somehow – write about it in a journal, take a picture to remember it, or find another way to remember this amazing feeling.",
                "Express your gratitude for this happiness. Recognizing and acknowledging joy makes it even more powerful in your life.",
                "Let this moment inspire you to create more joyful and happy experiences in your life going forward!"
            ),
            "love" to listOf(
                "Tell the people you love just how much they mean to you. A simple, heartfelt message can really make their day and brighten their spirits.",
                "Show your love in action through kindness, support, and presence. These things often matter more than just words alone, you know?",
                "Take some time to appreciate all the love that you have in your life, whether it’s from family, friends, or even yourself.",
                "Reflect on what love truly means to you personally. What moments have made you feel most loved in your life? How can you create more of those moments?",
                "Remember that love isn’t just about other people – it’s also about loving yourself, too. Be kind and gentle to yourself, always."
            ),
            "nervousness" to listOf(
                "Take a few deep breaths and remind yourself – you’ve totally got this! Anxiety often fades when you focus on what you *can* control in the situation.",
                "Prepare as much as possible, but don’t aim for perfection. You're capable, even if things don’t go exactly as you’ve planned them in your head.",
                "Try to visualize success in your mind. Imagine yourself handling the situation with confidence and grace, and your brain will actually start believing it.",
                "Talk to someone you trust about how you're feeling – sometimes, just saying ‘I’m nervous’ out loud can make it feel a lot less overwhelming.",
                "Remember, nervousness is just excitement in disguise! Try to reframe it as a sign that something important and meaningful is about to happen!"
            ),
            "optimism" to listOf(
                "Use your positivity to uplift and encourage someone else today – a few kind words can totally change someone’s entire day for the better.",
                "Set an inspiring goal for yourself and work towards achieving it. Optimism is so powerful when it fuels action and ambition!",
                "Remind yourself of past challenges that you’ve successfully overcome. It’s proof that brighter and better days *always* come eventually.",
                "Try to find the silver lining in something that didn’t go as planned recently. Growth and learning often come from unexpected places.",
                "Embrace today with open arms and a positive attitude. With the right mindset, anything really feels possible in your life."
            ),
            "pride" to listOf(
                "Take a moment to truly appreciate your achievement and everything you’ve accomplished – you definitely earned it through hard work!",
                "Celebrate your success with the people who supported you throughout this entire journey and helped you along the way.",
                "Use this feeling of pride as a stepping stone for your next big goal or ambition in life. Keep striving for greatness!",
                "Write down what you're proud of in a journal and revisit it on tough days when you need a reminder of your strength and resilience.",
                "Just remember: Every small victory and accomplishment adds up to something truly amazing in the long run!"
            ),
            "realization" to listOf(
                "Write down all of your thoughts and feelings – this can help clarify your newfound understanding and make sense of everything.",
                "Share your realization with someone you trust and value; their perspective and input matters a great deal too.",
                "Try to turn your newfound insight into action by making a meaningful change in your life or in the world around you.",
                "Sometimes, realizations can be tough and challenging – give yourself plenty of time and space to process everything you're feeling.",
            ),
            "relief" to listOf(
                "Close your eyes, take a deep breath, and really enjoy this peaceful moment – you deserve it after whatever you've been through!",
                "Reflect for a moment on what led to your stress and how you managed to overcome it – you did great! Give yourself some credit!",
                "Why not celebrate this feeling of relief with a self-care activity that you really love and enjoy? Treat yourself!",
                "Share this feeling of relief with someone who supported you through the tough times – they’ll be happy to know you're feeling better!",
                "Use this moment as a gentle reminder that challenges are always temporary, and you’re capable of getting through them."
            ),
            "remorse" to listOf(
                "It's okay to feel this way – acknowledge your emotions with kindness and understanding. Don't beat yourself up too much, okay?",
                "If your actions have hurt someone else, a sincere and heartfelt apology can really go a long way toward mending things.",
                "Try to learn from this moment so you can grow into an even better version of yourself. We all make mistakes, it's what we do after that counts.",
                "Consider writing a letter to yourself about what you’ve learned from this experience. It's a good way to organize your thoughts and process the situation.",
                "Remember that mistakes happen, but they absolutely don’t define who you are as a person. Use them as opportunities to become stronger and wiser."
            ),
            "sadness" to listOf(
                "Wrap yourself up in a cozy blanket and listen to your favorite calming music – sometimes a little comfort can make a big difference.",
                "Talk to someone you trust about what you're feeling – sometimes, just sharing your thoughts and feelings can help lighten the burden you're carrying.",
                "Try writing your thoughts and feelings in a journal; it’s a great way to release emotions and process what you’re going through.",
                "Go for a gentle walk outdoors and let nature soothe your heart and calm your mind. Fresh air can work wonders.",
                "Remember, it’s okay to feel sad sometimes – give yourself permission to rest, heal, and take care of yourself during this time. You deserve it."
            ),
            "surprise" to listOf(
                "Take a deep breath and really enjoy this unexpected and surprising moment! It's not every day that something like this happens!",
                "Share the surprise with someone you care about – it’s always fun to see their reaction and share the excitement!",
                "Remember that sometimes, surprises can bring new opportunities your way – try to stay open to whatever might be coming next!",
                "Consider journaling your thoughts and feelings about this moment – you might really appreciate having a record of it later on.",
                "Life is full of surprises, both big and small – try to embrace the adventure and go with the flow as best you can!"
            ),
            "neutral" to listOf(
                "Use this moment of balance to check in with yourself and see how you're really feeling beneath the surface. Are you truly okay, or is there something more going on?",
                "Plan something to look forward to, even if it’s something small. A little bit of excitement can really go a long way toward boosting your mood.",
                "Make some time for self-care today, whether that’s a quiet moment alone with a good book, a delicious and nourishing meal, or some kind of creative activity that you enjoy.",
                "Take a moment to reflect on your day so far. What went well? What are some things you might like to improve upon or change?",
                "Remember that neutral moments are often the perfect time for some rest and relaxation. Recharge your batteries, reset your mind, and get ready for whatever comes next!"
            )
        )
        val voiceRecommendations = recommendations[sentiment.lowercase()]
        return voiceRecommendations?.random()
            ?: "Take some time to reflect on your emotions and focus on self-care."
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


    fun addCheckIn(feeling: String, onSentimentAnalyzed: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val sentiment = analyzeSentiment(feeling)
                saveCheckIn(feeling = feeling, sentiment)
                onSentimentAnalyzed(sentiment)
            } catch (e: Exception) {
                Log.e("JournalViewModel", "Error Adding Daily Check In", e)
            }
        }
    }

    suspend fun analyzeSentiment(feeling: String): String {
        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val jsonBody = JSONObject()
        jsonBody.put("text", feeling)
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("http://10.0.2.2:5000/analyze")
                    .post(requestBody)
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonResponse = JSONObject(responseBody ?: "")
                    val predictions = jsonResponse.getJSONArray("predictions")
                    val topEmotion = predictions.getJSONObject(0).getString("Emotion")
                    topEmotion
                } else {
                    "Neutral"
                }
            } catch (e: Exception) {
                Log.e("JournalViewModel", "Sentiment Analysis Failed", e)
                "Neutral"
            }
        }
    }

    fun deleteCheckIn(entry: DailyCheckIn) {
        viewModelScope.launch {
            try {
                dao.deleteCheckIn(entry)
            } catch (e: Exception) {
                Log.e("JournalViewModel", "Error deleting check-in", e)
            }
        }
    }
}