package com.ncsavault.alabamavault.customviews;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.widget.MediaController;

import com.ncsavault.alabamavault.R;


/**
 * Created by aqeeb.pathan on 27-10-2015.
 */
public class CustomMediaController extends MediaController {
    public CustomMediaController(Context context) {
        super(new ContextThemeWrapper(context, R.style.MediaPlayer));
    }
}
