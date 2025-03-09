package com.aghakhani.tictactoe;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Button[][] buttons = new Button[3][3];
    private boolean playerXTurn = true;
    private int roundCount = 0;
    private TextView tvStatus;
    private TextView tvTimer;
    private Dialog resultDialog;
    private Dialog timeUpDialog;
    private MediaPlayer mediaPlayer;
    private MediaPlayer timeUpSound; // Sound for time up
    private MediaPlayer backgroundMusic; // Background music
    private CountDownTimer timer;

    // Emoji constants for players
    private static final String PLAYER_X_EMOJI = "☀️";
    private static final String PLAYER_O_EMOJI = "🌙";
    private static final long TIMER_DURATION = 10000; // 10 seconds in milliseconds
    private static final long TIMER_INTERVAL = 1000; // Update every second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize media players
        mediaPlayer = MediaPlayer.create(this, R.raw.win_sound);
        timeUpSound = MediaPlayer.create(this, R.raw.time_up_sound); // Sound for time up
        backgroundMusic = MediaPlayer.create(this, R.raw.background_music); // Background music
        if (backgroundMusic != null) {
            backgroundMusic.setLooping(true); // Set to loop during the game
            backgroundMusic.setVolume(0.3f, 0.3f); // Lower volume to make it calm
            backgroundMusic.start(); // Start background music
        }

        tvStatus = findViewById(R.id.tv_status);
        tvTimer = findViewById(R.id.tv_timer);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "btn_" + (i * 3 + j);
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
            }
        }

        // Initialize the result dialog
        resultDialog = new Dialog(this);
        resultDialog.setContentView(R.layout.dialog_game_result);
        resultDialog.setCancelable(false);

        Button btnResetDialog = resultDialog.findViewById(R.id.btn_reset_dialog);
        btnResetDialog.setOnClickListener(v -> {
            resetGame(null);
            resultDialog.dismiss();
        });

        // Initialize the time up dialog
        timeUpDialog = new Dialog(this);
        timeUpDialog.setContentView(R.layout.dialog_time_up);
        timeUpDialog.setCancelable(false);

        Button btnCloseDialog = timeUpDialog.findViewById(R.id.btn_close_dialog);
        btnCloseDialog.setOnClickListener(v -> timeUpDialog.dismiss());

        startTimer(); // Start the timer when activity starts
    }

    private void startTimer() {
        // Cancel any existing timer
        if (timer != null) {
            timer.cancel();
        }

        timer = new CountDownTimer(TIMER_DURATION, TIMER_INTERVAL) {
            int timeLeft = 10; // Initial time in seconds

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft--;
                tvTimer.setText("Time: " + timeLeft + "s");
            }

            @Override
            public void onFinish() {
                // Only proceed if the game is not over (win or draw)
                if (roundCount < 9 && !checkForWin()) {
                    // Play time up sound
                    if (timeUpSound != null) {
                        timeUpSound.start();
                    }

                    // Show time up dialog
                    TextView tvTimeUpMessage = timeUpDialog.findViewById(R.id.tv_time_up_message);
                    tvTimeUpMessage.setText("Time's up! Turn switched to Player " + (playerXTurn ? PLAYER_O_EMOJI : PLAYER_X_EMOJI));
                    timeUpDialog.show();

                    // Switch turn
                    playerXTurn = !playerXTurn;
                    tvStatus.setText("Player " + (playerXTurn ? PLAYER_X_EMOJI : PLAYER_O_EMOJI) + "'s Turn");
                    tvTimer.setText("Time: 10s");
                    startTimer(); // Restart timer for the next player
                }
            }
        }.start();
    }

    public void cellClicked(View view) {
        Button button = (Button) view;
        if (!button.getText().toString().equals("")) {
            return;
        }

        // Load and start scale animation
        Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        button.startAnimation(scaleAnimation);

        // Set emoji based on player's turn
        if (playerXTurn) {
            button.setText(PLAYER_X_EMOJI);
        } else {
            button.setText(PLAYER_O_EMOJI);
        }

        roundCount++;

        // Cancel and restart timer after a move
        if (timer != null) {
            timer.cancel();
        }
        startTimer();

        if (checkForWin()) {
            if (timer != null) timer.cancel(); // Stop timer on win
            if (backgroundMusic != null) backgroundMusic.pause(); // Pause background music on win
            mediaPlayer.start();
            showResultDialog(playerXTurn ? "Player " + PLAYER_X_EMOJI + " Wins!" : "Player " + PLAYER_O_EMOJI + " Wins!");
            disableButtons();
        } else if (roundCount == 9) {
            if (timer != null) timer.cancel(); // Stop timer on draw
            if (backgroundMusic != null) backgroundMusic.pause(); // Pause background music on draw
            showResultDialog("Draw!");
        } else {
            playerXTurn = !playerXTurn;
            tvStatus.setText("Player " + (playerXTurn ? PLAYER_X_EMOJI : PLAYER_O_EMOJI) + "'s Turn");
        }
    }

    private void showResultDialog(String result) {
        TextView tvResult = resultDialog.findViewById(R.id.tv_result);
        tvResult.setText(result);
        resultDialog.show();
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) {
                return true;
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) {
            return true;
        }

        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")) {
            return true;
        }

        return false;
    }

    private void disableButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    public void resetGame(View view) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
            }
        }

        playerXTurn = true;
        roundCount = 0;
        tvStatus.setText("Player " + PLAYER_X_EMOJI + "'s Turn");
        if (timer != null) timer.cancel(); // Cancel any running timer
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start(); // Restart background music on reset
        }
        startTimer(); // Start new timer
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (timeUpSound != null) {
            timeUpSound.release();
            timeUpSound = null;
        }
        if (backgroundMusic != null) {
            backgroundMusic.release();
            backgroundMusic = null;
        }
        if (timer != null) {
            timer.cancel();
        }
    }
}