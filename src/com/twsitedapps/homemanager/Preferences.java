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

/*****************************************************************************
 * AppPreferences - Preferences
 * 
 * @author Russell T Mackler
 * @version 1.0
 * @since 1.0
 */
package com.twsitedapps.homemanager;

import java.util.FormatFlagsConversionMismatchException;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;


/*****************************************************************************
 * Preferences - Used for preferences
 * 
 * @author Russell T Mackler
 * @version 1.0.1.8
 * @since 1.0
 */
public class Preferences extends FragmentActivity implements PreferenceFragmentCompat.OnPreferenceStartScreenCallback
{
    private final static String DEBUG_TAG            = Preferences.class.getSimpleName();
    private final static int    NOTIFICATION_REQUEST = 1;
    
    private Activity thisActivity = null;
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        thisActivity = this;
        setContentView( R.layout.preferences );

        if( savedInstanceState == null )
        {
            getSupportFragmentManager().beginTransaction()
                                       .replace( R.id.preferencesContainer, new PreferencesFragment() )
                                       .commit();
        }

    } // End onCreate
    
    
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override protected void onResume()
    {
        super.onResume();
        // Set the default language if the user changes it
        AppLocale.getInstance( thisActivity ).setDefaultLocale();
    }


    /*****************************************************************************
     * getPrefs - Get All of the current preferences
     * 
     * @param a - Activity - The current Activity calling this method
     *  
     */
    static public void getPrefs( final Activity a )
    {
        try
        {
            // Get the Preference Manager
            StaticConfig.preferences = PreferenceManager.getDefaultSharedPreferences( a );
               
            // Get Twisted home manager's theme 
            String theme = StaticConfig.preferences.getString( StaticConfig.THEME, Integer.toString( 0 ) );
            StaticConfig.theme = Integer.parseInt( theme );           
        }
        catch( FormatFlagsConversionMismatchException e )
        {
            Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "onCreate : FormatFlagsConversionMismatchException" );
            e.printStackTrace();
        }
        catch( NullPointerException e )
        {
            Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "onCreate : NullPointerException" );
            e.printStackTrace();
        }

    } // End getPrefs


    /*****************************************************************************
     * canPostNotifications - Check whether Android allows this app to post notifications
     * 
     * @param context - Context - The current Context calling this method
     * @return boolean - true if notifications can be posted, false otherwise
     *  
     */
    static public boolean canPostNotifications( final Context context )
    {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
               context.checkSelfPermission( Manifest.permission.POST_NOTIFICATIONS ) == PackageManager.PERMISSION_GRANTED;
    } // End canPostNotifications


    /*****************************************************************************
     * requestNotificationPermission - Request notification permission from user action
     * 
     */
    public void requestNotificationPermission()
    {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission( Manifest.permission.POST_NOTIFICATIONS ) != PackageManager.PERMISSION_GRANTED )
        {
            requestPermissions( new String[] { Manifest.permission.POST_NOTIFICATIONS }, NOTIFICATION_REQUEST );
        }
    } // End requestNotificationPermission


    /*
     * (non-Javadoc)
     * @see android.app.Activity#onRequestPermissionsResult(int, java.lang.String[], int[])
     */
    @Override public void onRequestPermissionsResult( int requestCode, String[] permissions, int[] grantResults )
    {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );

        if( requestCode == NOTIFICATION_REQUEST )
        {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences( thisActivity ).edit();

            if( grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED )
            {
                editor.putBoolean( StaticConfig.NOTIFICATION_KEY, true );
                editor.apply();
                Util.showNotification( thisActivity );
            }
            else
            {
                editor.putBoolean( StaticConfig.NOTIFICATION_KEY, false );
                editor.apply();
                NotificationManager notificationManager = (NotificationManager) thisActivity.getSystemService( Context.NOTIFICATION_SERVICE );
                notificationManager.cancelAll();
            }
        }
    } // End onRequestPermissionsResult


    /*
     * (non-Javadoc)
     * @see androidx.preference.PreferenceFragmentCompat.OnPreferenceStartScreenCallback#onPreferenceStartScreen(androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceScreen)
     */
    @Override public boolean onPreferenceStartScreen( PreferenceFragmentCompat caller, PreferenceScreen preferenceScreen )
    {
        PreferencesFragment fragment = new PreferencesFragment();
        Bundle args = new Bundle();
        args.putString( PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, preferenceScreen.getKey() );
        fragment.setArguments( args );

        getSupportFragmentManager().beginTransaction()
                                   .replace( R.id.preferencesContainer, fragment )
                                   .addToBackStack( preferenceScreen.getKey() )
                                   .commit();

        return true;
    }


    /*****************************************************************************
     * PreferencesFragment - Used for AndroidX preferences
     */
    public static class PreferencesFragment extends PreferenceFragmentCompat implements OnSharedPreferenceChangeListener
    {
        private Activity thisActivity = null;

        /*
         * (non-Javadoc)
         * @see androidx.preference.PreferenceFragmentCompat#onCreatePreferences(android.os.Bundle, java.lang.String)
         */
        @Override public void onCreatePreferences( Bundle savedInstanceState, String rootKey )
        {
            thisActivity = getActivity();

            try
            {
                setPreferencesFromResource( R.xml.preferences, rootKey );

                // Request notification permission only when the user enables notifications
                CheckBoxPreference notification = findPreference( StaticConfig.NOTIFICATION_KEY );
                if( notification != null )
                {
                    if( notification.isChecked() && !canPostNotifications( requireContext() ) )
                    {
                        notification.setChecked( false );
                    }

                    notification.setOnPreferenceChangeListener( new Preference.OnPreferenceChangeListener()
                    {
                        public boolean onPreferenceChange( Preference preference, Object newValue )
                        {
                            boolean enabled = (Boolean) newValue;

                            if( enabled )
                            {
                                if( canPostNotifications( requireContext() ) )
                                {
                                    Util.showNotification( thisActivity );
                                    return true;
                                }

                                ((Preferences) thisActivity).requestNotificationPermission();
                                return true;
                            }

                            NotificationManager notificationManager = (NotificationManager) thisActivity.getSystemService( Context.NOTIFICATION_SERVICE );
                            notificationManager.cancelAll();
                            return true;
                        }
                    } );
                }
        
                // Go to the Twisted home manager's blog version section
                Preference version = findPreference( "version" );
                if( version != null )
                {
                    version.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener()
                    {
                        public boolean onPreferenceClick( Preference preference )
                        {
                            try
                            {
                                startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( StaticConfig.THM_URL ) ) );
                            }
                            catch( NullPointerException e )
                            {
                                Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "version.setOnPreferenceClickListener : NullPointerException" );
                                e.printStackTrace();
                            }

                            return true;
                        }
                    } );
                }
                
                // Go to the Twisted home manager's blog
                Preference blog = findPreference( "blog" );
                if( blog != null )
                {
                    blog.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener()
                    {
                        public boolean onPreferenceClick( Preference preference )
                        {
                            try
                            {
                                startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( StaticConfig.THM_URL ) ) );
                            }
                            catch( NullPointerException e )
                            {
                                Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "blog.setOnPreferenceClickListener : NullPointerException" );
                                e.printStackTrace();
                            }
        
                            return true;
                        }
                    } );
                }
                
                // Get the custom preference send support e-mail
                Preference supportEmail = findPreference( "supportEmail" );
                if( supportEmail != null )
                {
                    supportEmail.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener()
                    {
                        public boolean onPreferenceClick( Preference preference )
                        {
                            /* Create the Intent */
                            final Intent emailIntent = new Intent( Intent.ACTION_SEND );
        
                            /* Fill it with Data */
                            emailIntent.setType( "plain/text" );
                            emailIntent.putExtra( Intent.EXTRA_EMAIL, new String[]{"twistedapps@twistedapps.org"} );
                            
                            final String subject = "THM - email " + getResources().getString( R.string.version_num );
                            
                            emailIntent.putExtra( Intent.EXTRA_SUBJECT, subject );
                            emailIntent.putExtra( Intent.EXTRA_TEXT, "Hi, " );
        
                            /* Send it off to the Activity-Chooser */
                            startActivity( Intent.createChooser( emailIntent, "Send mail..." ) );
        
                            return true;
                        }
                    } );
                }
        
                // Allow the user to rollback to an earlier release
                Preference rollback = findPreference( "rollback" );
                if( rollback != null )
                {
                    rollback.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener()
                    {
                        public boolean onPreferenceClick( Preference preference )
                        {
                            try
                            {
                                startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( StaticConfig.ROLL_URL ) ) );
                            }
                            catch( NullPointerException e )
                            {
                                Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "rollback.setOnPreferenceClickListener : NullPointerException" );
                                e.printStackTrace();
                            }
        
                            return true;
                        }
                    } );
                }
                
                // Allow the user to see open source project
                Preference github = findPreference( "github" );
                if( github != null )
                {
                    github.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener()
                    {
                        public boolean onPreferenceClick( Preference preference )
                        {
                            try
                            {
                                startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( StaticConfig.GITHUB_URL ) ) );
                            }
                            catch( NullPointerException e )
                            {
                                Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "github.setOnPreferenceClickListener : NullPointerException" );
                                e.printStackTrace();
                            }
        
                            return true;
                        }
                    } );
                }
                
                // Go to the Twisted home manager's Android Market location
                Preference market = findPreference( "market" );
                if( market != null )
                {
                    market.setOnPreferenceClickListener( new Preference.OnPreferenceClickListener()
                    {
                        public boolean onPreferenceClick( Preference preference )
                        {
                            try
                            {
                                startActivity( new Intent( Intent.ACTION_VIEW, Uri.parse( StaticConfig.THM_MRK ) ) );
                            }
                            catch( NullPointerException e )
                            {
                                Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "market.setOnPreferenceClickListener : NullPointerException" );
                                e.printStackTrace();
                            }
        
                            return true;
                        }
                    } );
                }
            }
            catch( FormatFlagsConversionMismatchException e )
            {
                Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "onCreate : FormatFlagsConversionMismatchException" );
                e.printStackTrace();
            }
            catch( NullPointerException e )
            {
                Log.e( DEBUG_TAG, StaticConfig.TWISTED_TAG + "onCreate : NullPointerException" );
                e.printStackTrace();
            }
        }
        
        
        /*
         * (non-Javadoc)
         * @see androidx.fragment.app.Fragment#onResume()
         */
        @Override public void onResume()
        {
            super.onResume();
            thisActivity = getActivity();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );
        }


        /*
         * (non-Javadoc)
         * @see androidx.fragment.app.Fragment#onPause()
         */
        @Override public void onPause()
        {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener( this );
        }
        
        
        /*
         * (non-Javadoc)
         * @see android.content.SharedPreferences.OnSharedPreferenceChangeListener#onSharedPreferenceChanged(android.content.SharedPreferences, java.lang.String)
         */
        @Override public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key )
        {
            try
            {
                // Detect when the user has changed the language option
                if( key.equals( "listLanguage" ) )
                {
                    // If there is a language change
                    // Make sure to set a flag for the main activity to restart
                    AppLocale.getInstance( thisActivity ).setLanguageChanged( true );
                    
                    // Check to see if we should clear the user's selected Locale language
                    if( sharedPreferences.getString( key, AppLocale.defaultInvalidValue ).equals( AppLocale.defaultInvalidValue ) )
                    {
                        // Clear the user's select Locale and use device real locale
                        AppLocale.getInstance( thisActivity ).clearLocale();
                    }
                    
                    // Preferences reloads, but language is updated?
                    Intent intent = thisActivity.getIntent();
                    intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                    thisActivity.finish();
                    startActivity( intent );
                }
            }
            catch( NullPointerException e )
            {
                e.printStackTrace();
            }
        }
    }
} // End Class AppPreferences
