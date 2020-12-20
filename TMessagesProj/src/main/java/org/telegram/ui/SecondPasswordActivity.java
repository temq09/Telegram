package org.telegram.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.upsidedown.DoubleBottomConfig;
import org.telegram.messenger.upsidedown.DoubleBottomConfig.Lexems;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;

public class SecondPasswordActivity extends BaseFragment {

    static final int TYPE_CREATE_NEW = 0;
    static final int TYPE_EDIT = 1;

    private static final int STEP_ENTER_FIRST_PASSWORD = 0;
    private static final int STEP_RETRY_NEW_PASSWORD = 0;

    private final int type;
    private int step = STEP_ENTER_FIRST_PASSWORD;

    private TextView titleTextView;
    private EditTextBoldCursor passwordEditText;

    private String firstPassword = "";

    public SecondPasswordActivity(int type) {
        this.type = type;
    }

    @Override
    public View createView(Context context) {
        FrameLayout parent = new FrameLayout(context);
        fragmentView = parent;

        switch (type) {
            case TYPE_CREATE_NEW:
                setupCreateNew(context, parent);
                break;

            case TYPE_EDIT:
                setupEdit();
                break;
        }

        return fragmentView;
    }

    private void setupCreateNew(Context context, FrameLayout parent) {
        titleTextView = new TextView(context);
        titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
        String text;
        if (step == STEP_ENTER_FIRST_PASSWORD) {
            text = LocaleController.getString(Lexems.DOUBLE_BOTTOM_NEW_PASSWORD, R.string.EnterNewPasscode);
        } else {
            text = LocaleController.getString(Lexems.DOUBLE_BOTTOM_RETRY_PASSWORD, R.string.EnterNewPasscode);
        }
        titleTextView.setText(text);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        titleTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        parent.addView(titleTextView, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL, 0, 38, 0, 0));

        passwordEditText = new EditTextBoldCursor(context);
        passwordEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        passwordEditText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(context, false));
        passwordEditText.setMaxLines(1);
        passwordEditText.setLines(1);
        passwordEditText.setGravity(Gravity.CENTER_HORIZONTAL);
        passwordEditText.setSingleLine(true);
        passwordEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordEditText.setTypeface(Typeface.DEFAULT);
        passwordEditText.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        passwordEditText.setCursorSize(AndroidUtilities.dp(20));
        passwordEditText.setCursorWidth(1.5f);
        parent.addView(passwordEditText, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 36, Gravity.TOP | Gravity.LEFT, 40, 90, 40, 0));

        passwordEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            processPassword(textView.getText().toString());
            return true;
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 4) {
                    processPassword(s.toString());
                }
            }
        });

        passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
    }

    private void processPassword(String password) {
        if (step == STEP_ENTER_FIRST_PASSWORD) {
            processNext(password);

        } else if (step == STEP_RETRY_NEW_PASSWORD) {
            processDone();
        }
    }

    private void setupEdit() {

    }

    private void processNext(String password) {
        if (password.length() != 4) {
            showError("Password must contains 4 digits");
            return;
        }

        titleTextView.setText(LocaleController.getString(Lexems.DOUBLE_BOTTOM_RETRY_PASSWORD));

        step = STEP_RETRY_NEW_PASSWORD;
    }

    private void processDone() {

    }

    private void showError(String text) {

    }
}
