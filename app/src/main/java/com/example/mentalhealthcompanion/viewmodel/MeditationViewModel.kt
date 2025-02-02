package com.example.mentalhealthcompanion.viewmodel

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mentalhealthcompanion.R

class MeditationViewModel(application: Application, private val moodViewModel: MoodViewModel) : AndroidViewModel(application) {
    private val _timerValue = MutableLiveData<Int>()
    val timerValue: LiveData<Int> get() = _timerValue
    private var countDownTimer: CountDownTimer? = null
    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying
    private var mediaPlayer: MediaPlayer? = null

    private val moodMusicMap = mapOf(
        "joy" to listOf(R.raw.happy1,R.raw.happy2),
        "love" to listOf(R.raw.love1,R.raw.love2),
        "gratitude" to listOf(R.raw.beautifulname,R.raw.hosanna),
        "sadness" to listOf(R.raw.sad1,R.raw.sad2),
        "anger" to listOf(R.raw.ragas),
        "neutral" to listOf(R.raw.ragas,R.raw.seeuagain),
        "fear" to listOf(R.raw.ragas),
        "optimism" to listOf(R.raw.krishna),
        "mild sadness" to listOf(R.raw.bhalobeshe,R.raw.robenirobe)
    )

    fun startTimer(duration:Int){
        _timerValue.value = duration
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer((duration * 1000).toLong(), 1000){
            override fun onTick(millisUntilFinished: Long) {
                _timerValue.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                _isPlaying.value = false
            }
        }.start()
        _isPlaying.value = true
    }
    fun stopTimer(){
        countDownTimer?.cancel()
        _isPlaying.value = false
    }

