# Android App Template

[![All Checks](https://github.com/lbressler13/android-app-template/actions/workflows/all_checks.yml/badge.svg?branch=main)](https://github.com/lbressler13/android-app-template/actions/workflows/all_checks.yml)

- [Overview](#overview)
- [Testing](#testing)
- [Linting](#linting)
- [Using the Template](#using-the-template)
- [Project Structure](#project-structure)
- [Image Attributions](#image-attributions)

## Overview

This is a template for creating an Android app with two build flavors.
It also contains a pre-made fragment to list icons that are used in the app, and a basic developer tools dialog. 
This dialog can easily be augmented with app-specific functionality, such as erasing data from ViewModels.

The code in this template is inspired by other repos that share functionality, tests, and utils, including the [Trick Calculator](https://github.com/lbressler13/trick-calculator) app.

### Build Flavors

The template is set up with 2 builds flavors: dev and final.
The developer tools dialog is only available in the dev variant, and it can be accessed via the icon in the bottom left corner of the screen.

See [here](https://developer.android.com/studio/build/build-variants) for information about configuring build variants in an Android app.

### Dependencies

The template has a dependency on a [kotlin-utils](https://github.com/lbressler13/kotlin-utils) package, which is hosted in the GitHub Packages registry.
To pull packages from the registry, you need a GitHub access token with the `read:packages` scope.
**Do not commit your access token.**

In order to build to with the package, you can add the following properties to a gradle.properties file:
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

### Unit Tests

Unit tests are implemented using the Kotlin test library. 
See [here](https://kotlinlang.org/api/latest/kotlin.test/) for information about the library.

Unit tests can be run via an IDE, or with the following command:
```./gradlew test```

### UI Tests

UI tests are implemented using the Espresso framework.
Tests can be configured for an individual build flavor, or can be shared across flavors.
The template has pre-existing tests for the image attributions fragment and developer tools dialog.

Tests can be run via an IDE, or with the following commands:
* All tests for both flavors: `./gradlew connectedCheck`
* Dev build flavor only: `./gradlew connectedDevDebugAndroidTest`
* Final build flavor only: `./gradlew connectedFinalDebugAndroidTest`

Espresso tests must be run on a physical device or an emulator.
See [here](https://developer.android.com/training/testing/espresso) for more information about testing with Espresso.

## Linting

Linting is done using [ktlint](https://ktlint.github.io/), using [this](https://github.com/jlleitschuh/ktlint-gradle) plugin.
See [here](https://pinterest.github.io/ktlint/latest/rules/standard) for a list of standard rules.

To run linting and fix formatting issues if possible, run the following command in the terminal or via an IDE:
```./gradlew ktlintFormat```

To run linting without fixing issues, run the following command in the terminal or via an IDE:
```./gradlew ktlintCheck```

## Using the Template

### Package Name

The package name can be updated using Android Studio.
To change it, right-click on the name of the package under project structure.
Select Refactor > Rename > In Whole Project, and input the new name.
This will change the name in all files and tests for the main source set, as well as the current build variant.
You will need to update the other build variant separately.

The name also needs to be changed in the following places:
- [ ] App name in the main and dev string resource files. These values will be used in the app manifest.
- [ ] Action bar title in the main string resource file.
- [ ] Theme name in the day and night theme resource files. The theme also needs to be updated in the app manifest.
- [ ] Application name and namespace in the app build.gradle.kts file
- [ ] Root project name in settings.gradle.kts

### Tests

The UI tests look at package name and action bar title.
These need to be updated in the following tests:
- [ ] BaseActivityTest, under useAppContext for both build variants
- [ ] HomeFragmentTest, under actionBarTitle
- [ ] AttributionsFragmentTest, under closeButton

### Workflows

The template contains 3 workflows. The first workflow runs basic checks, such as linting and unit tests.
The second workflow runs Espresso tests, and the third workflow runs both the first and second one.
The third workflow is triggered automatically on pushes to the main branch. 
However, Espresso tests use a large number of GitHub actions minutes, due to the amount of time they take and the OS they run on.
If there are restrictions on the minutes available, you should consider removing the Espresso step or disabling the automatic trigger for that workflow.

All three workflows use gradlew to execute commands.
You may need to run `git update-index --chmod=+x ./gradlew` to grant executable permissions in GitHub actions.

## Project Structure

```project
├── .github
│   ├── workflows           <-- workflow files to run in GitHub actions
├── app
│   ├── src
│   │   ├── dev             <-- code and resources that are specific to dev product flavor
│   │   ├── espresso        <-- UI tests
│   │   ├── espressoDev     <-- UI tests that are specific to dev product flavor
│   │   ├── espressoFinal   <-- UI tests that are specific to final product flavor
│   │   ├── final           <-- code and resources that are specific to final product flavor
│   │   ├── main
│   │   │   ├── kotlin      <-- main source code
│   │   │   │   ├── ext     <-- extension functions for existing classes
│   │   │   │   ├── ui      <-- UI to display the app
│   │   │   │   ├── utils   <-- shared utility functions
│   │   │   ├── res         <-- app resources, including strings, layouts, and images
│   │   │   ├── AndroidManifest.xml   <-- app manifest file
│   │   ├── test            <-- unit tests
│   ├── build.gradle.kts    <-- module level gradle file, contains app dependencies
├── build.gradle.kts        <-- project level gradle file
├── gradle.properties
├── README.md
└── settings.gradle
```

## Image Attributions

All images are taken from [Flaticon](https://www.flaticon.com/), which allows free use of icons for personal and commercial purposes with attribution.
This is the complete list of Flaticon images included in the template.
The list is also available within the template under the `ui.attributions` package.

| Icon                                                        | Creator                                                                                                                    | Link                                                                  |
|:------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------|:----------------------------------------------------------------------|
| ![img](app/src/main/res/drawable-hdpi/ic_chevron_down.png)  | Icon made by [Freepik](https://www.flaticon.com/authors/freepik) from <https://www.flaticon.com>                           | <https://www.flaticon.com/premium-icon/down-chevron_1633716>          |
| ![img](app/src/main/res/drawable-hdpi/ic_chevron_left.png)  | Icon made by [Freepik](https://www.flaticon.com/authors/freepik) from <https://www.flaticon.com>                           | <https://www.flaticon.com/premium-icon/left-chevron_1633718>          |
| ![img](app/src/main/res/drawable-hdpi/ic_chevron_right.png) | Icon made by [Freepik](https://www.flaticon.com/authors/freepik) from <https://www.flaticon.com>                           | <https://www.flaticon.com/premium-icon/right-chevron_1633719>         |
| ![img](app/src/main/res/drawable-hdpi/ic_chevron_up.png)    | Icon made by [Freepik](https://www.flaticon.com/authors/freepik) from <https://www.flaticon.com>                           | <https://www.flaticon.com/premium-icon/up-chevron_1633717>            |
| ![img](app/src/main/res/drawable-hdpi/ic_close.png)         | Icon made by [Ilham Fitrotul Hayat](https://www.flaticon.com/authors/ilham-fitrotul-hayat) from <https://www.flaticon.com> | <https://www.flaticon.com/premium-icon/cross_4421536>                 |
| ![img](app/src/main/res/drawable-hdpi/ic_info.png)          | Icon made by [Freepik](https://www.flaticon.com/authors/freepik) from <https://www.flaticon.com>                           | <https://www.flaticon.com/free-icon/info-button_64494>                |
| ![img](app/src/main/res/drawable-hdpi/ic_settings.png)      | Icon made by [Freepik](https://www.flaticon.com/authors/freepik) from <https://www.flaticon.com>                           | <https://www.flaticon.com/premium-icon/gear_484613>                   |

See [here](https://support.flaticon.com/s/article/Attribution-How-when-and-where-FI?language=en_US&Id=ka03V0000004Q5lQAE) for more information about Flaticon attributions.
