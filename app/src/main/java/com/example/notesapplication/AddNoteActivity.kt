package com.example.notesapplication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.ColorUtils
import java.util.Date


class AddNoteActivity:AppCompatActivity() {
    private lateinit var addNoteBackground:RelativeLayout
    private lateinit var addNoteWindowBg:LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        addNoteBackground = findViewById<RelativeLayout>(R.id.add_note_background)
        addNoteWindowBg = findViewById<LinearLayout>(R.id.add_note_window_bg)

        setActivityStyle()
        val noteDateAdded = intent.getSerializableExtra("note_date_added") as? Date
        val noteTextToEdit = intent.getStringExtra("note_text")

        val addNoteText = findViewById<TextView>(R.id.add_note_text)
        addNoteText.text= noteTextToEdit ?:""

        val addNoteButton = findViewById<Button>(R.id.add_note_button)
        addNoteButton.setOnClickListener{
            val data = Intent()
            data.putExtra("note_date_added",noteDateAdded)
            data.putExtra("note_text",addNoteText.text.toString())
            setResult(Activity.RESULT_OK,data)
            onBackPressed()
        }
    }

    private fun setActivityStyle() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        this.window.statusBarColor= Color.TRANSPARENT
        val winParams = this.window.attributes
        winParams.flags = winParams.flags and WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS.inv()
        this.window.attributes = winParams

        val alpha = 100
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"),alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            addNoteBackground.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()

        addNoteWindowBg.alpha=0f
        addNoteWindowBg.animate().alpha(1f).setDuration(500).setInterpolator(DecelerateInterpolator()).start()

        addNoteBackground.setOnClickListener{onBackPressed()}
        addNoteWindowBg.setOnClickListener{}
    }
    @Suppress
    override fun onBackPressed() {
        super.onBackPressed()
        val alpha = 100
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"),alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            addNoteBackground.setBackgroundColor(animator.animatedValue as Int)
        }

        addNoteWindowBg.animate().alpha(0f).setDuration(500).setInterpolator(DecelerateInterpolator()).start()
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }
}