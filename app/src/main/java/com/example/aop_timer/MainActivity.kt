package com.example.aop_timer

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView



class MainActivity : AppCompatActivity() {


    /*  */

    private val remainMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }

    private val remainSecondsTextView: TextView by lazy {
        findViewById(R.id.remainSecondsTextView)
    }

    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }



    private val soundPool = SoundPool.Builder().build()

    private var tickingSoundid: Int?= null
    private var currentCountDownTimer: CountDownTimer?= null
    private var bellSoundId: Int?= null

    /************************** onCreate **********************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
        initSound()
    }


    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }


    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }


    /* seekbar 변경부분 */
    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                    if(p2){
                        updateRemainTimes(progress * 60 * 1000L)
                    }


                } //2자리수로 표현

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    stopCountDown()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar ?: return
                    //0이면 중지하는 코드
                    if(seekBar.progress == 0) {
                        stopCountDown()
                    }else{
                        startCountDown()
                    }
                }
            }
        )
    }

    /** 소리 추가 **/
    private fun initSound() {
        tickingSoundid = soundPool.load(this, R.raw.tick_tok,1)
        bellSoundId = soundPool.load(this, R.raw.church, 1)
    }





    private fun createCountDownTimer(initialMillis: Long): CountDownTimer =
         object : CountDownTimer(initialMillis, 1000L){
            override fun onTick(p0: Long) {
                updateRemainTimes(p0)
                updateSeekBar(p0)
            }

            override fun onFinish() {
                completeCountDown()
            }
        }


    private fun completeCountDown(){
        updateRemainTimes(0)
        updateSeekBar(0)


        soundPool.autoPause()
        bellSoundId?.let{
                soundId -> soundPool.play(soundId, 1F, 1F, 0, 0 , 1F)
        }
    }

    private fun startCountDown(){
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start()
        tickingSoundid?.let { soundId ->
            soundPool.play(soundId, 1F, 1F, 0, -1, 1.0f)
        }
    }
    private fun stopCountDown(){
        currentCountDownTimer?.cancel()
        currentCountDownTimer=null  //동작중이 아닐경우
        soundPool.autoPause()
    }

    @SuppressLint("SetTextI18n")
    private fun updateRemainTimes(p0: Long){

        val remainSeconds = p0 / 1000

        remainMinutesTextView.text = "%02d:".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar (p0: Long){
        seekBar.progress = (p0 / 1000 / 60).toInt()
    }




}




