package com.quester.demo.tp;

import com.quester.demo.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;
import android.widget.Toast;

public class TouchpanelActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(new MyView(this));
		Toast.makeText(this, R.string.t_notice, Toast.LENGTH_SHORT).show();
	}
	
	public class MyView extends SurfaceView implements Callback {

        Paint mPaint[], mTextPaint[], paint;
        SurfaceHolder mSurfaceHolder = null;
        Canvas mCanvas = null;

        public MyView(Context context) {
            super(context);
            this.setFocusable(true);
            this.setFocusableInTouchMode(true);
            mSurfaceHolder = this.getHolder();
            mSurfaceHolder.addCallback(this);
            mCanvas = new Canvas();
            mPaint = new Paint[5];
        	mTextPaint = new Paint[5];
        	for (int i=0; i<5; i++) {
        		mPaint[i] = new Paint();
        		mTextPaint[i] = new Paint();
        		mPaint[i].setStyle(Paint.Style.STROKE);
        		mPaint[i].setStrokeWidth(5);
        		mPaint[i].setAntiAlias(true);
        		mTextPaint[i].setAntiAlias(true);
        	}
        	mPaint[0].setColor(Color.RED);
        	mPaint[1].setColor(Color.GREEN);
        	mPaint[2].setColor(Color.BLUE);
        	mPaint[3].setColor(Color.WHITE);
        	mPaint[4].setColor(Color.YELLOW);
        	mTextPaint[0].setColor(Color.RED);
        	mTextPaint[1].setColor(Color.GREEN);
        	mTextPaint[2].setColor(Color.BLUE);
        	mTextPaint[3].setColor(Color.WHITE);
        	mTextPaint[4].setColor(Color.YELLOW);
        	
        	paint = new Paint();
        	paint.setColor(Color.WHITE);
        	paint.setTextSize(20);
        	paint.setAntiAlias(true);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int action = event.getAction();
            boolean reset = false;
            switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                reset = true;
                break;
            }

            synchronized (mSurfaceHolder) {
                mCanvas = mSurfaceHolder.lockCanvas();
                mCanvas.drawColor(Color.BLACK);
                mCanvas.drawText(getString(R.string.t_notice), 10, 30, paint);
                if (!reset) {
                    int pointCount = event.getPointerCount();
                    for (int i = 0; i < pointCount; i++) {
                    	drawCircle(event.getX(i), event.getY(i), mPaint[i], mTextPaint[i]);
                    }
                } 
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
            
            return true;
        }
        
        private void drawCircle(float x, float y, Paint paint1, Paint paint2) {
        	mCanvas.drawCircle(x, y, 40, paint1);
    		mCanvas.drawText("Xc: " + x, x-40, y+55, paint2);
            mCanvas.drawText("Yc: " + y, x-40, y+65, paint2);
        }

		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			
		}

		public void surfaceCreated(SurfaceHolder arg0) {
			
		}

		public void surfaceDestroyed(SurfaceHolder arg0) {
			
		}
    }
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		/*
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Dialog dialog = new AlertDialog.Builder(this).setTitle(getString(R.string.t_prompt))
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(getString(R.string.t_confirm))
					.setPositiveButton(getString(R.string.t_passed), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							mEditor.putString(Utils.TOUCHPANEL, Utils.PASSED);
					        mEditor.commit();
							dialog.dismiss();
							TestTouch.this.finish();
						}
					}).setNegativeButton(getString(R.string.t_failed), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							mEditor.putString(Utils.TOUCHPANEL, Utils.FAILED);
					        mEditor.commit();
							dialog.dismiss();
							TestTouch.this.finish();
						}
					}).create();
			dialog.show();
		}*/
		if(keyCode == KeyEvent.KEYCODE_BACK){
			TouchpanelActivity.this.finish();
		}
		return true;
	}
}

