/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package io.movintracks.demo;

import android.os.Bundle;
import org.apache.cordova.*;

import com.facebook.UiLifecycleHelper;
import android.content.Intent;

public class MainActivity extends CordovaActivity
{

  private UiLifecycleHelper uiHelper;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.init();

        uiHelper = new UiLifecycleHelper(this, null);

        // Set by <content src="index.html" /> in config.xml
        loadUrl(launchUrl);
    }

    @Override
  public void onResume() {
    super.onResume();
    uiHelper.onResume();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    uiHelper.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onPause() {
    super.onPause();
    uiHelper.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    uiHelper.onDestroy();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    uiHelper.onSaveInstanceState(outState);
  }
}
