package com.coretera.clientview.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.coretera.clientview.R;
import com.coretera.clientview.utility.setting;

import java.util.ArrayList;
import java.util.List;

public class SlidingImage_Adapter extends PagerAdapter {
    //private ArrayList<Integer> IMAGES;
    private List<Bitmap> IMAGES = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;


    public SlidingImage_Adapter(Context context,List<Bitmap> IMAGES) {
        this.context = context;
        this.IMAGES=IMAGES;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);


        //imageView.setImageResource(IMAGES.get(position));
        imageView.setImageBitmap(IMAGES.get(position));

        Integer imageScaleType = setting.GetImageScaleType(this.context);
        switch (imageScaleType){
            case 1:
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                break;
            case 2:
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                break;
            case 3:
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                break;
            case 4:
                imageView.setScaleType(ImageView.ScaleType.FIT_START);
                break;
            case 5:
                imageView.setScaleType(ImageView.ScaleType.FIT_END);
                break;
            case 6:
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                break;
            case 7:
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case 8:
                imageView.setScaleType(ImageView.ScaleType.MATRIX);
                break;
        }

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
