/*****************************************************************************
 *    Copyright 2011 Twisted Apps LLC
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package com.twsitedapps.homemanager;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/*****************************************************************************
 * MainBroadcastReceiver - (Beta) - Manage the beta widget to change home
 * applications.  This was quickly hacked together for a widget.
 * 
 * IDEAS:
 * 1. Notification to change home apps
 * 2. Press home button and home apps change
 * 3. Visual widget
 * 
 * @author Russell T Mackler
 * @version 1.0
 * @since 1.0
 */
public class MainBroadcastReceiver extends BroadcastReceiver
{
    private final static String DEBUG_TAG = MainBroadcastReceiver.class.getSimpleName();

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
     */
    @Override public void onReceive( final Context context,
                                     Intent intent )
    {
        try
        {
            // Get home applications
            if ( HomeManagerActivity.listAppInfo != null )
            {
                if ( HomeManagerActivity.listAppInfo.isEmpty() )
                {
                    // Set this so we know the installed Home App list has been built
                    HomeManagerActivity.isFinishedBuildingList = false;
                    
                    new GetAppCacheTask( context, HomeManagerActivity.listAppInfo, null, HomeManagerActivity.isFinishedBuildingList ).execute();
                }
            }
            else
            {
                // Set this so we know the installed Home App list has been built
                HomeManagerActivity.isFinishedBuildingList = false;
                
                // Create the cached ArrayList of home applications
                HomeManagerActivity.listAppInfo = new ArrayList<AppInfo>();
                new GetAppCacheTask( context, HomeManagerActivity.listAppInfo, null, HomeManagerActivity.isFinishedBuildingList ).execute();
            }
        }
        catch( IllegalStateException e )
        {
            Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "onResume : IllegalStateException" );
            e.printStackTrace();
        }
        catch( Exception e )
        {
            Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "onResume : Exception" );
            e.printStackTrace();
        }
        
        // When the device reboots
        if ( intent.getAction().toString().equals( "android.intent.action.BOOT_COMPLETED" ) )
        {
            // XXX : Note this was a user request; to have
            // THM launch one home activity, and then wait then launch another home activity.
            // XXX : Currently I am not adding this to the master branch this was just a test
            
            // Build the intent to launch
            Intent AppIntent = new Intent( Intent.ACTION_MAIN );
            AppIntent.addCategory( Intent.CATEGORY_HOME );
            //AppIntent.setPackage( "com.teslacoilsw.launcher" );
            AppIntent.setPackage( "com.launcher" );
            AppIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );

            // Make sure the selected application is Callable
            if( HomeManagerActivity.isCallable( context, AppIntent ) )
            {                           
                // Start the selected application
                context.startActivity( AppIntent );
                Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "Launch - com.launcher" );
            }
            else
            {
                // Display user feedback if the home app is not callable
                Toast.makeText( context.getApplicationContext(), context.getResources().getString(R.string.installed) + "com.launcher" + context.getResources().getString( R.string.not_callable ), Toast.LENGTH_SHORT ).show();
                Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + context.getResources().getString(R.string.installed) + "com.launcher" + context.getResources().getString( R.string.not_callable ) );
            }
            
            new Thread( new Runnable() {
                public void run()
                {
                    try
                    {
                        Thread.sleep( 5000 );
                    }
                    catch( InterruptedException e )
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    
                    // Build the intent to launch
                    Intent AppIntent = new Intent( Intent.ACTION_MAIN );
                    AppIntent.addCategory( Intent.CATEGORY_HOME );
                    //AppIntent.setPackage( "org.adw.launcher" );
                    AppIntent.setPackage( "com.dlto.atom.launcher" );
                    AppIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );

                    // Make sure the selected application is Callable
                    if( HomeManagerActivity.isCallable( context, AppIntent ) )
                    {                           
                        // Start the selected application
                        context.startActivity( AppIntent );
                        Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "Launch - com.dlto.atom.launcher" );
                    }
                    else
                    {
                        // Display user feedback if the home app is not callable
                        Toast.makeText( context.getApplicationContext(), StaticConfig.TWISTED_TAG + context.getResources().getString(R.string.installed) + "com.dlto.atom.launcher" + context.getResources().getString( R.string.not_callable ), Toast.LENGTH_SHORT ).show();
                    }
                    
                }
            } ).start();
            
        }
        
        if ( intent.getAction().toString().equals( StaticConfig.NEXT_HOME ) )
        {
            try
            {               
                try
                {
                    // Get the name of the current selected home app
                    // XXX : how to manage the list correct as we step through each home app installed?
                    String name = HomeManagerActivity.listAppInfo.get( StaticConfig.position ).getappName();

                    // Get the package name of the current selected home app
                    // XXX : how to manage the list correct as we step through each home app installed?
                    String packagename = HomeManagerActivity.listAppInfo.get( StaticConfig.position ).getpackageName();

                    // Build the intent to launch
                    Intent AppIntent = new Intent( Intent.ACTION_MAIN );
                    AppIntent.addCategory( Intent.CATEGORY_HOME );
                    AppIntent.setPackage( packagename );
                    AppIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );

                    // Make sure the selected application is Callable
                    if( HomeManagerActivity.isCallable( context, AppIntent ) )
                    {                           
                        // Start the selected application
                        context.startActivity( AppIntent );
                    }
                    else
                    {
                        // Display user feedback if the home app is not callable
                        Toast.makeText( context.getApplicationContext(), context.getResources().getString(R.string.installed) + name + context.getResources().getString( R.string.not_callable ), Toast.LENGTH_SHORT ).show();
                    }
                    
                    if ( StaticConfig.position == HomeManagerActivity.listAppInfo.size()-1 )
                    {
                        StaticConfig.position = 0;
                    }
                    else
                    {
                        StaticConfig.position++;
                    }
                }
                catch( NullPointerException e )
                {
                    Log.e( DEBUG_TAG, "Launch App : NullPointerException" );
                    e.printStackTrace();
                }
                catch( SecurityException e )
                {
                    Toast.makeText( context.getApplicationContext(), context.getResources().getString( R.string.securityException ), Toast.LENGTH_SHORT ).show();
                    Log.e( DEBUG_TAG, "Launch App : SecurityException" );
                    e.printStackTrace();
                }
            }
            catch ( NullPointerException e )
            {
                Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "BroadcastReceiver : NullPointerException" );
                e.printStackTrace();
            }
            catch ( RuntimeException e )
            {
                Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "BroadcastReceiver : RuntimeException" );
                e.printStackTrace();
            }
        }
        else if ( intent.getAction().toString().equals( StaticConfig.PREV_HOME ) )
        {
            try
            {
                // Set the Previous Wallpaper
                //WallpaperUtil.previousWallpaper( context );

            }
            catch ( NullPointerException e )
            {
                Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "BroadcastReceiver : NullPointerException" );
                e.printStackTrace();
            }
            catch ( RuntimeException e )
            {
                Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "BroadcastReceiver : RuntimeException" );
                e.printStackTrace();
            }
        }
    } // End onReceive
} // End class MainBroadcastReceiver
// EOF