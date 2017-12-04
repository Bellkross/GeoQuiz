package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String CHEAT_INDEX = "com.bignerdranch.android.geoquiz.cheaterQuiz";
    private static final String CHEATED_QUESTIONS =
            "com.bignerdranch.android.geoquiz.cheatedQuestions";

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;
    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private float mGoodAnswers = 0;
    private float mAnswers = 0;
    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    private static final int REQUEST_CODE_CHEAT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Log.d(TAG, "onCreate(Bundle) called");

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(CHEAT_INDEX, false);
            boolean[] array = new boolean[mQuestionBank.length];
            int i = -1;
            for (Question q:
                 mQuestionBank) {
                q.setCheated(array[++i]);
            }
        }

        mQuestionTextView = findViewById(R.id.question_text_view);

        mQuestionTextView.setOnClickListener(v -> {
            mCurrentIndex = (++mCurrentIndex) % mQuestionBank.length;
            updateQuestion();
        });

        mTrueButton = findViewById(R.id.true_button);
        mFalseButton = findViewById(R.id.false_button);
        mNextButton = findViewById(R.id.next_button);
        mPrevButton = findViewById(R.id.prev_button);

        mTrueButton.setOnClickListener(v -> checkAnswer(true));

        mFalseButton.setOnClickListener(v -> checkAnswer(false));

        mNextButton.setOnClickListener(v -> {
            mCurrentIndex = (++mCurrentIndex) % mQuestionBank.length;
            mIsCheater = false;
            updateQuestion();
        });

        mPrevButton.setOnClickListener(v -> {
            previousQuestion();
            mIsCheater = false;
        });

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(v -> {
            boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
            Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
            startActivityForResult(intent, REQUEST_CODE_CHEAT);
        });

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            mQuestionBank[mCurrentIndex].setCheated(mIsCheater);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(CHEAT_INDEX,mIsCheater);
        boolean[] array  = new boolean[mQuestionBank.length];
        int i = -1;
        for (Question q:
             mQuestionBank) {
            array[++i] = q.isCheated();
        }
        savedInstanceState.putBooleanArray(CHEATED_QUESTIONS,array);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void previousQuestion() {
        if (--mCurrentIndex < 0) {
            mCurrentIndex = mQuestionBank.length - 1;
        }
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(final boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        if (!mQuestionBank[mCurrentIndex].isAnswered()) {
            int messageResId = 0;
            if (mIsCheater||mQuestionBank[mCurrentIndex].isCheated()) {
                messageResId = R.string.judgment_toast;
            } else {
                if (userPressedTrue == answerIsTrue) {
                    messageResId = R.string.correct_toast;
                    ++mGoodAnswers;
                } else {
                    messageResId = R.string.incorrect_toast;
                }
            }
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
                    .show();
            mQuestionBank[mCurrentIndex].setAnswered(true);
            if (++mAnswers == mQuestionBank.length) {
                Toast.makeText(this, "Ur result " + (mGoodAnswers / mAnswers) * 100. + "%", Toast.LENGTH_SHORT)
                        .show();
                refresh();
            }
        }
    }

    private void refresh() {
        mGoodAnswers = 0;
        mAnswers = 0;
        for (Question q :
                mQuestionBank) {
            q.setAnswered(false);
            q.setCheated(false);
        }
    }
}