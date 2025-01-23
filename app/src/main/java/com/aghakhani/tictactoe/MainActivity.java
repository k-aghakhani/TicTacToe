package com.aghakhani.tictactoe;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
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
    private Dialog resultDialog;

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

        tvStatus = findViewById(R.id.tv_status);

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
    }

    public void cellClicked(View view) {
        Button button = (Button) view;
        if (!button.getText().toString().equals("")) {
            return;
        }

        if (playerXTurn) {
            button.setText("X");
        } else {
            button.setText("O");
        }

        roundCount++;

        if (checkForWin()) {
            showResultDialog(playerXTurn ? "Player X Wins!" : "Player O Wins!");
            disableButtons();
        } else if (roundCount == 9) {
            showResultDialog("Draw!");
        } else {
            playerXTurn = !playerXTurn;
            if (playerXTurn) {
                tvStatus.setText("Player X's Turn");
            } else {
                tvStatus.setText("Player O's Turn");
            }
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
        tvStatus.setText("Player X's Turn");
    }
}