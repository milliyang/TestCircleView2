package com.example.testcircleview2;

import java.security.spec.MGF1ParameterSpec;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;



/**
 * @author Administrator
 *
 */
public class CircleView2 extends FrameLayout implements View.OnTouchListener, GestureDetector.OnGestureListener {
	///*

	/*
	private class CircleGestureDetector extends GestureDetector {

		public CircleGestureDetector(Context context, OnGestureListener listener) {
			super(context, listener);
			// TODO Auto-generated constructor stub
		}

	}*/

	private class TouchAsyncTask extends AsyncTask<Integer, Integer, Integer> {

		public CircleView2 mCircle;
		public int  mTimeCounter ;
		private int mSleepTime;
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			for( int i=0; i < mTimeCounter; i+=50){
				try{
					Thread.sleep(mSleepTime);
					mSleepTime +=1;
					
					this.publishProgress(null);
					}
				catch(InterruptedException ee){
					Log.i(MOTION_TASK_TAG, ee.toString());
				}
			}
			return null;
		}

		public TouchAsyncTask(CircleView2 mCircle, int Times) {
			super();
			this.mCircle = mCircle;
			this.mSleepTime = 15;
			this.mTimeCounter = Times;
			if(this.mTimeCounter <= 0){
				this.mTimeCounter = 1000;
			}
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			//just in case for auto delete
			this.mCircle = null;
		}

		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onCancelled()
		 */
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			//just in case for auto delete
			this.mCircle = null;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			if(this.mCircle != null){
				if(this.mCircle.isMovingDown)
					this.mCircle.setmAngle(5);
				else
					this.mCircle.setmAngle(-5);
				
				this.mCircle.requestLayout();
			}
			super.onProgressUpdate(values);
		}
		
	}//*/
	
	
	
	
	private View[] mSubViews;
	private int mSubViewNum;
	private static LinkedList<ApplicationInfo> mApplications;
	private LayoutInflater mInflater;
	
	//private TouchAsyncTask mTouchTask;
	private Handler mCircleHandler;
	private GestureDetector mCircleGestureDetector;
	private static final int CIRCLE_HANDLER_MSG_ANI = 01;
	
	private boolean isMotionMoving;
	private boolean isMovingDown;
	private int mMotionMovingCounter;
	private float mLastMotionY;
	private long mDelaydMillis;   //default start 10ms
	private long mElapseMillis;   //default start 10ms
	//private long mOnScrollLastX;
	//private long mOnScrollLastY;
	//private long mLastDownEventTime;
	
	private static final int DEFAULT_DELAY_MILLIS = 5;
	private static final int DEFAULT_DELAY_MILLIS_ADDITION = 3;
	private static final int DEFAULT_DELAY_MILLIS_END_TIME = 1500;
	
	private static final int MAX_SUB_VIEWS = 20;
	private static final int MAX_APP_NUMS = 9;
	
	private static final String MOTION_EVENT_TAG = "motionEvent";
	private static final String MOTION_TASK_TAG = "motionTask";
	private static final String TEST_DIFF_ON_TOUCH_EVENT = "TEST_DIFF_ON_TOUCH_EVENT";
	
	private static int mTestCounter = 0;
	
	private int mWidth;
	private int mHeigth;
	private int mRaduis;
	private int mchildViewSize;
	private int mAngle = 90;
	private Point mOriginalPoint;

	/**
	 * @param context
	 */
	public CircleView2(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public CircleView2(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CircleView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mSubViews = new View[MAX_SUB_VIEWS];
		mSubViewNum = 0;
		//mOnScrollLastX = 0;
		//mOnScrollLastY = 0;
		otherInitializeWork();
		_testLayoutSubView(context);
		 //*/
	}
	
	private void otherInitializeWork(){
		mInflater = LayoutInflater.from(getContext());
		//setOnTouchListener(this);		//no need for another touchlistener
		mOriginalPoint = new Point(0, 150);
		mCircleGestureDetector = new GestureDetector(this);
		/*
		mCircleHandler = new Handler(new Handler.Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case CIRCLE_HANDLER_MSG_ANI:
					break;
				}
				return false;
			}
		});//*/
		
		mCircleHandler = new Handler(){
			/* (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch(msg.what){
				case CIRCLE_HANDLER_MSG_ANI:
					
					if(!isMotionMoving)
						break; //stop animate!!
					
					if(mElapseMillis > DEFAULT_DELAY_MILLIS_END_TIME){
						mDelaydMillis = 0;
						mElapseMillis = 0;
						//isMotionMoving = false;
					}else{
						if(isMovingDown){
							setmAngle(5);
						}else{
							setmAngle(-5);
						}
						requestLayout();
						mElapseMillis += mDelaydMillis;
						mCircleHandler.sendEmptyMessageDelayed(CIRCLE_HANDLER_MSG_ANI, mDelaydMillis);
						mDelaydMillis += DEFAULT_DELAY_MILLIS_ADDITION;
						
					}
					break;
					
				}
				super.handleMessage(msg);
			}};
		
		
		
	}
	
 	private void _testLayoutSubView(Context context){
		//*
 		int widthSpec = MeasureSpec.makeMeasureSpec(15, MeasureSpec.AT_MOST);
 		int heigthSpec = MeasureSpec.makeMeasureSpec(15, MeasureSpec.AT_MOST);
 		
		Button b = new Button(context);
		b.setText("yang1");
		b.setHeight(15);
		b.setWidth(15);
		this.addSubView( b );
		b.measure(widthSpec,heigthSpec);
		
		/*
		b = new Button(context);
		b.setText("yang2");
		b.measure(widthSpec,heigthSpec);
		this.addSubView( b );
		
		b = new Button(context);
		b.setText("yang3");
		b.measure(widthSpec,heigthSpec);
		this.addSubView( b );
		
		b = new Button(context);
		b.setText("yang4");
		b.measure(widthSpec,heigthSpec);
		this.addSubView( b );
		
		b = new Button(context);
		b.setText("yang5");
		b.measure(widthSpec,heigthSpec);
		this.addSubView( b );
		
		b = new Button(context);
		b.setText("yang6");
		b.measure(widthSpec,heigthSpec);
		this.addSubView( b );
		
		b = new Button(context);
		b.setText("yang7");
		b.measure(widthSpec,heigthSpec);
		this.addSubView( b );
		
		b = new Button(context);
		b.setText("yang8");
		b.measure(widthSpec,heigthSpec);
		this.addSubView( b );
		
		b = new Button(context);
		b.setText("yang9");
		b.measure(widthSpec,heigthSpec);
		this.addSubView( b );
		*/
	}
	

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int _width = MeasureSpec.getSize(widthMeasureSpec);
		int _heigth = (int) (_width * 1.5);
		int _childWidth = _width / 4;
		
		mWidth = _width;
		mHeigth = _heigth;
		mchildViewSize = _childWidth;
		
		/*
		for(int ii=0 ; ii < mSubViewNum; ii++){
			mSubViews[ii].measure(	MeasureSpec.makeMeasureSpec(_childWidth, MeasureSpec.AT_MOST), 
									MeasureSpec.makeMeasureSpec(_childWidth, MeasureSpec.AT_MOST));
		}
		//*/
		
		setMeasuredDimension( _width, _heigth);
		Log.d("onMeasure", "called");
	}
	
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		
		int _width = mWidth;
		int _heigth = mHeigth;
		int _mchildSize = mchildViewSize;
		
		//original point:
		if( mOriginalPoint == null ){
			mOriginalPoint.x = mWidth  / 4;
			mOriginalPoint.y = mHeigth / 2;
		}
		int _opX = mOriginalPoint.x ;
		int _opY = mOriginalPoint.y ;
		
		double _radius = mWidth / 2;
		
		//int _du = mAngle; 
		int _du = mAngle; 
		int _step = -30;
		for(int i=0; i < this.mSubViewNum; i++, _du +=_step){
			double yy = Math.sin( _du *Math.PI/180) * _radius;
			double xx = Math.cos( _du *Math.PI/180) * _radius;
			
			xx+= _opX;
			yy+= _opY;
			
			int l,t,r,b;
			l = (int) xx ;
			t = (int) yy ;
			//r = (int) xx + _mchildSize ;
			//b = (int) yy + _mchildSize ;
			
			r = (int) xx + mSubViews[i].getWidth() ;
			b = (int) yy + mSubViews[i].getHeight() ;
			
			mSubViews[i].layout( l, t, r, b);
			
			Log.d("onLayoutRect02:", String.valueOf(l)+","+String.valueOf(t)+","+String.valueOf(r)+","+String.valueOf(b) );
		}
		
		Log.d("onLayout", "called");
	}

	public boolean addSubView(View child) {
		// TODO Auto-generated method stub
		
		if(mSubViewNum >= MAX_SUB_VIEWS)
			return false;
		
		super.addView(child );
		mSubViews[mSubViewNum++] = child;
		
		Log.d("subViewNum:", String.valueOf(mSubViewNum));
		return true;
	}
	
    private View createApplicationIcon(ViewGroup group, ApplicationInfo info) {

        TextView textView = (TextView) mInflater.inflate(R.layout.favorite, group, false);
        //info.icon.setBounds(0, 0, mchildViewSize, mchildViewSize);
        // Why!!!
        info.icon.setBounds(0, 0, 42, 42);
        //info.icon.setBounds(0, 0, 100, 100);
        textView.setCompoundDrawables(null, info.icon, null, null);
        textView.setText(info.title);
        textView.setTag(info.intent);
        //textView.setOnClickListener(this);
        return textView;
    }

	/**
	 * @return the mApplications
	 */
	public LinkedList<ApplicationInfo> getmApplications() {
		return mApplications;
	}

	/**
	 * @param mApplications the mApplications to set
	 */
	public void setmApplications(LinkedList<ApplicationInfo> mApplications) {
		CircleView2.mApplications = mApplications;
		
		final int count = mApplications.size();
		
		for(int i = count - 1; i >= 0; i--){
			
			final ApplicationInfo info = mApplications.get(i);
            final View view = createApplicationIcon(this, info);

            //ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            //int widthSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
            //int heightSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
            int widthSpec = MeasureSpec.makeMeasureSpec(mchildViewSize, MeasureSpec.EXACTLY);
            int heightSpec = MeasureSpec.makeMeasureSpec(mchildViewSize, MeasureSpec.EXACTLY);
            view.measure(widthSpec, heightSpec);
            
            this.addSubView(view);
            
		}
	}
	
	/**
	 * @return the mOriginalPoint
	 */
	public Point getmOriginalPoint() {
		return mOriginalPoint;
	}

	/**
	 * @param mOriginalPoint the mOriginalPoint to set
	 */
	public void setmOriginalPoint(Point mOriginalPoint) {
		this.mOriginalPoint = mOriginalPoint;
	}
	
	

	/**
	 * @return the mAngle
	 */
	private int getmAngle() {
		return mAngle;
	}

	/**
	 * @param mAngle the mAngle to set
	 */
	private void setmAngle(int mAngle) {
		this.mAngle +=mAngle;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		this.mTestCounter +=1;
		this.mMotionMovingCounter +=1;
		
		Log.i("onTouch", String.valueOf(this.mAngle));
		//*
	     final int historySize = event.getHistorySize();
	     final int pointerCount = event.getPointerCount();
	    
	     Log.d(TEST_DIFF_ON_TOUCH_EVENT, "This is View.setOnTouchLister().onTouch!");
	     
	     /*
	     //use handler instead of AsyncTask
	     //so... deleted
	    switch( event.getActionMasked()){
	    case MotionEvent.ACTION_MOVE :
	    	this.mMotionMovingCounter ++;
	    	
	    	if(this.mMotionMovingCounter%3 == 0){
		    	if( mLastMotionY < event.getY()){
		    		isMovingDown = true;
		    	}else{
		    		isMovingDown = false;
		    	}
		    	mLastMotionY = event.getY();
	    		if(isMovingDown)
	    			this.mAngle +=15;
	    		else
	    			this.mAngle -=15;
				requestLayout();
	    	}
			isMotionMoving = true;
			
	    	Log.i(MOTION_EVENT_TAG, "Counter:" + mTestCounter + " historySize:" + historySize + " pointerCount:" + pointerCount );
	    	Log.i(MOTION_EVENT_TAG, "         " + "pointerCount:" + pointerCount );
	    	break;
	    case MotionEvent.ACTION_DOWN:
	    	if(mTouchTask != null){
	    		mTouchTask.cancel(true);
	    		mTouchTask = null;
	    	}
	    	isMotionMoving = false;
	    	
	    	break;
	    case MotionEvent.ACTION_UP:
	    	if(isMotionMoving){
		    	if(mTouchTask != null){
		    		mTouchTask.cancel(true);
		    		mTouchTask = null;
		    	}else{
		    		mTouchTask = new TouchAsyncTask(this, 1500);
		    		mTouchTask.execute(null);
		    	}
	    	}
	    	break;
	    }
	    //*/
	   
	    /*
	     Log.i(MOTION_EVENT_TAG,"mTestCounter:" + mTestCounter + "historySize:" + historySize + "pointerCount:" + pointerCount );
	     for (int h = 0; h < historySize; h++) {
	    	 Log.i(MOTION_EVENT_TAG, "At time His:" + event.getHistoricalEventTime(h) );
	         for (int p = 0; p < pointerCount; p++) {
	        	 Log.i(MOTION_EVENT_TAG, 
	        			 "pointer id:" + event.getPointerId(p) +
	        			 "( " + event.getHistoricalX(p, h) + 
	        			 " , " + event.getHistoricalY(p, h) + " )" );
	         }
	     }*/
	    
	     /*
	     Log.i(MOTION_EVENT_TAG, "mTestCounter:" + mTestCounter + "pointerCount At time Cur:" + pointerCount );
	     for (int p = 0; p < pointerCount; p++) {
        	 Log.i(MOTION_EVENT_TAG, 
        			 "mTestCounter:" + mTestCounter + 
        			 "pointer id:" + event.getPointerId(p) +
        			 "( " + event.getX(p) + 
        			 " , " + event.getY(p) + " )" );
	     }
	     */
	     return false;
	}
	
	

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		Log.d(TEST_DIFF_ON_TOUCH_EVENT, "onFling is called!");
		
		mDelaydMillis = DEFAULT_DELAY_MILLIS;
		mElapseMillis = 0;
		if(e2.getY() > e1.getY()){
			isMovingDown = true;
		}else{
			isMovingDown = false;
		}
		if(e2.getEventTime() - e1.getEventTime() < 400){
			mCircleHandler.sendEmptyMessageDelayed(CIRCLE_HANDLER_MSG_ANI, DEFAULT_DELAY_MILLIS);
			isMotionMoving = true;
		}
		//Log.d(TEST_DIFF_ON_TOUCH_EVENT, "e1.time:" + e1.getEventTime() + "e2.time:" + e2.getEventTime() );
		//Log.d(TEST_DIFF_ON_TOUCH_EVENT, "e1.time-e2.time:" + (e1.getEventTime() - e2.getEventTime() ));
		
		return true;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onScroll(android.view.MotionEvent, android.view.MotionEvent, float, float)
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		//Log.d(TEST_DIFF_ON_TOUCH_EVENT, "onScroll is called!");
		
		//warning:
		//float x = distanceX + distanceY;
		//mOnScrollLastX +=distanceX;
		//mOnScrollLastY +=distanceY;
		
		if(distanceY < 0){
			setmAngle(5);
		}else{
			setmAngle(-5);
		}
		requestLayout();
		
		//Log.d(TEST_DIFF_ON_TOUCH_EVENT, "onScroll distanceX:" + distanceX + "distanceY:" + distanceY );
		
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onSingleTapUp(android.view.MotionEvent)
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		isMotionMoving = false;
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onDown(android.view.MotionEvent)
	 */
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		isMotionMoving = false;
		//mLastDownEventTime = e.getEventTime();
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onLongPress(android.view.MotionEvent)
	 */
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onShowPress(android.view.MotionEvent)
	 */
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		//Log.d(TEST_DIFF_ON_TOUCH_EVENT, "This is CircleView.onTouchEvent !");
		// this is the View's default onTouchEvent
		if(mCircleGestureDetector.onTouchEvent(event)){
			return true;
		}else{
			return super.onTouchEvent(event);
		}
	}
	
	
	
	
	

}
