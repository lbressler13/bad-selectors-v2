# Bad Selectors v2
    sourceSets.getByName("final") {
        java.setSrcDirs(listOf("src/final/kotlin"))
    }

[![All Tests](https://github.com/lbressler13/bad-selectors-v2/actions/workflows/all_checks.yml/badge.svg?branch=main)](https://github.com/lbressler13/bad-selectors-v2/actions/workflows/all_checks.yml)

## Overview

This is a rebuild of the [Bad Selectors](https://github.com/lbressler13/bad-selectors) app.

### Build Flavors

The app has 2 builds flavors: dev and final.
The developer tools dialog is only available in the dev variant, and it can be accessed via the icon in the bottom left corner of the screen.

See [here](https://developer.android.com/studio/build/build-variants) for information about configuring build variants in an Android app.

### Dependencies

The app has a dependency on a [kotlin-utils](https://github.com/lbressler13/kotlin-utils) package, which is hosted in the GitHub Packages registry.
To pull packages from the registry, you need a GitHub access token with the `read:packages` scope.
**Do not commit your access token.**

In order to build to with the package, you can add the following properties to a local.properties gradle file:
```properties
github.username=GITHUB_USERNAME
github.token=GITHUB_PAT
```
or as environment variables:
```shell
USERNAME=GITHUB_USERNAME
ACCESS_TOKEN=GITHUB_PAT
```

See [here](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#using-a-published-package) for more information on importing GitHub packages.

## Testing

Unit tests are implemented using the Kotlin test library. 
See [here](https://kotlinlang.org/api/latest/kotlin.test/) for information about the library.

Integrated tests are implemented using the [Espresso](https://developer.android.com/training/testing/espresso) framework with [Robolectric](https://robolectric.org/).
This allows integrated tests to run without a physical device or emulator.

Tests can be run with the following commands:
- Unit tests: `./gradlew test -PtestType=unit`
- Integrated tests: `./gradlew test -PtestType=robolectric`
- All tests: `./gradlew test`

Tests can also be run by build type and variant, such as `./gradlew testDevDebugUnitTest -PtestType=robolectric`

Note: Tests for hiding the dev tools button have not been moved to Robolectric due to issues with the looper. However, all other tests should be run with Robolectric.
The tests for hiding the button can be run with the following command:
```./gradlew connectedCheck```

See the Android docs for more information on testing with [Espresso](https://developer.android.com/training/testing/espresso) and [Robolectric](https://developer.android.com/training/testing/local-tests/robolectric).

## Linting

Linting is done using [ktlint](https://ktlint.github.io/), using [this](https://github.com/jlleitschuh/ktlint-gradle) plugin.
See [here](https://pinterest.github.io/ktlint/latest/rules/standard) for a list of standard rules.

To run linting and fix formatting issues if possible, run the following command in the terminal or via an IDE:
```./gradlew ktlintFormat```

To run linting without fixing issues, run the following command in the terminal or via an IDE:
```./gradlew ktlintCheck```

## Project Structure

```project
├── .github
│   ├── workflows   <-- workflow files to run in GitHub actions
├── app
│   ├── src
│   │   ├── androidTestDev    <-- UI tests that are specific to dev product flavor
│   │   ├── dev               <-- code and resources that are specific to dev product flavor
│   │   ├── final             <-- code and resources that are specific to final product flavor
│   │   ├── main
│   │   │   ├── kotlin      <-- main source code
│   │   │   │   ├── ext     <-- extension functions for existing classes
│   │   │   │   ├── ui      <-- UI to display the app
│   │   │   │   ├── utils   <-- shared utility functions
│   │   │   ├── res         <-- app resources, including strings, layouts, and images
│   │   │   ├── AndroidManifest.xml   <-- app manifest file
│   │   ├── test            <-- unit tests
│   │   ├── testDev         <-- unit tests that are specific to dev product flavor
│   │   ├── testFinal       <-- unit tests that are specific to final product flavor
│   ├── build.gradle.kts    <-- module level gradle file, contains app dependencies
├── build.gradle.kts        <-- project level gradle file
├── gradle.properties
├── README.md
└── settings.gradle
```

## Legal Stuff

All images are taken from [Flaticon](https://www.flaticon.com/), which allows free use of icons for personal and commercial purposes with attribution.
This is the complete list of Flaticon images included in the app.
The list is also available within the app under the `ui.attributions` package.

| Icon                                                        | Creator                                                                                                                    | Link                                                                   |
|:------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------|:-----------------------------------------------------------------------|
| ![img](src/main/res/drawable-hdpi/ic_arrow_left.png)        | Icon made by [Ilham Fitrotul Hayat](https://www.flaticon.com/authors/ilham-fitrotul-hayat) from <https://www.flaticon.com> | <https://www.flaticon.com/premium-icon/left_3416141>                   |
| ![img](src/main/res/drawable-hdpi/ic_calendar.png)          | Icon made by [Prosymbols Premium](https://www.flaticon.com/authors/prosymbols-premium) from <https://www.flaticon.com>     | <https://www.flaticon.com/premium-icon/calendar_2886665>               |
| ![img](src/main/res/drawable-hdpi/ic_calculator.png)        | Icon made by [Freepik](https://www.flaticon.com/authors/freepik)  from <https://www.flaticon.com>                          | <https://www.flaticon.com/premium-icon/calculator_1031688>             |
| ![img](src/main/res/drawable-hdpi/ic_cell_phone.png)        | Icon made by [Freepik](https://www.flaticon.com/authors/freepik)  from <https://www.flaticon.com>                          | <https://www.flaticon.com/free-icon/mobile-phone-with-nine-keys_77184> |
| ![img](app/src/main/res/drawable-hdpi/ic_chevron_down.png)  | Icon made by [Freepik](https://www.flaticon.com/authors/freepik) from <https://www.flaticon.com>                           | <https://www.flaticon.com/premium-icon/down-chevron_1633716>           |
| ![img](app/src/main/res/drawable-hdpi/ic_chevron_left.png)  | Icon made by [Freepik](https://www.flaticon.com/authors/freepik) from <https://www.flaticon.com>                           | <https://www.flaticon.com/premium-icon/left-chevron_1633718>           |
| ![img](app/src/main/res/drawable-hdpi/ic_chevron_right.png) | Icon made by [Freepik](https://www.flaticon.com/authors/freepik) from <https://www.flaticon.com>                           | <https://www.flaticon.com/premium-icon/right-chevron_1633719>          |
| ![img](app/src/main/res/drawable-hdpi/ic_chevron_up.png)    | Icon made by [Freepik](https://www.flaticon.com/authors/freepik) from <https://www.flaticon.com>                           | <https://www.flaticon.com/premium-icon/up-chevron_1633717>             |
| ![img](app/src/main/res/drawable-hdpi/ic_close.png)         | Icon made by [Ilham Fitrotul Hayat](https://www.flaticon.com/authors/ilham-fitrotul-hayat) from <https://www.flaticon.com> | <https://www.flaticon.com/premium-icon/cross_4421536>                  |
| ![img](app/src/main/res/drawable-hdpi/ic_divide.png)        | Icon made by [Icon Smart](https://www.flaticon.com/authors/icon-smart) from <https://www.flaticon.com>                     | <https://www.flaticon.com/free-icon/divide_9345134>                    |
| ![img](src/main/res/drawable-hdpi/ic_equals.png)            | Icon made by [Freepik](https://www.flaticon.com/authors/freepik)  from <https://www.flaticon.com>                          | <https://www.flaticon.com/free-icon/equal_56751>                       |
| ![img](app/src/main/res/drawable-hdpi/ic_home.png)          | Icon made by [Freepik](https://www.flaticon.com/authors/freepik) from <https://www.flaticon.com>                           | <https://www.flaticon.com/free-icon/home_1946436>                      |
| ![img](app/src/main/res/drawable-hdpi/ic_info.png)          | Icon made by [Freepik](https://www.flaticon.com/authors/freepik) from <https://www.flaticon.com>                           | <https://www.flaticon.com/free-icon/info-button_64494>                 |
| ![img](src/main/res/drawable-hdpi/ic_minus.png)             | Icon made by [Freepik](https://www.flaticon.com/authors/freepik)  from <https://www.flaticon.com>                          | <https://www.flaticon.com/free-icon/minus_56889>                       |
| ![img](app/src/main/res/drawable-hdpi/ic_phone.png)         | Icon made by [Ilham Fitrotul Hayat](https://www.flaticon.com/authors/ilham-fitrotul-hayat) from <https://www.flaticon.com> | <https://www.flaticon.com/premium-icon/phone-call_3059446>             |
| ![img](src/main/res/drawable-hdpi/ic_plus.png)              | Icon made by [Freepik](https://www.flaticon.com/authors/freepik)  from <https://www.flaticon.com>                          | <https://www.flaticon.com/premium-icon/plus_3524388>                   |
| ![img](src/main/res/drawable-hdpi/ic_restart.png)           | Icon made by [Freepik](https://www.flaticon.com/authors/freepik)  from <https://www.flaticon.com>                          | <https://www.flaticon.com/free-icon/refresh_561210>                    |
| ![img](app/src/main/res/drawable-hdpi/ic_settings.png)      | Icon made by [Freepik](https://www.flaticon.com/authors/freepik) from <https://www.flaticon.com>                           | <https://www.flaticon.com/premium-icon/gear_484613>                    |
| ![img](app/src/main/res/drawable-hdpi/ic_times.png)         | Icon made by [Pixel perfect](https://www.flaticon.com/authors/pixel-perfect) from <https://www.flaticon.com>               | <https://www.flaticon.com/free-icon/close_1828778>                     |

See [here](https://support.flaticon.com/s/article/Attribution-How-when-and-where-FI?language=en_US&Id=ka03V0000004Q5lQAE) for more information about Flaticon attributions.
