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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

/*****************************************************************************
 * GetAppCacheTask - AsyncTask - AsyncTask to get all Home apps. 
 * 
 * @author Russell T Mackler
 * @version 1.0
 * @since 1.0
 */
class GetAppCacheTask extends AsyncTask<Void, HashMap<String, AppInfo>, Integer>
{
    private final static String      DEBUG_TAG                = GetAppCacheTask.class.getSimpleName();
    
    // Used to make use of the package manager
    private PackageManager           pm                       = null;
    private Context                  context                  = null;
    private HomeManagerArrayAdapter  homeManagerArrayAdapter  = null;
    private boolean                  isFinishedBuildingList   = false;
    
    // The cached list of installed home applications
    public static ArrayList<AppInfo> listAppInfo;
    
    
    /*****************************************************************************
     * GetAppCacheTask - AsyncTask - AsyncTask to get all Home apps. 
     * 
     * @param context - Context - The Activity's context
     * @param listAppInfo - ArrayList<AppInfo> To fill out
     * @param homeManagerArrayAdapter - The Array Adapter for the Home manager list
     * @param isFinishedBuildingList - boolean if list is finished being built
     */
    public GetAppCacheTask( Context context, 
                            ArrayList<AppInfo> listAppInfo,
                            HomeManagerArrayAdapter homeManagerArrayAdapter,
                            boolean isFinishedBuildingList )
    {
        this.context = context;
        this.listAppInfo = listAppInfo;
        this.homeManagerArrayAdapter = homeManagerArrayAdapter;
        this.isFinishedBuildingList = isFinishedBuildingList;
        // Get Android's Package Manager
        pm = context.getPackageManager();
    }
    
    
    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @SuppressWarnings ( "unchecked" ) @Override protected Integer doInBackground( Void... unused )
    {
        // Keep track of RuntimeException if Media is no longer mounted
        Integer isOK = 1;
        
        // Default app if one exists
        String defaultPackageName = "none";
        
        // Get the running process memory
        HashMap<String, Integer> pidMap = HomeManagerActivity.getRunningProcess( this.context );

        try
        {
            // Clean out the cache before putting anything new in it.
            if( listAppInfo != null )
            {
                if( !listAppInfo.isEmpty() )
                {
                    listAppInfo.clear();
                }
            }
            else
            {
                listAppInfo = new ArrayList<AppInfo>();
            }

            // This will give me a list of Home Activities....
            Intent home_intent = new Intent( Intent.ACTION_MAIN );
            home_intent.addCategory( Intent.CATEGORY_HOME );
            home_intent.addCategory( Intent.CATEGORY_DEFAULT );

            // Intent PluginGetIntent = new Intent(
            List<ResolveInfo> installedHomeApps = null;

            // This specifically gets all of the Activities that are the
            // Home Activity
            installedHomeApps = pm.queryIntentActivities( home_intent, PackageManager.MATCH_DEFAULT_ONLY );
            
            // Get the default home app first
            ResolveInfo res = AppInfo.getHomeApp( this.context );
            
            // Only display default information if there is a default
            if ( res != null )
            {  
                try
                {
                    // Create the default home app's information and put it on the ListView
                    defaultPackageName    = res.activityInfo.packageName;
                    String appName        = pm.getApplicationLabel( pm.getApplicationInfo( defaultPackageName, PackageManager.GET_META_DATA ) ).toString();
                    Drawable iconDrawable = pm.getApplicationIcon( defaultPackageName );
                    String versionName    = pm.getPackageInfo( defaultPackageName, PackageManager.SIGNATURE_MATCH ).versionName;
                    boolean isDefaultApp  = true;
                    
                    // Get this packages memory if it is running
                    int memory = 0;
                    if ( pidMap.get( defaultPackageName ) != null )
                    {
                        memory = HomeManagerActivity.getPkgMemory( pidMap.get( defaultPackageName ), this.context );
                    }
                    
                    // Create the default AppInfo object
                    AppInfo appInfo = new AppInfo( appName, versionName, defaultPackageName, String.valueOf( memory ) + "M", iconDrawable, isDefaultApp );
                    
                    // Place the AppInfo within the map
                    HashMap<String, AppInfo> map = new HashMap<String, AppInfo>();
                    map.put( Integer.toString( 0 ), appInfo );

                    // Publish to create the cache
                    publishProgress( map );
                }
                catch( NameNotFoundException e )
                {
                    Log.e( DEBUG_TAG, "doInBackground : NameNotFoundException" );
                    e.printStackTrace();
                }
            } // End if ( res != null )
            
            // Loop through all activities and get their information
            for ( int i = 0; i < installedHomeApps.size(); i++ )
            {
                try
                {
                    // Get PackageName
                    String packageName = installedHomeApps.get( i ).activityInfo.packageName;
                    
                    // Check is this is the Preferred Package
                    if ( !packageName.equals( defaultPackageName ))
                    {
                        // This should never be the default home app
                        boolean isDefaultApp = false;
                        
                        // Get Application Name or null if there is an issue
                        String appName = pm.getApplicationLabel( pm.getApplicationInfo( packageName, PackageManager.GET_META_DATA ) ).toString();

                        // Get Application Icon as a Drawable
                        Drawable iconDrawable = pm.getApplicationIcon( installedHomeApps.get( i ).activityInfo.packageName );

                        // Get Application versionName
                        String versionName = pm.getPackageInfo( packageName, PackageManager.SIGNATURE_MATCH ).versionName;
                        
                        // Get this packages memory if it is running
                        int memory = 0;
                        if ( pidMap.get( packageName ) != null )
                        {
                            memory = HomeManagerActivity.getPkgMemory( pidMap.get( packageName ), this.context );
                        }
                        
                        AppInfo appInfo = new AppInfo( appName, versionName, packageName, String.valueOf( memory ) + "M", iconDrawable, isDefaultApp );
                        
                        // Place the AppInfo within the map
                        HashMap<String, AppInfo> map = new HashMap<String, AppInfo>();
                        map.put( Integer.toString( i ), appInfo );

                        publishProgress( map );
                    } // End if ( !packageName.equals( defaultPackageName ))
                }
                catch( NameNotFoundException e )
                {
                    Log.e( DEBUG_TAG, "doInBackground : NameNotFoundException" );
                    e.printStackTrace();
                }
                catch( IndexOutOfBoundsException e )
                {
                    Log.e( DEBUG_TAG, "doInBackground : IndexOutOfBoundsException" );
                    e.printStackTrace();
                }
            } // End for loop pluginActivities
        }
        catch( RuntimeException e )
        {
            Log.e( DEBUG_TAG, "doInBackground : RuntimeException" );
            e.printStackTrace();

            // Set to 0 to exit this activity
            isOK = 0;
        }
        catch( OutOfMemoryError e )
        {
            Log.e( DEBUG_TAG, "doInBackground : OutOfMemoryError" );
            e.printStackTrace();

            // Set to 0 to exit this activity
            isOK = 0;
        }

        return ( isOK );
    } // End doInBackground


    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override protected void onProgressUpdate( HashMap<String, AppInfo>... map )
    {
        if( listAppInfo != null )
        {
            // Put the First image within the directory within the cache
            listAppInfo.addAll( map[0].values() );
            
            if ( homeManagerArrayAdapter != null  )
            {
                homeManagerArrayAdapter.notifyDataSetChanged();
            }
        }
    } // End onProgressUpdate


    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override protected void onPostExecute( Integer isOK )
    {
        // Should we exit this activity due to an exception
        if( isOK == 0 )
        {
            // XXX : todo finish();
        }

        HomeManagerActivity.isFinishedBuildingList = true;
        this.isFinishedBuildingList = true;

        // Sort all Image array based on Date taken
        Collections.sort( listAppInfo, AppInfo.NAME_ORDER );
        Collections.sort( listAppInfo, AppInfo.DEFAULT_ORDER );
        
        if ( homeManagerArrayAdapter != null  )
        {
            homeManagerArrayAdapter.notifyDataSetChanged();
        }
    } // End onPostExecute
} // End GetAppCacheTask