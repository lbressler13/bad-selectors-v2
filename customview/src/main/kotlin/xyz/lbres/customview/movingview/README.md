# MovingView

[MovingView](MovingView.kt) is an interface for a view whose position changes.
All children of MotionLayout must implement MovingView.

The following types of motion are supported:
- [NonContinuous](README_NonContinuous.md)
- [Linear](README_Linear.md)

All motion types implement MovingView, but they may have additional XML properties as well.
See the motion type READMEs for further details.

A MovingView can have continuous or noncontinuous motion.
Continuous motion means that positions updates generate a new position which is adjacent to the current position.
Noncontinuous motion means that there is no relation between position updates.


## XML Attributes

### Summary

| XML Attribute            | Summary                  | Type    | Required | Default |
|:-------------------------|:-------------------------|:--------|:---------|:--------|
| [app:paused](#apppaused) | If view motion is paused | boolean | No       | false   |

### app:paused

Boolean value indicating if position updates are paused.
This value is assigned to the paused property.

If the view has an attached OnPauseChangedListener, its callback will be invoked when the value of the attribute is changed.


## Methods

### setInitialPosition

Set initial position based on size of parent layout.
The position will be set regardless of the value of the paused property.

If the view has an attached OnMoveListener, its callback will **not** be invoked when the initial position is set.

This method takes the following parameters, and returns Unit:

| Parameter    | Summary                                                | Type    | Default |
|:-------------|:-------------------------------------------------------|:--------|:--------|
| parentWidth  | Width of parent view                                   | Int     |         |
| parentHeight | Height of parent view                                  | Int     |         |


### updatePosition

Update position based on size of parent layout.
The position will not update if the paused property is true, unless the forceUpdate parameter is provided.

If the view has an attached OnMoveListener, its callback will be invoked after the position update is complete.

This method takes the following parameters, and returns Unit:

| Parameter    | Summary                                                | Type    | Default |
|:-------------|:-------------------------------------------------------|:--------|:--------|
| parentWidth  | Width of parent view                                   | Int     |         |
| parentHeight | Height of parent view                                  | Int     |         |
| forceUpdate  | If the position should update even when paused is true | Boolean | false   |


### forcePosition

Force view to move to a specific position, even if motion is paused.

If the view has an attached OnMoveListener, its callback will be invoked after the position update is complete.

This method has 2 signatures, and returns Unit.

Signature 1:

| Parameter    | Summary               | Type   |
|:-------------|:----------------------|:-------|
| parentWidth  | Width of parent view  | Int    |
| parentHeight | Height of parent view | Int    |
| x            | New x value           | Double |
| y            | New y value           | Double |

Signature 2:

| Parameter    | Summary               | Type |
|:-------------|:----------------------|:-----|
| parentWidth  | Width of parent view  | Int  |
| parentHeight | Height of parent view | Int  |
| x            | New x value           | Int  |
| y            | New y value           | Int  |


### setOnMoveListener

Set listener to bind to position update events.

The method has 2 signatures, and returns Unit.

Signature 1:

| Parameter | Summary                                            | Type                       |
|:----------|:---------------------------------------------------|:---------------------------|
| listener  | Listener to observe position updates. Can be null. | MovingView.OnMoveListener? |

Signature 2:

| Parameter | Summary                                   | Type                           |
|:----------|:------------------------------------------|:-------------------------------|
| callback  | Callback to invoke when position updates. | (View, Double, Double) -> Unit |


### setOnPauseChangedListener

Set listener to bind to paused state change events.

The method has 2 signatures, and returns Unit.

Signature 1:

| Parameter | Summary                                                   | Type                               |
|:----------|:----------------------------------------------------------|:-----------------------------------|
| listener  | Listener to observe changes to paused state. Can be null. | MovingView.OnPauseChangedListener? |

Second signSignature 2:

| Parameter | Summary                                       | Type                    |
|:----------|:----------------------------------------------|:------------------------|
| callback  | Callback to invoke when paused state changes. | (View, Boolean) -> Unit |


## Event Listeners

### MovingView.OnMoveListener

Interface to bind to position update events.
It can be attached to a MovingView using the [setOnMoveListener](#setonmovelistener) method.

The interface consists of a single onMove function, which takes the following parameters and returns Unit:

| Parameter | Summary                     | Type   |
|:----------|:----------------------------|:-------|
| view      | View whose position updated | View   |
| x         | New position on the x axis  | Double |
| y         | New position on the y axis  | Double |


### MovingView.OnPauseChangedListener

Interface to bind to a paused state change events.
It can be attached to a MovingView using the [setOnPauseChangedListener](#setonpausechangedlistener) method.

The interface consists of a single onChange method, which takes the following parameters and returns Unit:

| Parameter | Summary                         | Type    |
|:----------|:--------------------------------|:--------|
| view      | View whose paused state changed | View    |
| paused    | New paused state of view        | Boolean |


## Implementations

### NoncontinuousMovingButton

NonContinuousMovingButton extends the AppCompatButton class and implements the MovingView interface. It inherits all values, methods, and attributes from the class and interface.

See the AppCompatButton documentation for more information about this class: https://developer.android.com/reference/androidx/appcompat/widget/AppCompatButton.
