package me.hb.huarongdao01;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class GameActivity extends Activity {

    final static String titleFormatString  = "第%d关";

    AlertDialog.Builder dialog;
    Toast firstMissionToast;
    Toast lastMissionToast;

    final static String initTimeText = "00:00";

    RelativeLayout gameAreaLayout;
    int buttonUnit;
    int[][] hasButton;
    ArrayList<MyButton> buttons;
    private static final ButtonShape[] shapes;

    long baseTimer;

    MyButton mainButton;
    int currentGameId;

    TextView stepView;
    TextView timeText;
    int step;

    boolean gameSuccess;

    static {
        shapes = new ButtonShape[4];
        shapes[0] = ButtonShape.vertical;
        shapes[1] = ButtonShape.horizontal;
        shapes[2] = ButtonShape.point;
        shapes[3] = ButtonShape.block;
    }

    Timer timer;
    Handler startTimeHandler;

    int totalMissionNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initDialogAndToast();
        initTimer();

        hasButton = new int[5][4];
        relativeLayoutInit();

        Intent intent = getIntent();
        currentGameId = intent.getIntExtra(ChoosingActivity.missionString, 0);

        loadGameLayout(currentGameId);
    }

    void hideBottomOfGameAreaLayout() {
        View view;

        view = new Button(this);
        view.setId(View.generateViewId());
        view.setBackgroundColor(0xfffafafa);
        gameAreaLayout.addView(view);
        view.setX(0);
        view.setY(5 * buttonUnit);
        view.getLayoutParams().width = buttonUnit;
        view.getLayoutParams().height = buttonUnit / 2;

        view = new Button(this);
        view.setId(View.generateViewId());
        gameAreaLayout.addView(view);
        view.getLayoutParams().width = buttonUnit;
        view.getLayoutParams().height = buttonUnit / 2;
        view.setX(3 * buttonUnit);
        view.setY(5 * buttonUnit);
        view.setBackgroundColor(0xfffafafa);
    }

    protected void loadGameLayout(int gameId) {
        gameSuccess = false;
        currentGameId = gameId;
        MyButton.verticalCount = 0;
        MyButton.horizontalCount = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 4; j++) {
                hasButton[i][j] = 0;
            }
        }
        TypedArray a = getResources().obtainTypedArray(R.array.missions);
        int[] xmlArray = getResources().getIntArray(a.getResourceId(currentGameId, 0));
        int[][] gameIntLayout = new int[xmlArray.length / 3][3];
        for (int i = 0; i < xmlArray.length / 3; i++) {
            System.arraycopy(xmlArray, i * 3, gameIntLayout[i], 0, 3);
        }
        a.recycle();
        if (buttons == null) {
            buttons = new ArrayList<>();
        } else {
            buttons.clear();
            gameAreaLayout.removeAllViews();
        }
        for (int[] buttonInfo : gameIntLayout) {
            addNewGameButton(buttonInfo[0], buttonInfo[1], buttonInfo[2]);
        }
        TextView gameTitle = findViewById(R.id.game_title);
        gameTitle.setText(String.format(Locale.SIMPLIFIED_CHINESE, titleFormatString, currentGameId + 1));
        startTimer();
        hideBottomOfGameAreaLayout();
    }

    protected void addNewGameButton(int row, int column, int buttonShapeInt) {
        MyButton button = new MyButton(this);
        buttons.add(button);
        gameAreaLayout.addView(button);
        button.setVisibility(View.VISIBLE);
        button.setGameLocation(row, column);
        int buttonShapeHeight = shapes[buttonShapeInt].height;
        int buttonShapeWidth = shapes[buttonShapeInt].width;
        button.setGameShape(shapes[buttonShapeInt]);
        for (int i = 0; i < buttonShapeHeight; i++) {
            for (int j = 0; j < buttonShapeWidth; j++) {
                button.coordinates.add(new Coordinate(row + i, column + j));
            }
        }
        for (Coordinate coordinate : button.coordinates) {
            hasButton[coordinate.row][coordinate.column] = 1;
        }
        final MyButton finalButton = button;
        button.setOnTouchListener(new View.OnTouchListener() {
            int startX;
            int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        startY = (int) event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        moveButton(finalButton, (int) event.getX() - startX, (int) event.getY() - startY);
                        checkSuccess();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        if (shapes[buttonShapeInt] == ButtonShape.block) {
            mainButton = button;
        }
    }

    void checkSuccess() {
        if (mainButton.gameRow == 3 && mainButton.gameColumn == 1) {
            gameSuccess = true;
            mainButton.setY((float) buttonUnit * 7 / 2);
            dialog.show();
        }
    }

    protected void moveButton(MyButton button, final int deltaX, final int deltaY) {
        int row = button.gameRow;
        int column = button.gameColumn;
        int deltaRow = 0;
        int deltaColumn = 0;
        boolean canMove = true;
        if (Math.max(Math.abs(deltaX), Math.abs(deltaY)) <= 10) {
            return;
        }
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            if (deltaX > 0) {
                deltaColumn = 1;
            } else {
                deltaColumn = -1;
            }
        } else {
            if (deltaY > 0) {
                deltaRow = 1;
            } else {
                deltaRow = -1;
            }
        }
        for (Coordinate coordinate : button.coordinates) {
            hasButton[coordinate.row][coordinate.column] = 0;
        }
        for (Coordinate coordinate : button.coordinates) {
            if (coordinate.row + deltaRow < 0 || coordinate.row + deltaRow >= 5 || coordinate.column + deltaColumn < 0 || coordinate.column + deltaColumn >= 4) {
                canMove = false;
                break;
            }
            if (hasButton[coordinate.row + deltaRow][coordinate.column + deltaColumn] == 1) {
                canMove = false;
                break;
            }
        }
        if (!canMove) {
            for (Coordinate coordinate : button.coordinates) {
                hasButton[coordinate.row][coordinate.column] = 1;
            }
            return;
        }
        for (Coordinate coordinate : button.coordinates) {
            coordinate.row += deltaRow;
            coordinate.column += deltaColumn;
            hasButton[coordinate.row][coordinate.column] = 1;
        }
        button.setGameLocation(row + deltaRow, column + deltaColumn);
        if(!gameSuccess) {
            stepView.setText(String.format(Locale.SIMPLIFIED_CHINESE, "%d步", ++step));
        }
    }

    protected void relativeLayoutInit() {
//        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
//        DisplayMetrics dm = new DisplayMetrics();
//        wm.getDefaultDisplay().getMetrics(dm);
//        int width = dm.widthPixels;         // 屏幕宽度（像素）
//        int height = dm.heightPixels;       // 屏幕高度（像素）
//        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
//        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
//        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
//        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
//        int screenHeight = (int) (height / density);// 屏幕高度(dp)

//        Log.d("Size", "屏幕宽度（像素）：" + width);
//        Log.d("Size", "屏幕高度（像素）：" + height);
//        Log.d("Size", "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
//        Log.d("Size", "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
//        Log.d("Size", "屏幕宽度（dp）：" + screenWidth);
//        Log.d("Size", "屏幕高度（dp）：" + screenHeight);
        gameAreaLayout = findViewById(R.id.gameArea);
        gameAreaLayout.measure(0, 0);

        int gameAreaLayoutWidth = gameAreaLayout.getMeasuredWidth();
        int gameAreaLayoutHeight = gameAreaLayout.getMeasuredHeight();
        Log.d("measure", "" + gameAreaLayoutHeight + " " + gameAreaLayoutWidth);
        buttonUnit = gameAreaLayout.getMeasuredWidth() / 4;

        gameAreaLayout.getLayoutParams().width = buttonUnit * 4;
        gameAreaLayout.getLayoutParams().height = buttonUnit / 2 * 11;
        gameAreaLayout.setBackgroundColor(0xffcf9e64);

        ConstraintLayout constraintLayout = findViewById(R.id.gameConstraintLayout);
        constraintLayout.setBackgroundColor(0xfffafafa);

        MyButton.unit = buttonUnit;
        Log.d("Size", "" + gameAreaLayoutWidth + " " + gameAreaLayoutHeight);
    }

    protected void startTimer() {
        this.baseTimer = SystemClock.elapsedRealtime();
        this.step = 0;
        timeText.setText(initTimeText);
        stepView.setText(String.format(Locale.SIMPLIFIED_CHINESE, "%d步", step));
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int time = (int) ((SystemClock.elapsedRealtime() - baseTimer) / 1000);
                String m = new DecimalFormat("00").format(time / 60);
                String s = new DecimalFormat("00").format(time % 60);
                Message msg = new Message();
                msg.obj = m + ":" + s;
                startTimeHandler.sendMessage(msg);
            }
        }, 0, 1000L);
    }

    void initDialogAndToast() {
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle("过关啦~");
        dialog.setMessage("是否下一关");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Button button = findViewById(R.id.button_to_next_mission);
                button.callOnClick();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("Success", "success");
            }
        });
        dialog.setNeutralButton("选择其他关卡", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        firstMissionToast = Toast.makeText(this, "已经是第一关啦~", Toast.LENGTH_SHORT);
        lastMissionToast = Toast.makeText(this, "已经是最后一关啦~", Toast.LENGTH_SHORT);
    }

    void initTimer() {
        totalMissionNumber = getResources().getInteger(R.integer.missionNumber);
        currentGameId = 0;
        stepView = findViewById(R.id.step_text);
        timeText = findViewById(R.id.time_text);
        gameSuccess = false;
        startTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (timeText != null) {
                    if (!gameSuccess) {
                        timeText.setText((String) msg.obj);
                    }
                }
            }
        };
        timer = new Timer("计时器");
    }

    public void restart(View view) {
        switch (view.getId()) {
            case R.id.button_to_last_mission:
                if (currentGameId > 0) {
                    loadGameLayout(--currentGameId);
                } else {
                    firstMissionToast.show();
                }
                break;
            case R.id.button_to_next_mission:
                if (currentGameId < totalMissionNumber - 1) {
                    loadGameLayout(++currentGameId);
                } else {
                    lastMissionToast.show();
                }
                break;
            case R.id.button_restart:
            default:
                loadGameLayout(currentGameId);
                break;
        }
    }
}
