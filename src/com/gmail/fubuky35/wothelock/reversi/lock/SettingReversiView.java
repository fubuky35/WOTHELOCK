package com.gmail.fubuky35.wothelock.reversi.lock;

import java.util.ArrayList;
import java.util.List;

import com.gmail.fubuky35.wothelock.R;
import com.gmail.fubuky35.wothelock.preference.SaveLoadManager;
import com.gmail.fubuky35.wothelock.reversi.Utils;
import com.gmail.fubuky35.wothelock.reversi.model.Board;
import com.gmail.fubuky35.wothelock.reversi.model.Cell;
import com.gmail.fubuky35.wothelock.reversi.model.Cell.E_STATUS;
import com.gmail.fubuky35.wothelock.reversi.model.IPlayerCallback;
import com.gmail.fubuky35.wothelock.reversi.model.Player;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class SettingReversiView extends View implements IPlayerCallback, Runnable{

	private static final int VIEW_ID = 1001;

	private Board mBoard;
	
	private Paint mPaintScreenBg = new Paint();
	private Paint mPaintScreenBg2 = new Paint();
	private Paint mPaintBoardBg = new Paint();
	private Paint mPaintBoardBorder = new Paint();
	private Paint mPaintCellFgB = new Paint();
	private Paint mPaintCellFgW = new Paint();
	private Paint mPaintCellAvB = new Paint();
	private Paint mPaintCellAvW = new Paint();
	private Paint mPaintTextFg = new Paint();
	private Paint mPaintTurnRect = new Paint();
	private Paint mPaintWinnerRect = new Paint();
	private Paint mPaintCellCur = new Paint();
	private Paint mPaintCurrentTouch = new Paint();
	
	private Bitmap mBitmapWhite;
	private Bitmap mBitmapBlack;
	private Bitmap mBitmapBoard;
	
	private int mWidth;
	private int mHeight;
	private static final float CELL_SIZE_FACTOR = 0.40f;
	private static final float CELL_SIZE_FACTOR_PRG = 0.30f;
	private boolean mPaused; 

	private Handler mHandler = new Handler();
	private List<Cell> mTurnningCells = null;
	private List<Cell> mChangedCells = null;
	private int mTurnningProgress = 0;
	private static final int TURNNING_FREQ = 15;    //frames to complete a turn.
	private static final int TURNING_TIME = 600;  //msec
	
	private Bitmap[] mBitmapBtoW = new Bitmap[TURNNING_FREQ];
	private Bitmap[] mBitmapWtoB = new Bitmap[TURNNING_FREQ];
	
	private Point mCurrentTouch = null;
	private Point[] mSettingPattern = new Point[ReversiLock.LOCK_PATTERN_MAX_SIZE];
	private int mSettingCount = 0;
	
	private static final String[] NUMMBER_MARKS = {"①","②","③","④"};
	
	public SettingReversiView(Context context) {
		super(context);

		setId(VIEW_ID);
		setFocusable(true);
		
		mPaintScreenBg.setColor(getResources().getColor(R.color.screen_bg));
		mPaintScreenBg2.setColor(getResources().getColor(R.color.screen_bg2));
		mPaintBoardBg.setColor(getResources().getColor(R.color.board_bg));
		mPaintBoardBorder.setColor(getResources().getColor(R.color.board_border));
		mPaintCellFgB.setColor(getResources().getColor(R.color.cell_fg_black));
		mPaintCellFgW.setColor(getResources().getColor(R.color.cell_fg_white));
		mPaintCellAvB.setColor(getResources().getColor(R.color.cell_fg_black));
		mPaintCellAvW.setColor(getResources().getColor(R.color.cell_fg_white));
		mPaintCellCur.setColor(getResources().getColor(R.color.cell_fg_current));
		mPaintTextFg.setColor(getResources().getColor(R.color.text_fg));
		mPaintTurnRect.setColor(getResources().getColor(R.color.turn_rect));
		mPaintWinnerRect.setColor(getResources().getColor(R.color.winner_rect));
		mPaintCurrentTouch.setColor(getResources().getColor(R.color.touch_marker));

		//アンチエイリアスを指定。これをしないと縁がギザギザになる。
		mPaintCellFgB.setAntiAlias(true);
		mPaintCellFgW.setAntiAlias(true);
		mPaintCellAvB.setAntiAlias(true);
		mPaintCellAvW.setAntiAlias(true);
		mPaintCellCur.setAntiAlias(true);
		mPaintCurrentTouch.setAntiAlias(true);

		mPaintCellAvB.setAlpha(32);
		mPaintCellAvW.setAlpha(64);
		mPaintCellCur.setAlpha(128);
		mPaintCurrentTouch.setAlpha(32);
		
		mPaintTextFg.setAntiAlias(true);
		mPaintTextFg.setStyle(Style.FILL);
		
		//参考URL:
		// http://y-anz-m.blogspot.com/2010/02/android-multi-screen.html 
		// http://y-anz-m.blogspot.com/2010/05/androiddimension.html
		Resources res = getResources();  
		int fontSize = res.getDimensionPixelSize(R.dimen.font_size_status); 
		mPaintTextFg.setTextSize(fontSize);
		
		mPaintTurnRect.setAntiAlias(true);
		mPaintTurnRect.setAlpha(128);
		mPaintTurnRect.setStyle(Style.STROKE);
		mPaintTurnRect.setStrokeWidth(5f);

		mPaintWinnerRect.setAntiAlias(true);
		mPaintWinnerRect.setAlpha(192);
		mPaintWinnerRect.setStyle(Style.STROKE);
		mPaintWinnerRect.setStrokeWidth(5f);
		
		load(context);
		
		init(false);
	}
	
	public void init(boolean auto_start){
		mBoard = new Board(false);
		mPaused = false;
		
		mBoard.setPlayer1(Player.getPlayer1(getContext(), mBoard, E_STATUS.Black));
		mBoard.setPlayer2(Player.getPlayer1(getContext(), mBoard, E_STATUS.White));
		
		invalidate();
		
		if (auto_start){
			callPlayer();
		}

	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		this.mWidth = getWidth();
		this.mHeight = getHeight();
		mBoard.setSize(this.mWidth, this.mHeight);
		
		if (mBitmapBlack == null){
			loadBitmap();
		}
		
		drawBoard(canvas);
	}
	
	private void loadBitmap(){
		float cw = mBoard.getCellWidth();
		float ch = mBoard.getCellHeight();
		int INSET = (int)(cw * CELL_SIZE_FACTOR * 0.3);
		Resources res = this.getContext().getResources();

		try {
			//ボードの背景
			Bitmap board = BitmapFactory.decodeResource(res, R.drawable.bg2_green2);
			mBitmapBoard = Bitmap.createScaledBitmap(board, (int)mBoard.getRectF().width(), (int)mBoard.getRectF().height(), true);
		} catch (Exception ex) {
			Utils.d(ex.getMessage());
		}

		try {
			//黒のコマ
			Bitmap black = BitmapFactory.decodeResource(res, R.drawable.b1);
			mBitmapBlack = Bitmap.createScaledBitmap(black, (int)(cw-INSET*2), (int)(ch-INSET*2), true);

			//白のコマ
			Bitmap white = BitmapFactory.decodeResource(res, R.drawable.w1);
			mBitmapWhite = Bitmap.createScaledBitmap(white, (int)cw-INSET*2, 
					(int)ch-INSET*2, true);

			//裏返すアニメーションの為のビットマップを用意しておく。
			for (int i = 1;i <= TURNNING_FREQ; i++){
				if (i <= TURNNING_FREQ/2){
					mBitmapBtoW[i-1] = Bitmap.createScaledBitmap(mBitmapBlack, 
																(int)(cw-INSET*2) * (TURNNING_FREQ - i*2) / TURNNING_FREQ, 
																(int)(ch-INSET*2), true);

					mBitmapWtoB[i-1] = Bitmap.createScaledBitmap(mBitmapWhite, 
																(int)(cw-INSET*2) * (TURNNING_FREQ - i*2) / TURNNING_FREQ, 
																(int)(ch-INSET*2), true);
				} else {
					mBitmapBtoW[i-1] = Bitmap.createScaledBitmap(mBitmapWhite, 
																(int)(cw-INSET*2) * (i*2 - TURNNING_FREQ) / TURNNING_FREQ, 
																(int)(ch-INSET*2), true);

					mBitmapWtoB[i-1] = Bitmap.createScaledBitmap(mBitmapBlack, 
																(int)(cw-INSET*2) * (i*2 - TURNNING_FREQ) / TURNNING_FREQ, 
																(int)(ch-INSET*2), true);
				}
			}

		} catch (Exception ex){
			Utils.d(ex.getMessage());
		}
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//裏返しの最中は何も出来ない。
		if (mTurnningCells != null || mTurnningProgress > 0) return true;		
		
		// 終了後はタッチで次のゲームへ
		if (mBoard.isFinished()){
			init(true);
			return true;
		}
		
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
//			int r = (int)(y / mBoard.getCellHeight());
//			int c = (int)(x / mBoard.getCellWidth());
			
			float rFlt = y / mBoard.getCellHeight();
			float cFlt = x / mBoard.getCellWidth();
			
			int r = (int) rFlt;
			int c = (int) cFlt;
			
			float rTmp = rFlt - r;
			float cTmp = cFlt - c;
			
			// 誤打防止、あたり判定をマスの90パーセントに
			if((0. <= rTmp && rTmp <= 0.05)
					|| (0.95 <= rTmp && rTmp <= 1.0)){
				return true;
			}
			if((0. <= cTmp && cTmp <= 0.05)
					|| (0.95 <= cTmp && cTmp <= 1.0)){
				return true;
			}
			
			if (r < Board.ROWS && c < Board.COLS && r >=0 && c >= 0){
				
				mCurrentTouch = new Point(c, r);
				
				if(mSettingCount < ReversiLock.LOCK_PATTERN_MAX_SIZE){
					mSettingPattern[mSettingCount] = mCurrentTouch;
					++mSettingCount;
				}
								
//				invalidate(mBoard.getCell(mCurrentTouch).getRect());
//				onProgress();
				invalidate();
				
				// TODO ロックパターン記憶
			}
			break;
		default:
		}
		
		return true;
	}

	private void move(final Point point){
		
		//裏返しアニメーション中の場合は終わるまで待ってから実行する。
		if (mTurnningCells != null){
			mHandler.postDelayed(new Runnable(){
				@Override public void run(){ move(point); }
			}, TURNING_TIME / 2);
			return;
		}
		
		mChangedCells = null;
		mTurnningCells = null;
		mTurnningProgress = 0;
		
		Cell currentCell = mBoard.getCell(point);
		
		if (currentCell.getReversibleCells().size() == 0){
			String s = String.format("Invalid move. (r,c=%d,%d)", point.y, point.x);
			//Toast.makeText(this.getContext(), s, Toast.LENGTH_SHORT).show();
			Utils.d(s);
			return;
		} 
		
		// 有効打のタッチ点は表示しない
		mCurrentTouch = null;
		
		mChangedCells = mBoard.changeCell(point, mBoard.getTurn());
		mTurnningCells = new ArrayList<Cell>(mChangedCells);
		mTurnningCells.remove(currentCell);
		
		int nextAvailableCellCount = mBoard.changeTurn(mChangedCells); 
		if (nextAvailableCellCount == 0){
			if (mBoard.countBlankCells() == 0){				//全部のセルが埋まった場合は終了
				finish();											
			} else {
				showSkippMessage();					//スキップ
				nextAvailableCellCount = mBoard.changeTurn(mChangedCells);
				if (nextAvailableCellCount == 0){	//どちらも打つ場所が無くなった場合は終了
					finish();							
				}
			}
		}
		
		invalidate(currentCell.getRect());

		//裏返し処理用タイマースレッドを開始
		startTurnning();

		//次のプレーヤーに順番を渡す。
		callPlayer();
	}
	
	@Override
	public void onEndThinking(final Point pos) {
		if (pos == null) return;
		if (pos.y < 0 || pos.x < 0) return;
		if (mPaused) return;

		move(pos);
	}
	
	@Override
	public void onProgress() {
		invalidate(0, (int)mBoard.getRectF().bottom, mWidth, mHeight);
	}
	
	@Override
	public void onPointStarted(Point pos) {
		Cell cell = mBoard.getCell(pos);
		invalidate(cell.getRect());			//変更された領域のみを再描画
	}
	
	@Override
	public void onPointEnded(Point pos) {
		Cell cell = mBoard.getCell(pos);
		invalidate(cell.getRect());			//変更された領域のみを再描画
	}
	
	private void finish(){
		
		//裏返しアニメーション中の場合は終わるまで待ってから実行する。
		if (mTurnningCells != null){
			mHandler.postDelayed(new Runnable(){
				@Override public void run(){ finish(); }
			}, TURNING_TIME / 2);
			return;
		}
		
		mBoard.setFinished();
		invalidate();			
	}
	
	public void showCountsToast(){
		Cell.E_STATUS winner = mBoard.getWinnerStatus();
		String msg = "Black: " + mBoard.countCells(Cell.E_STATUS.Black) + "\n"
			+ "White: " + mBoard.countCells(Cell.E_STATUS.White) + "\n\n";
		
		if (mBoard.isFinished()){
			if (winner != Cell.E_STATUS.None){
				msg += "Winner is: " + Cell.statusToDisplay(winner) + "!!";
			} else {
				msg += "Draw game! ";
			}
		} else {
			if (winner != Cell.E_STATUS.None){
				msg += Cell.statusToDisplay(winner) + " is winning...\n\n";
			}
			msg += mBoard.getTurnDisplay() + "'s turn.";
		}
		Toast.makeText(this.getContext(), msg, Toast.LENGTH_LONG).show();
		Utils.d(msg);
	}
	
	private void showSkippMessage(){
		String msg = Cell.statusToDisplay(mBoard.getTurn()) + " has been skipped.";
		Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
		Utils.d(msg);
	}
	
	private void drawBoard(Canvas canvas){

		if (mBoard.getRectF().width() <= 0f ) return;
		
		float bleft = mBoard.getRectF().left;
		float btop = mBoard.getRectF().top;
		float bw = mBoard.getRectF().width();
		float bh = mBoard.getRectF().height();
		float cw = mBoard.getCellWidth();
		float ch = mBoard.getCellHeight();

		//ボードの背景 
		canvas.drawBitmap(mBitmapBoard, bleft, btop, null);

		//縦線
		for (int i = 0; i < Board.COLS; i++) {
			canvas.drawLine(cw * (i+1) + bleft, btop, cw * (i+1) + bleft, bh + btop, mPaintBoardBorder);
		}
		//横線
		for (int i = 0; i < Board.ROWS; i++) {
			canvas.drawLine(bleft, ch * (i+1) + btop, bw + bleft, ch * (i+1) + btop, mPaintBoardBorder);
		}

		//全てのCellを描画
		drawCells(canvas, cw);
		
		//手番の表示、現在の黒と白の数の表示
		drawStatus(canvas);
	}
	
	//全てのCellについてコマが配置されていれば描く
	private void drawCells(Canvas canvas, float cw){
		//boolean show_hints = Pref.getShowHints(getContext());

		Cell[][] cells = mBoard.getCells();
		for (int i = 0; i < Board.ROWS; i++) {
			for (int j = 0; j < Board.COLS; j++) {
				Cell cell =cells[i][j]; 
				Cell.E_STATUS st = cell.getStatus();
				
				if (st == E_STATUS.None){
					// if (show_hints) drawHints(cell, canvas, cw);
					drawHints(cell, canvas, cw);
				} else {
					drawStone(cell, canvas, cw, st);
				}
			}
		}
	}
	
	private void drawStone(Cell cell, Canvas canvas, float cw, Cell.E_STATUS st){
		final float INSET = (cell.getWidth() * CELL_SIZE_FACTOR * 0.3f);
		Bitmap bm;
		
		if (mTurnningProgress >0 && mTurnningCells != null && mTurnningCells.contains(cell)){
			bm = (st == E_STATUS.Black) ? mBitmapWtoB[mTurnningProgress-1] : mBitmapBtoW[mTurnningProgress-1];
			
			int offset_w;
			if (mTurnningProgress <= TURNNING_FREQ/2){
				offset_w = (int)(cw-INSET*2) * (mTurnningProgress*2) / TURNNING_FREQ / 2;
			} else {
				offset_w = (int)(cw-INSET*2) * (TURNNING_FREQ - mTurnningProgress)*2 / TURNNING_FREQ / 2;
			}
			
			canvas.drawBitmap(bm, 
							  cell.getLeft()+INSET + offset_w, 
							  cell.getTop()+INSET, 
							  null);
			
		} else {
			bm = (st == E_STATUS.Black) ? mBitmapBlack : mBitmapWhite;
			canvas.drawBitmap(bm, cell.getLeft()+INSET, cell.getTop()+INSET, null);
		}
	
	}
	
	private void drawHints(Cell cell, Canvas canvas, float cw){
		if (cell.getReversibleCells().size() == 0) 
			return;
		
		//次に配置可能なセルであれば小さな丸を表示する
		float aw = cw * 0.1f;
		Paint pt = mBoard.getTurn() == Cell.E_STATUS.Black ? mPaintCellAvB : mPaintCellAvW;
		canvas.drawCircle(cell.getCx(), cell.getCy(), aw, pt);
	}
	
	public void drawTouch(Cell cell, Canvas canvas, float cw){
		float aw = cw * 0.2f;
		Paint pt = mPaintBoardBg;
		canvas.drawCircle(cell.getCx(), cell.getCy(), aw, pt);
	}

	private void drawStatus(Canvas canvas){
		Resources res = getResources();  
		float turn_rect_inset = res.getDimension(R.dimen.turn_rect_inset); 
		float turn_rect_round = res.getDimension(R.dimen.turn_rect_round); 
		float turn_circle_x = res.getDimension(R.dimen.turn_circle_x); 
		float turn_circle_y = res.getDimension(R.dimen.turn_circle_y); 
		float turn_text_x = res.getDimension(R.dimen.turn_text_x); 
		float turn_text_y = res.getDimension(R.dimen.turn_text_y); 
		float top = mBoard.getRectF().bottom + mBoard.getRectF().top;
		float center = mBoard.getRectF().width() / 2f;

		//ボード以外の余白部分の背景
//		Shader shader = new LinearGradient(mWidth/2, top + (mHeight - top) * 0.5f, mWidth/2, mHeight, mPaintScreenBg.getColor(), mPaintScreenBg2.getColor(), Shader.TileMode.CLAMP);  
		Shader shader = new RadialGradient(mWidth/2f, top * 0.9f, mWidth * 0.7f, mPaintScreenBg2.getColor(), mPaintScreenBg.getColor(), Shader.TileMode.CLAMP);  
		Paint  paint = new Paint( mPaintScreenBg);  
		paint.setShader(shader);  
		canvas.drawRect(0, top, mWidth, mHeight, paint);

		//各プレーヤーのコマ数を表示
		int fontSize = res.getDimensionPixelSize(R.dimen.font_size_name); 
		mPaintTextFg.setTextSize(fontSize);
		String s;
		
//		for(int i = 0;i < ReversiLock.LOCK_PATTERN_MAX_SIZE;++i){
//			s = NUMMBER_MARKS[i] + "";
//			if(i < mSettingCount){
//				s = s + "(" + mSettingPattern[i].x + "," + mSettingPattern[i].y + ")";
//			} else {
//				s = s + "None";
//			}
//			canvas.drawText(s, turn_circle_x, top + turn_text_y * (1 + 0.4f*i), mPaintTextFg);
//		}
		
		for(int i = 0;i < mSettingCount;++i){
			Cell cell;
			cell = mBoard.getCell(mSettingPattern[i]);
			if (cell != null){
				s = NUMMBER_MARKS[i];
				mPaintTextFg.setTextSize(cell.getWidth());
				canvas.drawText(s,cell.getRectF().left, cell.getRectF().bottom-cell.getWidth()*0.08f, mPaintTextFg);
				//invalidate(cell.getRect());
			}
		}
		
	}
	
	public void back(){
		if(0 < mSettingCount){
			--mSettingCount;
			mSettingPattern[mSettingCount] = null;
			invalidate();
		}
	}
	
