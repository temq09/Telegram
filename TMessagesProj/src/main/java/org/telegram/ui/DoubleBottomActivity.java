package org.telegram.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.upsidedown.DoubleBottom;
import org.telegram.messenger.upsidedown.DoubleBottomConfig.Lexems;
import org.telegram.messenger.upsidedown.data.PasswordData;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DoubleBottomActivity extends BaseFragment {

    private final DoubleBottom doubleBottom;

    private RecyclerListView listView;
    private PasswordListAdapter adapter;

    public static class DoubleBottomHeaderCell extends FrameLayout {

        private RLottieImageView imageView;
        private TextView messageTextView;

        public DoubleBottomHeaderCell(Context context) {
            super(context);

            imageView = new RLottieImageView(context);
            imageView.setAnimation(R.raw.filters, 90, 90);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.playAnimation();
            addView(imageView, LayoutHelper.createFrame(90, 90, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 14, 0, 0));
            imageView.setOnClickListener(v -> {
                if (!imageView.isPlaying()) {
                    imageView.setProgress(0.0f);
                    imageView.playAnimation();
                }
            });

            messageTextView = new TextView(context);
            messageTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
            messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            messageTextView.setGravity(Gravity.CENTER);
            messageTextView.setText(LocaleController.getString(Lexems.DOUBLE_BOTTOM_ADD_NEW_INFO, R.string.DoubleBottomAddNewInfo));
            addView(messageTextView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 40, 121, 40, 24));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), heightMeasureSpec);
        }
    }

    public DoubleBottomActivity() {
        doubleBottom = DoubleBottom.Factory.get();
    }

    @Override
    public View createView(Context context) {
        fragmentView = new FrameLayout(context);

        setupActionBar();
        setupUi(context);
        loadData();

        return fragmentView;
    }

    private void setupActionBar() {
        actionBar.setAllowOverlayTitle(false);
        actionBar.setTitle(
                LocaleController.getString(Lexems.DOUBLE_BOTTOM_NAME, R.string.DoubleBottomName)
        );
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);

        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int id) {
                finishFragment();
            }
        });
    }

    private void setupUi(Context context) {
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setTag(Theme.key_windowBackgroundGray);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        listView.setAdapter(adapter = new PasswordListAdapter(context));
        listView.setOnItemClickListener((view, position) -> {
            if (position == PasswordListAdapter.POSITION_ADD_NEW) {
                presentFragment(new SecondPasswordActivity(SecondPasswordActivity.TYPE_CREATE_NEW));
            } else {
                presentFragment(new SecondPasswordActivity(SecondPasswordActivity.TYPE_EDIT));
            }
        });
    }

    private void loadData() {
        final List<PasswordData> passwords = doubleBottom.getPasswords();
        adapter.submitList(passwords);
    }

    private static class PasswordListAdapter extends RecyclerListView.SelectionAdapter {

        private static int POSITION_BASE = 0;
        private static final int POSITION_ICON = POSITION_BASE++;
        private static final int POSITION_ADD_NEW = POSITION_BASE++;
        private static final int TYPE_PASSWORD = POSITION_BASE;

        private final Context context;
        private List<PasswordData> items;

        protected PasswordListAdapter(Context context) {
            this.context = context;
            items = Collections.emptyList();
        }

        @Override
        public int getItemViewType(int position) {
            if (position >= TYPE_PASSWORD) {
                return TYPE_PASSWORD;
            } else {
                return position;
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == POSITION_ICON) {
                view = new DoubleBottomHeaderCell(context);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else if (viewType == POSITION_ADD_NEW) {
                view = new FiltersSetupActivity.TextCell(context);
                view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            } else {
                view = new TextSettingsCell(context);
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == POSITION_ADD_NEW) {
                bindAddNew(holder);
            } else if (holder.getItemViewType() == TYPE_PASSWORD) {
                bindPassword(holder, position);
            }
        }

        @Override
        public int getItemCount() {
            return items.size() + POSITION_BASE;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            final int itemViewType = holder.getItemViewType();
            return itemViewType == POSITION_ADD_NEW;
        }

        public void submitList(List<PasswordData> data) {
            items = data;
        }

        private void bindAddNew(@NonNull RecyclerView.ViewHolder holder) {
            FiltersSetupActivity.TextCell textCell = (FiltersSetupActivity.TextCell) holder.itemView;
            Drawable drawable1 = context.getResources().getDrawable(R.drawable.poll_add_circle);
            Drawable drawable2 = context.getResources().getDrawable(R.drawable.poll_add_plus);
            drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_switchTrackChecked), PorterDuff.Mode.MULTIPLY));
            drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_checkboxCheck), PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(drawable1, drawable2);

            textCell.setTextAndIcon(LocaleController.getString(Lexems.DOUBLE_BOTTOM_ADD_NEW, R.string.DoubleBottomAddNew), combinedDrawable, false);
        }

        private void bindPassword(@NonNull RecyclerView.ViewHolder holder, int position) {
            PasswordData passwordData = items.get(position - POSITION_BASE);
            TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
            textCell.setText(passwordData.id, true);
            textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
            textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, FiltersSetupActivity.TextCell.class, FiltersSetupActivity.FilterCell.class, FiltersSetupActivity.SuggestedFilterCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));

        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{FiltersSetupActivity.FilterCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{FiltersSetupActivity.FilterCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{FiltersSetupActivity.FilterCell.class}, new String[]{"moveImageView"}, null, null, null, Theme.key_stickers_menu));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{FiltersSetupActivity.FilterCell.class}, new String[]{"optionsImageView"}, null, null, null, Theme.key_stickers_menu));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{FiltersSetupActivity.FilterCell.class}, new String[]{"optionsImageView"}, null, null, null, Theme.key_stickers_menuSelector));

        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{FiltersSetupActivity.TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueText2));
        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{FiltersSetupActivity.TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(listView, 0, new Class[]{FiltersSetupActivity.TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_checkboxCheck));

        themeDescriptions.add(new ThemeDescription(listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));

        return themeDescriptions;
    }
}
