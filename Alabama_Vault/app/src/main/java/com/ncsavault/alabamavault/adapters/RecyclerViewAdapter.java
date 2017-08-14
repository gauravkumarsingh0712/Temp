package com.ncsavault.alabamavault.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.NativeExpressAdView;
import com.ncsavault.alabamavault.R;
import com.ncsavault.alabamavault.carousel.CarouselLayoutManager;
import com.ncsavault.alabamavault.carousel.CarouselZoomPostLayoutListener;
import com.ncsavault.alabamavault.carousel.CenterScrollListener;
import com.ncsavault.alabamavault.carousel.DefaultChildSelectionListener;
import com.ncsavault.alabamavault.dto.MenuItem;
import com.ncsavault.alabamavault.utils.CarouselEffectTransformer;
import com.ncsavault.alabamavault.views.HomeScreen;


import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * The {@link RecyclerViewAdapter} class.
 * <p>The adapter provides access to the items in the {@link MenuItemViewHolder}
 * or the {@link NativeExpressAdViewHolder}.</p>
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // A menu item view type.
    public static final int MENU_ITEM_VIEW_TYPE = 1;

    // The Native Express ad view type.
    public static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 2;

    // Header view type
    private static final int HEADER_VIEW = 0;

    // An Activity's Context.
    private final Context mContext;

    // The list of Native Express ads and menu items.
    private final List<Object> mRecyclerViewItems;

    private int displayHeight = 0, displayWidth = 0;

    /**
     * For this example app, the recyclerViewItems list contains only
     * {@link android.view.MenuItem} and {@link NativeExpressAdView} types.
     */
    public RecyclerViewAdapter(Context context, List<Object> recyclerViewItems) {
        this.mContext = context;
        this.mRecyclerViewItems = recyclerViewItems;
        getScreenDimensions();
    }

    /**
     * The {@link MenuItemViewHolder} class.
     * Provides a reference to each view in the menu item view.
     */
    public class MenuItemViewHolder extends RecyclerView.ViewHolder {
        private TextView menuItemName;
        private ImageView menuItemImage;

        MenuItemViewHolder(View view) {
            super(view);
            menuItemImage = (ImageView) view.findViewById(R.id.menu_item_image);
            menuItemName = (TextView) view.findViewById(R.id.menu_item_name);
        }
    }

    public void getScreenDimensions() {

        Point size = new Point();
        WindowManager w = HomeScreen.activity.getWindowManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            w.getDefaultDisplay().getSize(size);
            displayHeight = size.y;
            displayWidth = size.x;
        } else {
            Display d = w.getDefaultDisplay();
            displayHeight = d.getHeight();
            displayWidth = d.getWidth();
        }
    }

    /**
     * The {@link NativeExpressAdViewHolder} class.
     */
    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

        NativeExpressAdViewHolder(View view) {
            super(view);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {

        private ViewPager pager;
        private TextView topTenText;

        public VHHeader(View itemView) {
            super(itemView);
            pager = (ViewPager) itemView.findViewById(R.id.pager_introduction);
            topTenText = (TextView) itemView.findViewById(R.id.top_ten_text_view);

            }

    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }

    /**
     * Determines the view type for the given position.
     */
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return HEADER_VIEW;

        if (position % 5 == 0)
            return NATIVE_EXPRESS_AD_VIEW_TYPE;
        return MENU_ITEM_VIEW_TYPE;
    }

    /**
     * Creates a new view for a menu item view or a Native Express ad view
     * based on the viewType. This method is invoked by the layout manager.
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {

            case HEADER_VIEW:
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.home_screen_horizental_layout, viewGroup, false);
                return new VHHeader(v);
            case MENU_ITEM_VIEW_TYPE:
                View menuItemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                        R.layout.menu_item_container, viewGroup, false);
                return new MenuItemViewHolder(menuItemLayoutView);
            case NATIVE_EXPRESS_AD_VIEW_TYPE:

                View nativeExpressLayoutView = LayoutInflater.from(
                        viewGroup.getContext()).inflate(R.layout.native_express_ad_container,
                        viewGroup, false);
                return new NativeExpressAdViewHolder(nativeExpressLayoutView);
        }
        return null;

    }

    private void setHorizentalPager(RecyclerView.ViewHolder holder)
    {
//        VHHeader vhHeader = (VHHeader)holder;
//        vhHeader.pager.setClipChildren(false);
//        //vhHeader.pager.setPageMargin(mContext.getResources().getDimensionPixelOffset(R.dimen.pager_margin));
//        vhHeader.pager.setOffscreenPageLimit(3);
//        vhHeader.pager.setPageTransformer(false, new CarouselEffectTransformer(mContext,displayWidth));
//
//        HorizontalPagerAdapter adapter = new HorizontalPagerAdapter(mContext, Arra);
//        vhHeader.pager.setAdapter(adapter);
//
//        vhHeader.pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//
//            private int index = 0;
//
//            @Override
//            public void onPageSelected(int position) {
//                index = position;
//
//            }
//
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                if (state == ViewPager.SCROLL_STATE_IDLE) {
//                }
//            }
//        });


       /* vhHeader.topTenText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(mContext,TopTenVideoScreen.class);
//                mContext.startActivity(intent);
            }
        });*/

    }

    /**
     *  Replaces the content in the views that make up the menu item view and the
     *  Native Express ad view. This method is invoked by the layout manager.
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case HEADER_VIEW:
            {
                setHorizentalPager(holder);
//                TestViewHolder vhHeader = (TestViewHolder)holder;
//                initRecyclerView(vhHeader.recyclerView, new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false));
            }
            break;
            case MENU_ITEM_VIEW_TYPE:
                try {
                            MenuItemViewHolder menuItemHolder = (MenuItemViewHolder) holder;
                            final MenuItem menuItem = (MenuItem) mRecyclerViewItems.get(position);

                            menuItemHolder.menuItemImage.setImageResource(HomeScreen.listItems[position]);
                            menuItemHolder.menuItemName.setText(menuItem.getName());

                            menuItemHolder.menuItemImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
//                                    Intent intent = new Intent(mContext,PlayerProfileScreen.class);
//                                    mContext.startActivity(intent);
                                }
                            });
                }catch(Exception e)
                {

                }
                break;
            case NATIVE_EXPRESS_AD_VIEW_TYPE:
                try {
                        NativeExpressAdViewHolder nativeExpressHolder =
                                (NativeExpressAdViewHolder) holder;
                        NativeExpressAdView adView =
                                (NativeExpressAdView) mRecyclerViewItems.get(position);
                        ViewGroup adCardView = (ViewGroup) nativeExpressHolder.itemView;

                        if (adCardView.getChildCount() > 0) {
                            adCardView.removeAllViews();
                        }
                        if (adView.getParent() != null) {
                            ((ViewGroup) adView.getParent()).removeView(adView);
                        }

                        // Add the Native Express ad to the native express ad view.
                        adCardView.addView(adView);

                }catch(Exception e)
                {

                }

                break;
        }

    }

    private static final class TestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @SuppressWarnings("UnsecureRandomNumberGeneration")
        private final Random mRandom = new Random();
        private final int[] mColors;
        private final int[] mPosition;
        private int mItemsCount = 10;

        TestAdapter() {
            mColors = new int[mItemsCount];
            mPosition = new int[mItemsCount];
            for (int i = 0; mItemsCount > i; ++i) {
                //noinspection MagicNumber
                mColors[i] = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
                mPosition[i] = i;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
            return new ItemViewHolder(v);
             }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemViewHolder mHolder = (ItemViewHolder)holder;
            mHolder.cItem1.setText(String.valueOf(mPosition[position]));
            mHolder.cItem2.setText(String.valueOf(mPosition[position]));
            mHolder.relativeLayout.setBackgroundColor(mColors[position]);
        }



        @Override
        public int getItemCount() {
            return mItemsCount;
        }
    }

    private static class TestViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TestViewHolder(View v) {
            super(v);
          //  recyclerView = (RecyclerView) v.findViewById(R.id.list_horizontal);

        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView cItem1;
        TextView cItem2;
        RelativeLayout relativeLayout;

        ItemViewHolder(View v) {
            super(v);
            cItem1 = (TextView) v.findViewById(R.id.c_item_1);
            cItem2 = (TextView) v.findViewById(R.id.c_item_2);
            relativeLayout = (RelativeLayout)  v.findViewById(R.id.layout);
        }
    }

    private void initRecyclerView(final RecyclerView recyclerView, final CarouselLayoutManager layoutManager) {
        // enable zoom effect. this line can be customized

        final TestAdapter testAdapter = new TestAdapter();
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        layoutManager.setMaxVisibleItems(2);

        recyclerView.setLayoutManager(layoutManager);
        // we expect only fixed sized item for now
        recyclerView.setHasFixedSize(true);
        // sample adapter with random data
        recyclerView.setAdapter(testAdapter);
        // enable center post scrolling
        recyclerView.addOnScrollListener(new CenterScrollListener());
        // enable center post touching on item and item click listener
        DefaultChildSelectionListener.initCenterItemListener(new DefaultChildSelectionListener.OnCenterItemClickListener() {
            @Override
            public void onCenterItemClicked(@NonNull final RecyclerView recyclerView, @NonNull final CarouselLayoutManager carouselLayoutManager, @NonNull final View v) {
                final int position = recyclerView.getChildLayoutPosition(v);
                final String msg = String.format(Locale.US, "Item %1$d was clicked", position);
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        }, recyclerView, layoutManager);

        layoutManager.addOnItemSelectionListener(new CarouselLayoutManager.OnCenterItemSelectionListener() {

            @Override
            public void onCenterItemChanged(final int adapterPosition) {
                if (CarouselLayoutManager.INVALID_POSITION != adapterPosition) {
                    final int value = testAdapter.mPosition[adapterPosition];
/*
                    adapter.mPosition[adapterPosition] = (value % 10) + (value / 10 + 1) * 10;
                    adapter.notifyItemChanged(adapterPosition);
*/
                }
            }
        });
    }

}
