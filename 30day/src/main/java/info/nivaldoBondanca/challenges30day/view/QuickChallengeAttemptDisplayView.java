package info.nivaldoBondanca.challenges30day.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import info.nivaldoBondanca.challenges30day.R;


public class QuickChallengeAttemptDisplayView extends View {

    private boolean  mCurrentDayComplete     = false;
    private int      mCompleteDays           = 0;
    private int      mNumberColumns          = 3;
    private int      mNumberRows             = 3;
    private Drawable mDrawableComplete       = null;
    private int      mDimensionGridThickness;
    private int      mDimensionSquare;
    private int      mColorComplete;
    private int      mColorCurrent;
    private int      mColorFuture;
    private int      mColorGrid;

    private Paint mPaint;
    private Rect  mRectangle;

    public QuickChallengeAttemptDisplayView(Context context) {
        super(context);
        init(null, 0);
    }
    public QuickChallengeAttemptDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }
    public QuickChallengeAttemptDisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load up default values
        mColorComplete = getResources().getColor(R.color.QuickChallengeAttemptDisplayView_complete);
        mColorCurrent = getResources().getColor(R.color.QuickChallengeAttemptDisplayView_current);
        mColorFuture = getResources().getColor(R.color.QuickChallengeAttemptDisplayView_future);
        mColorGrid = getResources().getColor(R.color.QuickChallengeAttemptDisplayView_grid);
        mDimensionSquare = getResources().getDimensionPixelSize(R.dimen.QuickChallengeAttemptDisplayView_square);
        mDimensionGridThickness = getResources().getDimensionPixelSize(R.dimen.QuickChallengeAttemptDisplayView_gridThickness);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                                                                 R.styleable.QuickChallengeAttemptDisplayView,
                                                                 defStyle,
                                                                 0);

        mPaint = new Paint();
        mRectangle = new Rect();

        mCompleteDays = a.getInt(R.styleable.QuickChallengeAttemptDisplayView_completeDays, mCompleteDays);
        mCurrentDayComplete = a.getBoolean(R.styleable.QuickChallengeAttemptDisplayView_currentDayComplete,
                                           mCurrentDayComplete);

        mNumberColumns = a.getInt(R.styleable.QuickChallengeAttemptDisplayView_numberOfColumns, mNumberColumns);
        mNumberRows = a.getInt(R.styleable.QuickChallengeAttemptDisplayView_numberOfRows, mNumberRows);

        mColorComplete = a.getColor(R.styleable.QuickChallengeAttemptDisplayView_colorComplete,mColorComplete);
        mColorCurrent = a.getColor(R.styleable.QuickChallengeAttemptDisplayView_colorCurrent, mColorCurrent);
        mColorFuture = a.getColor(R.styleable.QuickChallengeAttemptDisplayView_colorFuture, mColorFuture);
        mColorGrid = a.getColor(R.styleable.QuickChallengeAttemptDisplayView_colorGrid, mColorGrid);

        mDimensionSquare = a.getDimensionPixelSize(R.styleable.QuickChallengeAttemptDisplayView_dimensionSquare,
                                                   mDimensionSquare);
        mDimensionGridThickness = a.getDimensionPixelSize(R.styleable.QuickChallengeAttemptDisplayView_dimensionGridThickness,
                                                   mDimensionGridThickness);

        if (a.hasValue(R.styleable.QuickChallengeAttemptDisplayView_drawableComplete)) {
            mDrawableComplete = a.getDrawable(R.styleable.QuickChallengeAttemptDisplayView_drawableComplete);
            mDrawableComplete.setCallback(this);
        }

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        if (mDimensionSquare * mNumberColumns > contentWidth ||
            mDimensionSquare * mNumberRows    > contentHeight) {
            // Shrinks the square size if necessary
            mDimensionSquare = Math.min(contentWidth  / mNumberColumns,
                                        contentHeight / mNumberRows);
        }

        int extraPadding = (contentWidth - mDimensionSquare * mNumberColumns) / 2;
        paddingRight += extraPadding;
        paddingLeft += extraPadding;
        extraPadding = (contentHeight - mDimensionSquare * mNumberRows) / 2;
        paddingTop += extraPadding;
        paddingBottom += extraPadding;

        // Draw the squares
        int left = paddingRight;
        int top = paddingTop;
        for (int i = 0; i < mNumberRows; i++) {
            for (int j = 0; j < mNumberColumns; j++) {
                int thisDay = i * mNumberColumns + j;
                if (thisDay < mCompleteDays) {
                    // Complete day
                    mPaint.setColor(mColorComplete);
                }
                else if (thisDay > mCompleteDays) {
                    // Future day
                    mPaint.setColor(mColorFuture);
                }
                else {
                    // Current day!
                    mPaint.setColor(mCurrentDayComplete ? mColorFuture : mColorCurrent);
                }

                mRectangle.set(left,
                               top,
                               left + mDimensionSquare,
                               top + mDimensionSquare);
                canvas.drawRect(mRectangle, mPaint);

                if (thisDay < mCompleteDays && mDrawableComplete != null) {
                    mDrawableComplete.setBounds(left, top, left + mDimensionSquare, top + mDimensionSquare);
                    mDrawableComplete.draw(canvas);
                }

                // Move one square to the right
                left += mDimensionSquare;
            }
            // Move one square to the bottom
            top += mDimensionSquare;
            left = paddingLeft;
        }

        // Draw the grid colors
        mPaint.setColor(mColorGrid);
        mPaint.setStrokeWidth(mDimensionGridThickness);
        for (int i = 0; i <= mNumberRows; i++) {
            // Vertical lines
            int verticalPosition = i * mDimensionSquare + paddingTop;
            canvas.drawLine(paddingLeft,                                verticalPosition,
                            paddingLeft + mDimensionSquare*mNumberColumns, verticalPosition,
                            mPaint);
        }
        for (int i = 0; i <= mNumberColumns; i++) {
            // Horizontal lines
            int horizontalPosition = i * mDimensionSquare + paddingLeft;
            canvas.drawLine(horizontalPosition, paddingTop,
                            horizontalPosition, paddingTop + mDimensionSquare*mNumberRows,
                            mPaint);
        }
