package org.pondar.pacmankotlin

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


/**
 *
 * This class should contain all your game logic
 */

class Game(private var context: Context, view: TextView, view2: TextView, view3: TextView) {

    private var pointsView: TextView = view
    private var points = 0

    //bitmap of the pacman
    var pacBitmap: Bitmap
    var pacx = 0
    var pacy = 0

    //bitmap of coin
    var coinBitmap: Bitmap

    //bitmap of enemy
    var enemyBitmap: Bitmap
    var enemy2Bitmap: Bitmap

    /* var pacCenterX = 78
     var pacCenterY = 58

     var coinCenterX = 36
     var coinCenterY = 28*/

    var direction = 0
    var running = false

    var downCounter: Int = 30
    var counter: Int = 0

    //did we initialize the coins?
    var coinsInitialized = false

    //the list of goldcoins - initially empty
    var coins = ArrayList<GoldCoin>()

    // initialize the enemies
    var enemiesInitialized = false

    //the list of enemies
    var enemies = ArrayList<Enemy>()

    //levels
    var level = 0
    var levelsView: TextView = view2

    //total points
    var totalCoins = 0
    var totalPointsView: TextView = view3

    //a reference to the gameview
    private var gameView: GameView? = null
    private var h = 0
    private var w = 0 //height and width of screen

    //The init code is called when we create a new Game class.
    //it's a good place to initialize our images.
    init {
        pacBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pacman)
        coinBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.goldcoin)
        enemyBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bandit1)
        enemy2Bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.bandit2)
    }

    fun setGameView(view: GameView) {
        this.gameView = view
    }

    //TODO initialize goldcoins also here
    fun initializeGoldcoins() {
        var minX = 0
        var maxX = w - coinBitmap.width

        var minY = 0
        var maxY = h - coinBitmap.width

        val random = Random()

        for (i in 0..11) {
            var randomX: Int = random.nextInt(maxX - minX + 1) + minX
            var randomY: Int = random.nextInt(maxY - minY + 1) + minY
            coins.add(GoldCoin(randomX, randomY, false))
        }
        //DO Stuff to initialize the array list with some coins.

        coinsInitialized = true
    }

    //Initializing enemies
    fun initializeEnemies() {
        var minX = 0
        var maxX = w - enemyBitmap.width

        var minY = 0
        var maxY = h - enemyBitmap.width

        val random = Random()

        for (i in 0..level) {
            var randomX: Int = random.nextInt(maxX - minX + 1) + minX
            var randomY: Int = random.nextInt(maxY - minY + 1) + minY
            enemies.add(Enemy(randomX, randomY, false))
        }
        enemiesInitialized = true
    }

    fun newLevel() {
        pacx = 50
        pacy = 400 //just some starting coordinates - you can change this.

        enemies.clear()
        enemiesInitialized = false

        points = 0
        pointsView.text = "${context.resources.getString(R.string.points)} $points"

        levelsView.text = "${"Level:"} ${level + 1}"

        totalPointsView.text = "${"Total Points:"} $totalCoins"

        coins.clear()
        coinsInitialized = false

        counter = 0
        downCounter = 30

        gameView?.invalidate() //redraw screen
        running = false
    }

    fun newGame() {
        totalCoins = 0
        level = 0
        newLevel()
    }

    fun setSize(h: Int, w: Int) {
        this.h = h
        this.w = w
    }

    fun movePacmanLeft(pixels: Int) {
        if (pacx > 0) {
            pacx = pacx - pixels
            doCollisionCheck()
            gameView!!.invalidate()
        }
    }

    fun movePacmanRight(pixels: Int) {
        //still within our boundaries?
        if (pacx + pixels + pacBitmap.width < w) {
            pacx = pacx + pixels
            doCollisionCheck()
            gameView!!.invalidate()
        }
    }

    fun movePacmanUp(pixels: Int) {
        if (pacy > 0) {
            pacy = pacy - pixels
            doCollisionCheck()
            gameView!!.invalidate()
        }
    }

    fun movePacmanDown(pixels: Int) {
        if (pacy + pixels + pacBitmap.height < h) {
            pacy = pacy + pixels
            doCollisionCheck()
            gameView!!.invalidate()
        }
    }

    //direction of enemy
    fun moveEnemyLeft(pixels: Int) {
        for (enemy in enemies) {
            Log.d("enemy", "enemies")
            if (enemy.enemyX > 0) {
                enemy.enemyX -= pixels
            }
        }
        doCollisionCheck2()
        gameView!!.invalidate()
    }

    fun moveEnemyRight(pixels: Int) {
        for (enemy in enemies) {
            Log.d("enemy", "enemies")
            if (enemy.enemyX + pixels + enemyBitmap.width < w) {
                enemy.enemyX += pixels
            }
        }
        doCollisionCheck2()
        gameView!!.invalidate()
    }

    fun moveEnemyUp(pixels: Int) {
        for (enemy in enemies) {
            Log.d("enemy", "enemies")
            if (enemy.enemyY > 0) {
                enemy.enemyY -= pixels
            }
        }
        doCollisionCheck2()
        gameView!!.invalidate()
    }

    fun moveEnemyDown(pixels: Int) {
        for (enemy in enemies) {
            if (enemy.enemyY + pixels + enemyBitmap.height < h) {
                enemy.enemyY += pixels
            }
        }
        doCollisionCheck2()
        gameView!!.invalidate()
    }

    //TODO check if the pacman touches a gold coin
    //and if yes, then update the neccesseary data
    //for the gold coins and the points
    //so you need to go through the arraylist of goldcoins and
    //check each of them for a collision with the pacman

    /* fun distance(x1: Int, y1: Int, x2: Int, y2: Int) : Float {
         if (pacCenterX + coinCenterX > 0) {
             pacCenterX = pacx + pacBitmap.width / 2
             coinCenterX = pacx + coinBitmap.width/2
             pacCenterY = pacy + pacBitmap.height/2
             coinCenterY = pacy + coinBitmap.height/2
         }
     }*/

    fun doCollisionCheck() {
        for (coin in coins) {
            if (pacx + pacBitmap.width >= coin.coinX && pacx <= coin.coinX + coinBitmap.width
                    && pacy + pacBitmap.height >= coin.coinY && pacy <= coin.coinY + coinBitmap.height && !coin.taken) {
                Toast.makeText(this.context, "Haps!", Toast.LENGTH_SHORT).show()
                coin.taken = true
                points += 1
                totalCoins += 1
                pointsView.text = "${context.resources.getString(R.string.points)} $points"
                totalPointsView.text = "${"Total Points:"} $totalCoins"
            }
        }
        if (points == coins.size) {
            Toast.makeText(this.context, "You Got them all!", Toast.LENGTH_LONG).show()
            level += 1
            levelsView.text = "${context.resources.getString(R.string.levels)} ${level}+1"
            Log.d("returnLevel", level.toString())
            return newLevel()
        }
    }

    //Enemy collisionCheck
    fun doCollisionCheck2() {
        var playerDead = false
        for (enemy in enemies) {
            if (pacx + pacBitmap.width >= enemy.enemyX && pacx <= enemy.enemyX + enemyBitmap.width
                    && pacy + pacBitmap.height >= enemy.enemyY && pacy <= enemy.enemyY + enemyBitmap.height && !enemy.alive) {
                Toast.makeText(this.context, "Got you!", Toast.LENGTH_SHORT).show()
                enemy.alive = true
                playerDead = true
            }
        }
        if (playerDead == true) {
            return newGame()
        }
    }
}