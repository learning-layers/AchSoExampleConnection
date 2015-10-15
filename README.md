AchSoExampleConnection
======================

Example app for communicating with [Ach so!][achso].

See `src/main/java/fi/aalto/legroup/achsoexampleconnection/ExampleActivity.java`

Player intents
--------------

Ach so! player can be launched from another apps using Android Intents.

See [Ach so! readme][achso-readme] for explanations of intents to launch Ach so! with.

Layers Box URL
--------------

Sharing the Layers Box URL is done using [`SharedPreferences`][sharedprefs] and `MODE_WORLD_READABLE`.
This technique is marked as deprecated and discouraged, but the alternatives seem even worse (using
databases, which is way more complicated and has the same risks, or writing plain files to the
users' directories, which would confuse the users).

Because the `SharedPreferences` are user readable the URL is encrypted before storing it. This is
done using [`CryptoHelper`][cryptohelper]. It can be installed from jCenter as
`fi.aalto.legroup:cryptohelper`. `CryptoHelper` requires an 128-bit base64 encoded key.

The secret key is stored in `src/main/res/values/secrets.xml`. In real apps this should be kept hidden
from source control. The key needs to be shared between all Layers apps that should be able to read
the shared properties.

[achso]: https://github.com/learning-layers/achso
[achso-readme]: https://github.com/learning-layers/AchSo/tree/6e90fc49678f1b4eb41e173a284acfaebd7b29e2#launching-ach-so-from-intents
[sharedprefs]: http://developer.android.com/reference/android/content/SharedPreferences.html
[cryptohelper]: https://github.com/learning-layers/CryptoHelper

License
-------

```
Copyright 2013–2015 Aalto University

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