//        // Just make sure the border of the whole content doesn't have "bevels"
//        canvas.drawRect(paddingRight, paddingTop,
//                        paddingRight + mDimensionSquare*mNumberColumns, paddingTop + mDimensionSquare*mNumberRows,
//                        mPaint);
    }

    public float getDimensionSquare() {
        return mDimensionSquare;
    }
    public void setDimensionSquare(int dimensionSquare) {
        mDimensionSquare = dimensionSquare;
        invalidate();
    }

    public int getDimensionGridThickness() {
        return mDimensionGridThickness;
    }
    public void setDimensionGridThickness(int dimensionGridThickness) {
        mDimensionGridThickness = dimensionGridThickness;
        invalidate();
    }

    public Drawable getDrawableComplete() {
        return mDrawableComplete;
    }
    public void setDrawableComplete(Drawable drawableComplete) {
        mDrawableComplete = drawableComplete;
    }

    public int getColorComplete() {
        return mColorComplete;
    }
    public void setColorComplete(int colorComplete) {
        mColorComplete = colorComplete;
        invalidate();
    }

    public int getColorCurrent() {
        return mColorCurrent;
    }
    public void setColorCurrent(int colorCurrent) {
        mColorCurrent = colorCurrent;
        invalidate();
    }

    public int getColorFuture() {
        return mColorFuture;
    }
    public void setColorFuture(int colorFuture) {
        mColorFuture = colorFuture;
        invalidate();
    }

    public int getColorGrid() {
        return mColorGrid;
    }
    public void setColorGrid(int colorGrid) {
        mColorGrid = colorGrid;
        invalidate();
    }

    public boolean isCurrentDayComplete() {
        return mCurrentDayComplete;
    }
    public void setCurrentDayComplete(boolean currentDayComplete) {
        mCurrentDayComplete = currentDayComplete;
        invalidate();
    }

    public int getCompleteDays() {
        return mCompleteDays;
    }
    public void setCompleteDays(int completeDays) {
        mCompleteDays = completeDays;
        invalidate();
    }

    public int getNumberColumns() {
        return mNumberColumns;
    }
    public void setNumberColumns(int numberColumns) {
        mNumberColumns = numberColumns;
        invalidate();
    }

    public int getNumberRows() {
        return mNumberRows;
    }
    public void setNumberRows(int numberRows) {
        mNumberRows = numberRows;
        invalidate();
    }
}
