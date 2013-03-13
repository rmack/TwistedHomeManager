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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


/*****************************************************************************
 * HomeManagerActivity - Activity - display the installed home apps via a
 * listview. 
 * 
 * @author Russell T Mackler
 * @version 1.0
 * @since 1.0
 */
public class HomeManagerActivity extends Activity
{
    private final static String      DEBUG_TAG                = HomeManagerActivity.class.getSimpleName();

    // This activity
    private Activity                 thisActivity             = null;
    
    // The Array Adapter for this ListView
    private HomeManagerArrayAdapter  homeManagerArrayAdapter;
    
    // The cached list of installed home applications
    public static ArrayList<AppInfo> listAppInfo;

    // Set to true when the Async Task is finished building the installed home app list
    public static boolean            isFinishedBuildingList   = false;

    // Used for creating the intent for InstalledAppDetails
    private static final String      SCHEME                   = "package";
    private static final String      APP_PKG_NAME_21          = "com.android.settings.ApplicationPkgName";
    private static final String      APP_PKG_NAME_22          = "pkg";
    private static final String      APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String      APP_DETAILS_CLASS_NAME   = "com.android.settings.InstalledAppDetails";


    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override public void onCreate( Bundle savedInstanceState )
    {
        try
        {
            super.onCreate( savedInstanceState );
            setContentView( R.layout.homemanageractivity );
    
            thisActivity = this;
    
            // Get the preferences for this app
            AppPreferences.getPrefs( thisActivity );
    
            // Create the cached ArrayList of home applications
            listAppInfo = new ArrayList<AppInfo>();
    
            // Set the ListView
            ListView mainListView = (ListView) findViewById( R.id.main_listview );
            
            // Set the background color to black
            mainListView.setBackgroundColor( Color.BLACK );
    
            // Create the Array Adapter
            homeManagerArrayAdapter = new HomeManagerArrayAdapter( thisActivity, listAppInfo );
    
            // Set the adapter for the ListView
            mainListView.setAdapter( homeManagerArrayAdapter );
    
            // ListView LongClick Logic
            mainListView.setLongClickable( true );
            mainListView.setOnItemLongClickListener( new OnItemLongClickListener() 
            {
                public boolean onItemLongClick( AdapterView<?> parent, View view, int position, long id )
                {
                    // Get the name of the current selected home app
                    String name = listAppInfo.get( position ).getappName();
    
                    // Get the package name of the current selected home app 
                    String pkgname = listAppInfo.get( position ).getpackageName();
                    
                    Log.i( DEBUG_TAG, "Package Name = [" + pkgname + "]" );
    
                    // Make the right Intent to start InstalledAppDetails
                    Intent it = makeIntentInstalledAppDetails( pkgname );
    
                    // Make sure the selected application is Callable
                    if( isCallable( thisActivity, it ) )
                    {
                        // Start the selected application
                        startActivity( it );
                    }
                    else
                    {
                        // Display user feedback if the home app is not callable
                        Toast.makeText( getApplicationContext(), getResources().getString(R.string.installed) + name + getResources().getString( R.string.not_callable ), Toast.LENGTH_SHORT ).show();
                    }
    
                    return ( true );
                }
            } );
    
            // Launch a listed application if selected
            mainListView.setOnItemClickListener( new OnItemClickListener() 
            {
                public void onItemClick( AdapterView<?> parent, View view, int position, long id )
                {
                    try
                    {
                        // Get the name of the current selected home app
                        String name = listAppInfo.get( position ).getappName();
    
                        // Get the package name of the current selected home app
                        String packagename = listAppInfo.get( position ).getpackageName();
    
                        // Build the intent to launch
                        Intent AppIntent = new Intent( Intent.ACTION_MAIN );
                        AppIntent.addCategory( Intent.CATEGORY_HOME );
                        AppIntent.setPackage( packagename );
                        AppIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
    
                        // Make sure the selected application is Callable
                        if( isCallable( thisActivity, AppIntent ) )
                        {                           
                            // Start the selected application
                            startActivity( AppIntent );
                        }
                        else
                        {
                            // Display user feedback if the home app is not callable
                            Toast.makeText( getApplicationContext(), getResources().getString(R.string.installed) + name + getResources().getString( R.string.not_callable ), Toast.LENGTH_SHORT ).show();
                        }
                    }
                    catch( NullPointerException e )
                    {
                        Log.e( DEBUG_TAG, "Launch App : NullPointerException" );
                        e.printStackTrace();
                    }
                    catch( SecurityException e )
                    {
                        Toast.makeText( getApplicationContext(), getResources().getString( R.string.securityException ), Toast.LENGTH_SHORT ).show();
                        Log.e( DEBUG_TAG, "Launch App : SecurityException" );
                        e.printStackTrace();
                    }
    
                } // End onItemClick
            } ); // End mainListView.setOnItemClickListener
    
            // Clear Default logic
            Button btnClearDefault = (Button) findViewById( R.id.btnClearDefault );
            btnClearDefault.setOnClickListener( new View.OnClickListener() 
            {
                public void onClick( View v )
                {
                    ResolveInfo res = AppInfo.getHomeApp( thisActivity );
    
                    if( res != null )
                    {
                        // Get the name of the current selected home app
                        String name = res.activityInfo.name;
    
                        // Get the package name of the current selected home app
                        String pkgname = res.activityInfo.packageName;
    
                        // Make the right Intent to start InstalledAppDetails
                        Intent it = makeIntentInstalledAppDetails( pkgname );
    
                        // Make sure the selected application is Callable
                        if( isCallable( thisActivity, it ) )
                        {
                            // Start the selected application
                            startActivity( it );
                        }
                        else
                        {
                            // Display user feedback if the home app is not callable
                            Toast.makeText( getApplicationContext(), getResources().getString(R.string.installed) + name + getResources().getString( R.string.not_callable ), Toast.LENGTH_SHORT ).show();
                        }
                    }
                    else
                    {
                        Toast.makeText( getApplicationContext(), getResources().getString(R.string.noDefaultSet), Toast.LENGTH_SHORT ).show();
                    }
                }
            } );
    
            // Set Default logic
            Button btnSetDefault = (Button) findViewById( R.id.btnSetDefault );
            btnSetDefault.setOnClickListener( new View.OnClickListener() 
            {
                public void onClick( View v )
                {
                    if( AppInfo.getHomeApp( thisActivity ) == null )
                    {
                        // Used to have the user select the default app...
                        Intent selector = new Intent( Intent.ACTION_MAIN );
                        selector.addCategory( Intent.CATEGORY_HOME );
                        selector.setComponent( new ComponentName( "android", "com.android.internal.app.ResolverActivity" ) );
                        startActivity( selector );
                    }
                    else
                    {
                        // Display user feedback
                        Toast.makeText( getApplicationContext(), getResources().getString( R.string.default_set ), Toast.LENGTH_SHORT ).show();
                    }
                }
            } );
    
            // Get Home Apps
            Button btnGetHomeApps = (Button) findViewById( R.id.btnGetHomeApps );
            btnGetHomeApps.setOnClickListener( new View.OnClickListener() 
            {
                public void onClick( View v )
                {
                    // Only allow the the Get home activity if installed home list
                    // is built
                    if( isFinishedBuildingList )
                    {
                        // Inform User that all home apps listed are on their market
                        Toast.makeText( getApplicationContext(), getResources().getString( R.string.homeapp_info ), Toast.LENGTH_SHORT ).show();
                        
                        try
                        {
                            // Start the Market App Activity
                            Intent getHomeIntent = new Intent( StaticConfig.GETHOME_INTENT );
                            
                            if( isCallable( thisActivity, getHomeIntent ) )
                            { 
                                // Start Process Image activity
                                startActivity( getHomeIntent );
                            }
                            else
                            {
                                // Display user feedback if the home app is not callable
                                Toast.makeText( getApplicationContext(), getResources().getString( R.string.homeAppNotCallable ), Toast.LENGTH_SHORT ).show();
                            }
                        }
                        catch ( ActivityNotFoundException e )
                        {
                            Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "onCreate : ActivityNotFoundException" );
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        // Display user feedback
                        Toast.makeText( getApplicationContext(), getResources().getString( R.string.please_wait ), Toast.LENGTH_SHORT ).show();
                    }
                }
            } );
        }
        catch ( NullPointerException e )
        {
            Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "onCreate : NullPointerException" );
            e.printStackTrace();
        }
        catch ( Exception e )
        {
            Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "onCreate : Exception" );
            e.printStackTrace();
        }
    } // End onCreate


    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override protected void onResume()
    {
        super.onResume();

        try
        {
            // Get the preferences for this app
            AppPreferences.getPrefs( thisActivity );

            // Set this so we know the installed Home App list has been built
            isFinishedBuildingList = false;

            // Get cached images (First image within each directory within the
            // ListView)
            new GetAppCacheTask( thisActivity.getApplicationContext(), listAppInfo, homeManagerArrayAdapter, isFinishedBuildingList ).execute();
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
    } // End onResume


    /*****************************************************************************
     * isCallable - Check to make sure Activity to start is callable
     * set by the end user <br>
     * 
     * @param a - Activity - The activity calling this method
     * @param intent - Intent - The intent that starts the application
     * 
     * @return boolean - Application can be started (true) or not (false)
     */
    public static boolean isCallable( final Context context, final Intent intent )
    {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities( intent, PackageManager.MATCH_DEFAULT_ONLY );
        return list.size() > 0;
    } // End isCallable


    /*****************************************************************************
     * isPackagePreferred - Check to see if the Package is a Preferred Activity
     * set by the end user <br>
     * 
     * @param a - Activity - The activity calling this method
     * @param packageName - String - Package name of the app
     * 
     * @return boolean - Package is set as the default
     */
    public static boolean isPackagePreferred( Activity a, String packageName )
    {
        boolean isPreferredPackage = false;

        try
        {
            final Intent intent = new Intent( Intent.ACTION_MAIN );
            intent.addCategory( Intent.CATEGORY_HOME );
            final ResolveInfo res = a.getPackageManager().resolveActivity( intent, PackageManager.MATCH_DEFAULT_ONLY );
            String pkgName = res.activityInfo.packageName;
            if( res.activityInfo == null )
            {
                // should not happen. A home is always installed, isn't it?
            }
            else if( pkgName.equals( "android" ) )
            {
                // No default selected
            }
            else
            {
                // res.activityInfo.packageName and res.activityInfo.name gives you
                // the default app
                if( packageName.equals( pkgName ) )
                {
                    isPreferredPackage = true;
                }
            }
        }
        catch( IllegalStateException e )
        {
            Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "isPackagePreferred : IllegalStateException" );
            e.printStackTrace();
        }
        catch( Exception e )
        {
            Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "isPackagePreferred : Exception" );
            e.printStackTrace();
        }

        return isPreferredPackage;
    } // End isPackagePreferred

    
    /*****************************************************************************
     * getRunningProcess - Get a list of running processes 
     * 
     * @return HashMap<String, Integer> - Package-Name, PID (Process ID)
     */
    public static HashMap<String, Integer> getRunningProcess( Context context )
    {
        ActivityManager activityManager = (ActivityManager) context.getSystemService( ACTIVITY_SERVICE );
        android.app.ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo( memoryInfo );

        List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        
        HashMap<String, Integer> pidMap = new HashMap<String, Integer>();
        for ( RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses )
        {
            pidMap.put( runningAppProcessInfo.processName, runningAppProcessInfo.pid );
            
            for( String pkg : runningAppProcessInfo.pkgList )
            {
                pidMap.put( pkg, runningAppProcessInfo.pid );
            }
        }

        return pidMap;
    } // End getRunningProcess
    
    
    /*****************************************************************************
     * getPkgMemory - Get the package's memory given it's PID 
     * 
     * @return HashMap<String, Integer> - Package-Name, PID (Process ID)
     */
    @TargetApi ( 5 ) public static int getPkgMemory( final int pid, final Context context )
    {
        int total = 0;
        
        ActivityManager activityManager = (ActivityManager) context.getSystemService( ACTIVITY_SERVICE );
        android.app.ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo( memoryInfo );
        
        int pids[] = new int[1];
        pids[0] = pid;
        android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo( pids );
        total = ( memoryInfoArray[0].getTotalSharedDirty() + 
                  memoryInfoArray[0].getTotalPss() +
                  memoryInfoArray[0].getTotalPrivateDirty()) / 1024;
        
        return total;
    } // End getPkgMemory
    

    /*****************************************************************************
     * makeIntentInstalledAppDetails - Get app's Android settings 
     * 
     * @return packageName - String - Package name of the app
     */
    public Intent makeIntentInstalledAppDetails( String packageName )
    {
        Intent intent = new Intent();

        // Build intent for API version greater than 9
        if( Build.VERSION.SDK_INT >= 9 )
        {
            intent.setAction( Settings.ACTION_APPLICATION_DETAILS_SETTINGS );
            Uri uri = Uri.fromParts( SCHEME, packageName, null );
            intent.setData( uri );
        }
        else
        // Build intent for API version 8 and below
        {
            final String appPkgName = ( Build.VERSION.SDK_INT == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21 );
            intent.setAction( Intent.ACTION_VIEW );
            intent.setClassName( APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME );
            intent.putExtra( appPkgName, packageName );
        }

        return intent;
    } // End makeIntentInstalledAppDetails


    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override public boolean onCreateOptionsMenu( final Menu menu )
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.menu, menu );
        return true;
    } // End onCreateOptionsMenu


    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override public boolean onOptionsItemSelected( final MenuItem item )
    {
        boolean return_value = false;

        // Handle item selection
        switch( item.getItemId() )
        {
        case R.id.sortName:
        {
            // Sort Application by App Name
            Collections.sort( listAppInfo, AppInfo.NAME_ORDER );
            
            // Information on all apps installed (Debug)
            Log.e( DEBUG_TAG, "listAppInfo.size() [" + listAppInfo.size() + "]" );
//            for ( int app = 0; app < listAppInfo.size(); app++ )
//            {
//                Log.d( DEBUG_TAG, "App [" + listAppInfo.get( app ).getappName() + "] : Package Name: = [market://search?q=" + listAppInfo.get( app ).getpackageName() + "] " + app );
//            }

            // Refresh the ListView
            homeManagerArrayAdapter.notifyDataSetChanged();

            return_value = true;
            break;
        }
        case R.id.sortNameDec:
        {
            // Sort Application by App Name
            Collections.sort( listAppInfo, AppInfo.NAME_ORDER_DECEND );

            // Refresh the ListView
            homeManagerArrayAdapter.notifyDataSetChanged();
            return_value = true;
            break;
        }
        case R.id.sortDefault:
        {
            // Sort Application by App Name
            Collections.sort( listAppInfo, AppInfo.DEFAULT_ORDER );

            // Refresh the ListView
            homeManagerArrayAdapter.notifyDataSetChanged();

            return_value = true;
            break;
        }
        case R.id.sortMemory:
        {
            // Sort Application by App Name
            Collections.sort( listAppInfo, AppInfo.MEMORY_ORDER );

            // Refresh the ListView
            homeManagerArrayAdapter.notifyDataSetChanged();

            return_value = true;
            break;
        }
        case R.id.preferences:
        {
            Intent preferencesIntent = new Intent();
            preferencesIntent.setAction( StaticConfig.PREFERENCES_INTENT );
            startActivity( preferencesIntent );

            return_value = true;
            break;
        }
        default:
            return_value = super.onOptionsItemSelected( item );
            break;
        } // End Switch

        return return_value;
    } // End onOptionsItemSelected

} // End HomeManagerActivity
// EOF