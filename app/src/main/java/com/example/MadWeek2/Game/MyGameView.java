package com.example.MadWeek2.Game;

import android.view.MotionEvent;
import android.view.SurfaceView;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.WindowManager;


public class MyGameView extends SurfaceView implements Callback {

    GameThread mThread;                         // GameThread
    SurfaceHolder mHolder;                        // SurfaceHolder
    Context mContext;                              // Context

    public MyGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mHolder = holder;                                            // holder와 Context 보존
        mContext = context;
        mThread = new GameThread(holder, context);      // Thread 만들기

        InitGame();                                                   // 게임 초기화
        MakeStage();                                               // 스테이지 만들기
        setFocusable(true);     // View가 Focus받기
    }

    private void InitGame() {


    }

    public static void MakeStage() {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread.start();                           // Thread 실행
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int format, int width, int height) {


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        StopGame();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {

            }
            default: return false;
        }
    }

    public void StopGame() {
        mThread.StopThread();
    }

    public void PauseGame() {
        mThread.PauseNResume(true);
    }

    public void ResumeGame() {
        mThread.PauseNResume(false);
    }

    public void RestartGame() {
        mThread.StopThread();  // 스레드 중지

        // 현재의 스레드를 비우고 다시 생성
        mThread = null;
        mThread = new GameThread(mHolder, mContext);
        mThread.start();
    }

    class GameThread extends Thread {
        boolean canRun = true;   // Thread 제어용
        boolean isWait = false;   // Thread 제어용

        public GameThread(SurfaceHolder holder, Context context) {



        }

        public void MoveAll() {



        }

        public void DrawAll(Canvas canvas) {



        }

        public void run() {
            Canvas canvas = null;
            while (canRun) {
                canvas = mHolder.lockCanvas();
                try {
                    synchronized (mHolder) {
                        MoveAll();                                 // 모든 캐릭터 이동
                        DrawAll(canvas);                        // Canvas에 그리기
                    } // sync
                } finally {
                    if (canvas != null)
                        mHolder.unlockCanvasAndPost(canvas);
                } // try

                // 스레드 일시 정지 - 추가된 부분
                synchronized (this) {
                    if (isWait)             // Pause 모드이면
                        try {
                            wait();       // 스레드 대기
                        } catch (Exception e) {
                            // nothing
                        }
                } // sync

            } // while
        } // run

        public void StopThread() {
            canRun = false;
            synchronized (this) {
                this.notify();                   // 스레드에 통지
            }
        }

        public void PauseNResume(boolean value) {
            isWait = value;
            synchronized (this) {
                this.notify();               // 스레드에 통지
            }
        }

    } // GameThread 끝

} // SurfaceView