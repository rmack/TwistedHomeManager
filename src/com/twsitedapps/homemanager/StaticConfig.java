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

import android.content.SharedPreferences;

/*****************************************************************************
 * StaticConfig - Global static references.
 * 
 * @author Russell T Mackler
 * @version 1.1
 * @since 1.0.1.9
 */
public class StaticConfig
{
    // No way to instantiate
    private StaticConfig(){};
    
    public static final String      TWISTED_TAG                 = "+++++ TWISTED +++++ ";

    // Intents
    public static final String      GETHOME_INTENT              = "com.twsitedapps.homemanager.action.GETHOME";
    public static final String      QUICK_SELET_INTENT          = "com.twsitedapps.homemanager.action.QUICK_SELECT";

    // External Location references
    public static final String      THM_URL                     = "http://www.twistedapps.org";
    public static final String      ROLL_URL                    = "http://www.twistedapps.org/?cat=192";
    public static final String      GITHUB_URL                  = "https://github.com/rmack/TwistedHomeManager";
    public static final String      THM_BLOG                    = "http://www.twistedapps.org/?page_id=563";
    public static final String      THM_MRK                     = "market://search?q=com.twsitedapps.homemanager";

    // All Shared Preference keys
    // ---------------------------------------------------------
    public static SharedPreferences preferences;

    // Standard Options
    public static final String      THEME                       = "themeKey";
    public static final String      NOTIFICATION_KEY            = "notificationKey";
    public static int               theme                       = 0;
    public static final int         BLACK                       = 0;
    public static final int         WHITE                       = 1;
    public static final int         GREY                        = 2;
    public static final int         CYAN                        = 3;
    public static final int         GREEN                       = 4;
    public static final int         MAGENTA                     = 5;

       
    // List of known Home Apps
    public static final String      ADWLauncher2                = "market://details?id=org.adw.launcher";
    public static final String      ADWLauncher2Name            = "ADW Launcher 2";
    public static final String      ADWLauncher1EX              = "market://details?id=org.adwfreak.launcher";
    public static final String      ADWLauncher1EXName          = "ADW Launcher 1 EX";
    public static final String      MicrosoftLauncher           = "market://details?id=com.microsoft.launcher";
    public static final String      MicrosoftLauncherName       = "Microsoft Launcher";
    public static final String      CarHomeUltra                = "market://details?id=spinninghead.carhome";
    public static final String      CarHomeUltraName            = "Car Home Ultra";
    public static final String      GOLauncherEX                = "market://details?id=com.gau.go.launcherex";
    public static final String      GOLauncherEXName            = "GO Launcher";
    public static final String      NiagaraLauncher             = "market://details?id=bitpit.launcher";
    public static final String      NiagaraLauncherName         = "Niagara Launcher";
    public static final String      TotalLauncher               = "market://details?id=com.ss.launcher2";
    public static final String      TotalLauncherName           = "Total Launcher";
    public static final String      SquareHome                  = "market://details?id=com.ss.squarehome2";
    public static final String      SquareHomeName              = "Square Home";
    public static final String      POCOLauncher                = "market://details?id=com.mi.android.globallauncher";
    public static final String      POCOLauncherName            = "POCO Launcher 2.0";
    public static final String      Olauncher                   = "market://details?id=app.olauncher";
    public static final String      OlauncherName               = "Olauncher";
    public static final String      LynxLauncher                = "market://details?id=org.n277.lynxlauncher";
    public static final String      LynxLauncherName            = "Lynx Launcher";
    public static final String      HyperionLauncher            = "market://details?id=projekt.launcher";
    public static final String      HyperionLauncherName        = "Hyperion Launcher";
    public static final String      Lawnchair                   = "market://details?id=app.lawnchair.play";
    public static final String      LawnchairName               = "Lawnchair";
    public static final String      AIOLauncher                 = "market://details?id=ru.execbit.aiolauncher";
    public static final String      AIOLauncherName             = "AIO Launcher";
    public static final String      BeforeLauncher              = "market://details?id=com.beforesoft.launcher";
    public static final String      BeforeLauncherName          = "Before Launcher";
    public static final String      NovaLauncher                = "market://details?id=com.teslacoilsw.launcher";
    public static final String      NovaLauncherName            = "Nova Launcher";
    public static final String      SmartLauncher               = "market://details?id=ginlemon.flowerfree";
    public static final String      SmartLauncherName           = "Smart Launcher 6";

} // End StaticConfig
