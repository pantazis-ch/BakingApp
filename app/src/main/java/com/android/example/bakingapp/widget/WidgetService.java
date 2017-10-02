package com.android.example.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Work-PC on 9/29/2017.
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //return new WidgetDataProvider(this, intent);
        return new WidgetDataProvider(this, intent);
    }
}
