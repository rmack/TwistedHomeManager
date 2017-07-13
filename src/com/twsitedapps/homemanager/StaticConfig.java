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
    public static final String      PREFERENCES_INTENT          = "com.twsitedapps.homemanager.action.PREFERENCES";
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
    public static final String      Abode                       = "market://search?q=me.raspass.abode";
    public static final String      AbodeName                   = "Abode";
    public static final String      ADWLauncher                 = "market://search?q=org.adw.launcher.one";
    public static final String      ADWLauncherName             = "ADW Launcher 1";
    public static final String      ADWLauncher2                = "market://search?q=org.adw.launcher";
    public static final String      ADWLauncher2Name            = "ADW Launcher 2";
    public static final String      ADWLauncher1EX              = "market://search?q=org.adwfreak.launcher";
    public static final String      ADWLauncher1EXName          = "ADW Launcher 1 EX";
    public static final String      ApexLauncher                = "market://search?q=com.anddoes.launcher";
    public static final String      ApexLauncherName            = "Apex Launcher";
    public static final String      ApexLauncherPro             = "market://search?q=com.anddoes.launcher.pro";
    public static final String      ApexLauncherProName         = "Apex Launcher Pro";
    public static final String      aShell                      = "market://search?q=com.mobilityflow.ashell";
    public static final String      aShellName                  = "aShell";
    public static final String      ArrowLauncher               = "market://search?q=com.microsoft.launcher";
    public static final String      ArrowLauncherName           = "Arrow Launcher";
    public static final String      AtomLauncher                = "market://search?q=com.dlto.atom.launcher";
    public static final String      AtomLauncherName            = "Atom Launcher";
    public static final String      BuzzLauncher                = "market://search?q=com.buzzpia.aqua.launcher";
    public static final String      BuzzLauncherName            = "Buzz Launcher";
    public static final String      CarHomeUltra                = "market://search?q=spinninghead.carhome";
    public static final String      CarHomeUltraName            = "Car Home Ultra";
    public static final String      CrazyHomeLite               = "market://search?q=com.cdproductions.apps.crazyhomelite";
    public static final String      CrazyHomeLiteName           = "Crazy Home Lite";
    public static final String      EvieLauncher                = "market://search?q=is.shortcut";
    public static final String      EvieLauncherName            = "Evie Launcher";
    public static final String      EverythingHome              = "market://search?q=me.everything.launcher";
    public static final String      EverythingHomeName          = "Everything Home";
    public static final String      EZLauncher                  = "market://search?q=mobi.infolife.launcher2";
    public static final String      EZLauncherName              = "EZ Launcher";
    public static final String      FastHome                    = "market://search?q=com.bitzophrenic.android.FastHome";
    public static final String      FastHomeName                = "FastHome";
    public static final String      FinalLauncher               = "market://search?q=uistore.fieldsystem.final_launcher";
    public static final String      FinalLauncherName           = "Final Launcher";
    public static final String      GOLauncherEX                = "market://search?q=com.gau.go.launcherex";
    public static final String      GOLauncherEXName            = "GO Launcher EX";
    public static final String      GoogleNowLauncher           = "market://search?q=com.google.android.launcher";
    public static final String      GoogleNowLauncherName       = "Google Now Launcher";
    public static final String      HexyLauncherHD              = "market://search?q=com.swiftkey.hexy";
    public static final String      HexyLauncherName            = "Hexy Launcher";    
    public static final String      HoloLauncherHD              = "market://search?q=com.mobint.hololauncher.hd";
    public static final String      HoloLauncherHDName          = "Holo Launcher HD";
    public static final String      HoloLauncher                = "market://search?q=com.mobint.hololauncher";
    public static final String      HoloLauncherName            = "Holo Launcher";
    public static final String      homescreen3Dfreeversion     = "market://search?q=com.zeropointnine.homeScreen3d";
    public static final String      homescreen3DfreeversionName = "homescreen 3D (free version)";
    public static final String      KitKatLauncher              = "market://search?q=nl.ndsc.kitkatlauncher";
    public static final String      KitKatLauncherName          = "KitKat Launcher+";
    public static final String      Launcher8free               = "market://search?q=com.lx.launcher8";
    public static final String      Launcher8freeName           = "Launcher8 free";
    public static final String      LauncherPro                 = "market://search?q=com.fede.launcher";
    public static final String      LauncherProName             = "LauncherPro";
    public static final String      LightningLauncher           = "market://search?q=net.pierrox.lightning_launcher_extreme";
    public static final String      LightningLauncherName       = "Lightning Launcher";
    public static final String      MetroUI                     = "market://search?q=chrisman.android.home.metroui";
    public static final String      MetroUIName                 = "Metro UI";
    public static final String      MiniLauncher                = "market://search?q=com.jiubang.go.mini.launcher";
    public static final String      MiniLauncherName            = "Mini Launcher";
    public static final String      MXHomeLauncher              = "market://search?q=com.neomtel.mxhome";
    public static final String      MXHomeLauncherName          = "MXHome Launcher";
    public static final String      MyHomelite                  = "market://search?q=com.farm.myhome_lite";
    public static final String      MyHomeliteName              = "My Home Launcher";
    public static final String      NextLauncherLite            = "market://search?q=com.gtp.nextlauncher.trial";
    public static final String      NextLauncherLiteName        = "Next Launcher 3D Shell Lite";
    public static final String      NovaLauncher                = "market://search?q=com.teslacoilsw.launcher";
    public static final String      NovaLauncherName            = "Nova Launcher";
    public static final String      QQlauncher                  = "market://search?q=com.tencent.qqlauncher";
    public static final String      QQlauncherName              = "QQ launcher";
    public static final String      SmartLauncher               = "market://search?q=ginlemon.flowerfree";
    public static final String      SmartLauncherName           = "Smart Launcher";
    public static final String      Trebuchet                   = "market://search?q=com.kaneoriley.cyanogenport.launcher3";
    public static final String      TrebuchetName               = "Catapult";
    public static final String      ZenUILauncher               = "market://search?q=com.asus.launcher";
    public static final String      ZenUILauncherName           = "ZenUI Launcher";

} // End StaticConfig