    fun playSound(context: Context, mood: String) {
        stopSound()
        val musicList = moodMusicMap[mood] ?: listOf(R.raw.ragas)
        val randomMusic = musicList.random()
        mediaPlayer = MediaPlayer.create(context,randomMusic)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    fun stopSound(){
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        stopSound()
    }

    suspend fun getAIMeditation() : String{
        val averageMoodScore = moodViewModel.averageMood.value ?: 2.5f
        val emotion = when{
            averageMoodScore >= 4.2f -> listOf("joy", "love", "gratitude", "optimism").random()
            averageMoodScore >= 3.0f -> "neutral"
            averageMoodScore >= 2.0f -> "mild sadness"
            averageMoodScore >= 1.5f -> "sadness"
            else -> "anger"
        }

        playSound(getApplication(),emotion)
        return generatedMeditationSession(emotion)
    }

    private fun generatedMeditationSession(emotion: String): String {
        return when (emotion.lowercase()){
            "joy", "love", "gratitude", "excitement", "pride", "optimism" -> """
                🌞 **Positive Energy Meditation**
                Close your eyes and take a long, deep breath in... hold for a moment... and exhale slowly, releasing tension.
                Visualize a warm golden light surrounding you, filling you with joy, love, and gratitude. This light is full of vibrant energy and positivity.
                With every breath, allow this energy to flow through your body, energizing you from head to toe.
                Affirm to yourself: "I am a vessel of love, joy, and light."
                
                Feel the warmth of this energy as it reaches each part of your body. From your chest to your arms, down to your legs, and into your feet. Let the warmth spread, releasing any negativity.
                Imagine yourself standing in the center of a sunlit field, a light breeze gently brushing your face, and the sound of birds singing around you.
                With every step, feel the ground beneath you supporting you, while the air fills you with boundless energy.
                Breathe in deeply and exhale fully, knowing that you are capable of embracing every moment with joy.
            """.trimIndent()

            "sadness", "grief", "disappointment", "remorse", "nervousness" -> """
                🌧️ **Healing Meditation**
                Find a comfortable position, close your eyes, and take a slow, deep breath in... hold for a moment... then exhale fully, releasing all tension.
                Imagine a soft, glowing light of healing surrounding you. This light feels warm and comforting, like a gentle embrace that holds you in safety.
                Let go of any heaviness with each exhale. You are safe. You are loved. You are healing.
                Affirm to yourself: "I am open to healing and growth."
                
                Picture yourself surrounded by nature—a quiet, peaceful forest. The gentle sound of rain falling on the leaves, the soft rustle of the trees. Feel the earth below supporting you as you sit, grounded in this moment.
                With each breath, imagine the rain washing over you, releasing grief, sadness, and any remaining tension. Allow the water to cleanse your spirit and restore peace.
                As you inhale, feel the fresh air entering your lungs, bringing with it comfort and renewal.
                With every exhale, let go of any lingering sadness. You are healing, piece by piece, with every breath.
            """.trimIndent()

            "anger", "fear", "disgust", "annoyance", "disapproval" -> """
                🔥 **Calm & Balance Meditation**
                Close your eyes. Take a deep, deliberate breath in... hold for a moment... and slowly exhale, releasing all tension.
                Picture a calm, serene lake. The water is still, the surface like glass, reflecting the clear blue sky. Feel the stillness of the lake entering your body.
                As you breathe, visualize the anger or fear you feel as small ripples on the water. With each breath, the ripples fade, leaving the surface perfectly calm.
                Affirm to yourself: "I release all tension. I embrace calm and peace."
                
                Imagine the power of this lake’s stillness entering your body. Feel your heartbeat slowing down, your muscles relaxing.
                With each exhale, let go of any lingering anger. Feel your mind becoming clearer, your spirit lighter, and your body more at ease.
                Visualize your body surrounded by peaceful light, protecting you from any negativity. You are in control. You are at peace.
            """.trimIndent()

            "neutral", "approval", "curiosity", "realization", "amusement" -> """
                🌿 **Mindfulness Meditation**
                Find a comfortable sitting position, close your eyes, and begin to take slow, deliberate breaths. Inhale through your nose, hold for a moment, and then exhale slowly through your mouth.
                Bring your attention to the present moment. Notice the sensations in your body—your feet on the ground, your hands resting gently on your lap. Feel your body being grounded in this moment.
                Affirm to yourself: "I am present. I am aware. I am open to the flow of life."
                
                Picture yourself sitting in a beautiful garden, surrounded by flowers and trees. The air is fresh, and every breath you take is a reminder of the beauty around you.
                Feel the warmth of the sun on your face and the coolness of the earth beneath you. Let the sounds of nature—birds singing, leaves rustling in the breeze—help you stay rooted in the present.
                With each breath, become more aware of the sights, sounds, and feelings surrounding you. Let each breath bring you closer to a state of peaceful mindfulness.
            """.trimIndent()

            "mild sadness" -> """
                🌧️ **Mild Healing Meditation**
                Close your eyes and take a deep breath in... hold for a moment... then exhale slowly, letting go of any negative emotions.
                Visualize a soft, soothing light of comfort surrounding you. Feel the gentle warmth of this light bring a sense of calm and relief.
                Affirm to yourself: "I embrace this moment of healing and allow peace to flow."
                
                Picture yourself in a quiet space, perhaps a calm lake or a peaceful meadow. The air is gentle, and you feel safe in this moment of rest.
                With each breath, allow yourself to release the weight of mild sadness and open yourself to healing.
                Let each exhale bring you closer to a peaceful state, where you are fully present, calm, and supported.
            """.trimIndent()

            else -> """
                🌱 **General Relaxation Meditation**
                Close your eyes and take a deep, relaxing breath in... hold for a moment... and slowly exhale, feeling all tension melt away.
                Picture yourself in a peaceful place—a quiet forest, a calm beach, or a cozy room. Imagine the colors and textures of your surroundings—soft green leaves, gentle waves, or warm, plush surroundings.
                Feel the calmness of this place fill you, entering every muscle, every cell.
                Affirm to yourself: "I am at peace. I am relaxed. I am here in the present moment."
                
                Picture yourself lying in a hammock, swaying gently in the breeze. You hear the soft sounds of nature, the rustle of leaves, or the sound of the ocean waves.
                Feel the hammock rocking you into a deep state of relaxation, your body fully supported and at ease. Each breath you take brings you deeper into calmness and relaxation.
                Let go of any remaining stress, knowing that in this moment, you are safe and at peace.
            """.trimIndent()
        }
    }
}