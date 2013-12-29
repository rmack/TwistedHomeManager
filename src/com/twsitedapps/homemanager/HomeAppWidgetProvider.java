/*****************************************************************************
 * Copyright 2011 Twisted Apps LLC
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 */

package com.twsitedapps.homemanager;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.RemoteViews;
import android.widget.Toast;


/*****************************************************************************
 * HomeAppWidgetProvider - (Beta) - Manage the beta widget to change home
 * applications.  This was quickly hacked together for a widget.
 * 
 * IDEAS: <br>
 * 1. Notification to change home apps <br>
 * 2. Press home button and home apps change <br>
 * 3. Visual widget <br>
 * 
 * @author Russell T Mackler
 * @version 1.0
 * @since 1.0
 */
public class HomeAppWidgetProvider extends AppWidgetProvider
{
    @SuppressWarnings ( "unused" ) private final static String DEBUG_TAG       = HomeAppWidgetProvider.class.getSimpleName();

    public static String                                       ACTION_NEXTHOME = "NextHome";
    public static String                                       ACTION_PREVHOME = "PrevHome";

    /*
     * (non-Javadoc)
     * @see android.appwidget.AppWidgetProvider#onEnabled(android.content.Context)
     */
    @Override public void onEnabled( Context context )
    {
        // Log.d(TAG, "onEnabled");
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting( new ComponentName( "com.twsitedapps.homemanager", ".HomeAppWidgetProvider" ),
                                       PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                       PackageManager.DONT_KILL_APP );
    }


    /*
     * (non-Javadoc)
     * @see android.appwidget.AppWidgetProvider#onDeleted(android.content.Context, int[])
     */
    @Override public void onDeleted( Context context, int[] appWidgetIds )
    {
        // Log.d(TAG, "onDeleted");
        // When the user deletes the widget, delete the preference associated
        // with it.
        final int N = appWidgetIds.length;
        for ( int i = 0; i < N; i++ )
        {
            // ExampleAppWidgetConfigure.deleteTitlePref(context,
            // appWidgetIds[i]);
        }
    }


    /*
     * (non-Javadoc)
     * @see android.appwidget.AppWidgetProvider#onUpdate(android.content.Context, android.appwidget.AppWidgetManager, int[])
     */
    @Override public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds )
    {
        for ( int widgetId : appWidgetIds )
        {
            // Toast.makeText( context, "onUpdate", Toast.LENGTH_SHORT ).show();

            RemoteViews remoteViews = new RemoteViews( context.getPackageName(), R.layout.widget_layout );

            Intent nextHomeAppIntent = new Intent( context, HomeAppWidgetProvider.class );
            nextHomeAppIntent.setAction( ACTION_NEXTHOME );
            nextHomeAppIntent.putExtra( "msg", context.getResources().getString( R.string.next ) );

            Intent prevHomeAppIntent = new Intent( context, HomeAppWidgetProvider.class );
            prevHomeAppIntent.setAction( ACTION_PREVHOME );
            prevHomeAppIntent.putExtra( "msg", context.getResources().getString( R.string.previous ) );

            //
            PendingIntent nextHomeAppPendingIntent = PendingIntent.getBroadcast( context, 0, nextHomeAppIntent, 0 );
            PendingIntent prevHomeAppPendingIntent = PendingIntent.getBroadcast( context, 0, prevHomeAppIntent, 0 );

            remoteViews.setOnClickPendingIntent( R.id.buttonNextHomeApp, nextHomeAppPendingIntent );
            remoteViews.setOnClickPendingIntent( R.id.buttonPreviousHomeApp, prevHomeAppPendingIntent );

            appWidgetManager.updateAppWidget( widgetId, remoteViews );
        }
    }


    /*
     * (non-Javadoc)
     * @see android.appwidget.AppWidgetProvider#onReceive(android.content.Context, android.content.Intent)
     */
    @Override public void onReceive( Context context, Intent intent )
    {
        // this takes care of managing the widget
        // v1.5 fix that doesn't call onDelete Action
        final String action = intent.getAction();

        if( AppWidgetManager.ACTION_APPWIDGET_DELETED.equals( action ) )
        {
            final int appWidgetId = intent.getExtras().getInt( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID );
            if( appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID )
            {
                this.onDeleted( context, new int[] { appWidgetId } );
            }
        }
        else
        {
            // check, if our Action was called

            if( intent.getAction().equals( ACTION_NEXTHOME ) )
            {
                Toast.makeText( context, context.getResources().getString( R.string.next ), Toast.LENGTH_SHORT ).show();

                // Set the Next Home app
                context.sendBroadcast( new Intent( StaticConfig.NEXT_HOME ) );
            }
            else if( intent.getAction().equals( ACTION_PREVHOME ) )
            {
                Toast.makeText( context, context.getResources().getString( R.string.previous ), Toast.LENGTH_SHORT ).show();

                // Set the Previous Home app
                context.sendBroadcast( new Intent( StaticConfig.PREV_HOME ) );
            }
            else
            {
                // do nothing
            }

            super.onReceive( context, intent );
        }
    }
} // End HomeAppWidgetProvider
// EOF