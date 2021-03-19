package org.pondar.pacmankotlin

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), OnClickListener {

    //reference to the game class.
    private var game: Game? = null

    private var myTimer: Timer = Timer()

    private var myDownTimer: Timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //makes sure it always runs in portrait mode
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        playButton.setOnClickListener(this)
        pauseButton.setOnClickListener(this)
        resetButton.setOnClickListener(this)

        game?.running = true

        myTimer.schedule(object : TimerTask() {
            override fun run() {
                timerMethod()
            }
        }, 20, 200)

        //this timer for countdown
        myDownTimer.schedule(object : TimerTask() {
            override fun run() {
                downTimerMethod()
            }

        }, 0, 1000) //0 indicates we start now, 1000 is for each second

        game = Game(this, pointsView, levelsView, totalPointsView)

        //intialize the game view clas and game class
        game?.setGameView(gameView)
        gameView.setGame(game)
        game?.newGame()

        moveLeft.setOnClickListener {
            game?.direction = 1
        }
        moveRight.setOnClickListener {
            game?.direction = 2
        }
        moveUp.setOnClickListener {
            game?.direction = 3
        }
        moveDown.setOnClickListener {
            game?.direction = 4
        }

    }

    override fun onStop() {
        super.onStop()
        myTimer.cancel()
        myDownTimer.cancel()
    }

    private fun timerMethod() {
        this.runOnUiThread(timerTick)
    }

    private fun downTimerMethod() {
        this.runOnUiThread(downTimerTick)
    }

    private val timerTick = Runnable {
        if (game?.running == true) {
            game!!.counter++
            textView.text = getString(R.string.timerValue, game?.counter)
            if (game?.direction == 1) {
                game?.movePacmanLeft(30)
                game?.moveEnemyRight(40)
            } else if (game?.direction == 2) {
                game?.movePacmanRight(30)
                game?.moveEnemyLeft(40)
            } else if (game?.direction == 3) {
                game?.movePacmanUp(30)
                game?.moveEnemyDown(40)
            } else if (game?.direction == 4) {
                game?.movePacmanDown(30)
                game?.moveEnemyUp(40)
            }
        }
    }

    private val downTimerTick = Runnable {
        if (game?.running == true) {
            game!!.downCounter--
            textView2.text = getString(R.string.downTimerValue, game?.downCounter)
            if (game?.downCounter == 0) {

                /*GlobalScope.launch {
                    //Do stuff
                    delay(1000)*/

                Toast.makeText(this, "GAME OVER!!", Toast.LENGTH_SHORT).show()
                game?.newGame()
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_settings) {
            Toast.makeText(this, "settings clicked", Toast.LENGTH_LONG).show()
            return true
        } else if (id == R.id.action_newGame) {
            Toast.makeText(this, "New Game clicked", Toast.LENGTH_LONG).show()
            game?.newGame()
            textView.text = getString(R.string.timerValue, game?.counter)
            textView2.text = getString(R.string.downTimerValue, game?.downCounter)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.playButton) {
            game?.running = true
        } else if (v?.id == R.id.pauseButton) {
            game?.running = false
        } else if (v?.id == R.id.resetButton) {
            game?.newLevel() //you should call the newGame method instead of this
            textView.text = getString(R.string.timerValue, game?.counter)
            textView2.text = getString(R.string.downTimerValue, game?.downCounter)
            Toast.makeText(this, "Time is Reset", Toast.LENGTH_LONG).show()
        }
    }
}