//	private static final String KEY_HEAD = "lock_pattern";
//	private static final String KEY_COUNT = "_count";
//	private static final String KEY_X = "_x";
//	private static final String KEY_Y = "_y";
	
	public void save(Context context) {
		
		if(mSettingCount > 0){
			Toast.makeText(getContext(), R.string.msg_lock_pattern_detect, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getContext(), R.string.msg_lock_pattern_nothing, Toast.LENGTH_LONG).show();
		}
		
		
		SaveLoadManager sm = SaveLoadManager.getInstance(context);
		
		sm.saveLockPattern(mSettingCount, mSettingPattern);
		
//		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
//		Editor e = sp.edit();
//		
//		e.putInt(KEY_HEAD + KEY_COUNT, mSettingCount);
//		
//		for(int i = 0;i < mSettingCount;++i){
//			e.putInt(KEY_HEAD + KEY_X + i, mSettingPattern[i].x);
//			e.putInt(KEY_HEAD + KEY_Y + i, mSettingPattern[i].y);
//		}
//		
//		e.commit();
	}
	
	public void load(Context context) {
		SaveLoadManager sm = SaveLoadManager.getInstance(context);
		
		mSettingCount = sm.loadLockPatternCount();
		mSettingPattern = sm.loadLockPattern();
		
//		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
//		mSettingCount = sp.getInt(KEY_HEAD + KEY_COUNT, 0);
//		
//		for(int i = 0;i < mSettingCount;++i){
//			mSettingPattern[i] = new Point();
//			mSettingPattern[i].x = sp.getInt(KEY_HEAD + KEY_X + i, 0);
//			mSettingPattern[i].y = sp.getInt(KEY_HEAD + KEY_Y + i, 0);
//		}
		
	}
	
//	private void drawWinner(Canvas canvas){
//		String s;
//		Player winner = mBoard.getWinner();
//		if (winner != null){
//			s = winner.getName() + " wins! ";
//		} else {
//			s = "Draw game! ";
//		}
//
////		//盤面全体をグレーアウト
////		Paint paintBg = new Paint();
////		paintBg.setColor(Color.BLACK);
////		paintBg.setAlpha(128);
////		canvas.drawRect(mBoard.getRectF(), paintBg);
//		
////		GameActivity activity =  (GameActivity)this.getContext();
//		
//	}

	
	public String getState(){
		String s = mBoard.getStateString();
		Utils.d("getState: state=" + s);
		return s;
	}
	
	public void pause(){
		mPaused = true;
		
		Player p = mBoard.getCurrentPlayer();
		if (p != null && !p.isHuman()){
			//別スレッドで思考ルーチンが動いていれば中断する。
			p.stopThinking();
		}
	}
	
	public void resume(String state){
		Utils.d("onResume: state=" + state);

		mPaused = false;
		if (!TextUtils.isEmpty(state)){
			mBoard.loadFromStateString(state);

			mBoard.setPlayer1(Player.getPlayer1(getContext(), mBoard, E_STATUS.Black));
			mBoard.setPlayer2(Player.getPlayer1(getContext(), mBoard, E_STATUS.Black));
		}

		callPlayer();
	}
	
	private void callPlayer(){
		if (mPaused) return;
		
		//裏返しアニメーション中の場合は終わるまで待ってから実行する。
		if (mTurnningCells != null){
			mHandler.postDelayed(new Runnable(){
				@Override public void run(){ callPlayer(); }
			}, TURNING_TIME / 2);
			return;
		}
		
		Player p = mBoard.getCurrentPlayer();
		if (p != null && !p.isHuman()){
			p.startThinking(this);
		}
	}

	//石を裏返すアニメーションの為のスレッドを開始する
	private void startTurnning(){
		new Thread(this).start();
	}
	
	//石を裏返すアニメーションを別スレッドで処理する
	@Override
	public void run() {
		
		if (mTurnningCells != null && mTurnningCells.size() >0){
			for (mTurnningProgress = 1; mTurnningProgress <= TURNNING_FREQ; mTurnningProgress++){
				//ハンドラにUIスレッド側で実行する処理を渡す。
				mHandler.post(new Runnable(){
					@Override
					public void run(){
						if (mTurnningCells != null){
							for (Cell cell : mTurnningCells) {
								invalidate(cell.getRect());			//変更された領域のみを再描画
							}
						}
					}
				});
				try {
					Thread.sleep(TURNING_TIME / TURNNING_FREQ);
				} catch (Exception e) {
					Utils.d(e.getMessage());
				}
			}
		}
		mTurnningCells = null;
		mTurnningProgress = 0;

		//アニメーション完了後の描画処理
		mHandler.post(new Runnable(){
			@Override
			public void run(){
				if (mChangedCells != null){
					for (Cell cell : mChangedCells) {
						invalidate(cell.getRect());			//変更された領域のみを再描画
					}
				}
			
				//画面下部のステータス表示領域を再描画
				invalidate(0, (int)mBoard.getRectF().bottom, mWidth, mHeight);
			}
		});
	}

}
